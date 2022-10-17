package com.eastshine.auction.product.domain.item;

import com.eastshine.auction.order.web.OrderDto;
import com.eastshine.auction.product.web.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<ItemDto.SearchResponse> searchItems(ItemDto.SearchCondition condition, Pageable pageable);

    ItemDto.Info findGuestItemInfo(Long id);

    List<ItemDto.Info.ItemOption> findGuestItemOptionInfo(Long itemId);
}
