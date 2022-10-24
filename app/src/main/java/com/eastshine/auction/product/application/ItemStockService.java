package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.lock.RedissonLock;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemStockService {
    private static final String ITEM_LOCK_PREFIX = "ITEM_STOCK_";

    private final RedissonLock redissonLock;
    private final ItemRepository itemRepository;

    @Transactional
    public void decreaseStockWithLock(Long id, Integer quantity){
        redissonLock.executeWithLock(
                ITEM_LOCK_PREFIX,
                id.toString(),
                () -> decreaseItemStock(id, quantity)
        );
    }

    private void decreaseItemStock(Long id, Integer quantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        item.decreaseStockQuantity(quantity);
        itemRepository.saveAndFlush(item);
    }
}
