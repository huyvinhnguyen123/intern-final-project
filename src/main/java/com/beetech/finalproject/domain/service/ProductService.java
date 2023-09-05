package com.beetech.finalproject.domain.service;

import com.beetech.finalproject.common.DeleteFlag;
import com.beetech.finalproject.common.LogStatus;
import com.beetech.finalproject.domain.entities.*;
import com.beetech.finalproject.domain.entities.statistics.ProductStatistic;
import com.beetech.finalproject.domain.repository.*;
import com.beetech.finalproject.domain.service.other.EmailDetailsService;
import com.beetech.finalproject.domain.service.other.GoogleDriveService;
import com.beetech.finalproject.domain.service.statistic.ProductStatisticService;
import com.beetech.finalproject.web.dtos.email.EmailDetails;
import com.beetech.finalproject.web.dtos.image.ImageRetrieveDto;
import com.beetech.finalproject.web.dtos.page.PageEntities;
import com.beetech.finalproject.web.dtos.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageForProductRepository imageForProductRepository;
    private final ProductImageRepository productImageRepository;
    private final DetailImageRepository detailImageRepository;
    private final ProductStatisticRepository productStatisticRepository;
    private final ProductUserRepository productUserRepository;
    private final GoogleDriveService googleDriveService;
    private final EmailDetailsService emailDetailService;
    private final ProductStatisticService productStatisticService;

    @Value("${drive.folder.product}")
    private String productPath;
    @Value("${drive.folder.detail-images}")
    private String detailImagesPath;

    /**
     * send mail to user
     *
     * @param emails - input list emails
     */
    public void sendProductUpdateMail(String[] emails, String sku, String productName, Double price) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipients(emails);
        emailDetails.setSubject("You have 1 notification about product's information");
        emailDetailService.sendMultipleMailWithFormHTML(emailDetails, sku, productName, price);
    }

    /**
     * create product
     *
     * @param productCreateDto - input productCreateDto's properties
     */
    public void createProduct(ProductCreateDto productCreateDto) throws GeneralSecurityException, IOException {
        Product product = new Product();
        product.setSku(productCreateDto.getSku());
        product.setProductName(productCreateDto.getProductName());
        product.setDetailInfo(productCreateDto.getDetailInfo());
        product.setPrice(productCreateDto.getPrice());

        List<Category> categoryForProductList = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        // Loop categories list
        for(Category category: categories) {
            // Get list categories for product
            if(category.getCategoryId().equals(productCreateDto.getCategoryId())) {
                categoryForProductList.add(category);
            } else {
                throw new NullPointerException("Not found this category");
            }
        }

        product.setCategories(categoryForProductList);
        product.setDeleteFlag(DeleteFlag.NON_DELETE.getCode());
        productRepository.save(product);
        log.info(LogStatus.createSuccess("product"));

        ImageForProduct imageForProduct = new ImageForProduct();
        imageForProduct.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getThumbnailImage(), productPath));
        imageForProduct.setName(productCreateDto.getThumbnailImage().getOriginalFilename());
        imageForProductRepository.save(imageForProduct);
        log.info(LogStatus.createSuccess("image for product"));

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageForProduct(imageForProduct);
        productImageRepository.save(productImage);
        log.info(LogStatus.createSuccess("product image"));

        List<MultipartFile> multipartImages = productCreateDto.getDetailImages();
        for(int i = 0; i < multipartImages.size(); i++) {
            DetailImage detailImage = new DetailImage();
            detailImage.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getDetailImages().get(i), detailImagesPath));
            detailImage.setName(productCreateDto.getDetailImages().get(i).getOriginalFilename());
            detailImage.setImageForProduct(imageForProduct);
            detailImageRepository.save(detailImage);
            log.info(LogStatus.createSuccess("detail image"));
        }
        productStatisticService.createProductStatistic(product.getProductId());
    }

    /**
     * get product by id
     *
     * @param productId - input productId
     * @return - product
     */
    private Product getProductById(Long productId) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    log.error(LogStatus.selectOneFail("product"));
                    return new NullPointerException(LogStatus.searchOneFail("product") + productId);
                }
        );
        log.info(LogStatus.selectOneSuccess("product"));
        return existingProduct;
    }

    /**
     * find product by id and add view
     *
     * @param productId - input productId
     * @return - product
     */
    public ProductDetailDto findProductById(Long productId) {
        Product existingProduct = getProductById(productId);
        productStatisticService.updateProductViewStatistic(existingProduct.getProductId());

        ProductDetailDto productDetailDto = new ProductDetailDto();
        productDetailDto.setProductId(existingProduct.getProductId());
        productDetailDto.setSku(existingProduct.getSku());
        productDetailDto.setProductName(existingProduct.getProductName());
        productDetailDto.setPrice(existingProduct.getPrice());
        productDetailDto.setDetailInfo(existingProduct.getDetailInfo());

        for(ProductImage productImage: productImageRepository.findAll()) {
            if(productImage.getProduct().equals(existingProduct)) {
                for(ImageForProduct imageForProduct: imageForProductRepository.findAll()){
                    if(imageForProduct.equals(productImage.getImageForProduct())) {
                        productDetailDto.setImage(imageForProduct.getName());
                        productDetailDto.setPath(imageForProduct.getPath());
                    }
                }
            }
        }

        ProductStatistic existingProductStatistic = productStatisticRepository.findByProductId(productId).orElseThrow(
                () -> {
                    log.error(LogStatus.selectOneFail("product statistic"));
                    return new NullPointerException(LogStatus.searchOneFail("product statistic") + productId);
                }
        );
        log.info(LogStatus.selectOneSuccess("product statistic"));

        productDetailDto.setViewsCount(existingProductStatistic.getViewCount());
        productDetailDto.setLikesCount(existingProductStatistic.getLikeCount());
        productDetailDto.setDislikesCount(existingProductStatistic.getDislikeCount());

        return productDetailDto;
    }

    /**
     * search products & pagination
     *
     * @param productSearchInputDto - input productSearchInputDto's properties
     * @param pageable - input pageable
     * @return - list products with pagination
     */
    public Page<ProductRetrieveDto> searchProductsAndPagination(ProductSearchInputDto productSearchInputDto,
                                                                Pageable pageable) {

        Page<Product> products = productRepository.searchProductsAndPagination(productSearchInputDto.getCategoryId(),
                productSearchInputDto.getSku(), productSearchInputDto.getProductName(),
                pageable);

        return products.map(product -> {
            ProductRetrieveDto productRetrieveDto = new ProductRetrieveDto();
            productRetrieveDto.setProductId(product.getProductId());
            productRetrieveDto.setProductName(product.getProductName());
            productRetrieveDto.setSku(product.getSku());
            productRetrieveDto.setDetailInfo(product.getDetailInfo());
            productRetrieveDto.setPrice(product.getPrice());

            for(ProductImage productImage: productImageRepository.findAll()) {
                if(productImage.getProduct().equals(product)) {
                    productRetrieveDto.setName(productImage.getImageForProduct().getName());
                    productRetrieveDto.setPath(productImage.getImageForProduct().getPath());
                }
            }

            PageEntities pageEntities = new PageEntities();
            pageEntities.setSize(pageable.getPageSize());
            pageEntities.setNumber(pageable.getPageNumber());

            productRetrieveDto.setPageable(pageEntities);

            log.info("Search products success");
            return productRetrieveDto;
        });
    }
