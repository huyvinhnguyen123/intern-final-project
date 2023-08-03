package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.common.ValidationException;
import com.beetech.finalproject.domain.service.CategoryService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.category.CategoryCreateDto;
import com.beetech.finalproject.web.dtos.category.CategoryRetrieveDto;
import com.beetech.finalproject.web.dtos.category.CategoryUpdateDto;
import com.beetech.finalproject.web.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CategoryController {
    private final CategoryService categoryService;

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
        } catch (AuthenticationException e) {
            log.error("Create category failed: " + e.getMessage());
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
        } catch (AuthenticationException e) {
            log.error("Update category failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @DeleteMapping("/delete-category")
    public ResponseEntity<ResponseDto<Object>> deleteCategory(@RequestParam Long categoryId) {

        log.info("request deleting category");

        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException e) {
            log.error("Delete category failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

}
