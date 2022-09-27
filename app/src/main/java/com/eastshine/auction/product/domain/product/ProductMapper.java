package com.eastshine.auction.product.domain.product;

import com.eastshine.auction.product.application.SellerProductPatchValidationBean;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


// Todo unmappedTargetPolicy를 사용하지 않고 개별 메소드에 ignore 설정을 적용할 경우 lombok과의 마찰이 발생한다.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    SellerProductPatchValidationBean toValidationBean(Product product);

    SellerProductDto.Info toDto(Product product);
}
