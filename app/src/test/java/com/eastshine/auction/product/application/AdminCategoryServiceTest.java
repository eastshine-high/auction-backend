package com.eastshine.auction.product.application;

import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.domain.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryRegistrationRequest;
import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.test.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminCategoryServiceTest extends IntegrationTest{
    private static final int EXIST_PARENT_ID = 641;

    @Autowired
    AdminCategoryService adminCategoryService;
    @Autowired CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        Category category = Category.builder()
                .id(EXIST_PARENT_ID)
                .ordering(1)
                .name("부모 카테고리")
                .build();
        categoryRepository.save(category);
    }

    @Nested
    @DisplayName("registerCategory 메소드는")
    class Describe_registerCategory{
        AdminCategoryRegistrationRequest adminCategoryRegistrationRequest;

        @Nested
        @DisplayName("부모 카테고리가 null이거나 존재하는 경우,")
        class Context_with_valid_parent_id{
            int categoryId = 999;
            int ordering = 1;
            String name = "카테고리명";

            @DisplayName("등록된 카테고리를 반환한다.")
            @ParameterizedTest
            @NullSource
            @ValueSource(ints = {EXIST_PARENT_ID})
            void it_returns_registered_category(Integer existParentId) {
                adminCategoryRegistrationRequest = AdminCategoryRegistrationRequest.builder()
                        .id(categoryId)
                        .parentId(existParentId)
                        .ordering(ordering)
                        .name(name)
                        .build();

                Category category = adminCategoryService.registerCategory(adminCategoryRegistrationRequest);

                assertThat(category.getId()).isEqualTo(categoryId);
                assertThat(category.getName()).isEqualTo(name);
                assertThat(category.getOrdering()).isEqualTo(ordering);
            }
        }

        @Nested
        @DisplayName("부모 카테고리가 null이 아니면서 존재하지 않는 경우")
        class Context_with_not_exist_parent_id{
            @BeforeEach
            void setUp() {
                adminCategoryRegistrationRequest = AdminCategoryRegistrationRequest.builder()
                        .id(999)
                        .parentId(99999)
                        .ordering(1)
                        .name("카테고리명")
                        .build();
            }

            @DisplayName("EntityNotFoundException을 던진다.")
            @Test
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(
                        () -> adminCategoryService.registerCategory(adminCategoryRegistrationRequest)
                )
                        .isInstanceOf(EntityNotFoundException.class);
            }
        }
    }
}
