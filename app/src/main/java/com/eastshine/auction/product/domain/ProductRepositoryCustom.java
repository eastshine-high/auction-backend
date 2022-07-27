package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;
import com.eastshine.auction.product.web.dto.ProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Boolean existsProduct(ProductRegistrationRequest condition);

    Page<ProductDto> findProducts(ProductSearchCondition condition, Pageable pageable);
}
