package com.eastshine.auction.product.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.product.application.SellerProductService;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductMapper;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/seller-api/products")
@RestController
public class SellerProductController {
    private final SellerProductService sellerProductService;
    private final ObjectMapper objectMapper;

    /**
     * 상품 정보로 상품을 등록 후, 등록된 상품의 URI를 응답한다.
     *
     * @param sellerProductRegistrationRequest 등록할 상품 정보.
     * @return 등록된 상품의 URI.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity registerProduct(@RequestBody @Validated SellerProductDto.RegistrationRequest sellerProductRegistrationRequest) {
        Product registeredProduct = sellerProductService.registerProduct(sellerProductRegistrationRequest);
        return ResponseEntity.created(URI.create("/seller-api/products/" + registeredProduct.getId())).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SELLER')")
    public SellerProductDto.Info getProduct(
            @PathVariable Long id,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo)authentication.getPrincipal();
        Product productInfo = sellerProductService.getProduct(id, userInfo.getId());
        return ProductMapper.INSTANCE.toDto(productInfo);
    }

    /**
     * 상품 정보를 패치하고 패치 결과를 응답합니다.
     *
     * @param productId 패치할 상품 정보의 식별자.
     * @param sellerProductPatchRequest 패치 요청 정보.
     * @param authentication 인증 정보.
     */
    @PatchMapping("/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    @ResponseStatus(HttpStatus.OK)
    public void patchProduct(
            @PathVariable Long productId,
            @RequestBody SellerProductDto.PatchRequest sellerProductPatchRequest,
            Authentication authentication
    ) {
        JsonValue jsonValue = objectMapper.convertValue(sellerProductPatchRequest, JsonValue.class);
        JsonMergePatch patchDocument = Json.createMergePatch(jsonValue);
        UserInfo userInfo = (UserInfo)authentication.getPrincipal();
        sellerProductService.patchProduct(productId, patchDocument, userInfo.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SELLER')")
    public void deleteProduct(
            @PathVariable Long id,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo)authentication.getPrincipal();
        sellerProductService.deleteProduct(id, userInfo.getId());
    }
}
