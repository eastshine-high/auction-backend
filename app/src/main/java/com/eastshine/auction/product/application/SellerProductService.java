package com.eastshine.auction.product.application;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductMapper;
import com.eastshine.auction.product.domain.ProductRepository;

import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
        Category category = findCategory(registrationRequest.getCategoryId());
        Product product = registrationRequest.toEntity(category);

        if(!CollectionUtils.isEmpty(registrationRequest.getProductOptions())) {
            registrationRequest.getProductOptions().stream().forEach(
                    optionRegistrationRequest -> {
                        product.addProductOption(optionRegistrationRequest.toEntity());
                    });
        }

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product fetchProduct(Long id) {
        return productRepository.findEagerById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product updatePatchedProduct(Product patchedProduct) {
        Category category = findCategory(patchedProduct.getCategory().getId());
        patchedProduct.setCategory(category);
        validatePatchedProduct(patchedProduct);
        return productRepository.save(patchedProduct);
    }

    private void validatePatchedProduct(Product patchedProduct) {
        SellerProductPatchValidationBean sellerProductPatchValidationBean = productMapper.toValidationBean(patchedProduct);
        Set<ConstraintViolation<SellerProductPatchValidationBean>> violations = validator.validate(sellerProductPatchValidationBean);
        if (!violations.isEmpty()) {
            ConstraintViolation<SellerProductPatchValidationBean> constraintViolation = violations.stream().findFirst().get();
            throw new InvalidArgumentException(constraintViolation.getPropertyPath() + " : " +constraintViolation.getMessage());
        }
    }

    private Category findCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.PRODUCT_INVALID_CATEGORY_ID));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
