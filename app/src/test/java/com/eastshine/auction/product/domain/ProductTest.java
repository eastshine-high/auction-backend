package com.eastshine.auction.product.domain;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.product.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void validateAccessibleUser() {
        Product product = new Product();
        Long creatorId = 21L;
        Long accessorId = 2000L;
        ReflectionTestUtils.setField(product, "createdBy", creatorId);

        assertThatThrownBy(
                () -> product.validateAccessibleUser(accessorId)
        )
                .isExactlyInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.PRODUCT_INACCESSIBLE.getErrorMsg());
    }

    @DisplayName("decreaseStockQuantity 메소드는")
    @Nested
    class Describe_decreaseStockQuantity{

        @Nested
        @DisplayName("보유한 재고 이상으로 재고를 차감하면")
        class Context_with_invalid_quantity{
            Product product = Product.builder()
                    .stockQuantity(1)
                    .build();

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_throws_InvalidArgumentException() {
                int invalidQuantity = 30;

                assertThatThrownBy(() ->
                        product.decreaseStockQuantity(invalidQuantity)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.PRODUCT_STOCK_QUANTITY_ERROR.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("보유한 재고 이내의 재고를 차감하면")
        class Context_with_valid_quantity{
            Product product = Product.builder()
                    .stockQuantity(30)
                    .build();

            @Test
            @DisplayName("재고를 감소시킨다.")
            void it_decrease_stockQuantity() {
                int validQuantity = 1;

                product.decreaseStockQuantity(validQuantity);
                assertThat(product.getStockQuantity())
                        .isEqualTo(29);
            }
        }
    }
}
