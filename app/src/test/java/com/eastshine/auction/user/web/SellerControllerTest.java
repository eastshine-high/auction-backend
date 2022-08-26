package com.eastshine.auction.user.web;

import com.eastshine.auction.common.test.RestDocsTest;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.domain.role.RoleRepository;
import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.domain.seller.SellerLevelType;
import com.eastshine.auction.user.web.dto.SellerDto;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SellerControllerTest extends RestDocsTest {
    private static Long registeredSellerId;

    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Seller seller = Seller.sellerBuilder()
                .email("seller@email.com")
                .nickname("seller")
                .password("seller")
                .businessNumber("1234567890")
                .build();
        userRepository.save(seller);
        ReflectionTestUtils.setField(seller, "sellerLevel", SellerLevelType.NEW);
        registeredSellerId = seller.getId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Nested
    @DisplayName("signUpUser 메소드는")
    class Describe_signUpUser {

        @Nested
        class 유효한_정보를_통해_회원가입을_요청할_경우{
            private String nickname = "nickname";
            private String validEmail = "test@eamil.com";
            private String password = "test1234";
            private String businessNumber = "1234567897";

            @Test
            void 상태코드_201_Created_를_응답한다() throws Exception {
                SellerDto.Signup requestSignup = SellerDto.Signup.builder()
                        .nickname(nickname)
                        .email(validEmail)
                        .password(password)
                        .businessNumber(businessNumber)
                        .build();

                mockMvc.perform(
                                post("/seller-api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestSignup))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("location"))
                        .andDo(document("seller-users-post-201",
                                requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("nickname").description("사용자 닉네임"),
                                        fieldWithPath("password").description("비밀번호"),
                                        fieldWithPath("businessNumber").description("사업자 번호")
                                )
                        ));
            }
        }

        @Nested
        class 유효하지_못한_사업자_번호를_통해_회원가입을_요청할_경우{

            @Test
            void signUpSeller() throws Exception {
                String invalidBusinessNumber = "0102323";
                SellerDto.Signup sellerSignupDto = SellerDto.Signup.builder()
                        .nickname("닉네임")
                        .email("seller2@email.com")
                        .password("1Q2w3e4r%t")
                        .businessNumber(invalidBusinessNumber)
                        .build();

                mockMvc.perform(
                                post("/seller-api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson(sellerSignupDto))
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        class 유효하지_못한_이메일_정보를_통해_회원가입을_요청할_경우{

            @ParameterizedTest
            @ValueSource(strings = {"invalidEmail"})
            @NullAndEmptySource
            void 상태코드_400_BadRequest_를_응답한다(String invalidEmail) throws Exception {
                SellerDto.Signup sellerSignupDto = SellerDto.Signup.builder()
                        .nickname("validNickname")
                        .email(invalidEmail)
                        .password("validPassword")
                        .businessNumber("1234567897")
                        .build();

                mockMvc.perform(
                                post("/seller-api/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(sellerSignupDto))
                        )
                        .andExpect(status().isBadRequest())
                        .andDo(document("post-users-400"));
            }
        }
    }

    @Test
    void getSeller() throws Exception {
        SellerLevelType.values();
        SellerLevelType.NEW.name();
        mockMvc.perform(
                        get("/seller-api/users/" + registeredSellerId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.nickname").exists())
                .andExpect(jsonPath("$.businessNumber").exists())
                .andExpect(jsonPath("$.sellerLevel").exists())
                .andDo(document("seller-users-get-200"));
    }
}