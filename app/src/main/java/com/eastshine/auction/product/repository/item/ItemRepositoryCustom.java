package com.eastshine.auction.product.repository.item;

import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.web.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ItemRepositoryCustom {
    Page<ItemDto.SearchResponse> searchItems(ItemDto.SearchCondition condition, Pageable pageable);

    Optional<Item> findByIdWithFetchJoin(Long itemId);
}
