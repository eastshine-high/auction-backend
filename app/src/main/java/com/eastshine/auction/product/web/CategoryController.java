package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.CategoryService;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.web.dto.CategoryRegistrationRequest;
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
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity registerCategory(@RequestBody @Validated CategoryRegistrationRequest categoryRegistrationRequest) {
        Category registeredCategory = categoryService.registerCategory(categoryRegistrationRequest);
        return ResponseEntity.created(URI.create("/api/categories/" + registeredCategory.getId())).build();
    }
}
