package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.product.domain.product.Product;
import com.eastshine.auction.product.domain.product.ProductRepository;
import com.eastshine.auction.product.lock.ProductStockLock;
import com.eastshine.auction.product.web.dto.SystemProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProductStockService {
    private static final String PRODUCT_LOCK_PREFIX = "PRODUCT_STOCK_";
    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final ProductStockLock productStockLock;
    private final ProductRepository productRepository;
    private final ProductOptionStockService productOptionStockService;

    @Transactional
    public void decreaseStock(SystemProductDto.DecreaseStock decreaseStockRequest) {
        decreaseStockRequest.getProducts().stream()
                .forEach(product -> {
                    if(Objects.isNull(product.getQuantity()) || product.getQuantity() == 0){
                        productOptionStockService.decreaseStock(product.getProductOptions());
                    }

                    productStockLock.executeWithLock(
                            PRODUCT_LOCK_PREFIX + String.valueOf(product.getId()),
                            LOCK_TIMEOUT_SECONDS,
                            () -> decreaseProductStock(product.getId(), product.getQuantity())
                    );
                });
    }

    private Product decreaseProductStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        product.decreaseStockQuantity(quantity);
        return productRepository.saveAndFlush(product);
    }
}
