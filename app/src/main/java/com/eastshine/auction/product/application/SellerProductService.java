package com.eastshine.auction.product.application;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductMapper;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.web.dto.SellerProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SellerProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public Product registerProduct(SellerProductRegistrationRequest registrationRequest) {
        Category category = categoryRepository.findById(registrationRequest.getCategoryId())
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.PRODUCT_INVALID_CATEGORY_ID));

        Product product = productMapper.of(registrationRequest);
        product.setCategory(category);
        return productRepository.save(product);
    }
}
