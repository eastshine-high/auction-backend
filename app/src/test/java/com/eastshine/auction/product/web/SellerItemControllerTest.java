package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.application.SellerItemService;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.web.dto.SellerItemDto;
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

class SellerItemControllerTest extends WebIntegrationTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;
    private static Long registeredItemId;
    private static Long registeredItemOptionId;

    @Autowired SellerItemService sellerItemService;
    @Autowired CategoryFactory categoryFactory;
    @Autowired ItemRepository itemRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        categoryFactory.deleteAll();

        // Test 데이터 생성
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품");
        SellerItemDto.RegistrationRequest.ItemOption optionRegistrationRequest = SellerItemDto.RegistrationRequest.ItemOption.builder()
                .itemOptionName("300ml")
                .stockQuantity(50)
                .additionalPrice(500)
                .ordering(1)
                .build();
        SellerItemDto.RegistrationRequest registrationRequest = SellerItemDto.RegistrationRequest.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("비판텐")
                .price(3000)
                .stockQuantity(0)
                .onSale(Boolean.TRUE)
                .itemOptions(List.of(optionRegistrationRequest))
                .build();

        Item registerItem = sellerItemService.registerItem(registrationRequest);
        registeredItemId = registerItem.getId();
        registeredItemOptionId = registerItem.getItemOptions().get(0).getId();
    }

    @Nested
    @DisplayName("createItem 메소드는")
    class Describe_createItem {

        @Nested
        @DisplayName("유효한 인증 정보와 상품 정보를 통해 등록을 요청할 경우,")
        class Context_with_valid_itemRegistrationRequest{
            SellerItemDto.RegistrationRequest validRegistrationRequest;
            SellerItemDto.RegistrationRequest.ItemOption validOptionRegistrationRequest;

            @Test
            @WithSeller("bestSeller")
            void createItem() throws Exception {
                validOptionRegistrationRequest = SellerItemDto.RegistrationRequest.ItemOption.builder()
                        .itemOptionName("300ml")
                        .additionalPrice(500)
                        .stockQuantity(9999)
                        .ordering(1)
                        .build();

                validRegistrationRequest = SellerItemDto.RegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(3000)
                        .stockQuantity(0)
                        .onSale(Boolean.FALSE)
                        .itemOptionsTitle("용량")
                        .itemOptions(List.of(validOptionRegistrationRequest))
                        .build();

                mockMvc.perform(
                                post("/seller-api/items")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson(validRegistrationRequest))
                        )
                        .andExpect(status().isCreated())
                        .andDo(document("seller-items-post-201",
                                PayloadDocumentation.requestFields(
                                        fieldWithPath("categoryId").description("카테고리 식별자"),
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("price").description("상품 가격"),
                                        fieldWithPath("stockQuantity").description("상품 재고"),
                                        fieldWithPath("onSale").description("판매 여부"),
                                        fieldWithPath("itemOptionsTitle").description("상품 옵션의 제목"),
                                        fieldWithPath("itemOptions[]").description("상품 옵션").optional(),
                                        fieldWithPath("itemOptions[].itemOptionName").description("상품 옵션의 이름").optional(),
                                        fieldWithPath("itemOptions[].additionalPrice").description("상품 옵션의 추가 가격").optional(),
                                        fieldWithPath("itemOptions[].stockQuantity").description("상품 옵션의 재고").optional(),
                                        fieldWithPath("itemOptions[].ordering").description("옵션 순서").optional()
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보를 통해 요청할 경우,")
        class Context_with_unauthorized_request{
            SellerItemDto.RegistrationRequest validRegistrationRequest;

            @Test
            @DisplayName("unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {
                validRegistrationRequest = SellerItemDto.RegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(1000)
                        .stockQuantity(20)
                        .onSale(Boolean.TRUE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/items")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .content(objectMapper.writeValueAsString(validRegistrationRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-items-post-401"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 상품 정보를 통해 등록 요청할 경우,")
        class Context_with_invalid_itemRegistrationRequest{
            SellerItemDto.RegistrationRequest invalidRegistrationRequest;
            int invalidPrice = 999;

            @Test
            @WithSeller("bestSeller")
            @DisplayName("badRequest를 응답한다.")
            void it_responses_badRequest() throws Exception {
                invalidRegistrationRequest = SellerItemDto.RegistrationRequest.builder()
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name("후시딘")
                        .price(invalidPrice)
                        .stockQuantity(20)
                        .onSale(Boolean.TRUE)
                        .build();

                mockMvc.perform(
                                post("/seller-api/items")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRegistrationRequest))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("seller-items-post-400"));
            }
        }
    }

    @Nested
    @DisplayName("getItem 메소드는")
    class Describe_getItem {

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 조회할 경우")
        class Context_with_valid_request{

            @Test
            @WithSeller("bestSeller")
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {

                mockMvc.perform(
                                get("/seller-api/items/"+ registeredItemId)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-items-id-get-200"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보로 상품 조회를 요청할 경우")
        class Context_with_invalid_authentication_request{

            @Test
            @DisplayName("Unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {

                mockMvc.perform(
                                get("/seller-api/items/"+ registeredItemId)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-items-id-get-401"));
            }
        }
    }

    @Nested
    @DisplayName("patchItem 메소드는")
    class Describe_patchItem {

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 변경을 요청할 경우")
        class Context_with_valid_modification_request{
            SellerItemDto.PatchRequest patchRequest;
            SellerItemDto.PatchRequest.ItemOption optionPatchRequest;

            @Test
            @WithSeller("bestSeller")
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {
                optionPatchRequest = SellerItemDto.PatchRequest.ItemOption.builder()
                        .id(registeredItemOptionId)
                        .stockQuantity(30)
                        .itemOptionName("300ml")
                        .additionalPrice(500)
                        .ordering(1)
                        .build();
                patchRequest = SellerItemDto.PatchRequest.builder()
                        .name("비판텐")
                        .price(99999)
                        .stockQuantity(0)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .onSale(Boolean.TRUE)
                        .itemOptionsTitle("용량")
                        .itemOptions(List.of(optionPatchRequest))
                        .build();

                mockMvc.perform(
                                patch("/seller-api/items/"+ registeredItemId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(patchRequest))
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-items-id-patch-200",
                                PayloadDocumentation.requestFields(
                                        fieldWithPath("categoryId").description("카테고리 식별자").optional(),
                                        fieldWithPath("name").description("상품명").optional(),
                                        fieldWithPath("price").description("상품 가격").optional(),
                                        fieldWithPath("stockQuantity").description("상품 재고").optional(),
                                        fieldWithPath("onSale").description("판매 여부").optional(),
                                        fieldWithPath("itemOptionsTitle").description("상품 옵션의 제목").optional(),
                                        fieldWithPath("itemOptions[]").description("상품 옵션").optional(),
                                        fieldWithPath("itemOptions[].id").description("상품 옵션 식별자").optional(),
                                        fieldWithPath("itemOptions[].itemOptionName").description("상품 옵션의 이름").optional(),
                                        fieldWithPath("itemOptions[].additionalPrice").description("상품 옵션의 추가 가격").optional(),
                                        fieldWithPath("itemOptions[].stockQuantity").description("상품 옵션의 재고").optional(),
                                        fieldWithPath("itemOptions[].ordering").description("옵션 순서").optional()
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보로 상품 변경을 요청할 경우")
        class Context_with_invalid_authentication_request{
            SellerItemDto.PatchRequest patchRequest;
            SellerItemDto.PatchRequest.ItemOption optionPatchRequest;

            @Test
            @DisplayName("Unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {
                optionPatchRequest = SellerItemDto.PatchRequest.ItemOption.builder()
                        .id(registeredItemOptionId)
                        .stockQuantity(0)
                        .build();

                patchRequest = SellerItemDto.PatchRequest.builder()
                        .price(99999)
                        .name("modify name")
                        .stockQuantity(30)
                        .onSale(Boolean.TRUE)
                        .itemOptions(List.of(optionPatchRequest))
                        .build();

                mockMvc.perform(
                                patch("/seller-api/items/"+ registeredItemId)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(patchRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-items-id-patch-401"));
            }
        }
    }

    @Nested
    @DisplayName("deleteItem 메소드는")
    class Describe_deleteItem {

        @Nested
        @DisplayName("유효한 인증 정보와 상품 ID로 삭제할 경우")
        class Context_with_valid_request{

            @Test
            @WithSeller("bestSeller")
            @DisplayName("ok를 응답한다.")
            void it_responses_ok() throws Exception {

                mockMvc.perform(
                                delete("/seller-api/items/"+ registeredItemId)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-items-id-delete-200"));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 인증 정보로 상품 삭제를 요청할 경우")
        class Context_with_invalid_authentication_request{

            @Test
            @DisplayName("Unauthorized를 응답한다.")
            void it_responses_unauthorized() throws Exception {

                mockMvc.perform(
                                delete("/seller-api/items/"+ registeredItemId)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-items-id-delete-401"));
            }
        }
    }
}
