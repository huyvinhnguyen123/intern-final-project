package com.beetech.finalproject.domain.repository;

import com.beetech.finalproject.domain.entities.Product;
import com.beetech.finalproject.domain.entities.ProductUser;
import com.beetech.finalproject.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUserRepository extends JpaRepository<ProductUser, Long> {
    ProductUser findByProductAndUser(Product product, User user);
    Iterable<ProductUser> findAllByProduct(Product product);
}