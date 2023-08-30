package com.beetech.finalproject.domain.service.other;

import com.beetech.finalproject.common.DeleteFlag;
import com.beetech.finalproject.common.LockFlag;
import com.beetech.finalproject.domain.entities.*;
import com.beetech.finalproject.domain.repository.*;
import com.beetech.finalproject.domain.service.statistic.ProductStatisticService;
import com.beetech.finalproject.exception.ValidFileExtensionException;
import com.beetech.finalproject.utils.CustomDateTimeFormatter;
import com.beetech.finalproject.web.dtos.statistic.ProductStatisticDto;
import com.beetech.finalproject.web.security.PasswordEncrypt;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
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
    private final ProductStatisticService productStatisticService;
    private final UserRepository userRepository;

    /**
     * check file extension (only accept csv file)
     *
     * @param file - input file
     */
    public void importCSVFile(MultipartFile file, String type) throws IOException {
        String fileName = file.getOriginalFilename();

        // Check file extension
        assert fileName != null;
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!fileExtension.equalsIgnoreCase("csv")) {
            throw new ValidFileExtensionException("Invalid file format. Only CSV files are allowed.");
        }

        if(type.equals("user")) {
            parseUser(file.getInputStream());
        }

        if(type.equals("product")) {
            parseProduct(file.getInputStream());
        }

    }

    /**
     * export file csv
     *
     * @param writer - input writer
     * @throws IOException - error
     */
    public void exportProductCSVFile(Writer writer, String time, String title) throws IOException {
        List<ProductStatisticDto> productStatisticDtoList = new ArrayList<>();

        // statistic today
        if(time.equals("today")) {
            title = "Product's statistic today";
            productStatisticDtoList = productStatisticService.getProductStatistic();
        }

        // statistic this week
        if(time.equals("week")) {
            title = "Product's statistic this week";
            productStatisticDtoList = productStatisticService.getProductStatisticByWeek();
        }

        // statistic this month
        if(time.equals("month")) {
            title = "Product's statistic this month";
            productStatisticDtoList = productStatisticService.getProductStatisticByMonth();
        }

        // statistic this year
        if(time.equals("year")) {
            title = "Product's statistic this year";
            productStatisticDtoList = productStatisticService.getProductStatisticByYear();
        }

        CSVWriter csvWriter = new CSVWriter(writer);

        // write header
        csvWriter.writeNext(new String[]{title});
        csvWriter.writeNext(new String[]{
                "ID","SKU","NAME","PRICE","VIEWS","LIKES","DISLIKES","DATE OF STATISTIC"
        });
        csvWriter.writeNext(new String[]{
                "-----", "-----", "-----",
                "-----", "-----", "-----",
                "-----", "-----"
        });

        // write data
        for(ProductStatisticDto productStatisticDto: productStatisticDtoList) {
            String[] data = {
                    productStatisticDto.getProductId().toString(),
                    productStatisticDto.getSku(),
                    productStatisticDto.getProductName(),
                    productStatisticDto.getPrice().toString(),
                    productStatisticDto.getViewsCount().toString(),
                    productStatisticDto.getLikesCount().toString(),
                    productStatisticDto.getDislikesCount().toString(),
                    productStatisticDto.getStatisticDate()
            };
            csvWriter.writeNext(data);
        }
        log.info("Export file csv success");
        csvWriter.close();
    }

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

                productStatisticService.createProductStatistic(product.getProductId());
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Import user's file
     *
     * @param inputStream - input file
     * @throws IOException - error
     */
    public void parseUser(InputStream inputStream) throws IOException {
        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream)).build()) {
            csvReader.skip(1);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Data in CSV file
                String loginId = line[0];
                String username = line[1];
                String birthday = line[2];
                String password = line[3];
                String role = line[4];

                // Create a new user instance and populate fields
                User user = new User();
                user.setLoginId(loginId);
                user.setUsername(username);
                user.setBirthDay(CustomDateTimeFormatter.dateOfBirthFormatter(birthday));
                user.setPassword(PasswordEncrypt.bcryptPassword(password));
                user.setLogFlag(LockFlag.NON_LOCK.getCode());
                user.setDeleteFlag(DeleteFlag.NON_DELETE.getCode());
                user.setRole(role);

                userRepository.save(user);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
