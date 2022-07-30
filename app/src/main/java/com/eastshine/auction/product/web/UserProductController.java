package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.UserProductService;
import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.UserProductSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class UserProductController {
    private final UserProductService userProductService;

    /**
     * 상품을 조회 조건에 맞게 검색하고 검색된 상품 정보를 응답한다.
     *
     * @param condition 상품 조회 조건.
     * @param pageable 페이징 정보.
     * @return 페이징된 상품 정보.
     */
    @GetMapping
    public Page<ProductDto> getProducts(
            @Validated UserProductSearchCondition condition,
            Pageable pageable
    ) {
        return userProductService.getProducts(condition, pageable);
    }
}
