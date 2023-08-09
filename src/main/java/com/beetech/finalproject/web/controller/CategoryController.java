package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.domain.service.CategoryService;
import com.beetech.finalproject.domain.service.GoogleDriveService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.category.CategoryCreateDto;
import com.beetech.finalproject.web.dtos.category.CategoryRetrieveDto;
import com.beetech.finalproject.web.dtos.category.CategoryUpdateDto;
import com.beetech.finalproject.web.response.CategoryResponse;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CategoryController {
    private final CategoryService categoryService;
    private final GoogleDriveService googleDriveService;

    @PostMapping(value = "/add-category",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Object>> createCategory(@RequestBody @ModelAttribute
                                                                  CategoryCreateDto categoryCreateDto) {
        log.info("request creating category");

        try {
            categoryService.createCategory(categoryCreateDto);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Create category failed: ", e);
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }


    @GetMapping("/categories")
    public ResponseEntity<ResponseDto<Object>> findAllCategories() {
        log.info("request finding all categories");
        try {
            List<CategoryRetrieveDto> categoryRetrieveDtos = (List<CategoryRetrieveDto>)
                    categoryService.DisplayCategories();

            // add result inside response
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            CategoryResponse categoryResponse =  CategoryResponse.builder()
                    .categories(categoryRetrieveDtos)
                    .build();

            categoryResponses.add(categoryResponse);

            return ResponseEntity.ok(ResponseDto.build().withData(categoryResponses));
        } catch (AuthenticationException e) {
            log.error("Find all categories failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @PutMapping(value = "/delete-category",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Object>> updateCategory(@RequestBody @ModelAttribute
                                                              CategoryUpdateDto categoryUpdateDto,
                                                              @RequestParam Long categoryId) {
        log.info("request updating category");

        try {
            categoryService.updateCategory(categoryId, categoryUpdateDto);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Update category failed: ", e);
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @DeleteMapping("/delete-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Object>> deleteCategory(@RequestParam Long categoryId) {

        log.info("request deleting category");

        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | GeneralSecurityException | IOException e) {
            log.error("Delete category failed: ", e);
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @GetMapping("/category/download-image")
    public ResponseEntity<byte[]> downloadCategoryImage(@RequestParam Long categoryId) {

        log.info("request downloading category");

        try {
            String fileId = categoryService.getCategoryImage(categoryId);

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
            log.error("Download category failed: ", e);
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }
}
