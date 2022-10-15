package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.domain.product.option.ProductOption;
import com.eastshine.auction.product.domain.product.option.ProductOptionRepository;
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

class ProductControllerTest extends WebIntegrationTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;

    private static Long registeredProductId;

    @Autowired CategoryFactory categoryFactory;
    @Autowired ProductRepository productRepository;
    @Autowired ProductOptionRepository productOptionRepository;
    @Autowired UserFactory userFactory;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productRepository.deleteAll();
        categoryFactory.deleteAll();
        userFactory.deleteAll();

        Seller seller = userFactory.createSeller("판매왕");
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);

        Product product = Product.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("비판텐")
                .stockQuantity(50000)
                .price(50000)
                .onSale(true)
                .build();
        ReflectionTestUtils.setField(product, "createdBy", seller.getId());
        productRepository.save(product);
        registeredProductId = product.getId();

        ProductOption option1 = ProductOption.builder()
                .product(product)
                .productOptionName("50ml")
                .stockQuantity(30)
                .ordering(1)
                .build();

        ProductOption option2 = ProductOption.builder()
                .product(product)
                .productOptionName("70ml")
                .stockQuantity(30)
                .ordering(2)
                .build();

        productOptionRepository.save(option1);
        productOptionRepository.save(option2);
    }

    @Nested
    @DisplayName("getProducts 메소드는")
    class Describe_getProducts{

        @Nested
        @DisplayName("필수 파라미터와 함께 요청했을 경우")
        class Context_with_required_parameter{
            String productName = "비판텐";
            String requiredParameter = "name=" + productName;

            @Test
            @DisplayName("파라미터 조건에 맞게 검색된 상품들을 반환한다.")
            void it_returns_products() throws Exception {
                mockMvc.perform(
                                get("/api/products?" + requiredParameter)
                                        .accept(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(productName)))
                        .andDo(document("guest-products-get-200",
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
                                    get("/api/products?" + notRequiredParameter)
                                            .accept(MediaType.APPLICATION_JSON)
                            )
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    @DisplayName("getProduct 메소드는")
    class Describe_getProduct{

        @Test
        @DisplayName("식별자에 해당하는 상품을 반환한다.")
        void it_returns_products() throws Exception {
            mockMvc.perform(
                            get("/api/products/" + registeredProductId)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(document("guest-products-id-get-200"));
        }
    }
}
