package com.beetech.finalproject.domain.service;

import com.beetech.finalproject.common.LogStatus;
import com.beetech.finalproject.domain.entities.Category;
import com.beetech.finalproject.domain.entities.CategoryImage;
import com.beetech.finalproject.domain.entities.ImageForCategory;
import com.beetech.finalproject.domain.entities.Product;
import com.beetech.finalproject.domain.repository.CategoryImageRepository;
import com.beetech.finalproject.domain.repository.CategoryRepository;
import com.beetech.finalproject.domain.repository.ImageForCategoryRepository;
import com.beetech.finalproject.domain.repository.ProductRepository;
import com.beetech.finalproject.domain.service.other.GoogleDriveService;
import com.beetech.finalproject.web.dtos.category.CategoryCreateDto;
import com.beetech.finalproject.web.dtos.category.CategoryRetrieveDto;
import com.beetech.finalproject.web.dtos.category.CategoryUpdateDto;
import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageForCategoryRepository imageForCategoryRepository;
    private final CategoryImageRepository categoryImageRepository;
    private final ProductRepository productRepository;
    private final GoogleDriveService googleDriveService;

    @Value("${drive.folder.category}")
    private String categoryPath;

    /**
     * create new category
     *
     * @param categoryCreateDto - input categoryCreateDto's properties
     */
    @Transactional
    public void createCategory(CategoryCreateDto categoryCreateDto) throws GeneralSecurityException, IOException {
        Category category = new Category();
        category.setCategoryName(categoryCreateDto.getCategoryName());
        categoryRepository.save(category);
        log.info(LogStatus.createSuccess("category"));

        ImageForCategory imageForCategory = new ImageForCategory();
        imageForCategory.setPath(googleDriveService.uploadImageAndGetId(categoryCreateDto.getImage(), categoryPath));
        imageForCategory.setName(categoryCreateDto.getImage().getOriginalFilename());
        imageForCategoryRepository.save(imageForCategory);
        log.info(LogStatus.createSuccess("image for category"));

        CategoryImage categoryImage = new CategoryImage();
        categoryImage.setCategory(category);
        categoryImage.setImageForCategory(imageForCategory);
        categoryImageRepository.save(categoryImage);
        log.info(LogStatus.createSuccess("category image"));
    }

    /**
     * find all categories
     *
     * @return - list categories
     */
    public Iterable<CategoryRetrieveDto> displayCategories() {
        List<CategoryRetrieveDto> categoryRetrieveDtoList = new ArrayList<>();

        List<CategoryImage> categoryImages = categoryImageRepository.findAll();
        List<ImageForCategory> imageForCategories = imageForCategoryRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        // loop list categories
        for(Category category: categories) {
            CategoryRetrieveDto categoryRetrieveDto = new CategoryRetrieveDto();
            categoryRetrieveDto.setId(category.getCategoryId());
            categoryRetrieveDto.setName(category.getCategoryName());

            List<ImageRetrieveDto> imageRetrieveDtoList = new ArrayList<>();
            ImageRetrieveDto imageRetrieveDto = new ImageRetrieveDto();

            // loop list categoryImages
            for(CategoryImage categoryImage: categoryImages) {
                // check if category id in category is same as category id in categoryImage
                if(categoryImage.getCategory().getCategoryId().equals(categoryRetrieveDto.getId())) {
                    // loop list imageCategories
                    for(ImageForCategory imageForCategory: imageForCategories) {
                        // check if image id in imageForCategory is same as image id in categoryImage
                        if(imageForCategory.getImageId().equals(categoryImage.getImageForCategory().getImageId())) {
                            imageRetrieveDto.setName(imageForCategory.getName());
                            imageRetrieveDto.setPath(imageForCategory.getPath());

                            imageRetrieveDtoList.add(imageRetrieveDto);
                            log.info("Get category's images success");
                        }
                    }
                }
            }
            categoryRetrieveDto.setImages(imageRetrieveDtoList);

            categoryRetrieveDtoList.add(categoryRetrieveDto);
            log.info(LogStatus.selectAllSuccess("categories"));
        }
        return categoryRetrieveDtoList;
    }

    /**
     * update category
     *
     * @param categoryId        - input categoryId
     * @param categoryUpdateDto - input categoryUpdateDto
     */
    @Transactional
    public void updateCategory(Long categoryId, CategoryUpdateDto categoryUpdateDto) throws GeneralSecurityException, IOException {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.error(LogStatus.selectOneFail("category"));
                    return new NullPointerException(LogStatus.searchOneFail("category") + categoryId);
                }
        );
        log.info(LogStatus.selectOneSuccess("category"));

        existingCategory.setCategoryName(categoryUpdateDto.getCategoryName());
        categoryRepository.save(existingCategory);

        // check if user input image
        if(categoryUpdateDto.getImage() != null) {
            // loop list categoryImages
            for(CategoryImage categoryImage: categoryImageRepository.findAll()) {
                // check if categoryId in categoryImage is same as categoryId in existingCategory
                if(categoryImage.getCategory().getCategoryId().equals(existingCategory.getCategoryId())) {
                    googleDriveService.deleteImageFromDrive(categoryImage.getImageForCategory().getPath());
                    log.info(LogStatus.deleteSuccess("drive file"));
                    categoryImageRepository.deleteById(categoryImage.getCategoryImageId());
                    log.info(LogStatus.deleteSuccess("category image"));
                    imageForCategoryRepository.deleteById(categoryImage.getImageForCategory().getImageId());
                    log.info(LogStatus.deleteSuccess("image for category"));
                }
            }

            ImageForCategory imageForCategory = new ImageForCategory();
            imageForCategory.setPath(googleDriveService.uploadImageAndGetId(categoryUpdateDto.getImage(), categoryPath));
            imageForCategory.setName(categoryUpdateDto.getImage().getOriginalFilename());
            imageForCategoryRepository.save(imageForCategory);
            log.info(LogStatus.createSuccess("image for category"));

            CategoryImage categoryImage = new CategoryImage();
            categoryImage.setCategory(existingCategory);
            categoryImage.setImageForCategory(imageForCategory);
            categoryImageRepository.save(categoryImage);
            log.info(LogStatus.createSuccess("category image"));
        }
        log.info(LogStatus.updateSuccess("category"));
    }

    /**
     * delete category
     *
     * @param categoryId - input categoryId
     */
    @Transactional
    public void deleteCategory(Long categoryId) throws RuntimeException, GeneralSecurityException, IOException {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.error(LogStatus.selectOneFail("category"));
                    return new NullPointerException(LogStatus.searchOneFail("category") + categoryId);
                }
        );
        log.info(LogStatus.selectOneSuccess("category"));

        for(Product product: productRepository.findAll()) {
            for(Category category: product.getCategories()) {
                if(category.equals(existingCategory) && product.getProductId() != null ) {
                    log.error("The category is still relation with product");
                }
            }
        }


        for(CategoryImage categoryImage: categoryImageRepository.findAll()) {
            if(categoryImage.getCategory().equals(existingCategory)) {
                googleDriveService.deleteImageFromDrive(categoryImage.getImageForCategory().getPath());
                log.info(LogStatus.deleteSuccess("drive file"));
                categoryImageRepository.deleteById(categoryImage.getCategoryImageId());
                log.info(LogStatus.deleteSuccess("category image"));
                imageForCategoryRepository.deleteById(categoryImage.getImageForCategory().getImageId());
                log.info(LogStatus.deleteSuccess("image for category"));
                categoryRepository.deleteById(categoryId);
                log.info(LogStatus.deleteSuccess("category"));
            }
        }
    }

    /**
     * Get category's image
     *
     * @param categoryId - input categoryId
     * @throws GeneralSecurityException - error
     * @throws IOException - error
     */
    public String getCategoryImage(Long categoryId) throws GeneralSecurityException, IOException {
        String fileId = "";
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.error("Not found this category");
                    return new NullPointerException("Not found this category: " + categoryId);
                }
        );
        log.info("Found this category");

        for(CategoryImage categoryImage: categoryImageRepository.findAll()) {
            if(categoryImage.getCategory().equals(existingCategory)) {
                fileId = categoryImage.getImageForCategory().getPath();
                log.info("Get file success");
            }
        }
        return fileId;
    }
}
