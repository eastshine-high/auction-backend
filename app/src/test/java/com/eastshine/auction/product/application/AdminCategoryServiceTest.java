package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.product.domain.category.Category;
import com.eastshine.auction.product.repository.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
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

class AdminCategoryServiceTest extends IntegrationTest {
    private static final int EXIST_PARENT_CATEGORY_ID = 641;
    private static final int EXIST_CATEGORY_ID = 649;

    @Autowired
    AdminCategoryService adminCategoryService;
    @Autowired CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        Category parentCategory = Category.builder()
                .id(EXIST_PARENT_CATEGORY_ID)
                .ordering(1)
                .name("부모 카테고리")
                .build();
        categoryRepository.save(parentCategory);

        Category category = Category.builder()
                .id(EXIST_CATEGORY_ID)
                .ordering(1)
                .name("카테고리")
                .build();
        categoryRepository.save(category);
    }

    @Nested
    @DisplayName("registerCategory 메소드는")
    class Describe_registerCategory{
        AdminCategoryDto.Request adminCategoryRegistrationRequest;

        @Nested
        @DisplayName("부모 카테고리가 존재하거나 null일 경우,")
        class Context_with_valid_parent_id{
            int categoryId = 999;
            int ordering = 1;
            String name = "카테고리명";

            @DisplayName("등록된 카테고리를 반환한다.")
            @ParameterizedTest
            @NullSource
            @ValueSource(ints = {EXIST_PARENT_CATEGORY_ID})
            void it_returns_registered_category(Integer existParentId) {
                adminCategoryRegistrationRequest = AdminCategoryDto.Request.builder()
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
                adminCategoryRegistrationRequest = AdminCategoryDto.Request.builder()
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

    @Nested
    @DisplayName("getCategory 메소드는")
    class Describe_getCategory{

        @Test
        @DisplayName("식별자에 해당하는 카테고리 정보를 반환한다.")
        void it_returns_the_category() {
            Category category = adminCategoryService.getCategory(EXIST_PARENT_CATEGORY_ID);

            assertThat(category.getId()).isEqualTo(EXIST_PARENT_CATEGORY_ID);
        }

        @Test
        @DisplayName("식별자에 해당하는 카테고리가 없을 경우, EntityNotFoundException을 던진다.")
        void it_throws_EntityNotFoundException() {
            assertThatThrownBy(() -> adminCategoryService.getCategory(-1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(ErrorCode.CATEGORY_ENTITY_NOT_FOUND.getErrorMsg());
        }
    }

    @Nested
    @DisplayName("modifyCategory 메소드는")
    class Describe_modifyCategory{

        @ParameterizedTest
        @DisplayName("카테고리를 수정한다.")
        @NullSource
        @ValueSource(ints = {EXIST_PARENT_CATEGORY_ID})
        void it_modifies_category(Integer parentId) {
            //given
            int newId = 878;
            Integer newParentId = null; //null 사용에 대해서는 고민해볼 필요가 있다.
            String newName = "새로운 카테고리";
            int newOrdering = 3;

            AdminCategoryDto.Request request = AdminCategoryDto.Request.builder()
                    .id(878)
                    .name(newName)
                    .ordering(newOrdering)
                    .parentId(newParentId)
                    .build();

            //when
            Category category = adminCategoryService.modifyCategory(EXIST_CATEGORY_ID, request);

            //then
            assertThat(category.getId()).isEqualTo(newId);
            assertThat(category.getName()).isEqualTo(newName);
            assertThat(category.getOrdering()).isEqualTo(newOrdering);
        }

        @Test
        @DisplayName("수정할 카테고리 정보 중에서 부모 카테고리의 식별자가 존재하지 않을 경우, EntityNotFoundException을 던진다.")
        void it_throws_EntityNotFoundException() {
            //given
            Integer inValidParentId = -1;
            AdminCategoryDto.Request request = AdminCategoryDto.Request.builder()
                    .id(878)
                    .name("새로운 카테고리")
                    .ordering(3)
                    .parentId(inValidParentId)
                    .build();

            //when, then
            assertThatThrownBy(() -> adminCategoryService.modifyCategory(EXIST_CATEGORY_ID, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND.getErrorMsg());
        }
    }

    @Nested
    @DisplayName("deleteCategory 메소드는")
    class Describe_deleteCategory{

        @Test
        @DisplayName("식별자에 해당하는 카테고리 정보를 삭제한다.")
        void it_returns_the_category() {
            adminCategoryService.deleteCategory(EXIST_CATEGORY_ID);

            assertThat(categoryRepository.findById(EXIST_CATEGORY_ID).isEmpty()).isTrue();
        }

        @Test
        @DisplayName("식별자에 해당하는 카테고리가 없을 경우, EntityNotFoundException을 던진다.")
        void it_throws_EntityNotFoundException() {
            assertThatThrownBy(() -> adminCategoryService.deleteCategory(-1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage(ErrorCode.CATEGORY_ENTITY_NOT_FOUND.getErrorMsg());
        }
    }
}