// ==========================================================================================================
    /**
     * delete product
     *
     * @param productId - input productId
     */
    @Transactional
    public void deleteProduct(Long productId) throws GeneralSecurityException, IOException {
        Product existingProduct = getProductById(productId);

        existingProduct.setOldSku(existingProduct.getSku());
        existingProduct.setSku(null);
        existingProduct.setDeleteFlag(DeleteFlag.DELETED.getCode());
        productRepository.save(existingProduct);
        log.info(LogStatus.updateSuccess("product"));

        deleteProductImageAndImageForProduct(existingProduct);
    }

    private void deleteProductImageAndImageForProduct(Product existingProduct) throws GeneralSecurityException, IOException {
        for (ProductImage productImage : productImageRepository.findAll()) {
            deleteDetailImage(productImage, existingProduct);
            productImageRepository.deleteById(productImage.getProductImageId());
            log.info(LogStatus.deleteSuccess("product image"));
            imageForProductRepository.deleteById(productImage.getImageForProduct().getImageProductId());
            log.info(LogStatus.deleteSuccess("image for product"));
        }
    }

    private void deleteDetailImage(ProductImage productImage, Product existingProduct) throws GeneralSecurityException, IOException {
        if (productImage.getProduct().equals(existingProduct)) {
            googleDriveService.deleteImageFromDrive(productImage.getImageForProduct().getPath());
            log.info(LogStatus.deleteSuccess("file"));
            for (ImageForProduct imageForProduct : imageForProductRepository.findAll()) {
                if (imageForProduct.equals(productImage.getImageForProduct())) {
                    for (DetailImage detailImage : detailImageRepository.findAll()) {
                        if (detailImage.getImageForProduct().equals(imageForProduct)) {
                            googleDriveService.deleteImageFromDrive(detailImage.getPath());
                            detailImageRepository.deleteById(detailImage.getDetailImageId());
                            log.info(LogStatus.deleteSuccess("detail image"));
                        }
                    }
                }
            }
        }
    }
