package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.product.domain.product.option.ProductOption;
import com.eastshine.auction.product.domain.product.option.ProductOptionRepository;
import com.eastshine.auction.product.lock.ProductStockLock;
import com.eastshine.auction.product.web.dto.SystemProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductOptionStockService {
    private static final String PRODUCT_OPTION_LOCK_PREFIX = "PRODUCT_OPTION_STOCK_";
    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final ProductStockLock productStockLock;
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public void decreaseStock(List<SystemProductDto.DecreaseStock.ProductOption> productOptions) {
        productOptions.stream().forEach(
                productOption -> {
                    productStockLock.executeWithLock(
                            PRODUCT_OPTION_LOCK_PREFIX + String.valueOf(productOption.getId()),
                            LOCK_TIMEOUT_SECONDS,
                            () -> decreaseProductOptionStock(productOption.getId(), productOption.getQuantity())
                    );
                }
        );
    }

    private ProductOption decreaseProductOptionStock(Long id, int quantity) {
        ProductOption option = productOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        option.decreaseStockQuantity(quantity);
        return productOptionRepository.saveAndFlush(option);
    }
}
