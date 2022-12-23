package com.eastshine.auction.product.web;

import com.eastshine.auction.product.application.AdminCategoryService;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<Category> registerCategory(@RequestBody @Validated AdminCategoryDto.Request registrationRequest) {
        Category registeredCategory = adminCategoryService.registerCategory(registrationRequest);
        return ResponseEntity
                .created(URI.create("/admin-api/v1/categories/" + registeredCategory.getId()))
                .body(registeredCategory);
    }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Integer id) {
        return adminCategoryService.getCategory(id);
    }

    @PutMapping("/categories/{id}")
    public Category putCategory(@PathVariable Integer id, @RequestBody @Validated AdminCategoryDto.Request request) {
        return adminCategoryService.modifyCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        adminCategoryService.deleteCategory(id);
    }
}
