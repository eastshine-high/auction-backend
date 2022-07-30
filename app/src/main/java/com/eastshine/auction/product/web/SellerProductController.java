package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.SellerProductService;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/seller-api/products")
@RestController
public class SellerProductController {
    private final SellerProductService sellerProductService;

    /**
     * 상품 정보로 상품을 등록 후, 등록된 상품의 URI를 응답한다.
     *
     * @param sellerProductRegistrationRequest 등록할 상품 정보.
     * @return 등록된 상품의 URI
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity registerProduct(@RequestBody @Validated SellerProductRegistrationRequest sellerProductRegistrationRequest) {
        Product registeredProduct = sellerProductService.registerProduct(sellerProductRegistrationRequest);
        return ResponseEntity.created(URI.create("/api/products/" + registeredProduct.getId())).build();
    }
}
