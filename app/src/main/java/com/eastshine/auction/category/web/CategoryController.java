package com.eastshine.auction.category.web;

import com.eastshine.auction.category.application.CategoryService;
import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.category.web.dto.CategoryRegistrationRequest;
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
        return ResponseEntity.created(URI.create("/users/" + registeredCategory.getId())).build();
    }
}
