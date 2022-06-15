package com.eastshine.auction.category.web;

import com.eastshine.auction.category.application.DisplayCategoryService;
import com.eastshine.auction.category.web.dto.MainDisplayCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/display/categories")
public class DisplayCategoryController {

    private final DisplayCategoryService menuService;

    @GetMapping()
    public List<MainDisplayCategoryDto> getDisplayCategories() {
        return menuService.getDisplayCategories();
    }
}
