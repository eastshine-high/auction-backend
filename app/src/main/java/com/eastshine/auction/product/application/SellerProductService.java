package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductMapper;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.json.JsonMergePatch;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SellerProductService {
    private final ProductRepository productRepository;
    private final JsonMergePatchMapper<Product> mergeMapper;
    private final Validator validator;

    @Transactional
    public Product registerProduct(SellerProductRegistrationRequest registrationRequest) {
        Product product = registrationRequest.toEntity();

        if(!CollectionUtils.isEmpty(registrationRequest.getProductOptions())) {
            registrationRequest.getProductOptions().stream().forEach(
                    optionRegistrationRequest -> {
                        product.addProductOption(optionRegistrationRequest.toEntity());
                    });
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product patchProduct(Long productId, JsonMergePatch patchDocument, Long accessor) {
        Product product = findProduct(productId);
        product.validateAccessibleUser(accessor);

        Product patchedProduct = mergeMapper.apply(patchDocument, product);
        validatePatchedProduct(patchedProduct);
        return productRepository.save(patchedProduct);
    }

    private void validatePatchedProduct(Product patchedProduct) {
        SellerProductPatchValidationBean sellerProductPatchValidationBean = ProductMapper.INSTANCE.toValidationBean(patchedProduct);
        Set<ConstraintViolation<SellerProductPatchValidationBean>> violations = validator.validate(sellerProductPatchValidationBean);
        if (!violations.isEmpty()) {
            ConstraintViolation<SellerProductPatchValidationBean> constraintViolation = violations.stream().findFirst().get();
            throw new InvalidArgumentException(constraintViolation.getPropertyPath() + " : " +constraintViolation.getMessage());
        }
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
