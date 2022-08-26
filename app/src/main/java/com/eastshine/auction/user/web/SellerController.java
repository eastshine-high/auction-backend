package com.eastshine.auction.user.web;

import com.eastshine.auction.user.application.SellerService;
import com.eastshine.auction.user.domain.UserMapper;
import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.web.dto.SellerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/seller-api/users")
public class SellerController {
    private final SellerService sellerService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity signUpSeller(@RequestBody @Validated SellerDto.Signup request) {
        Seller requestSignup = userMapper.of(request);

        Seller signedUpSeller = sellerService.signUpSeller(requestSignup);

        return ResponseEntity.created(URI.create("/seller-api/users/" + signedUpSeller.getId())).build();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public SellerDto.Info getSeller(@PathVariable Long id) {
        return sellerService.findSellerInfo(id);
    }
}
