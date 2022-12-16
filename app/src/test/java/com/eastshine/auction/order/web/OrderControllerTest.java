package com.eastshine.auction.order.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.order.application.OrderService;
import com.eastshine.auction.order.application.PlaceOrderService;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.web.dto.OrderDto;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.repository.item.ItemRepository;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import com.eastshine.auction.user.UserFactory;
import com.eastshine.auction.user.WithUser;
import com.eastshine.auction.user.domain.seller.Seller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends WebIntegrationTest {
    private static final Integer REGISTERED_CATEGORY_ID = 101;

    private static Long registeredItemId;
    private static Long registeredItemId2;
    private static Long registeredItemOptionId1;
    private static Long registeredItemOptionId2;
    private static Long registeredOrderId;

    @Autowired UserFactory userFactory;
    @Autowired CategoryFactory categoryFactory;
    @Autowired ItemRepository itemRepository;
    @Autowired ItemOptionRepository itemOptionRepository;
    @Autowired PlaceOrderService placeOrderService;
    @Autowired OrderService orderService;
    @Autowired JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        setTestData();
    }

    private void setTestData() {
        Seller seller = userFactory.createSeller("판매왕");
        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "의약품", 1);

        Item item = Item.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("비판텐")
                .itemOptionsTitle("용량")
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
                .additionalPrice(1000)
                .stockQuantity(30)
                .ordering(2)
                .build();

        itemOptionRepository.save(option1);
        registeredItemOptionId1 = option1.getId();
        itemOptionRepository.save(option2);
        registeredItemOptionId2 = option2.getId();

        Item item2 = Item.builder()
                .categoryId(REGISTERED_CATEGORY_ID)
                .name("후시딘")
                .itemOptionsTitle("용량")
                .stockQuantity(30)
                .price(51000)
                .onSale(true)
                .build();
        ReflectionTestUtils.setField(item2, "createdBy", seller.getId());
        itemRepository.save(item2);
        registeredItemId2 = item2.getId();

        if (!Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            OrderDto.PlaceOrderRequest orderRequest = OrderDto.PlaceOrderRequest.builder()
                    .orderItems(List.of(
                            new OrderDto.PlaceOrderItem(registeredItemId, 0,
                                    List.of(new OrderDto.PlaceOrderItemOption(registeredItemOptionId1, 2),
                                            new OrderDto.PlaceOrderItemOption(registeredItemOptionId2, 3))),
                            new OrderDto.PlaceOrderItem(registeredItemId2, 2, null)
                    ))
                    .receiverAddress1("경기도 안양시")
                    .receiverAddress2("만안구")
                    .receiverPhone("01026799999")
                    .receiverName("최동호")
                    .etcMessage("부재시 경비실에 맡겨주세요.")
                    .build();

            orderRequest.setUserInfo(userInfo);
            Order order = orderService.registerOrder(orderRequest);
            registeredOrderId = order.getId();
        }
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("delete from order_item_option");
        jdbcTemplate.execute("delete from order_item");
        jdbcTemplate.execute("delete from orders");
        itemRepository.deleteAll();
        categoryFactory.deleteAll();
        userFactory.deleteAll();
    }

    @Nested
    @DisplayName("placeOrder 메소드는")
    class Describe_placeOrder{

        @Test
        @WithUser("buyer1")
        @DisplayName("인증 정보와 유효한 요청 정보로 주문을 할 경우, 정상 응답한다.")
        void placeOrderWithValidRequest() throws Exception {
            String receiverAddress1 = "경기도 안양시";
            String receiverAddress2 = "만안구";
            String receiverZipcode = "430012";
            String receiverPhone = "01026799999";
            String receiverName = "최동호";
            String etcMessage = "부재시 경비실에 맡겨주세요.";

            OrderDto.PlaceOrderRequest orderRequest = OrderDto.PlaceOrderRequest.builder()
                    .receiverAddress1(receiverAddress1)
                    .receiverAddress2(receiverAddress2)
                    .receiverZipcode(receiverZipcode)
                    .receiverPhone(receiverPhone)
                    .receiverName(receiverName)
                    .etcMessage(etcMessage)
                    .orderItems(List.of(
                            new OrderDto.PlaceOrderItem(registeredItemId, 0,
                                    List.of(new OrderDto.PlaceOrderItemOption(registeredItemOptionId1, 2),
                                            new OrderDto.PlaceOrderItemOption(registeredItemOptionId2, 3)))
                    ))
                    .build();

            mockMvc.perform(
                            post("/user-api/v1/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createJson(orderRequest))
                    )
                    .andExpect(status().isCreated())
                    // [orderItems[].orderItemOptions[], orderItems[].orderItemOptions[].itemOptionId, orderItems[].orderItemOptions[].orderCount]
                    .andDo(document("user-orders-post-201",
                            PayloadDocumentation.requestFields(
                                    fieldWithPath("receiverAddress1").description("수령자 주소1"),
                                    fieldWithPath("receiverAddress2").description("수령자 주소2"),
                                    fieldWithPath("receiverZipcode").description("수령자 우편번호"),
                                    fieldWithPath("receiverPhone").description("수령자 휴대폰번호"),
                                    fieldWithPath("receiverName").description("수령자명"),
                                    fieldWithPath("etcMessage").description("남기는 말").optional(),
                                    fieldWithPath("orderItems[]").description("주문 물품 리스트"),
                                    fieldWithPath("orderItems[].itemId").description("주문 물품 식별자"),
                                    fieldWithPath("orderItems[].orderCount").description("주문 물품의 주문 수량").optional(),
                                    fieldWithPath("orderItems[].orderItemOptions[]").description("주문 물품의 옵션 리스트"),
                                    fieldWithPath("orderItems[].orderItemOptions[].itemOptionId").description("주문 물품 옵션의 식별자"),
                                    fieldWithPath("orderItems[].orderItemOptions[].orderCount").description("주문 물품 옵션의 주문 수량")
                            )
                    ));
        }

        @Test
        @DisplayName("유효하지 못한 인증 정보를 통해 주문을 할 경우, Unauthorized 상태를 응답한다.")
        void placeOrderWithoutAuthentication() throws Exception {
            String receiverAddress1 = "경기도 안양시";
            String receiverAddress2 = "만안구";
            String receiverZipcode = "430012";
            String receiverPhone = "01026799999";
            String receiverName = "최동호";
            String etcMessage = "부재시 경비실에 맡겨주세요.";

            OrderDto.PlaceOrderRequest orderRequest = OrderDto.PlaceOrderRequest.builder()
                    .orderItems(List.of(
                            new OrderDto.PlaceOrderItem(registeredItemId, 0,
                                    List.of(new OrderDto.PlaceOrderItemOption(registeredItemOptionId1, 2),
                                            new OrderDto.PlaceOrderItemOption(registeredItemOptionId2, 3)))
                    ))
                    .receiverAddress1(receiverAddress1)
                    .receiverAddress2(receiverAddress2)
                    .receiverZipcode(receiverZipcode)
                    .receiverPhone(receiverPhone)
                    .receiverName(receiverName)
                    .etcMessage(etcMessage)
                    .build();

            mockMvc.perform(
                            post("/user-api/v1/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", ACCESS_TOKEN)
                                    .content(createJson(orderRequest))
                    )
                    .andExpect(status().isUnauthorized())
                    .andDo(document("user-orders-post-401"));
        }
    }

    @Nested
    @DisplayName("getOrder 메소드는")
    class Describe_getOrder{

        // 조회로직 바꿔야 한다.

        @Test
        @WithUser("buyer1")
        @DisplayName("유효한 인증 정보와 함께 요청할 경우, 정상 응답한다.")
        void getOrderWithAuthentication() throws Exception {
            mockMvc.perform(
                            get("/user-api/v1/orders/" + registeredOrderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(document("user-orders-id-get-200"));
        }

        @Test
        @DisplayName("유효하지 못한 인증 정보를 통해 주문을 조회할 경우, Unauthorized 상태를 응답한다.")
        void getOrderWithoutAuthentication() throws Exception {
            mockMvc.perform(
                            get("/user-api/v1/orders/" + registeredOrderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", ACCESS_TOKEN)
                    )
                    .andExpect(status().isUnauthorized())
                    .andDo(document("user-orders-id-get-401"));
        }
    }

    @Nested
    @DisplayName("cancelOrder 메소드는")
    class Describe_cancelOrder{

        @Test
        @WithUser("buyer1")
        @DisplayName("유효한 인증 정보와 함께 요청할 경우, 정상 응답한다.")
        void getOrderWithAuthentication() throws Exception {
            mockMvc.perform(
                            delete("/user-api/v1/orders/" + registeredOrderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(document("user-orders-id-delete-200"));
        }

        @Test
        @DisplayName("유효하지 못한 인증 정보를 통해 주문을 조회할 경우, Unauthorized 상태를 응답한다.")
        void getOrderWithoutAuthentication() throws Exception {
            mockMvc.perform(
                            delete("/user-api/v1/orders/" + registeredOrderId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", ACCESS_TOKEN)
                    )
                    .andExpect(status().isUnauthorized())
                    .andDo(document("user-orders-id-delete-401"));
        }
    }
}
