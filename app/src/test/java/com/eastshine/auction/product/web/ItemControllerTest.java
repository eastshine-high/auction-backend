package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import com.eastshine.auction.user.UserFactory;
import com.eastshine.auction.user.domain.seller.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest extends WebIntegrationTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;

    private static Long registeredItemId;

    @Autowired CategoryFactory categoryFactory;
    @Autowired ItemRepository itemRepository;
    @Autowired ItemOptionRepository itemOptionRepository;
    @Autowired UserFactory userFactory;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        itemRepository.deleteAll();
        categoryFactory.deleteAll();
        userFactory.deleteAll();

        Seller seller = userFactory.createSeller("판매왕");
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);

        Item item = Item.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("비판텐")
                .stockQuantity(50000)
                .price(50000)
                .onSale(true)
                .build();
        ReflectionTestUtils.setField(item, "createdBy", seller.getId());
        itemRepository.save(item);
        registeredItemId = item.getId();

        ItemOption option1 = ItemOption.builder()
                .item(item)
                .itemOptionName("50ml")
                .stockQuantity(30)
                .ordering(1)
                .build();

        ItemOption option2 = ItemOption.builder()
                .item(item)
                .itemOptionName("70ml")
                .stockQuantity(30)
                .ordering(2)
                .build();

        itemOptionRepository.save(option1);
        itemOptionRepository.save(option2);
    }

    @Nested
    @DisplayName("getItems 메소드는")
    class Describe_getItems{

        @Nested
        @DisplayName("필수 파라미터와 함께 요청했을 경우")
        class Context_with_required_parameter{
            String itemName = "비판텐";
            String requiredParameter = "name=" + itemName;

            @Test
            @DisplayName("파라미터 조건에 맞게 검색된 상품들을 반환한다.")
            void it_returns_items() throws Exception {
                mockMvc.perform(
                                get("/api/items?" + requiredParameter)
                                        .accept(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(itemName)))
                        .andDo(document("guest-items-get-200",
                                requestParameters(
                                        parameterWithName("name").description("상품명"),
                                        parameterWithName("categoryId").description("카테고리 식별자").optional(),
                                        parameterWithName("page").description("조회할 상품 목록의 페이지").optional(),
                                        parameterWithName("size").description("조회할 상품 목록의 수").optional()
                                )
                        ));
            }

            @Nested
            @DisplayName("필수 파라미터 없이 요청을 했을 경우")
            class Context_without_required_parameter{
                String notRequiredParameter = "page=0&size=5";

                @Test
                @DisplayName("BadRequest를 응답한다.")
                void it_responses_badRequest() throws Exception {
                    mockMvc.perform(
                                    get("/api/items?" + notRequiredParameter)
                                            .accept(MediaType.APPLICATION_JSON)
                            )
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    @DisplayName("getItem 메소드는")
    class Describe_getItem {

        @Test
        @DisplayName("식별자에 해당하는 상품을 반환한다.")
        void it_returns_items() throws Exception {
            mockMvc.perform(
                            get("/api/items/" + registeredItemId)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(document("guest-items-id-get-200"));
        }
    }
}
