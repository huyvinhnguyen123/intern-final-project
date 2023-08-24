package com.beetech.finalproject.domain.repository;

import com.beetech.finalproject.domain.entities.statistics.ProductStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStatisticRepository extends CrudRepository<ProductStatistic, Long>,
        ListCrudRepository<ProductStatistic, Long> {
    Optional<ProductStatistic> findByProductId(Long productId);

    @Query(value= """
               SELECT ps.statistic_id, ps.product_id, ps.like_count, ps.dislike_count, ps.view_count,\s
               ps.statistic_date, ps.created_date, ps.last_modified_date\s
               FROM product_statistic ps
               JOIN product p ON p.product_id = ps.product_id
               JOIN product_user pu ON pu.product_id = p.product_id
               WHERE DATE_FORMAT(STR_TO_DATE(ps.statistic_date, '%Y-%m-%d'), '%Y-%m-%d') = CURDATE()
               """, nativeQuery = true)
    List<ProductStatistic> findProductStatistics();

    @Query(value= """
               SELECT ps.statistic_id, ps.product_id, ps.like_count, ps.dislike_count, ps.view_count,\s
               ps.statistic_date, ps.created_date, ps.last_modified_date\s
               FROM product_statistic ps
               JOIN product p ON p.product_id = ps.product_id
               JOIN product_user pu ON pu.product_id = p.product_id
               WHERE DATE_FORMAT(STR_TO_DATE(ps.statistic_date, '%Y-%m-%d'), '%Y-%u') = DATE_FORMAT(CURDATE(), '%Y-%u')
               """, nativeQuery = true)
    List<ProductStatistic> findProductStatisticsByWeek();

    @Query(value= """
               SELECT ps.statistic_id, ps.product_id, ps.like_count, ps.dislike_count, ps.view_count,\s
               ps.statistic_date, ps.created_date, ps.last_modified_date\s
               FROM product_statistic ps
               JOIN product p ON p.product_id = ps.product_id
               JOIN product_user pu ON pu.product_id = p.product_id
               WHERE DATE_FORMAT(STR_TO_DATE(ps.statistic_date, '%Y-%m-%d'), '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')
               """, nativeQuery = true)
    List<ProductStatistic> findProductStatisticsByMonth();

    @Query(value= """
            SELECT ps.statistic_id, ps.product_id, ps.like_count, ps.dislike_count, ps.view_count,\s
            ps.statistic_date, ps.created_date, ps.last_modified_date\s
            FROM product_statistic ps
            JOIN product p ON p.product_id = ps.product_id
            JOIN product_user pu ON pu.product_id = p.product_id
            WHERE DATE_FORMAT(STR_TO_DATE(ps.statistic_date, '%Y-%m-%d'), '%Y') = DATE_FORMAT(CURDATE(), '%Y')                                       
            """, nativeQuery = true)
    List<ProductStatistic> findProductStatisticsByYear();
}
