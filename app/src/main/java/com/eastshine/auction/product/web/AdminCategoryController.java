package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.AdminCategoryService;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.web.dto.AdminCategoryRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping("/admin-api/categories")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity registerCategory(@RequestBody @Validated AdminCategoryRegistrationRequest adminCategoryRegistrationRequest) {
        Category registeredCategory = adminCategoryService.registerCategory(adminCategoryRegistrationRequest);
        return ResponseEntity.created(URI.create("/admin-api/categories/" + registeredCategory.getId())).build();
    }
}