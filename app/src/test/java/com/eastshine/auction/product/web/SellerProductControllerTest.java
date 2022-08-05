package com.eastshine.auction.product.web;

import com.eastshine.auction.category.CategoryFactory;
import com.eastshine.auction.common.test.RestDocsTest;
import com.eastshine.auction.product.ProductFactory;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductPatchRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SellerProductControllerTest extends RestDocsTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;
    private static long registeredProductId;

    @Autowired
    CategoryFactory categoryFactory;
    @Autowired
    ProductFactory productFactory;
    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        productFactory.deleteAll();
        categoryFactory.deleteAllCategory();

        // Test 데이터 생성
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품");
        SellerProductRegistrationRequest registrationRequest = SellerProductRegistrationRequest.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("비판텐")
                .price(3000)
                .stockQuantity(20)
                .onSale(Boolean.TRUE)
                .build();
        mockMvc.perform(
                post("/seller-api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", SELLER_AUTHENTICATION)
                        .content(createJson(registrationRequest))
        );
        Product registeredProduct = productRepository.findByName("비판텐").orElse(null);
        registeredProductId = registeredProduct.getId();
    }

    @Nested
    @DisplayName("createProduct 메소드는")
    class Describe_createProduct{

        @Nested
        @DisplayName("유효한 인증 정보와 상품 정보를 통해 등록을 요청할 경우,")
        class Context_with_valid_productRegistrationRequest{
            SellerProductRegistrationRequest validRegistrationRequest;

            @Test
            void createProduct() throws Exception {
                validRegistrationRequest = SellerProductRegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(3000)
                        .stockQuantity(0)
                        .onSale(Boolean.FALSE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", SELLER_AUTHENTICATION)
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
                        ));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보를 통해 요청할 경우,")
        class Context_with_unauthorized_request{
            SellerProductRegistrationRequest validRegistrationRequest;

            @Test
            @DisplayName("unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {
                validRegistrationRequest = SellerProductRegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(1000)
                        .stockQuantity(20)
                        .onSale(Boolean.TRUE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", INVALID_AUTHENTICATION)
                                        .content(objectMapper.writeValueAsString(validRegistrationRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("products-post-401"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 상품 정보를 통해 등록 요청할 경우,")
        class Context_with_invalid_productRegistrationRequest{
            SellerProductRegistrationRequest invalidRegistrationRequest;
            int invalidPrice = 999;

            @Test
            @DisplayName("badRequest를 응답한다.")
            void it_responses_badRequest() throws Exception {
                invalidRegistrationRequest = SellerProductRegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(invalidPrice)
                        .stockQuantity(20)
                        .onSale(Boolean.TRUE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", VALID_AUTHENTICATION)
                                        .content(objectMapper.writeValueAsString(invalidRegistrationRequest))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("products-post-400"));
            }
        }
    }

    @Nested
    @DisplayName("patchProduct 메소드는")
    class Describe_patchProduct{

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 변경을 요청할 경우")
        class Context_with_valid_modification_request{
            SellerProductPatchRequest patchRequest;

            @Test
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {
                patchRequest = SellerProductPatchRequest.builder()
                        .price(99999)
                        .name("modify name")
                        .stockQuantity(0)
                        .onSale(Boolean.FALSE)
                        .build();

                mockMvc.perform(
                                patch("/seller-api/products/"+registeredProductId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", SELLER_AUTHENTICATION)
                                        .content(objectMapper.writeValueAsString(patchRequest))
                        )
                        .andExpect(status().isOk())
                        .andDo(document("products-patch-200"));
            }
        }
    }
}
