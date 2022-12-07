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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin-api/v1")
@RestController
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping("/categories")
    public ResponseEntity registerCategory(@RequestBody @Validated AdminCategoryDto.RegistrationRequest registrationRequest) {
        Category registeredCategory = adminCategoryService.registerCategory(registrationRequest);
        return ResponseEntity.created(URI.create("/admin-api/v1/categories/" + registeredCategory.getId())).build();
    }
}
