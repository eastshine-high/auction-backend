package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductMapper;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.JsonMergePatch;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SellerProductService {
    private final JsonMergePatchMapper<Product> mergeMapper;
    private final Validator validator;
    private final ProductRepository productRepository;
    private final SellerProductOptionService sellerProductOptionService;

    @Transactional
    public Product registerProduct(SellerProductDto.RegistrationRequest registrationRequest) {
        Product product = registrationRequest.toProductEntity();
        productRepository.save(product);
        sellerProductOptionService
                .registerProductOptions(registrationRequest.toProductOptionEntities(product));
        return product;
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long productId, Long accessorId) {
        Product product = productRepository.findEagerById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        product.validateAccessibleUser(accessorId);
        return product;
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
