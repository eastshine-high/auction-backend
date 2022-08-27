package com.eastshine.auction.user.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.test.RestDocsTest;
import com.eastshine.auction.common.utils.JwtUtil;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.web.dto.UserSignupDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends RestDocsTest {
    private static Long registeredUserId;
    private static String registeredUserAuthentication;

    @Autowired UserRepository userRepository;
    @Autowired JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("nickname@email.com")
                .nickname("nickname")
                .password("nickname")
                .build();
        userRepository.save(user);
        registeredUserId = user.getId();
        registeredUserAuthentication = jwtUtil.encode(new UserInfo(user));
    }

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
                        .andDo(document("post-users-201",
                                requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("nickname").description("사용자 닉네임"),
                                        fieldWithPath("password").description("비밀번호")
                                )
                        ));
            }
        }

        @Nested
        class 유효하지_못한_이메일_정보를_통해_회원가입을_요청할_경우{

            @ParameterizedTest
            @ValueSource(strings = {"invalidEmail"})
            @NullAndEmptySource
            void 상태코드_400_BadRequest_를_응답한다(String invalidEmail) throws Exception {
                UserSignupDto requestSignup = UserSignupDto.builder()
                        .nickname("validNickname")
                        .email(invalidEmail)
                        .password("validPassword")
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

    @Nested
    @DisplayName("deleteUser 메소드는")
    class Describe_deleteUser {

        @Nested
        class 인증되지_않은_사용자의_요청일_경우{

            @Test
            void 상태코드_400_Unauthorized를_응답한다() throws Exception {
                mockMvc.perform(
                                delete("/api/users/" + registeredUserId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + INVALID_AUTHENTICATION)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("user-delete-users-401"));
            }
        }

        @Nested
        class 유효한_인증_정보를_통해_삭제를_요청할_경우{

            @Test
            void 상태코드_200을_응답한다() throws Exception {

                mockMvc.perform(
                                delete("/api/users/" + registeredUserId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + registeredUserAuthentication)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("user-delete-users-200"));
            }
        }
    }
}
