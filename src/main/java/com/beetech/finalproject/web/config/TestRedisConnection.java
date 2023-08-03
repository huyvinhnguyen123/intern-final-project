//package com.beetech.finalproject.web.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestRedisConnection {
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    public void testRedisConnection() {
//        try {
//            redisTemplate.opsForValue().set("test_key", "test_value");
//            String value = redisTemplate.opsForValue().get("test_key");
//            System.out.println("Successfully connected to Redis. Retrieved value: " + value);
//        } catch (Exception e) {
//            System.out.println("Failed to connect to Redis: " + e.getMessage());
//        }
//    }
//}
