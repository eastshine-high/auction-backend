package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.domain.product.option.ProductOption;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import javax.json.Json;
import javax.json.JsonMergePatch;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerProductServiceTest extends IntegrationTest {
    private static final int REGISTERED_CATEGORY_ID = 101;
    private static final String REGISTERED_PRODUCT_NAME = "마데카솔";
    private static Long PRODUCT_CREATOR_ID = 4L;

    private static Long registeredProductId;
    private static Product registeredProduct;


    @Autowired CategoryFactory categoryFactory;
    @Autowired ProductFactory productFactory;
    @Autowired ProductRepository productRepository;
    @Autowired SellerProductService sellerProductService;
    @Autowired JsonMergePatchMapper<Product> jsonMergePatchMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Product product = Product.builder()
                .onSale(true)
                .productOptionsTitle("용량")
                .categoryId(300)
                .name(REGISTERED_PRODUCT_NAME)
                .price(3000)
                .productOptions(new ArrayList<>())
                .build();
        ProductOption productOption = ProductOption.builder()
                .productOptionName("500ML")
                .stockQuantity(500)
                .ordering(1)
                .build();
        product.addProductOption(productOption);
        ReflectionTestUtils.setField(product, "createdBy", PRODUCT_CREATOR_ID);

        registeredProduct = productRepository.save(product);
        registeredProductId = product.getId();
    }

    @DisplayName("registerProduct 메소드는")
    @Nested
    class Describe_registerProduct{

        @DisplayName("유효한 상품 정보로 등록할 경우")
        @Nested
        class Context_with_registered_category{
            SellerProductDto.RegistrationRequest productInfo;
            boolean onSale = true;
            int stockQuantity = 20;
            String productName = "비판텐";
            int price = 3200;

            SellerProductDto.RegistrationRequest.ProductOption optionInfo;
            String productOptionName = "300ml";
            int ordering = 1;
            int optionStockQuantity = 9999;

            @DisplayName("등록된 상품을 반환한다.")
            @Test
            void it_returns_registerd_product() {
                optionInfo = SellerProductDto.RegistrationRequest.ProductOption.builder()
                        .productOptionName(productOptionName)
                        .ordering(ordering)
                        .stockQuantity(optionStockQuantity)
                        .build();

                productInfo = SellerProductDto.RegistrationRequest.builder()
                        .onSale(onSale)
                        .stockQuantity(stockQuantity)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name(productName)
                        .price(price)
                        .productOptions(Arrays.asList(optionInfo))
                        .build();

                Product product = sellerProductService.registerProduct(productInfo);

                assertThat(product).isInstanceOf(Product.class);
                assertThat(product.getName()).isEqualTo(productName);
                assertThat(product.getPrice()).isEqualTo(price);
                assertThat(product.getStockQuantity()).isEqualTo(stockQuantity);
                assertThat(product.isOnSale()).isEqualTo(onSale);

                com.eastshine.auction.product.domain.product.option.ProductOption productOption = product.getProductOptions().get(0);
                assertThat(productOption.getProductOptionName()).isEqualTo(productOptionName);
                assertThat(productOption.getOrdering()).isEqualTo(ordering);
                assertThat(productOption.getStockQuantity()).isEqualTo(optionStockQuantity);
            }
        }
    }

    @Nested
    @DisplayName("patchProduct 메소드는")
    class Describe_patchProduct {

        @Nested
        @DisplayName("등록되지 않은 상품을 수정할 경우")
        class Context_with_unregistered_product_id {
            private final long unregisteredProductId = -1;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("price", 99999)
                    .build());

            @Test
            @DisplayName("EntityNotFoundException 예외를 던진다.")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(() ->
                        sellerProductService.patchProduct(unregisteredProductId, patchDocument, PRODUCT_CREATOR_ID)
                )
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효하지 못한 요청 정보로 상품을 수정할 경우")
        class Context_with_invalid_product_info {
            int invalidPrice = 456;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("price", invalidPrice)
                    .build());

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_throws_InvalidArgumentException() {
                assertThatThrownBy(() ->
                        sellerProductService.patchProduct(registeredProductId, patchDocument, PRODUCT_CREATOR_ID)
                )
                        .isInstanceOf(InvalidArgumentException.class);
            }
        }

        @Nested
        @DisplayName("상품을 등록한 사용자가 아닌 사용자가 수정할 경우")
        class Context_with_inaccessible_user {
            private final Long inaccessibleUserId = -1L;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("price", 99999)
                    .build());

            @Test
            @DisplayName("UnauthorizedException 예외를 던진다.")
            void it_throws_UnauthorizedException() {
                assertThatThrownBy(() ->
                        sellerProductService.patchProduct(registeredProductId, patchDocument, inaccessibleUserId)
                )
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage(ErrorCode.PRODUCT_UNACCESSABLE.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효한 요청 정보로 상품을 수정할 경우")
        class Context_with_valid_modification_info {
            String name = "수정완료";
            int price = 123456;
            boolean onSale = true;
            int stockQuantity = 99;
            Integer categoryId = 500;

            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("onSale", onSale)
                    .add("stockQuantity", stockQuantity)
                    .add("price", price)
                    .add("name", name)
                    .add("categoryId", categoryId)
                    .build());

            @Test
            @DisplayName("수정된 상품을 반환한다.")
            void it_returns_modified_product() {
                Product actual = sellerProductService.patchProduct(registeredProductId, patchDocument, PRODUCT_CREATOR_ID);

                assertThat(actual.getPrice()).isEqualTo(price);
                assertThat(actual.getName()).isEqualTo(name);
                assertThat(actual.isOnSale()).isEqualTo(onSale);
                assertThat(actual.getStockQuantity()).isEqualTo(stockQuantity);
                assertThat(actual.getCategoryId()).isEqualTo(categoryId);
            }
        }
    }
}
