package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.option.ProductOption;
import com.eastshine.auction.product.domain.product.option.ProductOptionRepository;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SellerProductOptionService {
    private final ObjectMapper objectMapper;
    private final JsonMergePatchMapper<ProductOption> mergeMapper;
    private final ProductOptionRepository productOptionRepository;

    public void addProductOptionsToProduct(SellerProductDto.RegistrationRequest registrationRequest, Product product) {
        if(!CollectionUtils.isEmpty(registrationRequest.getProductOptions())) {
            registrationRequest.getProductOptions().stream().forEach(
                    optionRegistrationRequest -> {
                        product.addProductOption(optionRegistrationRequest.toEntity(product));
                    });
        }
    }

    public List<ProductOption> patchProductOptions(Product product, List<ProductOption> productOptions) {
        return productOptions.stream()
                .map(patchRequest -> {
                    ProductOption productOption = findProductOption(patchRequest);

                    JsonValue jsonValue = objectMapper.convertValue(patchRequest, JsonValue.class);
                    JsonMergePatch patchDocument = Json.createMergePatch(jsonValue);

                    ProductOption patchedOption = mergeMapper.apply(patchDocument, productOption);
                    patchedOption.setProduct(product);
                    return patchedOption;
                })
                .collect(Collectors.toList());
    }

    private ProductOption findProductOption(ProductOption patchRequest) {
        return productOptionRepository.findById(patchRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException());
    }
}