// ==========================================================================================================
    /**
     * search products with detail information
     *
     * @param sku - input sku
     * @return - list products
     */
    public List<ProductRetrieveSearchDetailDto> searchProducts(String sku) {
        List<Product> products = productRepository.searchProducts(sku);

        List<ProductRetrieveSearchDetailDto> productRetrieveSearchDetailDtos = new ArrayList<>();
        for(Product p: products) {
            ProductRetrieveSearchDetailDto productRetrieveSearchDetailDto = new ProductRetrieveSearchDetailDto();
            productRetrieveSearchDetailDto.setProductId(p.getProductId());
            productRetrieveSearchDetailDto.setSku(p.getSku());
            productRetrieveSearchDetailDto.setProductName(p.getProductName());
            productRetrieveSearchDetailDto.setDetailInfo(p.getDetailInfo());
            productRetrieveSearchDetailDto.setPrice(p.getPrice());

            List<ImageForProduct> imageForProducts = new ArrayList<>();
            for(ProductImage productImage: productImageRepository.findAll()) {
                if(productImage.getProduct().equals(p)) {
                    imageForProducts.add(productImage.getImageForProduct());
                }
            }

            List<ImageRetrieveDto> imageRetrieveDtos = new ArrayList<>();
            for(ImageForProduct ifp: imageForProducts) {
                for(DetailImage detailImage: detailImageRepository.findAll()) {
                    if(detailImage.getImageForProduct().equals(ifp)) {
                        ImageRetrieveDto imageRetrieveDto = new ImageRetrieveDto();
                        imageRetrieveDto.setName(detailImage.getName());
                        imageRetrieveDto.setPath(detailImage.getPath());

                        imageRetrieveDtos.add(imageRetrieveDto);
                    }
                }
            }
            productRetrieveSearchDetailDto.setImages(imageRetrieveDtos);
            productRetrieveSearchDetailDtos.add(productRetrieveSearchDetailDto);



        }
        log.info("Search products success");

        return productRetrieveSearchDetailDtos;
    }
