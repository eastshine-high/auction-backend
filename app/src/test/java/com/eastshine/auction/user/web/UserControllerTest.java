package com.eastshine.auction.user.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.common.utils.JwtUtil;
import com.eastshine.auction.user.domain.User;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.web.dto.UserDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends WebIntegrationTest {
    private static Long registeredUserId;
    private static String userAuthentication;

    @Autowired UserRepository userRepository;
    @Autowired JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("nickname@email.com")
                .nickname("nickname")
                .password("nickname")
                .build();
        userRepository.save(user);
        registeredUserId = user.getId();
        userAuthentication = jwtUtil.encode(new UserInfo(user));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("signUpUser 메소드는")
    class Describe_signUpUser {

        @Nested
        class 유효한_정보를_통해_회원가입을_요청할_경우{
            private String nickname = "nickname2";
            private String validEmail = "test@eamil.com";
            private String password = "test1234";

            @Test
            void 상태코드_201_Created_를_응답한다() throws Exception {
                UserDto.Signup requestSignup = UserDto.Signup.builder()
                        .nickname(nickname)
                        .email(validEmail)
                        .password(password)
                        .build();

                mockMvc.perform(
                                post("/user-api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("location"))
                        .andDo(document("user-users-post-201",
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
                UserDto.Signup requestSignup = UserDto.Signup.builder()
                        .nickname("validNickname")
                        .email(invalidEmail)
                        .password("validPassword")
                        .build();

                mockMvc.perform(
                                post("/user-api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("user-users-post-400"));
            }
        }
    }

    @Test
    void getUser() throws Exception {

        mockMvc.perform(
                        get("/user-api/users/" + registeredUserId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.nickname").exists())
                .andDo(document("user-users-get-200"));
    }

    @Nested
    @DisplayName("patchNickname 메소드는")
    class Describe_patchNickname {

        @Nested
        class 인증되지_않은_사용자의_요청일_경우{

            @Test
            void 상태코드_401_Unauthorized를_응답한다() throws Exception {
                mockMvc.perform(
                                patch("/user-api/users/" + registeredUserId + "/nickname")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("user-users-nickname-patch-401"));
            }
        }

        @Nested
        class 유효한_인증_정보를_통해_닉네임_변경을_요청할_경우{

            @Test
            void 상태코드_200을_응답한다() throws Exception {
                UserDto.PatchNickname patchNickname = new UserDto.PatchNickname("newNickname");

                mockMvc.perform(
                                patch("/user-api/users/" + registeredUserId + "/nickname")
                                        .header("Authorization", "Bearer " + userAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson(patchNickname))
                        )
                        .andExpect(status().isOk())
                        .andDo(document("user-users-nickname-patch-200"));
            }
        }
    }

    @Nested
    @DisplayName("deleteUser 메소드는")
    class Describe_deleteUser {

        @Nested
        class 인증되지_않은_사용자의_요청일_경우{

            @Test
            void 상태코드_401_Unauthorized를_응답한다() throws Exception {
                mockMvc.perform(
                                delete("/user-api/users/" + registeredUserId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("user-users-delete-401"));
            }
        }

        @Nested
        class 유효한_인증_정보를_통해_삭제를_요청할_경우{

            @Test
            void 상태코드_200을_응답한다() throws Exception {

                mockMvc.perform(
                                delete("/user-api/users/" + registeredUserId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + userAuthentication)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("user-users-delete-200"));
            }
        }
    }
}
