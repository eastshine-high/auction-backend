package com.eastshine.auction.product.web;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.application.SellerProductService;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.web.dto.SellerProductPatchRequest;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
    private final JsonMergePatchMapper<Product> mergeMapper;
    private final ObjectMapper objectMapper;

    /**
     * 상품 정보로 상품을 등록 후, 등록된 상품의 URI를 응답한다.
     *
     * @param sellerProductRegistrationRequest 등록할 상품 정보.
     * @return 등록된 상품의 URI.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity registerProduct(@RequestBody @Validated SellerProductRegistrationRequest sellerProductRegistrationRequest) {
        Product registeredProduct = sellerProductService.registerProduct(sellerProductRegistrationRequest);
        return ResponseEntity.created(URI.create("/api/products/" + registeredProduct.getId())).build();
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
            @RequestBody SellerProductPatchRequest sellerProductPatchRequest,
            Authentication authentication
    ) {
        Product product = sellerProductService.fetchProduct(productId);
        validateAccessableProduct(product, authentication);

        JsonValue jsonValue = objectMapper.convertValue(sellerProductPatchRequest, JsonValue.class);
        JsonMergePatch patchDocument = Json.createMergePatch(jsonValue);
        Product patchedProduct = mergeMapper.apply(patchDocument, product);

        sellerProductService.updatePatchedProduct(patchedProduct);
    }

    /**
     * 상품 정보에 접근 가능한 인증 정보인지 검증한다.
     *
     * @param product 접근하려는 상품 정보
     * @param authentication 상품 정보에 접근하려는 사용자의 인증 정보
     * @throws UnauthorizedException 상품 정보의 등록자와 상품 정보에 접근하려는 요청자가 다른 경우.
     */
    private void validateAccessableProduct(Product product, Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        if(product.getCreatedBy() != userInfo.getId()){
            throw new UnauthorizedException(ErrorCode.PRODUCT_UNACCESSABLE);
        }
    }
}
