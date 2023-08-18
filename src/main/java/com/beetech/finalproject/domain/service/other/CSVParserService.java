package com.beetech.finalproject.domain.service.other;

import com.beetech.finalproject.common.DeleteFlag;
import com.beetech.finalproject.domain.entities.*;
import com.beetech.finalproject.domain.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CSVParserService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageForProductRepository imageForProductRepository;
    private final ProductImageRepository productImageRepository;
    private final DetailImageRepository detailImageRepository;

    /**
     * Import product's file
     *
     * @param inputStream - input file
     * @throws IOException - error
     */
    public void parseProduct(InputStream inputStream) throws IOException {
        List<Category> productCategories = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream)).build()) {
            csvReader.skip(1);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Data in CSV file
                String detailInfo = line[0];
                String price = line[1];
                String productName = line[2];
                String sku = line[3];
                String imageName = line[4];
                String imagePath = line[5];

                String detailImageName = line[6];
                String detailImagePath = line[7];

                Long categoryId = Long.parseLong(line[8]);

                // Create a new Product instance and populate fields
                Product product = new Product();
                product.setSku(sku);
                product.setProductName(productName);
                product.setDetailInfo(detailInfo);
                product.setPrice(Double.valueOf(price));
                List<Category> categories = categoryRepository.findAll();
                // Loop categories list
                for(Category category: categories) {
                    // Get list categories for product
                    if(category.getCategoryId().equals(categoryId)) {
                        productCategories.add(category);
                    }
                }
                product.setCategories(productCategories);
                product.setDeleteFlag(DeleteFlag.NON_DELETE.getCode());
                productRepository.save(product);
                log.info("Save product success!");

                // Create a new ImageForProduct instance and populate fields
                ImageForProduct imageForProduct = new ImageForProduct();
                imageForProduct.setPath(imagePath);
                imageForProduct.setName(imageName);
                imageForProductRepository.save(imageForProduct);
                log.info("Save image for product success");

                // Create a new ProductImage instance and populate fields
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageForProduct(imageForProduct);
                productImageRepository.save(productImage);
                log.info("Save product image success");

                // Create a new DetailImage instance and populate fields
                DetailImage detailImage = new DetailImage();
                detailImage.setPath(detailImagePath);
                detailImage.setName(detailImageName);
                detailImage.setImageForProduct(imageForProduct);
                detailImageRepository.save(detailImage);
                log.info("Save detail image success");
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
