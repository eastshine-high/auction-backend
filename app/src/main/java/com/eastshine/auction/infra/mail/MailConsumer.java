package com.eastshine.auction.infra.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailConsumer {
    public static final String TOPIC_MAIL = "topic_mail";

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC_MAIL, groupId = "auction")
    public void consume(String record) throws IOException {
        EmailMessage emailMessage = objectMapper.readValue(record, EmailMessage.class);
        mailService.sendEmail(emailMessage);
    }
}
