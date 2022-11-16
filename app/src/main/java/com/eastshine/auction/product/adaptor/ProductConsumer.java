package com.eastshine.auction.product.adaptor;

import com.eastshine.auction.order.domain.event.ProductStockIncreased;
import com.eastshine.auction.product.application.ProductStockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductConsumer {
    public static final String TOPIC_PRODUCT_STOCK_INCREASE = "topic_product_stock_increase";

    private final ObjectMapper objectMapper;
    private final ProductStockService productStockService;

    @KafkaListener(topics = TOPIC_PRODUCT_STOCK_INCREASE, groupId = "auction")
    @Transactional
    public void consume(String record) throws IOException {
        ProductStockIncreased productStockIncreased = objectMapper.readValue(record, ProductStockIncreased.class);
        productStockIncreased.getOrderItems().stream().forEach(productStockService::increaseStock);
    }
}
