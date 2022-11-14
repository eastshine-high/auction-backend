package com.eastshine.auction.product.domain.item.option;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemOptionTest {

    @DisplayName("decreaseStockQuantity 메소드는")
    @Nested
    class Describe_decreaseStockQuantity{

        @Nested
        @DisplayName("보유한 재고 이상으로 재고를 차감하면")
        class Context_with_invalid_quantity{
            ItemOption itemOption = ItemOption.builder()
                    .stockQuantity(1)
                    .build();

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_throws_InvalidArgumentException() {
                int invalidQuantity = 30;

                assertThatThrownBy(() ->
                        itemOption.decreaseStockQuantity(invalidQuantity)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.ITEM_STOCK_QUANTITY_ERROR.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("보유한 재고 이내의 재고를 차감하면")
        class Context_with_valid_quantity{
            ItemOption itemOption = ItemOption.builder()
                    .stockQuantity(30)
                    .build();

            @Test
            @DisplayName("재고를 감소시킨다.")
            void it_decrease_stockQuantity() {
                int validQuantity = 1;

                itemOption.decreaseStockQuantity(validQuantity);
                assertThat(itemOption.getStockQuantity())
                        .isEqualTo(29);
            }
        }
    }

    @DisplayName("increaseStockQuantity 메소드는")
    @Nested
    class Describe_increaseStockQuantity{

        @Test
        @DisplayName("재고를 증가시킨다.")
        void it_increase_stockQuantity() {
            int stockQuantity = 30;
            int increaseQuantity = 1;
            ItemOption itemOption = ItemOption.builder()
                    .stockQuantity(stockQuantity)
                    .build();

            itemOption.increaseStockQuantity(increaseQuantity);

            assertThat(itemOption.getStockQuantity()).isEqualTo(stockQuantity + increaseQuantity);
        }
    }
}
