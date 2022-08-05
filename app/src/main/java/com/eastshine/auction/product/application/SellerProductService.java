package com.eastshine.auction.product.application;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductMapper;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductPatchValidationBean;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SellerProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final Validator validator;

    @Transactional
    public Product registerProduct(SellerProductRegistrationRequest registrationRequest) {
        Category category = categoryRepository.findById(registrationRequest.getCategoryId())
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.PRODUCT_INVALID_CATEGORY_ID));

        Product product = productMapper.of(registrationRequest);
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product fetchProduct(Long id) {
        return productRepository.findEagerById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product updatePatchedProduct(Product patchedProduct) {
        Category category = categoryRepository.findById(patchedProduct.getCategory().getId())
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.PRODUCT_INVALID_CATEGORY_ID));
        patchedProduct.setCategory(category);
        validatePatchedProduct(patchedProduct);

        productRepository.save(patchedProduct);
        return patchedProduct;
    }

    private void validatePatchedProduct(Product patchedProduct) {
        SellerProductPatchValidationBean sellerProductPatchValidationBean = productMapper.toSellerProductModificationDto(patchedProduct);
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
