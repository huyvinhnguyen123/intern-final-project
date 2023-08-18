package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.domain.entities.User;
import com.beetech.finalproject.domain.service.ProductService;
import com.beetech.finalproject.domain.service.other.CSVParserService;
import com.beetech.finalproject.domain.service.other.GoogleDriveService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.product.*;
import com.beetech.finalproject.web.response.ProductDetailResponse;
import com.beetech.finalproject.web.response.ProductResponse;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ProductController {
    private final ProductService productService;
    private final GoogleDriveService googleDriveService;
    private final CSVParserService csvParserService;

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Object>> importData(@RequestParam("file") MultipartFile file) {
        log.info("request importing products");

        try {
            csvParserService.parseProduct(file.getInputStream());
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (IOException e) {
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to import products: " + e.getMessage());
        }
    }

    @PostMapping(value = "/create-product",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Object>> createProduct(@RequestBody @ModelAttribute
                                                             ProductCreateDto productCreateDto) {
        log.info("request creating product");

        try {
            productService.createProduct(productCreateDto);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Create product failed: ", e);
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to create product: " + e.getMessage());
        }
    }

    @PutMapping(value = "/update-product",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Object>> updateProduct(@RequestParam Long productId, @RequestBody @ModelAttribute
    ProductCreateDto productCreateDto) {
        log.info("request updating product");

        try {
            productService.updateProduct(productId, productCreateDto);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Update product failed: ", e);
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to update product: " + e.getMessage());
        }
    }

    @GetMapping("/products")
    public ResponseEntity<ResponseDto<Object>> searchProductsAndPagination(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestBody @ModelAttribute
                                                                           ProductSearchInputDto productSearchInputDto) {

        Pageable pageable = PageRequest.of(page, size);
        log.info("request searching products");
        try {
            Page<ProductRetrieveDto> productRetrievePage = productService.searchProductsAndPagination(productSearchInputDto, pageable);
            List<ProductRetrieveDto> productRetrieveDtos = productRetrievePage.getContent();

            // add result inside response
            List<ProductResponse> productResponses = new ArrayList<>();
            ProductResponse productResponse =  ProductResponse.builder()
                    .products(productRetrieveDtos)
                    .build();

            productResponses.add(productResponse);

            return ResponseEntity.ok(ResponseDto.build().withData(productResponses));
        } catch (AuthenticationException e) {
            log.error("Search products failed: " + e.getMessage());
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to search product: " + e.getMessage());
        }
    }

    @GetMapping("/products/")
    public ResponseEntity<ResponseDto<Object>> searchProducts(@RequestParam String sku) {

        log.info("request searching products");
        try {
            List<ProductRetrieveSearchDetailDto> productRetrieveSearchDetailDtos = productService.searchProducts(sku);

            // add result inside response
            List<ProductDetailResponse> productResponses = new ArrayList<>();
            ProductDetailResponse productResponse =  ProductDetailResponse.builder()
                    .productRetrieveSearchDetailDtos(productRetrieveSearchDetailDtos)
                    .build();

            productResponses.add(productResponse);

            return ResponseEntity.ok(ResponseDto.build().withData(productResponses));
        } catch (AuthenticationException e) {
            log.error("Search products failed: " + e.getMessage());
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to search product: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-product")
    public ResponseEntity<ResponseDto<Object>> deleteProduct(@RequestParam Long productId) {
        log.info("request deleting category");

        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Delete product failed: ", e);
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to delete product: " + e.getMessage());
        }
    }

    @GetMapping("/like-product")
    public ResponseEntity<ResponseDto<Object>> likeProduct(@RequestParam Long productId,
                                                           Authentication authentication) {
        log.info("request liking product");

        try {
            User currentUser = (User) authentication.getPrincipal();
            productService.likeProduct(productId,currentUser);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException e) {
            log.error("Like product failed: " + e.getMessage());
            return HandleRequestException.handleRequest(HttpStatus.BAD_REQUEST,"Failed to like product: " + e.getMessage());
        }
    }

    @GetMapping("/product/download-image")
    public ResponseEntity<byte[]> downloadProductImage(@RequestParam Long productId) {

        log.info("request downloading category");

        try {
            String fileId = productService.getProductImage(productId);

            File fileMetadata = googleDriveService.downloadFromDrive(fileId);
            String fileName = fileMetadata.getName();
            String mimeType = fileMetadata.getMimeType();

            // Fetch the file content as a byte array
            byte[] fileContent = googleDriveService.downloadFileContent(fileId);

            // Create response headers with the correct MIME type and content disposition
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDispositionFormData(fileName, fileName);

            // Return the file content as a ResponseEntity with the appropriate headers
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Download product failed: ", e);
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @GetMapping("/product/detail-images/download-image")
    public ResponseEntity<byte[]> downloadProductDetailImage(@RequestParam Long productId, @RequestParam String path) {

        log.info("request downloading category");

        try {
            String fileId = productService.getProductDetailImage(productId, path);

            File fileMetadata = googleDriveService.downloadFromDrive(fileId);
            String fileName = fileMetadata.getName();
            String mimeType = fileMetadata.getMimeType();

            // Fetch the file content as a byte array
            byte[] fileContent = googleDriveService.downloadFileContent(fileId);

            // Create response headers with the correct MIME type and content disposition
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDispositionFormData(fileName, fileName);

            // Return the file content as a ResponseEntity with the appropriate headers
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Download product detail images failed: ", e);
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }
}
