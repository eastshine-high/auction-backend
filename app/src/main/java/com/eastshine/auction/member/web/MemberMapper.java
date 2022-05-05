package com.eastshine.auction.member.web;

import com.eastshine.auction.member.domain.Member;
import com.eastshine.auction.member.web.dto.MemberSignupDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    Member of(MemberSignupDto memberSignupDto);
}
