package com.eastshine.auction.order.web.dto;

import com.eastshine.auction.common.model.UserInfo;
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
        private UserInfo userInfo;
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
        private List<OrderLineRequest> orderLines;

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
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
                    .userId(userInfo.getId())
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

    // 조회
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private Long orderId;
        private Long userId;
        private Long totalAmount;
        private DeliveryInfo deliveryInfo;
        private String orderStatus;
        private String orderStatusDescription;
        private List<OrderLineInfo> orderLines;
    }

    @Getter
    @Builder
    public static class DeliveryInfo {
        private final String receiverName;
        private final String receiverPhone;
        private final String receiverZipcode;
        private final String receiverAddress1;
        private final String receiverAddress2;
        private final String etcMessage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineInfo {
        private Long sellerId;
        private Long itemId;
        private String itemName;
        private Integer itemPrice;
        private Long itemOptionId;
        private String itemOptionName;
        private Integer itemOptionPrice;
        private Integer orderCount;
        private Long totalAmount;
        private String deliveryStatus;
        private String deliveryStatusDescription;
    }
}
