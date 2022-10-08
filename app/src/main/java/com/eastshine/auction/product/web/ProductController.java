package com.eastshine.auction.product.web;

import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.domain.product.option.ProductOptionRepository;
import com.eastshine.auction.product.web.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    /**
     * 상품을 조회 조건에 맞게 검색하고 검색된 상품 정보들을 응답한다.
     *
     * @param condition 상품 조회 조건.
     * @param pageable 페이징 정보.
     * @return 페이징된 상품 정보들.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto.SearchResponse> getProducts(
            @Validated ProductDto.SearchCondition condition,
            Pageable pageable
    ) {
        return productRepository.searchProducts(condition, pageable);
    }

    /**
     * 식별자에 해당하는 상품 정보를 찾아 응답한다.
     *
     * @param id 상품의 식별자
     * @return 해당 식별자의 상품 정보.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto.Info getProduct(@PathVariable Long id) {
        ProductDto.Info productInfo = productRepository.findGuestProductInfo(id);
        productInfo.setProductOptions(
                productOptionRepository.findByProductId(id).stream()
                        .map(ProductDto.Info.ProductOption::new)
                        .collect(Collectors.toList())
        );
        return productInfo;
    }
}
