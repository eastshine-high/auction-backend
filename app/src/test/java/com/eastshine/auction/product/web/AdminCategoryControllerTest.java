package com.eastshine.auction.product.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.product.domain.category.CategoryRepository;
import com.eastshine.auction.product.web.dto.AdminCategoryDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCategoryControllerTest extends WebIntegrationTest {

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Nested
    @DisplayName("registerCategory 메소드는")
    class Describe_registerCategory{

        @Nested
        @DisplayName("유효한 카테고리 정보로 등록했을 경우")
        class Context_with_valid_CategoryRegistrationDto{
            AdminCategoryDto.RegistrationRequest validRequest = AdminCategoryDto.RegistrationRequest.builder()
                    .id(101)
                    .name("패션/뷰티")
                    .ordering(1)
                    .build();

            @Test
            @DisplayName("created 상태를 응답한다.")
            void it_returns_created_status() throws Exception {
                mockMvc.perform(
                                post("/admin-api/categories")
                                        .header("Authorization", VALID_AUTHENTICATION)
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
            AdminCategoryDto.RegistrationRequest invalidRequest = AdminCategoryDto.RegistrationRequest.builder()
                    .id(12345)
                    .build();

            @DisplayName("badRequest 상태를 응답한다.")
            @Test
            void it_returns_badRequest_status() throws Exception {
                mockMvc.perform(
                                post("/admin-api/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", VALID_AUTHENTICATION)
                                        .content(objectMapper.writeValueAsString(invalidRequest))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("admin-categories-post-400"));
            }
        }

        @Nested
        @DisplayName("권한이 없는 요청일 경우")
        class Context_with_unauthorized_request{
            AdminCategoryDto.RegistrationRequest validRequest = AdminCategoryDto.RegistrationRequest.builder()
                    .id(101)
                    .name("패션/뷰티")
                    .ordering(1)
                    .build();

            @DisplayName("unauthorized 상태를 응답한다.")
            @Test
            void it_returns_badRequest_status() throws Exception {
                mockMvc.perform(
                                post("/admin-api/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                                        .content(objectMapper.writeValueAsString(validRequest))
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("admin-categories-post-401"));
            }
        }
    }
}
