package com.eastshine.auction.member.web;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.member.web.dto.MemberSignupDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("signUpMember 메소드는")
    class Describe_signUpMember{

        @Nested
        class 유효한_정보를_통해_회원가입을_요청할_경우{
            private String nickname = "nickname";
            private String validEmail = "test@eamil.com";
            private String password = "test1234";

            @Test
            void 상태코드_201_Created_를_응답한다() throws Exception {
                MemberSignupDto requestSignup = MemberSignupDto.builder()
                        .nickname(nickname)
                        .email(validEmail)
                        .password(password)
                        .build();

                mockMvc.perform(
                                post("/api/members")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("location"))
                        .andDo(document("post-members-201"));
            }
        }

        @Nested
        class 유효하지_못한_정보를_통해_회원가입을_요청할_경우{
            private String nickname = "nickname";
            private String wrongEmail = "test";
            private String password = "test1234";

            @Test
            void 상태코드_400_BadRequest_를_응답한다() throws Exception {
                MemberSignupDto requestSignup = MemberSignupDto.builder()
                        .nickname(nickname)
                        .email(wrongEmail)
                        .password(password)
                        .build();

                mockMvc.perform(
                                post("/api/members")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("post-members-400"));
            }
        }
    }
}
