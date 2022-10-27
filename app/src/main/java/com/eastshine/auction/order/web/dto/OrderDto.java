package com.eastshine.auction.order.web.dto;

import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.fragment.DeliveryFragment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class OrderDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        @NotBlank
        private String receiverName;
        @NotBlank
        private String receiverPhone;
        @NotBlank
        private String receiverZipcode;
        @NotBlank
        private String receiverAddress1;
        @NotBlank
        private String receiverAddress2;
        private String etcMessage;
        @NotNull
        private List<OrderLineRequest> orderItems;

        public void setOrderer(Long userId) {
            this.userId = userId;
        }

        public Order toEntity() {
            var deliveryFragment = DeliveryFragment.builder()
                    .receiverName(receiverName)
                    .receiverPhone(receiverPhone)
                    .receiverZipcode(receiverZipcode)
                    .receiverAddress1(receiverAddress1)
                    .receiverAddress2(receiverAddress2)
                    .etcMessage(etcMessage)
                    .build();

            return Order.builder()
                    .userId(userId)
                    .deliveryFragment(deliveryFragment)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineRequest {

        @NotNull
        private Long itemId;
        private Long itemOptionId;

        @NotNull
        private Integer orderCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private Long userId;
        private Long totalAmount;
        private List<OrderLineInfo> orderLineInfos;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineInfo {
        private Long sellerId;
        private Long itemId;
        private String itemName;
        private Integer itemPrice;
        private Long itemOptionId;
        private Long optionName;
        private Integer itemOptionPrice;
        private Integer orderCount;
    }
}
