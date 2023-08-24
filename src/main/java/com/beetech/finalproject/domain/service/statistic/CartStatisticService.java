package com.beetech.finalproject.domain.service.statistic;

import com.beetech.finalproject.domain.entities.statistics.CartStatistic;
import com.beetech.finalproject.domain.repository.CartRepository;
import com.beetech.finalproject.domain.repository.CartStatisticRepository;
import com.beetech.finalproject.utils.CustomDateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartStatisticService {
    private final CartStatisticRepository cartStatisticRepository;
    private final CartRepository cartRepository;

    private final static long DEFAULT_VALUE = 0;

    /**
     * create cart statistic with number of add to cart
     *
     * @param cartId - input cartId
     */
    public void createCartStatistic(Long cartId) {
        CartStatistic cartStatistic = new CartStatistic();
        cartStatistic.setCartId(cartId);
        cartStatistic.setNumberOfAddToCart(DEFAULT_VALUE);
        cartStatistic.setStatisticDate(CustomDateTimeFormatter.getLocalDate());

        cartStatisticRepository.save(cartStatistic);
        log.info("Create cart statistic success");
    }

    /**
     * find cart statistic by cartId
     *
     * @param cartId - input cartId
     * @return - cart statistic
     */
    public CartStatistic findCartStatisticById(Long cartId) {
        CartStatistic existingCartStatistic = cartStatisticRepository.findByCartId(cartId).orElseThrow(
                ()->{
                    log.error("Not found this cart statistic");
                    return new NullPointerException("Not found this cart statistic by this cart: " + cartId);
                }
        );
        log.info("Found this cart statistic");
        return existingCartStatistic;
    }

    /**
     * update cart statistic
     *
     * @param cartId - input cartId
     */
    public void updateCartStatistic(Long cartId) {
        CartStatistic existingCartStatistic = findCartStatisticById(cartId);
        existingCartStatistic.setNumberOfAddToCart(existingCartStatistic.getNumberOfAddToCart() + 1);
        existingCartStatistic.setStatisticDate(CustomDateTimeFormatter.getLocalDate());
        existingCartStatistic.setLastModifiedDate(Instant.now().atZone(ZoneId.systemDefault()));
        cartStatisticRepository.save(existingCartStatistic);
        log.info("Update cart statistic success");
    }
}
