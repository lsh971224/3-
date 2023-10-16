package com.kr.assignment.member.service;

import com.kr.assignment.member.dto.MemberDto;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {
    @Transactional
    String insertMember(MemberDto memberDto) throws Exception;

    String checkEmail(String email) throws Exception;

    MemberDto loginCheck(MemberDto vo) throws Exception;

    MemberDto selectMemberDto(String email) throws Exception;
}
