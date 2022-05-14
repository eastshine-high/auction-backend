package com.eastshine.auction.user.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health-check")
    public Long healthCheck() {
        return System.currentTimeMillis();
    }
}
