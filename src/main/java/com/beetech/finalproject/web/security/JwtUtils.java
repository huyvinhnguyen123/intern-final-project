package com.beetech.finalproject.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * This file with give us some method to work with jwt token:
 * Create new jwt token with time expiration
 * Verify token is valid
 * Extract information from token
 */
@Component
public class JwtUtils {
    private final String SECRET_KEY = "Secret Key";
    private static final String REDIS_KEY_PREFIX = "jwt:";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String createToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        String token = buildToken(new HashMap<>(), userDetails.getUsername());

        // store token in Redis with an expiration time
        String redisKey = REDIS_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(redisKey, token);

        // set expiration time in Redis to match token expiration
        redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);

        // create token from build token method below
        return token;
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        // we will build token with some properties down here
        // we will set claim, set subject, set issued at, set expiration (1 hour)
        // and sign with (algorithm hs256 with secret key we had declared above)
        // combine all step there we have built new token from username
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public String extractUsername(String token) {
        // extract username from token
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        String username = extractUsername(token);
        String redisKey = REDIS_KEY_PREFIX + username;

        // check if the token exists in Redis
        Boolean exists = redisTemplate.hasKey(redisKey);
        return !exists;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        // generate token from build token method
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails.getUsername());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        String redisKey = REDIS_KEY_PREFIX + username;

        // check if the token exists in Redis
        Boolean exists = redisTemplate.hasKey(redisKey);

        // If the token exists and it's not expired, consider it valid
        return exists && !isTokenExpired(token) && username.equals(userDetails.getUsername());
    }
}