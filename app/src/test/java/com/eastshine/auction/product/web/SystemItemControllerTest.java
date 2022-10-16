package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.web.dto.SystemItemDto;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"dev"})
class SystemItemControllerTest extends WebIntegrationTest {

    @Autowired SystemItemController systemItemController;
    @Autowired ItemRepository itemRepository;
    @Autowired CategoryFactory categoryFactory;

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        categoryFactory.deleteAll();
    }

    // @Test - 공개 리포지토리에서 테스트 불가
    public void decreaseStock() throws Exception {
        categoryFactory.createCategory(500, "식품");
        int stockQuantity = 6;
        Item item = Item.builder()
                .stockQuantity(stockQuantity)
                .categoryId(500)
                .name("김치")
                .onSale(true)
                .price(30000)
                .build();
        itemRepository.save(item);

        SystemItemDto.DecreaseStock decreaseStock = new SystemItemDto.DecreaseStock(
                List.of(new SystemItemDto.DecreaseStock.Item(item.getId(), 1,
                        List.of(new SystemItemDto.DecreaseStock.ItemOption(null, 0))
                ))
        );

        mockMvc.perform(
                post("/system-api/items/decrease-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson(decreaseStock))
        )
                .andExpect(status().isOk());
    }
}
