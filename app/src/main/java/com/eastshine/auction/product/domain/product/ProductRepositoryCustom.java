package com.eastshine.auction.product.domain.product;

import com.eastshine.auction.product.web.dto.ProductDto;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductDto.SearchResponse> searchProducts(ProductDto.SearchCondition condition, Pageable pageable);

    ProductDto.Info findGuestProductInfo(Long id);
}
