package com.eastshine.auction.product.application;

import com.eastshine.auction.category.CategoryFactory;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest extends IntegrationTest {
    public static final int REGISTERED_CATEGORY_ID = 101;
    public static final String REGISTERED_PRODUCT_NAME = "마데카솔";

    @Autowired ProductService productService;
    @Autowired CategoryFactory categoryFactory;
    @Autowired ProductFactory productFactory;

    @BeforeEach
    void setUp() {
        productFactory.deleteAll();
        categoryFactory.deleteAllCategory();
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);

        ProductRegistrationRequest productInfo = ProductRegistrationRequest.builder()
                .onSale(true)
                .stockQuantity(30)
                .categoryId(REGISTERED_CATEGORY_ID)
                .name(REGISTERED_PRODUCT_NAME)
                .price(3000)
                .build();
        productService.registerProduct(productInfo);
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

                ProductRegistrationRequest productInfo = ProductRegistrationRequest.builder()
                        .onSale(onSale)
                        .stockQuantity(stockQuantity)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name(productName)
                        .price(price)
                        .build();

                Product actual = productService.registerProduct(productInfo);

                assertThat(actual).isInstanceOf(Product.class);
                assertThat(actual.getName()).isEqualTo(productName);
                assertThat(actual.getPrice()).isEqualTo(price);
                assertThat(actual.getStockQuantity()).isEqualTo(stockQuantity);
                assertThat(actual.isOnSale()).isEqualTo(onSale);
            }
        }

        @DisplayName("동일한 카테고리 ID와 상품명의 상품이 이미 등록되어 있는 경우")
        @Nested
        class Context_with_duplicate_product{

            @DisplayName("InvalidArgumentException 예외를 던진다.")
            @Test
            void it_throws_InvalidArgumentException() {
                ProductRegistrationRequest duplicateProductInfo = ProductRegistrationRequest.builder()
                        .onSale(true)
                        .stockQuantity(30)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name(REGISTERED_PRODUCT_NAME)
                        .price(3000)
                        .build();

                assertThatThrownBy(() -> productService.registerProduct(duplicateProductInfo))
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.PRODUCT_DUPLICATE.getErrorMsg());
            }
        }

        @DisplayName("등록되지 않은 카테고리 ID를 통해 상품을 등록할 경우")
        @Nested
        class Context_with_not_registered_category{
            int notRegisteredCategoryId = 99999999;

            @DisplayName("InvalidArgumentException 예외를 던진다.")
            @Test
            void it_throws_InvalidArgumentException() {
                ProductRegistrationRequest productInfo = ProductRegistrationRequest.builder()
                        .onSale(true)
                        .stockQuantity(30)
                        .categoryId(notRegisteredCategoryId)
                        .name("productName")
                        .price(3000)
                        .build();

                assertThatThrownBy(() -> productService.registerProduct(productInfo))
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.PRODUCT_INVALID_CATEGORY_ID.getErrorMsg());
            }
        }
    }
}