// ==========================================================================================================
    /**
     * update product
     *
     * @param productId - input productId
     * @param productCreateDto - input productCreateDto's properties
     */
    @Transactional
    public void updateProduct(Long productId, ProductCreateDto productCreateDto) throws GeneralSecurityException, IOException {
        Product existingProduct = getProductById(productId);

        existingProduct.setSku(productCreateDto.getSku());
        existingProduct.setProductName(productCreateDto.getProductName());
        existingProduct.setDetailInfo(productCreateDto.getDetailInfo());
        existingProduct.setPrice(productCreateDto.getPrice());

        List<Category> categoryForProductList = getListCategoryForProduct(productCreateDto);
        existingProduct.setCategories(categoryForProductList);

        updateProductImageAndImageForProduct(existingProduct, productCreateDto);
        log.info(LogStatus.updateSuccess("product"));

        Iterable<ProductUser> productUsers = productUserRepository.findAllByProduct(existingProduct);
        for(ProductUser productUser: productUsers) {
            User userLikeProduct = productUser.getUser();
            ProductUser existingProductUser = productUserRepository.findByProductAndUser(existingProduct, userLikeProduct);

            if(existingProductUser.getIsLike().equals(true)) {
                List<String> emails = new ArrayList<>();
                emails.add(userLikeProduct.getLoginId());

                String[] emailArray = emails.toArray(new String[0]);
                sendProductUpdateMail(
                        emailArray,
                        existingProduct.getSku(),
                        existingProduct.getProductName(),
                        existingProduct.getPrice()
                );
            }
        }
        log.info("Update product & send mail for updating product success");
    }

    private List<Category> getListCategoryForProduct(ProductCreateDto productCreateDto) {
        List<Category> categoryForProductList = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        for(Category category: categories) {
            if(category.getCategoryId().equals(productCreateDto.getCategoryId())) {
                categoryForProductList.add(category);
            }
        }
        return categoryForProductList;
    }

    private void updateNewImage(Product existingProduct, ProductCreateDto productCreateDto) throws GeneralSecurityException, IOException {
        ImageForProduct imageForProduct = new ImageForProduct();
        imageForProduct.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getThumbnailImage(), productPath));
        imageForProduct.setName(productCreateDto.getThumbnailImage().getOriginalFilename());
        imageForProductRepository.save(imageForProduct);
        log.info(LogStatus.createSuccess("image for product"));

        ProductImage newProductImage = new ProductImage();
        newProductImage.setProduct(existingProduct);
        newProductImage.setImageForProduct(imageForProduct);
        productImageRepository.save(newProductImage);
        log.info(LogStatus.createSuccess("product image"));

        List<MultipartFile> multipartImages = productCreateDto.getDetailImages();
        for (int i = 0; i < multipartImages.size(); i++) {
            DetailImage detailImage = new DetailImage();
            detailImage.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getDetailImages().get(i), detailImagesPath));
            detailImage.setName(productCreateDto.getDetailImages().get(i).getOriginalFilename());
            detailImage.setImageForProduct(imageForProduct);
            detailImageRepository.save(detailImage);
            log.info(LogStatus.createSuccess("detail image"));
        }
    }

    private void updateProductImageAndImageForProduct(Product existingProduct, ProductCreateDto productCreateDto) throws GeneralSecurityException, IOException {
        for(ProductImage productImage: productImageRepository.findAll()) {
            if(!productImage.getProduct().equals(existingProduct)) {
                updateNewImage(existingProduct, productCreateDto);
            } else {
                ImageForProduct imageForProduct = productImage.getImageForProduct();
                imageForProduct.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getThumbnailImage(), productPath));
                imageForProduct.setName(productCreateDto.getThumbnailImage().getOriginalFilename());
                imageForProductRepository.save(imageForProduct);
                log.info("Save image for product success");

                List<MultipartFile> multipartImages = productCreateDto.getDetailImages();
                for(int i = 0; i < multipartImages.size(); i++) {
                    DetailImage detailImage = new DetailImage();
                    detailImage.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getDetailImages().get(i), detailImagesPath));
                    detailImage.setName(productCreateDto.getDetailImages().get(i).getOriginalFilename());
                    detailImage.setImageForProduct(imageForProduct);
                    detailImageRepository.save(detailImage);
                    log.info("Save detail image success");
                }
            }
        }
    }
