package com.eastshine.auction.product.domain.item;

import com.eastshine.auction.product.application.SellerItemPatchValidationBean;
import com.eastshine.auction.product.web.dto.ItemDto;
import com.eastshine.auction.product.web.dto.SellerItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


// Todo unmappedTargetPolicy를 사용하지 않고 개별 메소드에 ignore 설정을 적용할 경우 lombok과의 마찰이 발생한다.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    SellerItemPatchValidationBean toValidationBean(Item item);

    @Mappings({
            @Mapping(source = "item.shippingFragment", target = "shippingInfo"),
            @Mapping(source = "item.shippingFragment.returnFragment", target = "shippingInfo.returnInfo"),
            @Mapping(expression = "java(shippingFragment.getDeliveryMethod().name())", target = "shippingInfo.deliveryMethod"),
            @Mapping(expression = "java(shippingFragment.getDeliveryMethod().getDescription())", target = "shippingInfo.deliveryMethodDescription"),
            @Mapping(expression = "java(shippingFragment.getDeliveryChargePolicy().name())", target = "shippingInfo.deliveryChargePolicy"),
            @Mapping(expression = "java(shippingFragment.getDeliveryChargePolicy().getDescription())", target = "shippingInfo.deliveryChargePolicyDescription")
    })
    SellerItemDto.ItemInfo toDto(Item item);

    @Mappings({
            @Mapping(source = "item.shippingFragment", target = "shippingInfo"),
            @Mapping(expression = "java(shippingFragment.getDeliveryMethod().name())", target = "shippingInfo.deliveryMethod"),
            @Mapping(expression = "java(shippingFragment.getDeliveryMethod().getDescription())", target = "shippingInfo.deliveryMethodDescription"),
            @Mapping(expression = "java(shippingFragment.getDeliveryChargePolicy().name())", target = "shippingInfo.deliveryChargePolicy"),
            @Mapping(expression = "java(shippingFragment.getDeliveryChargePolicy().getDescription())", target = "shippingInfo.deliveryChargePolicyDescription")
    })
    ItemDto.ItemInfo toItemDto(Item item);
}
