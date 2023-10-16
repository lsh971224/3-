package com.kr.assignment.member.service;

import com.kr.assignment.member.dto.MemberDto;
import com.kr.assignment.member.entity.Member;
import com.kr.assignment.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public String insertMember(MemberDto memberDto) throws Exception {
        Member member =
                Member.builder().pass(memberDto.getPass())
                                .name(memberDto.getName())
                                        .email(memberDto.getEmail())
                                                .build();
        Member memberSave = memberRepository.save(member);
        if (memberSave != null) {
            return "성공";
        } else {
            return "실패";
        }
    }

    @Override
    public String checkEmail(String email) throws Exception {
        Member memberId = memberRepository.findByEmail(email);
        if (memberId == null) {
            return "성공";
        } else {
            return "실패";
        }

    }

    @Override
    public MemberDto loginCheck(MemberDto vo) throws Exception {
        Member selectMember = memberRepository.findByEmail(vo.getEmail());
        MemberDto dto = new MemberDto();
        if (selectMember != null) {
            log.info("dto id = " + vo.getEmail() + "\n entity id = " + selectMember.getEmail());
            if (vo.getEmail().equals(selectMember.getEmail()) && vo.getPass().equals(selectMember.getPass())) {
                dto.setEmail(selectMember.getEmail());
                dto.setMember_id(selectMember.getMember_id());
                dto.setName(selectMember.getName());
                return dto;
            }
        }
        dto = null;
        return dto;
    }

    @Override
    public MemberDto selectMemberDto(String email) throws Exception{
        Member member = memberRepository.findByEmail(email);
        MemberDto memberDto = MemberDto.builder().name(member.getName()).member_id(member.getMember_id()).build();
        return memberDto;
    }


}
