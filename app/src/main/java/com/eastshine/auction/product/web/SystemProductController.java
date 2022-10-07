package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.ProductStockService;
import com.eastshine.auction.product.web.dto.SystemProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/system-api/products")
@RestController
public class SystemProductController {
    private final ProductStockService productStockService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/decrease-stock")
    public void decreaseStock(
            @RequestBody SystemProductDto.DecreaseStock decreaseStockRequest
    ) {
        productStockService.decreaseStock(decreaseStockRequest);
    }
}
