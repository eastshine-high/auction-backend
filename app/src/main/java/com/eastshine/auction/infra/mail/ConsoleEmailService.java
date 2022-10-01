package com.eastshine.auction.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class ConsoleEmailService implements MailService{

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("-----------------------");
        log.info("Sent email : " + emailMessage.getMessage());
        log.info("-----------------------");
    }
}
