package com.beetech.finalproject.domain.repository;

import com.beetech.finalproject.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, ListCrudRepository<Product, Long> {
    @Query(value= """
            SELECT p.product_id, p.sku, p.old_sku, p.product_name, p.detail_info, p.price, p.delete_flag,
            ifp.name, ifp.path, c.category_id, COUNT(p.product_id) AS total_items
            FROM product p
            LEFT JOIN product_image pro_img ON pro_img.product_id = p.product_id
            LEFT JOIN image_for_product ifp ON ifp.image_id = pro_img.image_id
            LEFT JOIN product_category pc ON pc.product_id = p.product_id
            LEFT JOIN category c ON c.category_id  = pc.category_id
            WHERE c.category_id = :categoryId OR c.category_id IS NULL AND
            p.sku LIKE %:sku% OR p.sku IS NULL AND
            p.product_name LIKE %:productName% OR p.product_name IS NULL
            GROUP BY p.product_id""",
            nativeQuery = true)
    Page<Product> searchProductsAndPagination(@Param("categoryId") Long categoryId, @Param("sku") String sku,
                                              @Param("productName") String productName,
                                              Pageable pageable);

    @Query(value= """
            SELECT p.product_id, p.sku, p.old_sku, p.product_name, p.detail_info, p.price, p.delete_flag,
            ifp.name, ifp.path
            FROM product p
            LEFT JOIN product_image pro_img ON pro_img.product_id = p.product_id
            LEFT JOIN image_for_product ifp ON ifp.image_id = pro_img.image_id
            WHERE p.sku LIKE %:sku%""",
            nativeQuery = true)
    List<Product> searchProducts(@Param("sku") String sku);

    @Modifying
    @Query(value = "DELETE FROM ProductUser WHERE product_id = :productId AND user_id = :userId",
            nativeQuery = true)
    void deleteByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") String userId);
}
