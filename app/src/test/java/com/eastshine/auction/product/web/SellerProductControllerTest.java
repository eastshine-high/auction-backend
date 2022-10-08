package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.application.SellerProductService;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import com.eastshine.auction.user.WithSeller;
import com.eastshine.auction.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SellerProductControllerTest extends WebIntegrationTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;
    private static Long registeredProductId;
    private static Long registeredProductOptionId;

    @Autowired SellerProductService sellerProductService;
    @Autowired CategoryFactory categoryFactory;
    @Autowired ProductRepository productRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryFactory.deleteAll();

        // Test 데이터 생성
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품");
        SellerProductDto.RegistrationRequest.ProductOptionDto optionRegistrationRequest = SellerProductDto.RegistrationRequest.ProductOptionDto.builder()
                .productOptionName("300ml")
                .stockQuantity(50)
                .ordering(1)
                .build();
        SellerProductDto.RegistrationRequest registrationRequest = SellerProductDto.RegistrationRequest.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("비판텐")
                .price(3000)
                .stockQuantity(0)
                .onSale(Boolean.TRUE)
                .productOptions(List.of(optionRegistrationRequest))
                .build();

        Product registerProduct = sellerProductService.registerProduct(registrationRequest);
        registeredProductId = registerProduct.getId();
        registeredProductOptionId = registerProduct.getProductOptions().get(0).getId();
    }

    @Nested
    @DisplayName("createProduct 메소드는")
    class Describe_createProduct{

        @Nested
        @DisplayName("유효한 인증 정보와 상품 정보를 통해 등록을 요청할 경우,")
        class Context_with_valid_productRegistrationRequest{
            SellerProductDto.RegistrationRequest validRegistrationRequest;
            SellerProductDto.RegistrationRequest.ProductOptionDto validOptionRegistrationRequest;

            @Test
            @WithSeller("bestSeller")
            void createProduct() throws Exception {
                validOptionRegistrationRequest = SellerProductDto.RegistrationRequest.ProductOptionDto.builder()
                        .productOptionName("300ml")
                        .stockQuantity(9999)
                        .ordering(1)
                        .build();

                validRegistrationRequest = SellerProductDto.RegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(3000)
                        .stockQuantity(0)
                        .onSale(Boolean.FALSE)
                        .productOptionsTitle("용량")
                        .productOptions(List.of(validOptionRegistrationRequest))
                        .build();

                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson(validRegistrationRequest))
                        )
                        .andExpect(status().isCreated())
                        .andDo(document("seller-products-post-201",
                                PayloadDocumentation.requestFields(
                                        fieldWithPath("categoryId").description("카테고리 식별자"),
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("price").description("상품 가격"),
                                        fieldWithPath("stockQuantity").description("상품 재고"),
                                        fieldWithPath("onSale").description("판매 여부"),
                                        fieldWithPath("productOptionsTitle").description("상품 옵션의 제목"),
                                        fieldWithPath("productOptions[]").description("상품 옵션").optional(),
                                        fieldWithPath("productOptions[].productOptionName").description("상품 옵션의 이름").optional(),
                                        fieldWithPath("productOptions[].stockQuantity").description("상품 옵션의 재고").optional(),
                                        fieldWithPath("productOptions[].ordering").description("옵션 순서").optional()
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보를 통해 요청할 경우,")
        class Context_with_unauthorized_request{
            SellerProductDto.RegistrationRequest validRegistrationRequest;

            @Test
            @DisplayName("unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {
                validRegistrationRequest = SellerProductDto.RegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(1000)
                        .stockQuantity(20)
                        .onSale(Boolean.TRUE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .content(objectMapper.writeValueAsString(validRegistrationRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-products-post-401"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 상품 정보를 통해 등록 요청할 경우,")
        class Context_with_invalid_productRegistrationRequest{
            SellerProductDto.RegistrationRequest invalidRegistrationRequest;
            int invalidPrice = 999;

            @Test
            @WithSeller("bestSeller")
            @DisplayName("badRequest를 응답한다.")
            void it_responses_badRequest() throws Exception {
                invalidRegistrationRequest = SellerProductDto.RegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(invalidPrice)
                        .stockQuantity(20)
                        .onSale(Boolean.TRUE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRegistrationRequest))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("seller-products-post-400"));
            }
        }
    }

    @Nested
    @DisplayName("getProduct 메소드는")
    class Describe_getProduct{

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 조회할 경우")
        class Context_with_valid_request{

            @Test
            @WithSeller("bestSeller")
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {

                mockMvc.perform(
                                get("/seller-api/products/"+registeredProductId)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-products-id-get-200"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보로 상품 조회를 요청할 경우")
        class Context_with_invalid_authentication_request{

            @Test
            @DisplayName("Unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {

                mockMvc.perform(
                                get("/seller-api/products/"+registeredProductId)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-products-id-get-401"));
            }
        }
    }

    @Nested
    @DisplayName("patchProduct 메소드는")
    class Describe_patchProduct{

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 변경을 요청할 경우")
        class Context_with_valid_modification_request{
            SellerProductDto.PatchRequest patchRequest;
            SellerProductDto.PatchRequest.ProductOptionDto optionPatchRequest;

            @Test
            @WithSeller("bestSeller")
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {
                optionPatchRequest = SellerProductDto.PatchRequest.ProductOptionDto.builder()
                        .id(registeredProductOptionId)
                        .stockQuantity(30)
                        .productOptionName("300ml")
                        .ordering(1)
                        .build();
                patchRequest = SellerProductDto.PatchRequest.builder()
                        .name("비판텐")
                        .price(99999)
                        .stockQuantity(0)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .onSale(Boolean.TRUE)
                        .productOptionsTitle("용량")
                        .productOptions(List.of(optionPatchRequest))
                        .build();

                mockMvc.perform(
                                patch("/seller-api/products/"+registeredProductId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(patchRequest))
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-products-id-patch-200",
                                PayloadDocumentation.requestFields(
                                        fieldWithPath("categoryId").description("카테고리 식별자").optional(),
                                        fieldWithPath("name").description("상품명").optional(),
                                        fieldWithPath("price").description("상품 가격").optional(),
                                        fieldWithPath("stockQuantity").description("상품 재고").optional(),
                                        fieldWithPath("onSale").description("판매 여부").optional(),
                                        fieldWithPath("productOptionsTitle").description("상품 옵션의 제목").optional(),
                                        fieldWithPath("productOptions[]").description("상품 옵션").optional(),
                                        fieldWithPath("productOptions[].id").description("상품 옵션 식별자").optional(),
                                        fieldWithPath("productOptions[].productOptionName").description("상품 옵션의 이름").optional(),
                                        fieldWithPath("productOptions[].stockQuantity").description("상품 옵션의 재고").optional(),
                                        fieldWithPath("productOptions[].ordering").description("옵션 순서").optional()
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보로 상품 변경을 요청할 경우")
        class Context_with_invalid_authentication_request{
            SellerProductDto.PatchRequest patchRequest;
            SellerProductDto.PatchRequest.ProductOptionDto optionPatchRequest;

            @Test
            @DisplayName("Unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {
                optionPatchRequest = SellerProductDto.PatchRequest.ProductOptionDto.builder()
                        .id(registeredProductOptionId)
                        .stockQuantity(0)
                        .build();

                patchRequest = SellerProductDto.PatchRequest.builder()
                        .price(99999)
                        .name("modify name")
                        .stockQuantity(30)
                        .onSale(Boolean.TRUE)
                        .productOptions(List.of(optionPatchRequest))
                        .build();

                mockMvc.perform(
                                patch("/seller-api/products/"+registeredProductId)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(patchRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-products-id-patch-401"));
            }
        }
    }

    @Nested
    @DisplayName("deleteProduct 메소드는")
    class Describe_deleteProduct{

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 삭제할 경우")
        class Context_with_valid_request{

            @Test
            @WithSeller("bestSeller")
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {

                mockMvc.perform(
                                delete("/seller-api/products/"+registeredProductId)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-products-id-delete-200"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보로 상품 삭제를 요청할 경우")
        class Context_with_invalid_authentication_request{

            @Test
            @DisplayName("Unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {

                mockMvc.perform(
                                delete("/seller-api/products/"+registeredProductId)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-products-id-delete-401"));
            }
        }
    }
}
