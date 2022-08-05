package com.eastshine.auction.product.application;

import com.eastshine.auction.category.CategoryFactory;
import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.json.Json;
import javax.json.JsonMergePatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerProductServiceTest extends IntegrationTest {
    private static final int REGISTERED_CATEGORY_ID = 101;
    private static final String REGISTERED_PRODUCT_NAME = "마데카솔";

    private Product registeredProduct;
    private static long registeredProductId;

    @Autowired CategoryFactory categoryFactory;
    @Autowired ProductFactory productFactory;
    @Autowired SellerProductService sellerProductService;
    @Autowired JsonMergePatchMapper<Product> jsonMergePatchMapper;

    @BeforeEach
    void setUp() {
        productFactory.deleteAll();
        categoryFactory.deleteAllCategory();

        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품");
        SellerProductRegistrationRequest productInfo = SellerProductRegistrationRequest.builder()
                .onSale(true)
                .stockQuantity(30)
                .categoryId(REGISTERED_CATEGORY_ID)
                .name(REGISTERED_PRODUCT_NAME)
                .price(3000)
                .build();
        registeredProduct = sellerProductService.registerProduct(productInfo);
        registeredProductId = registeredProduct.getId();
        registeredProduct = sellerProductService.fetchProduct(registeredProductId);
        registeredProduct.getCategory().setChildrenNull();
    }

    @DisplayName("registerProduct 메소드는")
    @Nested
    class Describe_registerProduct{

        @DisplayName("카테고리 ID가 유효하고 중복되지 않은 상품명으로 등록할 경우")
        @Nested
        class Context_with_registered_category{
            SellerProductRegistrationRequest productInfo;
            boolean onSale = true;
            int stockQuantity = 20;
            String productName = "비판텐";
            int price = 3200;

            @DisplayName("등록된 상품을 반환한다.")
            @Test
            void it_returns_registerd_product() {
                productInfo = SellerProductRegistrationRequest.builder()
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
            private final int notRegisteredCategoryId = 99999999;
            SellerProductRegistrationRequest productInfo;

            @DisplayName("InvalidArgumentException 예외를 던진다.")
            @Test
            void it_throws_InvalidArgumentException() {
                productInfo = SellerProductRegistrationRequest.builder()
                        .onSale(true)
                        .stockQuantity(30)
                        .categoryId(notRegisteredCategoryId)
                        .name("productName")
                        .price(3000)
                        .build();

                assertThatThrownBy(
                        () -> sellerProductService.registerProduct(productInfo)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.PRODUCT_INVALID_CATEGORY_ID.getErrorMsg());
            }
        }
    }

    @Nested
    @DisplayName("updateProduct 메소드는")
    class Describe_updateProduct {

        @Nested
        @DisplayName("등록되지 않은 카테고리를 수정할 경우")
        class Context_with_unregistered_category_id {
            private final long unregisteredCategoryId = -1;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("category", Json.createObjectBuilder()
                            .add("id", unregisteredCategoryId))
                    .build());

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_throws_EntityNotFoundException() {
                Product product = jsonMergePatchMapper.apply(patchDocument, registeredProduct);

                assertThatThrownBy(() ->
                        sellerProductService.updatePatchedProduct(product)
                )
                        .isInstanceOf(InvalidArgumentException.class)
                        .hasMessage(ErrorCode.PRODUCT_INVALID_CATEGORY_ID.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효하지 못한 요청 정보로 상품을 수정할 경우")
        class Context_with_registered_product_id {
            int invalidPrice = 456;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("price", invalidPrice)
                    .build());

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_returns_modified_product() {
                Product product = jsonMergePatchMapper.apply(patchDocument, registeredProduct);

                assertThatThrownBy(() ->
                        sellerProductService.updatePatchedProduct(product)
                )
                        .isInstanceOf(InvalidArgumentException.class);
            }
        }

        @Nested
        @DisplayName("유효한 요청 정보로 상품을 수정할 경우")
        class Context_with_valid_modification_info {
            String name = "수정완료";
            int price = 123456;
            boolean onSale = true;
            int stockQuantity = 99;

            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("onSale", onSale)
                    .add("stockQuantity", stockQuantity)
                    .add("price", price)
                    .add("name", name)
                    .add("category", Json.createObjectBuilder()
                            .add("id", REGISTERED_CATEGORY_ID))
                    .build());

            @Test
            @DisplayName("수정된 상품을 반환한다.")
            void it_returns_modified_product() {
                Product product = jsonMergePatchMapper.apply(patchDocument, registeredProduct);

                Product actual = sellerProductService.updatePatchedProduct(product);

                assertThat(actual.getPrice()).isEqualTo(price);
                assertThat(actual.getName()).isEqualTo(name);
                assertThat(actual.isOnSale()).isEqualTo(onSale);
                assertThat(actual.getStockQuantity()).isEqualTo(stockQuantity);
            }
        }
    }

    @Nested
    @DisplayName("fetchProduct 메소드는")
    class Describe_fetchProduct {

        @Nested
        @DisplayName("등록되지 않은 상품을 조회할 경우")
        class Context_with_unregistered_product_id {
            private final long unregisteredProductId = -1;

            @Test
            @DisplayName("EntityNotFoundException 예외를 던진다.")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(() ->
                        sellerProductService.fetchProduct(unregisteredProductId)
                )
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("등록된 상품ID로 조회할 경우")
        class Context_with_registered_product_id {

            @Test
            @DisplayName("해당 ID의 상품을 조회한다.")
            void it_returns_product() {
                Product product = sellerProductService.fetchProduct(registeredProductId);

                assertThat(product.getId()).isEqualTo(registeredProductId);
            }
        }
    }
}
