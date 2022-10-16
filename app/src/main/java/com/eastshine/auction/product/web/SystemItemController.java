package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.ItemStockService;
import com.eastshine.auction.product.web.dto.SystemItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/system-api/items")
@RestController
public class SystemItemController {
    private final ItemStockService itemStockService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/decrease-stock")
    public void decreaseStock(
            @RequestBody SystemItemDto.DecreaseStock decreaseStockRequest
    ) {
        itemStockService.decreaseStock(decreaseStockRequest);
    }
}
