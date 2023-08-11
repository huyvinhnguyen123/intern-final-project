package com.beetech.finalproject.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long productId;

    @Column(name = "sku", unique = true)
    private String sku;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "detail_info")
    private String detailInfo;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "is_like")
    private Boolean isLike = false;

    @Column(name = "delete_flag")
    private int deleteFlag = 0;

    @Column(name = "old_sku")
    private String oldSku;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
            name="product_category",
            joinColumns=@JoinColumn(name="product_id"),
            inverseJoinColumns=@JoinColumn(name="category_id")
    )
    private List<Category> categories;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
            name="product_user",
            joinColumns=@JoinColumn(name="product_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
    )
    private List<User> users;
}
