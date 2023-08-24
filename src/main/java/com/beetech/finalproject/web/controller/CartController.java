package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.domain.entities.User;
import com.beetech.finalproject.domain.service.CartService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.cart.*;
import com.beetech.finalproject.web.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CartController {
    private final CartService cartService;

    @PostMapping("/add-cart")
    public ResponseEntity<ResponseDto<Object>> addProductToCart(@RequestBody CartCreateDto cartCreateDto, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User currentUser = (User) authentication.getPrincipal();

            log.info("request adding product to cart");
            CartRetrieveCreateDto cartRetrieveCreateDto = cartService.addProductToCart(cartCreateDto, currentUser);
            CartResponseCreate cartResponse = CartResponseCreate.builder()
                    .token(cartRetrieveCreateDto.getToken())
                    .totalPrice(cartRetrieveCreateDto.getTotalPrice())
                    .versionNo(cartRetrieveCreateDto.getVersionNo())
                    .build();

            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
        } else {
            log.info("request adding product to cart without authentication");
            CartRetrieveCreateDto cartRetrieveCreateDto = cartService.addProductToCartWithoutLogin(cartCreateDto);
            CartResponseCreate cartResponse = CartResponseCreate.builder()
                    .token(cartRetrieveCreateDto.getToken())
                    .totalPrice(cartRetrieveCreateDto.getTotalPrice())
                    .versionNo(cartRetrieveCreateDto.getVersionNo())
                    .build();

            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
        }
    }

//    @PostMapping("/sync-cart")
//    public ResponseEntity<ResponseDto<Object>> syncCartAfterLogin(@RequestBody String token, Authentication authentication) {
//        log.info("request syncing cart");
//        try {
//            User currentUser = (User) authentication.getPrincipal();
//
//            CartRetrieveSyncDto cartRetrieveSyncDto = cartService.syncCartAfterLogin(token, currentUser);
//            CartResponseSync cartResponse = CartResponseSync.builder()
//                    .totalPrice(cartRetrieveSyncDto.getTotalPrice())
//                    .totalQuantity(cartRetrieveSyncDto.getTotalQuantity())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        } catch (AuthenticationException e) {
//            log.error("Sync cart failed: " + e.getMessage());
//            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
//        }
//    }
//
//    @PostMapping("/cart-info")
//    public ResponseEntity<ResponseDto<Object>> displayCart(@RequestBody String token, Authentication authentication) {
//        log.info("request displaying cart");
//        if (authentication != null && authentication.isAuthenticated()) {
//            User currentUser = (User) authentication.getPrincipal();
//
//            CartRetrieveDto cartRetrieveDto = cartService.displayCart(token, currentUser);
//            CartResponse cartResponse = CartResponse.builder()
//                    .cartId(cartRetrieveDto.getCartId())
//                    .totalPrice(cartRetrieveDto.getTotalPrice())
//                    .versionNo(cartRetrieveDto.getVersionNo())
//                    .details(cartRetrieveDto.getCartDetailRetrieveDtos())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        } else {
//            CartRetrieveDto cartRetrieveDto = cartService.displayCart(token, null);
//            CartResponse cartResponse = CartResponse.builder()
//                    .cartId(cartRetrieveDto.getCartId())
//                    .totalPrice(cartRetrieveDto.getTotalPrice())
//                    .versionNo(cartRetrieveDto.getVersionNo())
//                    .details(cartRetrieveDto.getCartDetailRetrieveDtos())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        }
//    }
//
//    @GetMapping("/cart-quantity")
//    public ResponseEntity<ResponseDto<Object>> getTotalQuantityInCart(@RequestBody String token, Authentication authentication) {
//        log.info("request displaying cart");
//        if(authentication != null && authentication.isAuthenticated()) {
//            User currentUser = (User) authentication.getPrincipal();
//
//            CartSumQuantityDto cartSumQuantityDto = cartService.getTotalQuantityInCart(token, currentUser);
//            CartQuantitySumResponse cartResponse = CartQuantitySumResponse.builder()
//                    .cartSumQuantityDto(cartSumQuantityDto)
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        } else {
//            CartSumQuantityDto cartSumQuantityDto = cartService.getTotalQuantityInCart(token, null);
//            CartQuantitySumResponse cartResponse = CartQuantitySumResponse.builder()
//                    .cartSumQuantityDto(cartSumQuantityDto)
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        }
//    }
//
//    @PostMapping("/update-cart")
//    public ResponseEntity<ResponseDto<Object>> updateCart(@RequestBody CartUpdateDto cartUpdateDto, Authentication authentication) {
//        log.info("request updating cart");
//        if(authentication != null && authentication.isAuthenticated()) {
//            User currentUser = (User) authentication.getPrincipal();
//
//            CartRetrieveUpdateDto cartRetrieveUpdateDto = cartService.updateCart(cartUpdateDto, currentUser);
//            CartUpdateResponse cartResponse = CartUpdateResponse.builder()
//                    .totalQuantity(cartRetrieveUpdateDto.getTotalQuantity())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        } else {
//            CartRetrieveUpdateDto cartRetrieveUpdateDto = cartService.updateCart(cartUpdateDto, null);
//            CartUpdateResponse cartResponse = CartUpdateResponse.builder()
//                    .totalQuantity(cartRetrieveUpdateDto.getTotalQuantity())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        }
//    }
//
//    @DeleteMapping("/delete-cart")
//    public ResponseEntity<ResponseDto<Object>> deleteCart(@RequestBody CartDeleteDto cartDeleteDto, Authentication authentication) {
//        log.info("request deleting cart");
//        if(authentication != null && authentication.isAuthenticated()) {
//            User currentUser = (User) authentication.getPrincipal();
//
//            CartRetrieveSyncDto cartRetrieveSyncDto = cartService.deleteCart(cartDeleteDto, currentUser);
//            CartResponseSync cartResponse = CartResponseSync.builder()
//                    .totalQuantity(cartRetrieveSyncDto.getTotalQuantity())
//                    .totalPrice(cartRetrieveSyncDto.getTotalPrice())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        } else {
//            CartRetrieveSyncDto cartRetrieveSyncDto = cartService.deleteCart(cartDeleteDto, null);
//            CartResponseSync cartResponse = CartResponseSync.builder()
//                    .totalQuantity(cartRetrieveSyncDto.getTotalQuantity())
//                    .totalPrice(cartRetrieveSyncDto.getTotalPrice())
//                    .build();
//
//            return ResponseEntity.ok(ResponseDto.build().withData(cartResponse));
//        }
//    }
}
