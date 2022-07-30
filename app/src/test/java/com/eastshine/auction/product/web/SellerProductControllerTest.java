package com.eastshine.auction.product.web;

import com.eastshine.auction.category.CategoryFactory;
import com.eastshine.auction.common.test.RestDocsTest;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SellerProductControllerTest extends RestDocsTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;

    private static long registeredProductId;

    @Autowired
    CategoryFactory categoryFactory;
    @Autowired
    ProductFactory productFactory;

    @BeforeEach
    void setUp() {
        productFactory.deleteAll();
        categoryFactory.deleteAllCategory();

        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);
        Product createdProduct = productFactory.createProduct(REGISTERED_CATEGORY_ID, "비판텐");
        registeredProductId = createdProduct.getId();
    }

    @Nested
    @DisplayName("createProduct 메소드는")
    class Describe_createProduct{

        @Nested
        @DisplayName("유효한 상품 등록 요청 정보를 통해 요청할 경우,")
        class Context_with_valid_productRegistrationRequest{
            SellerProductRegistrationRequest validRegistrationRequest;

            @BeforeEach
            void setUp() {
                validRegistrationRequest = SellerProductRegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(3000)
                        .stockQuantity(0)
                        .onSale(Boolean.FALSE)
                        .build();
            }

            @Test
            void createProduct() throws Exception {
                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", VALID_AUTHENTICATION)
                                        .content(createJson(validRegistrationRequest))
                        )
                        .andExpect(status().isCreated())
                        .andDo(document("products-post-201",
                                PayloadDocumentation.requestFields(
                                        fieldWithPath("categoryId").description("카테고리 식별자"),
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("price").description("상품 가격"),
                                        fieldWithPath("stockQuantity").description("상품 재고"),
                                        fieldWithPath("onSale").description("판매 여부")
                                )
                        ));;
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보를 통해 요청할 경우,")
        class Context_with_unauthorized_request{

            @Test
            @DisplayName("unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {
                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", INVALID_AUTHENTICATION)
                                        .content(objectMapper.writeValueAsString(
                                                SellerProductRegistrationRequest.builder()
                                                        .categoryId(REGISTERED_CATEGORY_ID)
                                                        .name("후시딘")
                                                        .price(1000)
                                                        .stockQuantity(0)
                                                        .onSale(Boolean.FALSE)
                                                        .build()
                                        ))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("products-post-401"));;
            }
        }

        @Nested
        @DisplayName("유효하지 못한 상품 등록 요청 정보를 통해 요청할 경우,")
        class Context_with_invalid_productRegistrationRequest{
            int invalidPrice = 999;

            @Test
            @DisplayName("badRequest를 응답한다.")
            void it_responses_badRequest() throws Exception {
                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", VALID_AUTHENTICATION)
                                        .content(objectMapper.writeValueAsString(
                                                SellerProductRegistrationRequest.builder()
                                                        .categoryId(REGISTERED_CATEGORY_ID)
                                                        .name("후시딘")
                                                        .price(invalidPrice)
                                                        .stockQuantity(0)
                                                        .onSale(Boolean.FALSE)
                                                        .build()
                                        ))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("products-post-400"));;
            }
        }
    }
}
