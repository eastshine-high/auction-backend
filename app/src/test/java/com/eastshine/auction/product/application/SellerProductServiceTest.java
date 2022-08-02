package com.eastshine.auction.product.application;

import com.eastshine.auction.category.CategoryFactory;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.security.UserAuthentication;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import com.eastshine.auction.user.UserFactory;
import com.eastshine.auction.user.domain.role.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerProductServiceTest extends IntegrationTest {
    private static final int REGISTERED_CATEGORY_ID = 101;
    private static final long CREATED_ID_OF_PRODUCT = 303;
    private static final String REGISTERED_PRODUCT_NAME = "마데카솔";

    private static long registeredProductId;

    @Autowired CategoryFactory categoryFactory;
    @Autowired SellerProductService sellerProductService;
    @Autowired ProductFactory productFactory;
    @Autowired UserFactory userFactory;

    @BeforeEach
    void setUp() {
        productFactory.deleteAll();
        categoryFactory.deleteAllCategory();

        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);

        UserInfo productCreator = UserInfo.builder()
                .id(CREATED_ID_OF_PRODUCT)
                .roles(Arrays.asList(RoleType.USER))
                .build();
        SecurityContextHolder.getContext()
                .setAuthentication(new UserAuthentication(productCreator));

        SellerProductRegistrationRequest productInfo = SellerProductRegistrationRequest.builder()
                .onSale(true)
                .stockQuantity(30)
                .categoryId(REGISTERED_CATEGORY_ID)
                .name(REGISTERED_PRODUCT_NAME)
                .price(3000)
                .build();
        Product product = sellerProductService.registerProduct(productInfo);
        registeredProductId = product.getId();
    }

    @DisplayName("registerProduct 메소드는")
    @Nested
    class Describe_registerProduct{

        @DisplayName("카테고리 ID가 유효하고 중복되지 않은 상품명으로 등록할 경우")
        @Nested
        class Context_with_registered_category{

            @DisplayName("등록된 상품을 반환한다.")
            @Test
            void it_returns_registerd_product() {
                boolean onSale = true;
                int stockQuantity = 20;
                String productName = "비판텐";
                int price = 3200;

                SellerProductRegistrationRequest productInfo = SellerProductRegistrationRequest.builder()
                        .onSale(onSale)
                        .stockQuantity(stockQuantity)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name(productName)
                        .price(price)
                        .build();

                Product actual = sellerProductService.registerProduct(productInfo);

                assertThat(actual).isInstanceOf(Product.class);
                assertThat(actual.getName()).isEqualTo(productName);
                assertThat(actual.getPrice()).isEqualTo(price);
                assertThat(actual.getStockQuantity()).isEqualTo(stockQuantity);
                assertThat(actual.isOnSale()).isEqualTo(onSale);
            }
        }

        @DisplayName("등록되지 않은 카테고리 ID를 통해 상품을 등록할 경우")
        @Nested
        class Context_with_not_registered_category{
            int notRegisteredCategoryId = 99999999;

            @DisplayName("InvalidArgumentException 예외를 던진다.")
            @Test
            void it_throws_InvalidArgumentException() {
                SellerProductRegistrationRequest productInfo = SellerProductRegistrationRequest.builder()
                        .onSale(true)
                        .stockQuantity(30)
                        .categoryId(notRegisteredCategoryId)
                        .name("productName")
                        .price(3000)
                        .build();

                assertThatThrownBy(() -> sellerProductService.registerProduct(productInfo))
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.PRODUCT_INVALID_CATEGORY_ID.getErrorMsg());
            }
        }
    }

}
