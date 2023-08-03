package com.beetech.finalproject.domain.service;

import com.beetech.finalproject.domain.entities.Category;
import com.beetech.finalproject.domain.entities.CategoryImage;
import com.beetech.finalproject.domain.entities.ImageForCategory;
import com.beetech.finalproject.domain.entities.Product;
import com.beetech.finalproject.domain.repository.CategoryImageRepository;
import com.beetech.finalproject.domain.repository.CategoryRepository;
import com.beetech.finalproject.domain.repository.ImageForCategoryRepository;
import com.beetech.finalproject.domain.repository.ProductRepository;
import com.beetech.finalproject.exception.ValidFileExtensionException;
import com.beetech.finalproject.web.dtos.category.CategoryCreateDto;
import com.beetech.finalproject.web.dtos.category.CategoryRetrieveDto;
import com.beetech.finalproject.web.dtos.category.CategoryUpdateDto;
import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageForCategoryRepository imageForCategoryRepository;
    private final CategoryImageRepository categoryImageRepository;
    private final ProductRepository productRepository;

    @Value("${spring.folder.category}")
    private String categoryPath;

    /**
     * upload image for category
     *
     * @param file - input image(only accept .jpg)
     * @return url
     */
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            // Check file extension
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!fileExtension.equalsIgnoreCase("jpg")) {
                throw new ValidFileExtensionException("Invalid file format. Only JPG files are allowed.");
            }

            // Get the value of the file.upload.directory property
            String uploadDirectory = categoryPath;

            // Create the upload directory if it doesn't exist
            Path uploadDirectoryPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadDirectoryPath)) {
                Files.createDirectories(uploadDirectoryPath);
            }

            // Check if the file with the same name already exists
            Path filePath = uploadDirectoryPath.resolve(fileName);
            int count = 1;
            while (Files.exists(filePath)) {
                // If the file exists, append (count) before the extension and try again
                fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "(" + count + ")." + fileExtension;
                filePath = uploadDirectoryPath.resolve(fileName);
                count++;
            }

            // upload file to folder
            Files.copy(file.getInputStream(), filePath);

            return categoryPath + fileName;
        } catch (IOException e) {
            log.error("Failed to upload file: " + e.getMessage());
            return "Failed to upload file";
        }
    }

    /**
     * delete image that's exist in folder
     *
     * @param fileUrl - input file url from upload file
     */
    public void deleteFile(String fileUrl) {
        try {
            Path filePath = Paths.get(fileUrl).toAbsolutePath().normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted file: {}", fileUrl);
            } else {
                log.warn("File not found: {}", fileUrl);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl);
            e.printStackTrace();
        }
    }
    /**
     * create new category
     *
     * @param categoryCreateDto - input categoryCreateDto's properties
     * @return - category
     */
    @Transactional
    public Category createCategory(CategoryCreateDto categoryCreateDto) {
        Category category = new Category();
        category.setCategoryName(categoryCreateDto.getCategoryName());
        categoryRepository.save(category);
        log.info("Save new category success!");

        ImageForCategory imageForCategory = new ImageForCategory();
        imageForCategory.setPath(uploadFile(categoryCreateDto.getImage()));
        imageForCategory.setName(categoryCreateDto.getImage().getOriginalFilename());
        imageForCategoryRepository.save(imageForCategory);
        log.info("Save new image for category success!");

        CategoryImage categoryImage = new CategoryImage();
        categoryImage.setCategory(category);
        categoryImage.setImageForCategory(imageForCategory);
        categoryImageRepository.save(categoryImage);
        log.info("Save new category and image success!");

        log.info("Create category success!");
        return category;
    }

    /**
     * find all categories
     *
     * @return - list categories
     */
    public Iterable<CategoryRetrieveDto> DisplayCategories() {
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
            log.info("Get categories success");
            log.info("Display categories success");
        }
        return categoryRetrieveDtoList;
    }

    /**
     * update category
     *
     * @param categoryId - input categoryId
     * @param categoryUpdateDto - input categoryUpdateDto
     * @return - update category
     */
    @Transactional
    public Category updateCategory(Long categoryId, CategoryUpdateDto categoryUpdateDto) {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.error("Not found this category");
                    return new NullPointerException("Not found this category: " + categoryId);
                }
        );
        log.info("Found this category");

        existingCategory.setCategoryName(categoryUpdateDto.getCategoryName());
        categoryRepository.save(existingCategory);

        // check if user input image
        if(categoryUpdateDto.getImage() != null || !categoryUpdateDto.getImage().isEmpty() ) {
            // loop list categoryImages
            for(CategoryImage categoryImage: categoryImageRepository.findAll()) {
                // check if categoryId in categoryImage is same as categoryId in existingCategory
                if(categoryImage.getCategory().getCategoryId().equals(existingCategory.getCategoryId())) {
                    deleteFile(categoryImage.getImageForCategory().getPath());
                    log.info("Delete file success!");
                    categoryImageRepository.deleteById(categoryImage.getCategoryImageId());
                    log.info("Delete category image success!");
                    imageForCategoryRepository.deleteById(categoryImage.getImageForCategory().getImageId());
                    log.info("Delete image for category success!");
                }
            }

            ImageForCategory imageForCategory = new ImageForCategory();
            imageForCategory.setPath(uploadFile(categoryUpdateDto.getImage()));
            imageForCategory.setName(categoryUpdateDto.getImage().getOriginalFilename());
            imageForCategoryRepository.save(imageForCategory);
            log.info("Save new image for category success!");

            CategoryImage categoryImage = new CategoryImage();
            categoryImage.setCategory(existingCategory);
            categoryImage.setImageForCategory(imageForCategory);
            categoryImageRepository.save(categoryImage);
            log.info("Save new category and image success!");
        }
        log.info("Update category success!");
        return existingCategory;
    }

    /**
     * delete category
     *
     * @param categoryId - input categoryId
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.error("Not found this category");
                    return new NullPointerException("Not found this category: " + categoryId);
                }
        );
        log.info("Found this category");

        for(Product product: productRepository.findAll()) {
            for(Category category: product.getCategories()) {
                if(category.equals(existingCategory)) {
                    if(product.getProductId() != null ) {
                        log.error("The category is still relation with product");
                        throw new RuntimeException("The category still relation with product");
                    }
                }
            }
        }

        for(CategoryImage categoryImage: categoryImageRepository.findAll()) {
            if(categoryImage.getCategory().equals(existingCategory)) {
                deleteFile(categoryImage.getImageForCategory().getPath());
                log.info("Delete file success!");
                categoryImageRepository.deleteById(categoryImage.getCategoryImageId());
                log.info("Delete category image success!");
                imageForCategoryRepository.deleteById(categoryImage.getImageForCategory().getImageId());
                log.info("Delete image for category success!");
                categoryRepository.deleteById(categoryId);
                log.info("delete category success");
            }
        }
    }
}
