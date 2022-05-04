package com.eastshine.auction.member.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.member.domain.Member;
import com.eastshine.auction.member.domain.MemberRepository;
import com.eastshine.auction.member.domain.role.Role;
import com.eastshine.auction.member.domain.role.RoleId;
import com.eastshine.auction.member.domain.role.RoleRepository;
import com.eastshine.auction.member.domain.role.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    public MemberService(MemberRepository memberRepository, RoleRepository roleRepository) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 회원 가입과 권한을 생성한 뒤, 가입된 회원 정보를 반환합니다.
     *
     * @param signupInfo 회원 가입할 정보.
     * @return 회원 가입된 정보.
     * @Throw IllegalArgumentException 1 회원 가입할 이메일 정보가 사용 중인 경우. 2 닉네임 정보가 사용 중인 경우.
     */
    @Transactional
    public Member signUpMember(Member signupInfo) {
        if(memberRepository.existsByEmail(signupInfo.getEmail())){
            throw new InvalidArgumentException(ErrorCode.MEMBER_DUPLICATE_EMAIL);
        }
        if(memberRepository.existsByNickname(signupInfo.getNickname())){
            throw new InvalidArgumentException(ErrorCode.MEMBER_DUPLICATE_NICKNAME);
        }

        Member member = memberRepository.save(signupInfo);
        Role userRole = new Role(new RoleId(member, RoleType.USER));
        roleRepository.save(userRole);
        return member;
    }

    /**
     * 회원 정보를 수정하고 수정된 회원 정보를 반환합니다.
     *
     * @param requestMod 회원 수정 요청 정보.
     * @return 수정된 회원 정보.
     */
    public Member modifyMember(Member requestMod) {
        Member member = this.getMember(requestMod.getId());

        member.modifyEmail(requestMod.getEmail());
        member.modifyPassword(requestMod.getPassword());

        return member;
    }

    /**
     * 회원 상태를 탈퇴로 변경합니다.
     *
     * @param requestDropout 회원 삭제 요청 정보.
     */
    public void dropOutMember(Member requestDropout) {
        Member member = getMember(requestDropout.getId());

        member.changeMemberStatus(Member.Status.DROPOUT);
    }

    /**
     * 식별자에 해당하는 회원 엔티티를 찾아 반환합니다.
     *
     * @param id 회원 식별자.
     * @return 회원 엔티티.
     * @Throw IllegalArgumentException 식별자에 해당하는 엔티티를 찾을 수 없는 경우.
     */
    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());
    }
}
