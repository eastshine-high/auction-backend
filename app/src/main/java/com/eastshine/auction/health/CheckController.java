package com.eastshine.auction.health;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@RestController
public class CheckController {
    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/api/health")
    public String healthCheck() {
        return LocalDateTime.now().toString();
    }

    @GetMapping("/api/port")
    public String portNumber() {
        return String.valueOf(serverPort);
    }
}
