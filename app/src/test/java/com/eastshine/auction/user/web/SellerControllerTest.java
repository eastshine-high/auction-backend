package com.eastshine.auction.user.web;

import com.eastshine.auction.common.test.RestDocsTest;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.web.dto.SellerDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SellerControllerTest extends RestDocsTest {
    @Autowired UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void signUpSeller() throws Exception {
        SellerDto.Signup sellerSignupDto = SellerDto.Signup.builder()
                .nickname("닉네임")
                .email("seller2@email.com")
                .password("1Q2w3e4r%t")
                .businessNumber("1234567891")
                .build();

        mockMvc.perform(
                post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson(sellerSignupDto))
        )
                .andExpect(status().isCreated())
                .andDo(document("guest-sellers-post-201",
                        PayloadDocumentation.requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("businessNumber").description("사업자 번호")
                        )
                ));
    }
}
