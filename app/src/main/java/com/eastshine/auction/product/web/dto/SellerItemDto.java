package com.eastshine.auction.product.web.dto;

import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.fragment.DeliveryChargePolicyType;
import com.eastshine.auction.product.domain.item.fragment.DeliveryMethodType;
import com.eastshine.auction.product.domain.item.fragment.ReturnFragment;
import com.eastshine.auction.product.domain.item.fragment.ShippingFragment;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SellerItemDto {

    @ToString
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRegistration {

        @NotBlank
        private String name;
        @NotNull
        private Integer categoryId;
        @NotNull
        @Range(min = 1000)
        private Integer price;
        @NotNull
        private Boolean onSale;
        private Integer stockQuantity;
        @NotNull
        private DeliveryChargePolicyType deliveryChargePolicy;
        @NotNull
        private Integer deliveryTime;
        private Integer deliveryCharge;
        private Integer freeShipOverAmount;
        @NotNull
        private DeliveryMethodType deliveryMethod;
        @NotNull
        private String returnChargeName;
        @NotNull
        private String returnContactNumber;
        @NotNull
        private String returnZipCode;
        @NotNull
        private String returnAddress;
        @NotNull
        private String returnAddressDetail;
        @NotNull
        private Integer returnCharge;
        private String itemOptionsTitle;
        private List<ItemOptionRegistration> itemOptions;

        public Item toEntity() {
            ReturnFragment returnFragment = ReturnFragment.builder()
                    .returnChargeName(returnChargeName)
                    .returnContactNumber(returnContactNumber)
                    .returnZipCode(returnZipCode)
                    .returnAddress(returnAddress)
                    .returnAddressDetail(returnAddressDetail)
                    .returnCharge(returnCharge)
                    .build();

            ShippingFragment shippingFragment = ShippingFragment.builder()
                    .deliveryChargePolicy(deliveryChargePolicy)
                    .deliveryCharge(deliveryCharge)
                    .deliveryMethod(deliveryMethod)
                    .deliveryTime(deliveryTime)
                    .freeShipOverAmount(freeShipOverAmount)
                    .returnFragment(returnFragment)
                    .build();

            return Item.builder()
                    .name(name)
                    .categoryId(categoryId)
                    .price(price)
                    .onSale(onSale)
                    .stockQuantity(stockQuantity)
                    .shippingFragment(shippingFragment)
                    .itemOptionsTitle(itemOptionsTitle)
                    .build();
        }
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemOptionRegistration {
        @NotBlank
        private String itemOptionName;
        @NotNull
        private Integer ordering;
        private Integer stockQuantity;
        private Integer additionalPrice;

        public ItemOption toEntity() {
            return ItemOption.builder()
                    .itemOptionName(itemOptionName)
                    .additionalPrice(additionalPrice)
                    .stockQuantity(stockQuantity)
                    .ordering(ordering)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemInfo {
        private Long id;
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private Integer categoryId;
        private Boolean onSale;
        private ShippingInfo shippingInfo;
        private String itemOptionsTitle;
        private List<ItemOption> itemOptions;
    }

    @Getter
    @Builder
    public static class ShippingInfo{
        private String deliveryChargePolicy;
        private String deliveryChargePolicyDescription;
        private Integer deliveryTime;
        private Integer deliveryCharge;
        private Integer freeShipOverAmount;
        private String deliveryMethod;
        private String deliveryMethodDescription;
        private ReturnInfo returnInfo;
    }

    @Getter
    @Builder
    public static class ReturnInfo{
        private String returnChargeName;
        private String returnContactNumber;
        private String returnZipCode;
        private String returnAddress;
        private String returnAddressDetail;
        private Integer returnCharge;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemOptionInfo {
        private Long id;
        private String itemOptionName;
        private Integer additionalPrice;
        private Integer stockQuantity;
        private Integer ordering;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchItem {
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private Integer categoryId;
        private Boolean onSale;
        private String itemOptionsTitle;
        private PatchShipping shippingFragment;
        private List<PatchItemOption> itemOptions;
    }

    @Getter
    @Builder
    public static class PatchShipping {
        private DeliveryChargePolicyType deliveryChargePolicy;
        private Integer deliveryTime;
        private Integer deliveryCharge;
        private Integer freeShipOverAmount;
        private DeliveryMethodType deliveryMethod;
        private ReturnInfo returnInfo;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchItemOption {
        private Long id;
        private String itemOptionName;
        private Integer additionalPrice;
        private Integer stockQuantity;
        private Integer ordering;
    }
}
