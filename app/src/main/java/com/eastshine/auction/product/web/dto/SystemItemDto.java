package com.eastshine.auction.product.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SystemItemDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecreaseStock{
        List<Item> itemDtos;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Item {
            private Long id;
            private Integer quantity;
            private List<ItemOption> itemOptionDtos;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ItemOption {
            private Long id;
            private Integer quantity;
        }
    }
}
