package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.ProductService;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.ProductDto;
import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;
import com.eastshine.auction.product.web.dto.ProductSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductController {
    private final ProductService productService;

    /**
     * 상품 정보로 상품을 등록 후, 등록된 상품의 URI를 응답한다.
     *
     * @param productRegistrationRequest 등록할 상품 정보.
     * @return 등록된 상품의 URI
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity registerProduct(@RequestBody @Validated ProductRegistrationRequest productRegistrationRequest) {
        Product registeredProduct = productService.registerProduct(productRegistrationRequest);
        return ResponseEntity.created(URI.create("/api/products/" + registeredProduct.getId())).build();
    }

    /**
     * 상품을 조회 조건에 맞게 검색하고 검색된 상품 정보를 응답한다.
     *
     * @param condition 상품 조회 조건.
     * @param pageable 페이징 정보.
     * @return 페이징된 상품 정보.
     */
    @GetMapping
    public Page<ProductDto> getProducts(
            @Validated ProductSearchCondition condition,
            Pageable pageable
    ) {
        return productService.getProducts(condition, pageable);
    }
}
