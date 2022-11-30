package com.eastshine.auction.product;

import com.eastshine.auction.product.application.SellerItemService;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.web.dto.SellerItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ItemFactory {
    @Autowired
    SellerItemService sellerItemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    public Item createItem(Integer categoryId, String name) {
        return createItem(categoryId, name, 5000, true, 5000);
    }

    public Item createItem(Integer categoryId, String name, Integer price, Boolean onSale, Integer stockQuantity) {
        SellerItemDto.ItemRegistration registrationRequest = SellerItemDto.ItemRegistration.builder()
                .categoryId(categoryId)
                .name(name)
                .price(price)
                .onSale(onSale)
                .stockQuantity(stockQuantity)
                .itemOptions(new ArrayList<>())
                .build();
        return sellerItemService.registerItem(registrationRequest);
    }

    public void deleteAll() {
        itemRepository.deleteAll();
        jdbcTemplate.execute("delete from item_option");
    }
}
