package com.eastshine.auction.product.domain;

import com.eastshine.auction.product.web.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductDto.SearchResponse> searchProducts(ProductDto.SearchCondition condition, Pageable pageable);
}
