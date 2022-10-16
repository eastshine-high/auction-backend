package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import com.eastshine.auction.product.lock.ItemStockLock;
import com.eastshine.auction.product.web.dto.SystemItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemOptionStockService {
    private static final String ITEM_OPTION_LOCK_PREFIX = "ITEM_OPTION_STOCK_";
    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final ItemStockLock itemStockLock;
    private final ItemOptionRepository itemOptionRepository;

    @Transactional
    public void decreaseStock(List<SystemItemDto.DecreaseStock.ItemOption> itemOptions) {
        itemOptions.stream().forEach(
                itemOptionDto -> {
                    itemStockLock.executeWithLock(
                            ITEM_OPTION_LOCK_PREFIX + String.valueOf(itemOptionDto.getId()),
                            LOCK_TIMEOUT_SECONDS,
                            () -> decreaseItemOptionStock(itemOptionDto.getId(), itemOptionDto.getQuantity())
                    );
                }
        );
    }

    private ItemOption decreaseItemOptionStock(Long id, int quantity) {
        ItemOption option = itemOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_OPTION_NOT_FOUND));
        option.decreaseStockQuantity(quantity);
        return itemOptionRepository.saveAndFlush(option);
    }
}
