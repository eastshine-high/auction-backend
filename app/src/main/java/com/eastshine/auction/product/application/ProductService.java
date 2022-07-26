package com.eastshine.auction.product.application;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.domain.CategoryRepository;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.product.domain.Product;
import com.eastshine.auction.product.domain.ProductMapper;
import com.eastshine.auction.product.domain.ProductRepository;
import com.eastshine.auction.product.domain.category.ProductCategory;
import com.eastshine.auction.product.domain.category.ProductCategoryId;
import com.eastshine.auction.product.domain.category.ProductCategoryRepository;
import com.eastshine.auction.product.web.dto.ProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public Product registerProduct(ProductRegistrationRequest productRegistrationRequest) {
        Category category = categoryRepository.findById(productRegistrationRequest.getCategoryId())
                .orElseThrow(() -> new InvalidArgumentException(ErrorCode.PRODUCT_INVALID_CATEGORY_ID));

        if (productRepository.existsProduct(productRegistrationRequest)) {
            throw new InvalidArgumentException(ErrorCode.PRODUCT_DUPLICATE);
        }

        Product registeredProduct = productRepository.save(productMapper.of(productRegistrationRequest));
        productCategoryRepository.save(new ProductCategory(new ProductCategoryId(registeredProduct, category)));
        return registeredProduct;
    }
}
