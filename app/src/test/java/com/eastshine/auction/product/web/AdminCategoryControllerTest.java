package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
import com.eastshine.auction.user.WithAdmin;
import com.eastshine.auction.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCategoryControllerTest extends WebIntegrationTest {
    private static final int REGISTERED_CATEGORY_ID = 300;

    @Autowired UserRepository userRepository;
    @Autowired CategoryFactory categoryFactory;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        userRepository.deleteAll();

        categoryFactory.createCategory(REGISTERED_CATEGORY_ID, "식음료");
    }

    @Nested
    @DisplayName("registerCategory 메소드는")
    class Describe_registerCategory{

        @Nested
        @DisplayName("유효한 카테고리 정보로 등록했을 경우")
        class Context_with_valid_CategoryRegistrationDto{
            AdminCategoryDto.Request validRequest = AdminCategoryDto.Request.builder()
                    .id(101)
                    .name("패션/뷰티")
                    .ordering(1)
                    .build();

            @Test
            @WithAdmin("adminNickname")
            @DisplayName("created 상태를 응답한다.")
            void it_returns_created_status() throws Exception {
                mockMvc.perform(
                                post("/admin-api/v1/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validRequest))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("location"))
                        .andDo(document("admin-categories-post-201",
                                requestFields(
                                        fieldWithPath("id").description("카테고리 식별자"),
                                        fieldWithPath("parentId").description("상위 카테고리 식별자").type(JsonFieldType.NUMBER).optional(),
                                        fieldWithPath("ordering").description("정렬 순서"),
                                        fieldWithPath("name").description("카테고리 이름")
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("유효하지 못한 카테고리 정보로 등록할 경우")
        class Context_with_invalid_dto{
            AdminCategoryDto.Request invalidRequest = AdminCategoryDto.Request.builder()
                    .id(12345)
                    .build();

            @Test
            @WithAdmin("adminNickname")
            @DisplayName("badRequest 상태를 응답한다.")
            void it_returns_badRequest_status() throws Exception {
                mockMvc.perform(
                                post("/admin-api/v1/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("admin-categories-post-400"));
            }
        }

        @Nested
        @DisplayName("권한이 없는 요청일 경우")
        class Context_with_unauthorized_request{
            AdminCategoryDto.Request validRequest = AdminCategoryDto.Request.builder()
                    .id(101)
                    .name("패션/뷰티")
                    .ordering(1)
                    .build();

            @DisplayName("unauthorized 상태를 응답한다.")
            @Test
            void it_returns_unauthorized_status() throws Exception {
                mockMvc.perform(
                                post("/admin-api/v1/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .content(objectMapper.writeValueAsString(validRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("admin-categories-post-401"));
            }
        }
    }

    @Nested
    @DisplayName("getCategory 메소드는")
    class Describe_getCategory{

        @Test
        @WithAdmin("adminNickname")
        @DisplayName("유효한 권한과 함께 조회 요청할 경우, ok 상태를 응답한다.")
        void it_returns_ok_status() throws Exception {
            mockMvc.perform(
                            get("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(document("admin-categories-id-get-200"));
        }

        @Test
        @DisplayName("권한이 없는 요청일 경우, unauthorized 상태를 응답한다.")
        void it_returns_unauthorized_status() throws Exception {
            mockMvc.perform(
                            get("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", ACCESS_TOKEN)
                    )
                    .andExpect(status().isUnauthorized())
                    .andDo(document("admin-categories-id-get-401"));
        }
    }

    @Nested
    @DisplayName("putCategory 메소드는")
    class Describe_putCategory{

        @Test
        @WithAdmin("adminNickname")
        @DisplayName("유효한 카테고리 정보로 수정할 경우, ok 상태를 응답한다.")
        void it_returns_ok_status() throws Exception {
            AdminCategoryDto.Request validRequest = AdminCategoryDto.Request.builder()
                    .id(101)
                    .name("패션/뷰티")
                    .ordering(1)
                    .build();

            mockMvc.perform(
                            put("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(validRequest))
                    )
                    .andExpect(status().isOk())
                    .andDo(document("admin-categories-id-put-200",
                            requestFields(
                                    fieldWithPath("id").description("카테고리 식별자"),
                                    fieldWithPath("parentId").description("상위 카테고리 식별자").type(JsonFieldType.NUMBER).optional(),
                                    fieldWithPath("ordering").description("정렬 순서"),
                                    fieldWithPath("name").description("카테고리 이름")
                            )
                    ));
        }

        @Test
        @WithAdmin("adminNickname")
        @DisplayName("수정 요청 정보가 유효하지 못한 경우, badRequest 상태를 응답한다.")
        void it_returns_badRequest_status() throws Exception {
            AdminCategoryDto.Request invalidRequest = AdminCategoryDto.Request.builder()
                    .id(12345)
                    .build();

            mockMvc.perform(
                            put("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest))
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(document("admin-categories-id-put-400"));
        }

        @Test
        @DisplayName("권한이 없는 요청일 경우, unauthorized 상태를 응답한다.")
        void it_returns_unauthorized_status() throws Exception {
            AdminCategoryDto.Request validRequest = AdminCategoryDto.Request.builder()
                    .id(101)
                    .name("패션/뷰티")
                    .ordering(1)
                    .build();

            mockMvc.perform(
                            put("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", ACCESS_TOKEN)
                                    .content(objectMapper.writeValueAsString(validRequest))
                    )
                    .andExpect(status().isUnauthorized())
                    .andDo(document("admin-categories-id-put-401"));
        }
    }

    @Nested
    @DisplayName("deleteCategory 메소드는")
    class Describe_deleteCategory{

        @Test
        @WithAdmin("adminNickname")
        @DisplayName("유효한 권한과 함께 삭제 요청할 경우, ok 상태를 응답한다.")
        void it_returns_ok_status() throws Exception {
            mockMvc.perform(
                            get("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(document("admin-categories-id-delete-200"));
        }

        @Test
        @DisplayName("권한이 없는 요청일 경우, unauthorized 상태를 응답한다.")
        void it_returns_unauthorized_status() throws Exception {
            mockMvc.perform(
                            get("/admin-api/v1/categories/" + REGISTERED_CATEGORY_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", ACCESS_TOKEN)
                    )
                    .andExpect(status().isUnauthorized())
                    .andDo(document("admin-categories-id-delete-401"));
        }
    }
}
