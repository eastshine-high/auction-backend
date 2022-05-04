package com.eastshine.auction.member.application;

import com.eastshine.auction.common.test.IntegrationTest;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.member.domain.Member;
import com.eastshine.auction.member.domain.MemberRepository;
import com.eastshine.auction.member.domain.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static final String REGISTERED_EMAIL ="registered@email.com";
    private static final String REGISTERED_NICKNAME ="사용중";
    private static final String PASSWORD ="1234";
    private static Long REGISTERED_ID;

    @BeforeEach
    void setUpEach() {
        Member member = memberService.signUpMember(
                Member.builder()
                        .nickname(REGISTERED_NICKNAME)
                        .email(REGISTERED_EMAIL)
                        .password(PASSWORD)
                        .build()
        );
        REGISTERED_ID = member.getId();
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        roleRepository.deleteAll();

    }

    @Nested
    @DisplayName("회원 가입시에")
    class Describe_signUpMember {
        private Member signupInfo;

        // 닉네임 중복에 대해서도 확인해야해
        @Nested
        @DisplayName("이메일과 닉네임이 중복되지 않은 경우")
        class Context_with_not_exsisted_email{
            private String newEmail = "new@email.com";
            private String newNickname = "새로운";

            @BeforeEach
            void setUp() {
                signupInfo = Member.builder()
                        .nickname(newNickname)
                        .email(newEmail)
                        .password(PASSWORD)
                        .build();
            }

            @DisplayName("가입한 회원 정보를 반환한다.")
            @Test
            void it_returns_signedUpMember() {
                Member signedUpMember = memberService.signUpMember(signupInfo);
                signedUpMember = memberRepository.findById(signedUpMember.getId()).orElse(new Member());

                assertThat(signedUpMember.getNickname()).isEqualTo(newNickname);
                assertThat(signedUpMember.getEmail()).isEqualTo(newEmail);
                assertThat(signedUpMember.getPassword()).isEqualTo(PASSWORD);
                assertThat(signedUpMember.getStatus()).isEqualTo(Member.Status.SINGUP);
            }
        }

        @Nested
        @DisplayName("이메일이 중복된 경우")
        class Context_with_exsisted_email{

            @BeforeEach
            void setUp() {
                signupInfo = Member.builder()
                        .email(REGISTERED_EMAIL)
                        .password("other_password")
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 MEMBER_DUPLICATE_EMAIL인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                try {
                    memberService.signUpMember(signupInfo);
                }
                catch (InvalidArgumentException e) {
                    assertThat(e.getErrorCode()).isEqualTo(ErrorCode.MEMBER_DUPLICATE_EMAIL);
                }
            }
        }

        @Nested
        @DisplayName("닉네임이 중복된 경우")
        class Context_with_exsisted_nickname{
            private String newEmail = "new@email.com";

            @BeforeEach
            void setUp() {
                signupInfo = Member.builder()
                        .nickname(REGISTERED_NICKNAME)
                        .email(newEmail)
                        .password("other_password")
                        .build();
            }

            @Test
            @DisplayName("ErrorCode가 MEMBER_DUPLICATE_EMAIL인 예외를 던진다.")
            void it_throws_IllegalArgumentException() {
                try {
                    memberService.signUpMember(signupInfo);
                }
                catch (InvalidArgumentException e) {
                    assertThat(e.getErrorCode()).isEqualTo(ErrorCode.MEMBER_DUPLICATE_NICKNAME);
                }
            }
        }
    }


    // @Nested
    @DisplayName("회원 탈퇴시")
    class Describe_dropOutMember{
        Member dropoutRequest;

        @Nested
        @DisplayName("회원 식별자가 존재할 경우")
        class Context_with_exist_memberId {

            @BeforeEach
            void setUp() {
                // 이건 repository로 찾을 수 있지 않나
                dropoutRequest = Member.builder()
                        .email(REGISTERED_EMAIL) // id가 아니라 email로 가능하잖아
                        .build();
            }

            @DisplayName("회원 상태를 탈퇴로 변경한다.")
            @Test
            void it_modifies_member_status(){
                memberService.dropOutMember(dropoutRequest);

                Member member = memberRepository.findById(REGISTERED_ID).orElse(Member.builder().build());
                assertThat(member.getId()).isEqualTo(REGISTERED_ID);
                assertThat(member.getStatus()).isEqualTo(Member.Status.DROPOUT);
            }
        }

        @Nested
        @DisplayName("회원 식별자가 존재하지 않을 경우")
        class Context_with_not_exist_memberId {

            @BeforeEach
            void setUp() {
                dropoutRequest = Member.builder().build();
            }

            @DisplayName("IllegalArgumentException 예외를 던진다.")
            @Test
            void it_throws_IllegalArgumentException(){
                assertThatThrownBy(() -> memberService.dropOutMember(dropoutRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}
