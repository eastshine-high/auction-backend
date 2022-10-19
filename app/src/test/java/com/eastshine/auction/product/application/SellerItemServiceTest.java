package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.web.dto.SellerItemDto;
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

class SellerItemServiceTest extends IntegrationTest {
    private static final int REGISTERED_CATEGORY_ID = 101;
    private static final String REGISTERED_ITEM_NAME = "마데카솔";
    private static final Long ITEM_CREATOR_ID = 4L;

    private static Long registeredItemId;

    @Autowired CategoryFactory categoryFactory;
    @Autowired ItemRepository itemRepository;
    @Autowired SellerItemService sellerItemService;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        Item item = Item.builder()
                .onSale(true)
                .itemOptionsTitle("용량")
                .categoryId(300)
                .name(REGISTERED_ITEM_NAME)
                .price(3000)
                .itemOptions(new ArrayList<>())
                .build();
        ItemOption itemOption = ItemOption.builder()
                .itemOptionName("500ML")
                .stockQuantity(500)
                .additionalPrice(700)
                .ordering(1)
                .build();
        item.addItemOption(itemOption);
        ReflectionTestUtils.setField(item, "createdBy", ITEM_CREATOR_ID);

        itemRepository.save(item);
        registeredItemId = item.getId();
    }

    @DisplayName("registerItem 메소드는")
    @Nested
    class Describe_registerItem {

        @DisplayName("유효한 상품 정보로 등록할 경우")
        @Nested
        class Context_with_registered_category{
            SellerItemDto.RegistrationRequest itemInfo;
            boolean onSale = true;
            int stockQuantity = 20;
            String itemName = "비판텐";
            int price = 3200;

            SellerItemDto.RegistrationRequest.ItemOption optionInfo;
            String itemOptionName = "300ml";
            int ordering = 1;
            int optionStockQuantity = 9999;
            int additionalPrice = 600;

            @DisplayName("등록된 상품을 반환한다.")
            @Test
            void it_returns_registerd_item() {
                optionInfo = SellerItemDto.RegistrationRequest.ItemOption.builder()
                        .itemOptionName(itemOptionName)
                        .additionalPrice(additionalPrice)
                        .ordering(ordering)
                        .stockQuantity(optionStockQuantity)
                        .build();

                itemInfo = SellerItemDto.RegistrationRequest.builder()
                        .onSale(onSale)
                        .stockQuantity(stockQuantity)
                        .categoryId(REGISTERED_CATEGORY_ID)
                        .name(itemName)
                        .price(price)
                        .itemOptions(Arrays.asList(optionInfo))
                        .build();

                Item item = sellerItemService.registerItem(itemInfo);

                assertThat(item).isInstanceOf(Item.class);
                assertThat(item.getName()).isEqualTo(itemName);
                assertThat(item.getPrice()).isEqualTo(price);
                assertThat(item.getStockQuantity()).isEqualTo(stockQuantity);
                assertThat(item.isOnSale()).isEqualTo(onSale);

                ItemOption itemOption = item.getItemOptions().get(0);
                assertThat(itemOption.getItemOptionName()).isEqualTo(itemOptionName);
                assertThat(itemOption.getAdditionalPrice()).isEqualTo(additionalPrice);
                assertThat(itemOption.getOrdering()).isEqualTo(ordering);
                assertThat(itemOption.getStockQuantity()).isEqualTo(optionStockQuantity);
            }
        }
    }

    @Nested
    @DisplayName("getItem 메소드는")
    class Describe_getItem {

        @Nested
        @DisplayName("등록되지 않은 상품을 조회할 경우")
        class Context_with_unregistered_item_id {
            private final long unregisteredItemId = -1;

            @Test
            @DisplayName("EntityNotFoundException 예외를 던진다.")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(() ->
                        sellerItemService.getItem(unregisteredItemId, ITEM_CREATOR_ID)
                )
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage(ErrorCode.ITEM_NOT_FOUND.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("상품을 등록한 사용자가 아닌 사용자가 조회할 경우")
        class Context_with_inaccessible_user {
            private final Long inaccessibleUserId = -1L;

            @Test
            @DisplayName("UnauthorizedException 예외를 던진다.")
            void it_throws_UnauthorizedException() {
                assertThatThrownBy(() ->
                        sellerItemService.getItem(registeredItemId, inaccessibleUserId)
                )
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage(ErrorCode.ITEM_INACCESSIBLE.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효한 요청 정보로 상품을 조회할 경우")
        class Context_with_valid_modification_info {

            @Test
            @DisplayName("수정된 상품을 반환한다.")
            void it_returns_modified_item() {
                assertThat(sellerItemService.getItem(registeredItemId, ITEM_CREATOR_ID))
                        .isInstanceOf(Item.class);
            }
        }
    }

    @Nested
    @DisplayName("patchItem 메소드는")
    class Describe_patchItem {

        @Nested
        @DisplayName("등록되지 않은 상품을 수정할 경우")
        class Context_with_unregistered_item_id {
            private final long unregisteredItemId = -1;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("price", 99999)
                    .build());

            @Test
            @DisplayName("EntityNotFoundException 예외를 던진다.")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(() ->
                        sellerItemService.patchItem(unregisteredItemId, patchDocument, ITEM_CREATOR_ID)
                )
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage(ErrorCode.ITEM_NOT_FOUND.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효하지 못한 요청 정보로 상품을 수정할 경우")
        class Context_with_invalid_item_info {
            int invalidPrice = 456;
            private final JsonMergePatch patchDocument = Json.createMergePatch(Json.createObjectBuilder()
                    .add("price", invalidPrice)
                    .build());

            @Test
            @DisplayName("InvalidArgumentException 예외를 던진다.")
            void it_throws_InvalidArgumentException() {
                assertThatThrownBy(() ->
                        sellerItemService.patchItem(registeredItemId, patchDocument, ITEM_CREATOR_ID)
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
                        sellerItemService.patchItem(registeredItemId, patchDocument, inaccessibleUserId)
                )
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage(ErrorCode.ITEM_INACCESSIBLE.getErrorMsg());
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
            void it_returns_modified_item() {
                Item actual = sellerItemService.patchItem(registeredItemId, patchDocument, ITEM_CREATOR_ID);

                assertThat(actual.getPrice()).isEqualTo(price);
                assertThat(actual.getName()).isEqualTo(name);
                assertThat(actual.isOnSale()).isEqualTo(onSale);
                assertThat(actual.getStockQuantity()).isEqualTo(stockQuantity);
                assertThat(actual.getCategoryId()).isEqualTo(categoryId);
            }
        }
    }

    @Nested
    @DisplayName("deleteItem 메소드는")
    class Describe_deleteItem {

        @Nested
        @DisplayName("등록되지 않은 상품을 삭제할 경우")
        class Context_with_unregistered_item_id {
            private final long unregisteredItemId = -1;

            @Test
            @DisplayName("EntityNotFoundException 예외를 던진다.")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(() ->
                        sellerItemService.deleteItem(unregisteredItemId, ITEM_CREATOR_ID)
                )
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage(ErrorCode.ITEM_NOT_FOUND.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("상품을 등록한 사용자가 아닌 사용자가 삭제할 경우")
        class Context_with_inaccessible_user {
            private final Long inaccessibleUserId = -1L;

            @Test
            @DisplayName("UnauthorizedException 예외를 던진다.")
            void it_throws_UnauthorizedException() {
                assertThatThrownBy(() ->
                        sellerItemService.deleteItem(registeredItemId, inaccessibleUserId)
                )
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage(ErrorCode.ITEM_INACCESSIBLE.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효한 요청 정보로 상품을 삭제할 경우")
        class Context_with_valid_modification_info {

            @Test
            @DisplayName("상품을 삭제한다.")
            void it_returns_modified_item() {
                sellerItemService.deleteItem(registeredItemId, ITEM_CREATOR_ID);

                assertThat(itemRepository.findById(registeredItemId).isEmpty())
                        .isTrue();
            }
        }
    }
}
