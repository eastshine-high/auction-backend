package com.eastshine.auction.product.application;

import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.option.ProductOption;
import com.eastshine.auction.product.domain.product.option.ProductOptionRepository;
import com.eastshine.auction.product.web.dto.SellerProductOptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SellerProductOptionService {
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public List<ProductOption> registerProductOptions(List<ProductOption> productOptions) {
        productOptions.stream()
                .forEach(productOption -> productOptionRepository.save(productOption));
        return productOptions;
    }
}
