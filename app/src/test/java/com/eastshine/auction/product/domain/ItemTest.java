package com.eastshine.auction.product.domain;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.product.domain.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTest {

    @Nested
    @DisplayName("validateAccessibleUser 메소드는")
    class Describe_validateAccessibleUser{

        @Test
        @DisplayName("물품을 생성한 사용자가 아닐 경우, InvalidArgumentException 예외를 던진다.")
        void contextWithInaccessibleUser() {
            Item item = new Item();
            Long creatorId = 21L;
            Long accessorId = 2000L;
            ReflectionTestUtils.setField(item, "createdBy", creatorId);

            assertThatThrownBy(
                    () -> item.validateAccessibleUser(accessorId)
            )
                    .isExactlyInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorCode.ITEM_INACCESSIBLE.getErrorMsg());
        }

        @Test
        @DisplayName("물품을 생성한 사용자일 경우, 예외를 던지지 않는다.")
        void contextWithAccessibleUser() {
            Item item = new Item();
            Long creatorId = 21L;
            Long accessorId = 21L;
            ReflectionTestUtils.setField(item, "createdBy", creatorId);

            item.validateAccessibleUser(accessorId);
        }
    }

    @DisplayName("decreaseStockQuantity 메소드는")
    @Nested
    class Describe_decreaseStockQuantity{

        @Nested
        @DisplayName("보유한 재고 이상으로 재고를 차감하면")
        class Context_with_invalid_quantity{
            Item item = Item.builder()
                    .stockQuantity(1)
                    .build();

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_throws_InvalidArgumentException() {
                int invalidQuantity = 30;

                assertThatThrownBy(() ->
                        item.decreaseStockQuantity(invalidQuantity)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.ITEM_STOCK_QUANTITY_ERROR.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("보유한 재고 이내의 재고를 차감하면")
        class Context_with_valid_quantity{
            Item item = Item.builder()
                    .stockQuantity(30)
                    .build();

            @Test
            @DisplayName("재고를 감소시킨다.")
            void it_decrease_stockQuantity() {
                int validQuantity = 1;

                item.decreaseStockQuantity(validQuantity);
                assertThat(item.getStockQuantity())
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
            // given
            int stockQuantity = 30;
            int increaseQuantity = 1;
            Item item = Item.builder()
                    .stockQuantity(stockQuantity)
                    .build();

            // when
            item.increaseStockQuantity(increaseQuantity);

            // then
            assertThat(item.getStockQuantity()).isEqualTo(stockQuantity + increaseQuantity);
        }
    }
}
