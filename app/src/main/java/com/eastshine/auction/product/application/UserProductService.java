package com.eastshine.auction.product.application;

import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.UserProductSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(UserProductSearchCondition condition, Pageable pageRequest) {
        return productRepository.findProducts(condition, pageRequest);
    }
}
