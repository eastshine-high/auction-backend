package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.AdminCategoryService;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RestController
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping("/admin-api/categories")
    public ResponseEntity registerCategory(@RequestBody @Validated AdminCategoryDto.RegistrationRequest registrationRequest) {
        Category registeredCategory = adminCategoryService.registerCategory(registrationRequest);
        return ResponseEntity.created(URI.create("/admin-api/categories/" + registeredCategory.getId())).build();
    }
}
