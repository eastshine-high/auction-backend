package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.lock.ItemStockLock;
import com.eastshine.auction.product.web.dto.SystemItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ItemStockService {
    private static final String ITEM_LOCK_PREFIX = "ITEM_STOCK_";
    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final ItemStockLock itemStockLock;
    private final ItemRepository itemRepository;
    private final ItemOptionStockService itemOptionStockService;

    @Transactional
    public void decreaseStock(SystemItemDto.DecreaseStock decreaseStockRequest) {
        decreaseStockRequest.getItemDtos().stream()
                .forEach(itemDto -> {
                    if(Objects.isNull(itemDto.getQuantity()) || itemDto.getQuantity() == 0){
                        itemOptionStockService.decreaseStock(itemDto.getItemOptionDtos());
                    }

                    itemStockLock.executeWithLock(
                            ITEM_LOCK_PREFIX + String.valueOf(itemDto.getId()),
                            LOCK_TIMEOUT_SECONDS,
                            () -> decreaseItemStock(itemDto.getId(), itemDto.getQuantity())
                    );
                });
    }

    private Item decreaseItemStock(Long id, Integer quantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        item.decreaseStockQuantity(quantity);
        return itemRepository.saveAndFlush(item);
    }
}
