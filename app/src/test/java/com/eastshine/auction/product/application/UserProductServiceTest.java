package com.eastshine.auction.product.application;

import com.eastshine.auction.category.CategoryFactory;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.UserProductSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class UserProductServiceTest {
    private static final int REGISTERED_CATEGORY_ID = 101;
    private static final String REGISTERED_PRODUCT_NAME = "마데카솔";

    @Autowired
    CategoryFactory categoryFactory;
    @Autowired
    ProductFactory productFactory;
    @Autowired UserProductService userProductService;

    @BeforeEach
    void setUp() {
        productFactory.deleteAll();
        categoryFactory.deleteAllCategory();

        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);
        Product product = productFactory.createProduct(REGISTERED_CATEGORY_ID, REGISTERED_PRODUCT_NAME);
    }

    @DisplayName("getProducts 메서드는 입력 받는 조건의 상품을 검색합니다.")
    @Test
    void getProducts() {
        String name = REGISTERED_PRODUCT_NAME.substring(2);
        UserProductSearchCondition searchCondition = UserProductSearchCondition.builder()
                .name(name)
                .build();

        Page<ProductDto> actuals = userProductService.getProducts(
                searchCondition,
                PageRequest.of(0, 10)
        );

        assertThat(actuals)
                .anySatisfy(productDto -> productDto.getName().equals(name));
    }
}
