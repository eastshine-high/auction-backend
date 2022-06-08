package com.eastshine.auction.user.web;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.user.web.dto.UserSignupDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("signUpUser 메소드는")
    class Describe_signUpUser {

        @Nested
        class 유효한_정보를_통해_회원가입을_요청할_경우{
            private String nickname = "nickname";
            private String validEmail = "test@eamil.com";
            private String password = "test1234";

            @Test
            void 상태코드_201_Created_를_응답한다() throws Exception {
                UserSignupDto requestSignup = UserSignupDto.builder()
                        .nickname(nickname)
                        .email(validEmail)
                        .password(password)
                        .build();

                mockMvc.perform(
                                post("/api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("location"))
                        .andDo(document("post-users-201"));
            }
        }

        @Nested
        class 유효하지_못한_이메일_정보를_통해_회원가입을_요청할_경우{
            private String validNickname = "validNickname";
            private String validPassword = "test1234";

            @ParameterizedTest
            @ValueSource(strings = {"invalidEmail"})
            @NullAndEmptySource
            void 상태코드_400_BadRequest_를_응답한다(String invalidEmail) throws Exception {
                UserSignupDto requestSignup = UserSignupDto.builder()
                        .nickname(validNickname)
                        .email(invalidEmail)
                        .password(validPassword)
                        .build();

                mockMvc.perform(
                                post("/api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("post-users-400"));
            }
        }
    }
}
