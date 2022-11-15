package com.eastshine.auction.infra.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSent {
    private String to;
    private String subject;
    private String message;

    public EmailMessage toEmailMessage() {
        return EmailMessage.builder()
                .to(to)
                .subject(subject)
                .message(message)
                .build();
    }
}
