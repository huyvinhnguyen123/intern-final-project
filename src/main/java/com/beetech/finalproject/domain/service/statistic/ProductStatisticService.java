package com.beetech.finalproject.domain.service.statistic;

import com.beetech.finalproject.common.LogStatus;
import com.beetech.finalproject.domain.entities.Product;
import com.beetech.finalproject.domain.entities.statistics.ProductStatistic;
import com.beetech.finalproject.domain.repository.ProductRepository;
import com.beetech.finalproject.domain.repository.ProductStatisticRepository;
import com.beetech.finalproject.utils.CustomDateTimeFormatter;
import com.beetech.finalproject.web.dtos.statistic.ProductStatisticDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductStatisticService {
    private final ProductStatisticRepository productStatisticRepository;
    private final ProductRepository productRepository;

    private static final long DEFAULT_VALUE = 0;

    /**
     * create product statistic with views and likes
     *
     * @param productId - input productId;
     */
    public void createProductStatistic(Long productId) {
        ProductStatistic productStatistic = new ProductStatistic();
        productStatistic.setProductId(productId);
        productStatistic.setLikeCount(DEFAULT_VALUE);
        productStatistic.setDislikeCount(DEFAULT_VALUE);
        productStatistic.setViewCount(DEFAULT_VALUE);
        productStatistic.setStatisticDate(CustomDateTimeFormatter.getLocalDate());

        productStatisticRepository.save(productStatistic);
        log.info("Create product statistic success");
    }

    /**
     * find product statistic by productId
     *
     * @param productId - input productId
     * @return - product statistic
     */
    private ProductStatistic findProductStatistic(Long productId) {
        ProductStatistic existingProductStatistic = productStatisticRepository.findByProductId(productId).orElseThrow(
                () -> {
                    log.error("Not found this product");
                    return new NullPointerException("Not found statistic by this product: " + productId);
                }
        );
        log.info("Found this statistic");
        return existingProductStatistic;
    }

    /**
     * update product statistic with views
     *
     * @param productId - input productStatisticId
     */
    public void updateProductViewStatistic(Long productId) {
        ProductStatistic existingProductStatistic = findProductStatistic(productId);
        existingProductStatistic.setViewCount(existingProductStatistic.getViewCount() + 1);
        existingProductStatistic.setStatisticDate(CustomDateTimeFormatter.getLocalDate());
        existingProductStatistic.setLastModifiedDate(Instant.now().atZone(ZoneId.systemDefault()));
        productStatisticRepository.save(existingProductStatistic);
        log.info("Update product statistic with view success");
    }

    /**
     * update product statistic with likes
     *
     * @param productId - input productStatisticId
     */
    public void updateProductLikeStatistic(Long productId) {
        ProductStatistic existingProductStatistic = findProductStatistic(productId);
        existingProductStatistic.setLikeCount(existingProductStatistic.getLikeCount() + 1);
        existingProductStatistic.setStatisticDate(CustomDateTimeFormatter.getLocalDate());
        existingProductStatistic.setLastModifiedDate(Instant.now().atZone(ZoneId.systemDefault()));
        productStatisticRepository.save(existingProductStatistic);
        log.info(LogStatus.updateSuccess("product with like"));
    }

    /**
     * update product statistic with dislikes
     *
     * @param productId - input productStatisticId
     */
    public void updateProductDisLikeStatistic(Long productId) {
        ProductStatistic existingProductStatistic = findProductStatistic(productId);
        existingProductStatistic.setDislikeCount(existingProductStatistic.getDislikeCount() + 1);
        existingProductStatistic.setStatisticDate(CustomDateTimeFormatter.getLocalDate());
        existingProductStatistic.setLastModifiedDate(Instant.now().atZone(ZoneId.systemDefault()));
        productStatisticRepository.save(existingProductStatistic);
        log.info(LogStatus.updateSuccess("product with dislike"));
    }

    /**
     * get product statistic list
     *
     * @return - product statistic
     */
    public List<ProductStatisticDto> getProductStatistic() {
        List<ProductStatisticDto> productStatisticDtoList = new ArrayList<>();
        List<ProductStatistic> productStatisticList = productStatisticRepository.findProductStatistics();

        for(ProductStatistic productStatistic: productStatisticList) {
            ProductStatisticDto productStatisticDto = new ProductStatisticDto();
            productStatisticDto.setProductId(productStatistic.getProductId());
            productStatisticDto.setViewsCount(productStatistic.getViewCount());
            productStatisticDto.setLikesCount(productStatistic.getLikeCount());
            productStatisticDto.setDislikesCount(productStatistic.getDislikeCount());

            for(Product product: productRepository.findAll()) {
                if(productStatistic.getProductId().equals(product.getProductId())) {
                    productStatisticDto.setSku(product.getSku());
                    productStatisticDto.setProductName(product.getProductName());
                    productStatisticDto.setPrice(product.getPrice());
                }
            }

            productStatisticDto.setStatisticDate(productStatistic.getStatisticDate());
            productStatisticDtoList.add(productStatisticDto);
        }
        log.info(LogStatus.selectAllSuccess("product statistic by today"));
        return productStatisticDtoList;
    }

    /**
     * get product statistic list
     *
     * @return - product statistic
     */
    public List<ProductStatisticDto> getProductStatisticByWeek() {
        List<ProductStatisticDto> productStatisticDtoList = new ArrayList<>();
        List<ProductStatistic> productStatisticList = productStatisticRepository.findProductStatisticsByWeek();

        for(ProductStatistic productStatistic: productStatisticList) {
            ProductStatisticDto productStatisticDto = new ProductStatisticDto();
            productStatisticDto.setProductId(productStatistic.getProductId());
            productStatisticDto.setViewsCount(productStatistic.getViewCount());
            productStatisticDto.setLikesCount(productStatistic.getLikeCount());
            productStatisticDto.setDislikesCount(productStatistic.getDislikeCount());

            for(Product product: productRepository.findAll()) {
                if(productStatistic.getProductId().equals(product.getProductId())) {
                    productStatisticDto.setSku(product.getSku());
                    productStatisticDto.setProductName(product.getProductName());
                    productStatisticDto.setPrice(product.getPrice());
                }
            }

            productStatisticDto.setStatisticDate(productStatistic.getStatisticDate());
            productStatisticDtoList.add(productStatisticDto);
        }
        log.info(LogStatus.selectAllSuccess("product statistic by week"));
        return productStatisticDtoList;
    }

    /**
     * get product statistic list
     *
     * @return - product statistic
     */
    public List<ProductStatisticDto> getProductStatisticByMonth() {
        List<ProductStatisticDto> productStatisticDtoList = new ArrayList<>();
        List<ProductStatistic> productStatisticList = productStatisticRepository.findProductStatisticsByMonth();

        for(ProductStatistic productStatistic: productStatisticList) {
            ProductStatisticDto productStatisticDto = new ProductStatisticDto();
            productStatisticDto.setProductId(productStatistic.getProductId());
            productStatisticDto.setViewsCount(productStatistic.getViewCount());
            productStatisticDto.setLikesCount(productStatistic.getLikeCount());
            productStatisticDto.setDislikesCount(productStatistic.getDislikeCount());

            for(Product product: productRepository.findAll()) {
                if(productStatistic.getProductId().equals(product.getProductId())) {
                    productStatisticDto.setSku(product.getSku());
                    productStatisticDto.setProductName(product.getProductName());
                    productStatisticDto.setPrice(product.getPrice());
                }
            }

            productStatisticDto.setStatisticDate(productStatistic.getStatisticDate());
            productStatisticDtoList.add(productStatisticDto);
        }
        log.info(LogStatus.selectAllSuccess("product statistic by month"));
        return productStatisticDtoList;
    }

    /**
     * get product statistic list
     *
     * @return - product statistic
     */
    public List<ProductStatisticDto> getProductStatisticByYear() {
        List<ProductStatisticDto> productStatisticDtoList = new ArrayList<>();
        List<ProductStatistic> productStatisticList = productStatisticRepository.findProductStatisticsByYear();

        for(ProductStatistic productStatistic: productStatisticList) {
            ProductStatisticDto productStatisticDto = new ProductStatisticDto();
            productStatisticDto.setProductId(productStatistic.getProductId());
            productStatisticDto.setViewsCount(productStatistic.getViewCount());
            productStatisticDto.setLikesCount(productStatistic.getLikeCount());
            productStatisticDto.setDislikesCount(productStatistic.getDislikeCount());

            for(Product product: productRepository.findAll()) {
                if(productStatistic.getProductId().equals(product.getProductId())) {
                    productStatisticDto.setSku(product.getSku());
                    productStatisticDto.setProductName(product.getProductName());
                    productStatisticDto.setPrice(product.getPrice());
                }
            }

            productStatisticDto.setStatisticDate(productStatistic.getStatisticDate());
            productStatisticDtoList.add(productStatisticDto);
        }
        log.info(LogStatus.selectAllSuccess("product statistic by year"));
        return productStatisticDtoList;
    }
}