// ==========================================================================================================
    /**
     * Get product's image
     *
     * @param productId - input productId
     * @throws GeneralSecurityException - error
     * @throws IOException - error
     */
    public String getProductImage(Long productId) throws GeneralSecurityException, IOException {
        String fileId = "";
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                ()->{
                    log.error(LogStatus.selectOneFail("product"));
                    return new NullPointerException(LogStatus.searchOneFail("product") + productId);
                }
        );
        log.info(LogStatus.selectOneSuccess("product"));

        for(ProductImage productImage: productImageRepository.findAll()) {
            if(productImage.getProduct().equals(existingProduct)) {
                fileId = productImage.getImageForProduct().getPath();
                log.info("Get file success");
            }
        }
        return fileId;
    }
// ==========================================================================================================
    /**
     * Get product detail image
     *
     * @param productId - input productId
     * @throws GeneralSecurityException - error
     * @throws IOException - error
     */
    public String getProductDetailImage(Long productId, String path) throws GeneralSecurityException, IOException {
        String fileId = "";
        Product existingProduct = getProductById(productId);
        fileId = getFileIdFromDrive(existingProduct, path, fileId);
        return fileId;
    }

    private String getFileIdFromDrive(Product existingProduct, String path, String fileId) {
        for (ProductImage productImage : productImageRepository.findProductImagesByProduct(existingProduct)) {
            ImageForProduct imageForProduct = productImage.getImageForProduct();
            for (DetailImage detailImage : detailImageRepository.findAll()) {
                if(detailImage.getImageForProduct().equals(imageForProduct) && (detailImage.getPath().equals(path))) {
                    fileId = productImage.getImageForProduct().getPath();
                    log.info("Get file success");
                }
            }
        }
        return fileId;
    }
// ==========================================================================================================
    /**
     * like product by user
     *
     * @param productId - input productId
     * @param user - input current user
     */
    @Transactional
    public void likeProduct(Long productId, User user) {
        Product existingProduct = getProductById(productId);
        ProductUser existingProductUser = productUserRepository.findByProductAndUser(existingProduct, user);
        if (existingProductUser != null) {
            // User has already interacted with the product, handle accordingly
            if (Boolean.FALSE.equals(existingProductUser.getIsLike())) {
                existingProductUser.setIsLike(true);
                productUserRepository.save(existingProductUser);
                productStatisticService.updateProductLikeStatistic(productId);
                log.info("Updated product user, set like success");
            } else {
                log.info("user has been liked this product");
            }
        } else {
            ProductUser productUser = new ProductUser();
            productUser.setProduct(existingProduct);
            productUser.setUser(user);
            productUser.setIsLike(true);

            productUserRepository.save(productUser);
            productStatisticService.updateProductLikeStatistic(productId);
            log.info("Saved product user, set like success");
        }
    }

    /**
     * dislike product by user
     *
     * @param productId - input productId
     * @param user - input current user
     */
    @Transactional
    public void dislikeProduct(Long productId, User user) {
        Product existingProduct = getProductById(productId);
        ProductUser existingProductUser = productUserRepository.findByProductAndUser(existingProduct, user);
        if (existingProductUser != null) {
            // User has already interacted with the product, handle accordingly
            if (Boolean.TRUE.equals(existingProductUser.getIsLike())) {
                existingProductUser.setIsLike(false);
                productUserRepository.save(existingProductUser);
                productStatisticService.updateProductDisLikeStatistic(productId);
                log.info("Updated product user, set dislike success");
            } else {
                log.info("user has been disliked this product");
            }
        } else {
            ProductUser productUser = new ProductUser();
            productUser.setProduct(existingProduct);
            productUser.setUser(user);
            productUser.setIsLike(false);

            productUserRepository.save(productUser);
            productStatisticService.updateProductDisLikeStatistic(productId);
            log.info("Saved product user, set dislike success");
        }
    }
}
