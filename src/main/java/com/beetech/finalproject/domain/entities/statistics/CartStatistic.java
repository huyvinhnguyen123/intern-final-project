package com.beetech.finalproject.domain.entities.statistics;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_statistic")
@EntityListeners(AuditingEntityListener.class)
public class CartStatistic {
    @Id
    @Column(name = "statistic_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticId;
    private Long cartId;
    private Long numberOfAddToCart;
    private String statisticDate;
    @CreatedDate
    private ZonedDateTime createdDate = Instant.now().atZone(ZoneId.systemDefault());
    @LastModifiedDate
    private ZonedDateTime lastModifiedDate = Instant.now().atZone(ZoneId.systemDefault());
}
