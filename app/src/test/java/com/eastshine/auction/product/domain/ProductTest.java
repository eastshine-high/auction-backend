package com.eastshine.auction.product.domain;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
                .hasMessage(ErrorCode.PRODUCT_UNACCESSABLE.getErrorMsg());
    }
}
