package com.beetech.finalproject.domain.service;

import com.beetech.finalproject.common.DeleteFlag;
import com.beetech.finalproject.domain.entities.*;
import com.beetech.finalproject.domain.repository.*;
import com.beetech.finalproject.domain.service.other.EmailDetailsService;
import com.beetech.finalproject.domain.service.other.GoogleDriveService;
import com.beetech.finalproject.utils.mail.CustomMailGenerator;
import com.beetech.finalproject.web.dtos.email.EmailDetails;
import com.beetech.finalproject.web.dtos.email.ProductOrigin;
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
    private final GoogleDriveService googleDriveService;

    @Value("${drive.folder.product}")
    private String productPath;
    @Value("${drive.folder.detail-images}")
    private String detailImagesPath;

    /**
     * send mail to user
     *
     * @param emails - input list emails
     */
    public void sendProductUpdateMail(String[] emails, ProductOrigin productOrigin, ProductOrigin productAfterUpdate) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipients(emails);
        emailDetails.setSubject("You have 1 notification about product's information");
        emailDetails.setMsgBody(CustomMailGenerator.productWhistListMessage(productOrigin, productAfterUpdate));
        emailDetailService.sendMultipleMailWithFormHTML(emailDetails);
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
            }
        }

        product.setCategories(categoryForProductList);
        product.setDeleteFlag(DeleteFlag.NON_DELETE.getCode());
        productRepository.save(product);
        log.info("Save product success!");

        ImageForProduct imageForProduct = new ImageForProduct();
        imageForProduct.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getThumbnailImage(), productPath));
        imageForProduct.setName(productCreateDto.getThumbnailImage().getOriginalFilename());
        imageForProductRepository.save(imageForProduct);
        log.info("Save image for product success");

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageForProduct(imageForProduct);
        productImageRepository.save(productImage);
        log.info("Save product image success");

        List<MultipartFile> multipartImages = productCreateDto.getDetailImages();
        for(int i = 0; i < multipartImages.size(); i++) {
            DetailImage detailImage = new DetailImage();
            detailImage.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getDetailImages().get(i), detailImagesPath));
            detailImage.setName(productCreateDto.getDetailImages().get(i).getOriginalFilename());
            detailImage.setImageForProduct(imageForProduct);
            detailImageRepository.save(detailImage);
            log.info("Save detail image success");
        }

        log.info("Create product success");
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

    /**
     * delete product
     *
     * @param productId - input productId
     */
    @Transactional
    public void deleteProduct(Long productId) throws GeneralSecurityException, IOException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    log.error("Not found this product");
                    return new NullPointerException("Not found this product: " + productId);
                }
        );
        log.info("Found product");

        existingProduct.setOldSku(existingProduct.getSku());
        existingProduct.setSku(null);
        existingProduct.setDeleteFlag(DeleteFlag.DELETED.getCode());
        productRepository.save(existingProduct);
        log.info("Update product success");

        for(ProductImage productImage: productImageRepository.findAll()) {
            if(productImage.getProduct().equals(existingProduct)) {
                googleDriveService.deleteImageFromDrive(productImage.getImageForProduct().getPath());
                log.info("Delete file success!");
                for(ImageForProduct imageForProduct: imageForProductRepository.findAll()) {
                    if(imageForProduct.equals(productImage.getImageForProduct())) {
                        for(DetailImage detailImage: detailImageRepository.findAll()) {
                            if(detailImage.getImageForProduct().equals(imageForProduct)) {
                                googleDriveService.deleteImageFromDrive(detailImage.getPath());
                                detailImageRepository.deleteById(detailImage.getDetailImageId());
                                log.info("Delete detail image success");
                            }
                        }
                    }
                }
            }
            productImageRepository.deleteById(productImage.getProductImageId());
            log.info("Delete product image success");
            imageForProductRepository.deleteById(productImage.getImageForProduct().getImageProductId());
            log.info("Delete image for product success");
        }
    }

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

    /**
     * update product
     *
     * @param productId - input productId
     * @param productCreateDto - input productCreateDto's properties
     */
    @Transactional
    public Product updateProduct(Long productId, ProductCreateDto productCreateDto) throws GeneralSecurityException, IOException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                ()->{
                    log.error("Not found this product");
                    return new NullPointerException("Not found this product: " + productId);
                }
        );
        log.info("Found product");

        existingProduct.setSku(productCreateDto.getSku());
        existingProduct.setProductName(productCreateDto.getProductName());
        existingProduct.setDetailInfo(productCreateDto.getDetailInfo());
        existingProduct.setPrice(productCreateDto.getPrice());

        List<Category> categoryForProductList = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        for(Category c: categories) {
            if(c.getCategoryId().equals(productCreateDto.getCategoryId())) {
                categoryForProductList.add(c);
            }
        }

        existingProduct.setCategories(categoryForProductList);
        productRepository.save(existingProduct);
        log.info("Update product success");

        for(ProductImage productImage: productImageRepository.findAll()) {
            if(!productImage.getProduct().equals(existingProduct)) {
                ImageForProduct imageForProduct = new ImageForProduct();
                imageForProduct.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getThumbnailImage(), productPath));
                imageForProduct.setName(productCreateDto.getThumbnailImage().getOriginalFilename());
                imageForProductRepository.save(imageForProduct);
                log.info("Save image for product success");

                ProductImage newProductImage = new ProductImage();
                newProductImage.setProduct(existingProduct);
                newProductImage.setImageForProduct(imageForProduct);
                productImageRepository.save(newProductImage);
                log.info("Save product image success");

                List<MultipartFile> multipartImages = productCreateDto.getDetailImages();
                for(int i = 0; i < multipartImages.size(); i++) {
                    DetailImage detailImage = new DetailImage();
                    detailImage.setPath(googleDriveService.uploadImageAndGetId(productCreateDto.getDetailImages().get(i), detailImagesPath));
                    detailImage.setName(productCreateDto.getDetailImages().get(i).getOriginalFilename());
                    detailImage.setImageForProduct(imageForProduct);
                    detailImageRepository.save(detailImage);
                    log.info("Save detail image success");
                }
            } else {
                googleDriveService.deleteImageFromDrive(productImage.getImageForProduct().getPath());
                log.info("Delete file success!");
                for(ImageForProduct imageForProduct: imageForProductRepository.findAll()) {
                    if(imageForProduct.equals(productImage.getImageForProduct())) {
                        for(DetailImage detailImage: detailImageRepository.findAll()) {
                            if(detailImage.getImageForProduct().equals(imageForProduct)) {
                                googleDriveService.deleteImageFromDrive(detailImage.getPath());
                                detailImageRepository.deleteById(detailImage.getDetailImageId());
                                log.info("Delete detail image success");
                            }
                        }
                    }
                }

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
        log.info("Update product success");

        ProductOrigin productAfterUpdate = getOriginProduct(existingProduct);

        List<String> emails = new ArrayList<>();
        for(User user: existingProduct.getUsers()) {
            emails.add(user.getLoginId());
        }

        String[] emailArray = emails.toArray(new String[0]);
        sendProductUpdateMail(emailArray, productOrigin, productAfterUpdate);

        log.info("Update product & send mail for updating product success");
    }

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
                () -> {
                    log.error("Not found this product");
                    return new NullPointerException("Not found this product: " + productId);
                }
        );
        log.info("Found this product");

        for(ProductImage productImage: productImageRepository.findAll()) {
            if(productImage.getProduct().equals(existingProduct)) {
                fileId = productImage.getImageForProduct().getPath();
                log.info("Get file success");
            }
        }
        return fileId;
    }

    /**
     * Get product detail image
     *
     * @param productId - input productId
     * @throws GeneralSecurityException - error
     * @throws IOException - error
     */
    public String getProductDetailImage(Long productId, String path) throws GeneralSecurityException, IOException {
        String fileId = "";
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    log.error("Not found this product");
                    return new NullPointerException("Not found this product: " + productId);
                }
        );
        log.info("Found this product");

        for(ProductImage productImage: productImageRepository.findAll()) {
            if(productImage.getProduct().equals(existingProduct)) {
                for(ImageForProduct imageForProduct: imageForProductRepository.findAll()) {
                    if(imageForProduct.equals(productImage.getImageForProduct())) {
                        for(DetailImage detailImage: detailImageRepository.findAll()) {
                            if(detailImage.getImageForProduct().equals(imageForProduct)) {
                                if(detailImage.getPath().equals(path)) {
                                    fileId = productImage.getImageForProduct().getPath();
                                    log.info("Get file success");
                                }
                            }
                        }
                    }
                }
            }
        }
        return fileId;
    }
}
