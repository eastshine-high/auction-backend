package com.eastshine.auction.product.web;

import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import com.eastshine.auction.product.web.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/items")
@RestController
public class ItemController {
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;

    /**
     * 물품을 조회 조건에 맞게 검색하고 검색된 물품 정보들을 응답한다.
     *
     * @param condition 물품 조회 조건.
     * @param pageable 페이징 정보.
     * @return 페이징된 물품 정보들.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ItemDto.SearchResponse> getItems(
            @Validated ItemDto.SearchCondition condition,
            Pageable pageable
    ) {
        return itemRepository.searchItems(condition, pageable);
    }

    /**
     * 식별자에 해당하는 물품 정보를 찾아 응답한다.
     *
     * @param id 물품의 식별자
     * @return 해당 식별자의 물품 정보.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto.Info getItem(@PathVariable Long id) {
        ItemDto.Info itemInfo = itemRepository.findGuestItemInfo(id);
        itemInfo.setItemOptions(
                itemOptionRepository.findByItemId(id).stream()
                        .map(ItemDto.Info.ItemOption::new)
                        .collect(Collectors.toList())
        );
        return itemInfo;
    }
}
