package com.eastshine.auction.user.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.user.UserFactory;
import com.eastshine.auction.user.web.dto.SessionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerTest extends WebIntegrationTest {
    public static final String REGISTERED_EMAIL = "registered@naver.com";
    public static final String REGISTERED_USER_PW = "password";

    @Autowired
    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        userFactory.createUser(REGISTERED_EMAIL, REGISTERED_USER_PW);
    }

    @AfterEach
    void afterEach() {
        userFactory.deleteAllUser();
    }

    @Nested
    class login메서드는 {

        @Test
        @DisplayName("유효한 사용자 정보로 로그인 했을 경우, created를 응답한다.")
        void loginWithValidUserInfo() throws Exception {
            SessionDto.Request sessionRequestDto = SessionDto.Request.builder()
                    .email(REGISTERED_EMAIL)
                    .password(REGISTERED_USER_PW)
                    .build();

            mockMvc.perform(
                            post("/api/session")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(sessionRequestDto))
                    )
                    .andExpect(status().isCreated())
                    .andDo(
                            document("guest-session-post-201",
                                    requestFields(
                                            fieldWithPath("email").description("사용자 이메일"),
                                            fieldWithPath("password").description("비밀번호")
                                    )
                            )
                    );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"invalid@email.com", "   "})
        @DisplayName("유효하지 못한 사용자 정보로 로그인 했을 경우, badRequest를 응답한다.")
        void loginWithInvalidUserInfo(String invalidEmail) throws Exception {
            SessionDto.Request sessionRequestDto = SessionDto.Request.builder()
                    .email(invalidEmail)
                    .password(REGISTERED_USER_PW)
                    .build();

            mockMvc.perform(
                            post("/api/session")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(sessionRequestDto))
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(document("guest-session-post-400"));
        }
    }
}
