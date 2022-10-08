package com.eastshine.auction.user.web;

import com.eastshine.auction.common.test.WebIntegrationTest;
import com.eastshine.auction.user.application.AuthenticationService;
import com.eastshine.auction.user.application.SellerService;
import com.eastshine.auction.user.domain.UserRepository;
import com.eastshine.auction.user.domain.seller.Seller;
import com.eastshine.auction.user.web.dto.SellerDto;
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

class SellerControllerTest extends WebIntegrationTest {
    private static Long registeredSellerId;
    private static String registeredSellerAuthentication;

    @Autowired UserRepository userRepository;
    @Autowired SellerService sellerService;
    @Autowired AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        String nickname = "nickname";
        String email = nickname + "@email.com";

        Seller seller = Seller.sellerBuilder()
                .email(email)
                .nickname(nickname)
                .password(nickname)
                .businessNumber("1234567890")
                .build();

        sellerService.signUpSeller(seller);
        registeredSellerId = seller.getId();
        registeredSellerAuthentication = authenticationService.login(email, nickname);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("signUpSeller 메소드는")
    class Describe_signUpSeller {

        @Nested
        class 유효한_정보를_통해_회원가입을_요청할_경우{
            private String nickname = "newNickname";
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
                        .andDo(document("seller-users-post-400"));
            }
        }
    }

    @Test
    void getSeller() throws Exception {
        mockMvc.perform(
                        get("/seller-api/users/" + registeredSellerId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.nickname").exists())
                .andExpect(jsonPath("$.businessNumber").exists())
                .andExpect(jsonPath("$.sellerLevel").exists())
                .andDo(document("seller-users-id-get-200"));
    }


    @Nested
    @DisplayName("patchNickname 메소드는")
    class Describe_patchNickname {

        @Nested
        class 인증되지_않은_사용자의_요청일_경우{

            @Test
            void 상태코드_401_Unauthorized를_응답한다() throws Exception {
                mockMvc.perform(
                                patch("/user-api/users/" + registeredSellerId + "/nickname")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-users-id-nickname-patch-401"));
            }
        }

        @Nested
        class 유효한_인증_정보를_통해_닉네임_변경을_요청할_경우{

            @Test
            void 상태코드_200을_응답한다() throws Exception {
                UserDto.PatchNickname patchNickname = new UserDto.PatchNickname("newNickname");

                mockMvc.perform(
                                patch("/user-api/users/" + registeredSellerId + "/nickname")
                                        .header("Authorization", "Bearer " + registeredSellerAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson(patchNickname))
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-users-id-nickname-patch-200"));
            }
        }
    }

    @Nested
    @DisplayName("deleteSeller 메소드는")
    class Describe_deleteSeller {

        @Nested
        class 인증되지_않은_판매자의_요청일_경우{

            @Test
            void 상태코드_400_Unauthorized를_응답한다() throws Exception {
                mockMvc.perform(
                                delete("/seller-api/users/" + registeredSellerId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", ACCESS_TOKEN)
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(document("seller-users-id-delete-401"));
            }
        }

        @Nested
        class 유효한_인증_정보를_통해_삭제를_요청할_경우{

            @Test
            void 상태코드_200을_응답한다() throws Exception {

                mockMvc.perform(
                                delete("/seller-api/users/" + registeredSellerId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + registeredSellerAuthentication)
                        )
                        .andExpect(status().isOk())
                        .andDo(document("seller-users-id-delete-200"));
            }
        }
    }
}
