package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductModificationRequest;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


// Todo unmappedTargetPolicy를 사용하지 않고 개별 메소드에 ignore 설정을 적용할 경우 lombok과의 마찰이 발생한다.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product of(SellerProductRegistrationRequest sellerProductRegistrationRequest);

    Product of(ProductModificationRequest productModificationRequest);
}
