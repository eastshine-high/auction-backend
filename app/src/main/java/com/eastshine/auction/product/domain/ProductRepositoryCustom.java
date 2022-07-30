package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.UserProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Boolean existsProduct(Integer categoryId, String productName);

    Page<ProductDto> findProducts(UserProductSearchCondition condition, Pageable pageable);
}
