package com.beetech.finalproject.domain.repository;

import com.beetech.finalproject.domain.entities.Product;
import com.beetech.finalproject.domain.entities.ProductImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends CrudRepository<ProductImage, Long>,
        ListCrudRepository<ProductImage, Long> {
    List<ProductImage> findProductImagesByProduct(Product product);
}
