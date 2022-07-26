package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;

public interface ProductRepositoryCustom {

    Boolean existsProduct(ProductRegistrationRequest condition);
}
