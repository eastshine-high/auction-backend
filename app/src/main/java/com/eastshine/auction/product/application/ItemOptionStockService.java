package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.lock.RedissonLock;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemOptionStockService {
    private static final String ITEM_OPTION_LOCK_PREFIX = "ITEM_OPTION_STOCK_";

    private final RedissonLock redissonLock;
    private final ItemOptionRepository itemOptionRepository;

    @Transactional
    public void decreaseStockWithLock(Long optionId, Integer quantity) {
        redissonLock.executeWithLock(
                ITEM_OPTION_LOCK_PREFIX,
                optionId.toString(),
                () -> decreaseItemOptionStock(optionId, quantity)
        );
    }

    private void decreaseItemOptionStock(Long optionId, Integer quantity) {
        ItemOption option = itemOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        option.decreaseStockQuantity(quantity);
        itemOptionRepository.saveAndFlush(option);
    }

    @Transactional
    public void increaseStockWithLock(Long optionId, Integer quantity) {
        redissonLock.executeWithLock(
                ITEM_OPTION_LOCK_PREFIX,
                optionId.toString(),
                () -> increaseItemOptionStock(optionId, quantity)
        );
    }

    private void increaseItemOptionStock(Long optionId, Integer quantity) {
        ItemOption option = itemOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        option.increaseStockQuantity(quantity);
        itemOptionRepository.saveAndFlush(option);
    }
}
