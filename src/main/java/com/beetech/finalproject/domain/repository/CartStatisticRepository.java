package com.beetech.finalproject.domain.repository;

import com.beetech.finalproject.domain.entities.statistics.CartStatistic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartStatisticRepository extends CrudRepository<CartStatistic, Long>,
        ListCrudRepository<CartStatistic, Long> {
    Optional<CartStatistic> findByCartId(Long cartId);

}
