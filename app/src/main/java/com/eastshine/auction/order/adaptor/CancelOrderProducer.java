package com.eastshine.auction.order.adaptor;

import com.eastshine.auction.common.exception.BaseException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.infra.mail.EmailSent;
import com.eastshine.auction.infra.mail.MailConsumer;
import com.eastshine.auction.order.domain.Order;
import com.eastshine.auction.order.domain.event.ProductStockIncreased;
import com.eastshine.auction.product.adaptor.ProductConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CancelOrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void increaseStock(Order order) {
        ProductStockIncreased productStockIncreased = new ProductStockIncreased(order.getOrderItems());
        try {
            kafkaTemplate.send(ProductConsumer.TOPIC_PRODUCT_STOCK_INCREASE, objectMapper.writeValueAsString(productStockIncreased));

        } catch (JsonProcessingException e) {
            log.error("orderId : ", order.getId());
            log.error("failed to Json processing", e);
            throw new BaseException(ErrorCode.COMMON_SYSTEM_ERROR);
        }
    }

    public void sendMail(UserInfo userInfo, Order order) {
        EmailSent emailMessage = EmailSent.builder()
                .to(userInfo.getEmail())
                .subject("주문 취소 확인")
                .message("주문 번호 '" + order.getId() + "'의 주문이 취소되었습니다.") // Template 처리 필요.
                .build();
        try {
            kafkaTemplate.send(MailConsumer.TOPIC_EMAIL, objectMapper.writeValueAsString(emailMessage));

        } catch (JsonProcessingException exception) {
            log.error("orderId : ", order.getId());
            log.error("failed to Json processing", exception);
            throw new BaseException(ErrorCode.COMMON_SYSTEM_ERROR);
        }
    }
}
