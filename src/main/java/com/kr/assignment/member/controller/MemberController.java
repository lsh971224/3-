package com.kr.assignment.member.controller;


import com.kr.assignment.member.dto.MemberDto;
import com.kr.assignment.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/createMember.do")
    public String newMember(){
        return "member/createmember";
    }

    @GetMapping("/memberMain.do")
    public String mainMember(){
        return "member/main";
    }
    @PostMapping("/saveMember.do")
    public ResponseEntity<String> saveMember(MemberDto memberDto) throws Exception{
        log.info("member.getName=="+memberDto.getName());
        String result = memberService.insertMember(memberDto);
        log.info("result==="+result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checkEmail.do")
    public ResponseEntity<String> emailCheck(@RequestParam("email") String email) throws Exception {
        log.info("email==="+email);
        String m = memberService.checkEmail(email);
        log.info(m);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/loginMember.do")
    public String loginPage() {
            return "member/login";
    }
    // 아작스 로그인 처리
    @PostMapping("/loginCheck.do")
    public ResponseEntity<String> loginCheck(HttpServletResponse response, MemberDto vo, HttpSession session, Model model) throws Exception {
        MemberDto loginResult = memberService.loginCheck(vo);
        log.info("결과는="+loginResult);
        if(loginResult!=null){
            session.setAttribute("id",loginResult.getMember_id());
            session.setAttribute("name", loginResult.getName());
            session.setAttribute("email", loginResult.getEmail());
            session.setAttribute("role",loginResult.getRole());
            log.info("세션의 저장된 role ="+session.getAttribute("role"));
            Cookie cookie;
            if(vo.isRemember_id()){
                cookie = new Cookie("id", loginResult.getEmail());
            } else {
                cookie = new Cookie("id", null);
                cookie.setMaxAge(0);
            }
            response.addCookie(cookie);
            return ResponseEntity.ok("성공");
        }else{
            return ResponseEntity.ok("실패");
        }

    }

    @GetMapping("/memberLogout.do")
    public String logout(HttpSession session) {
        session.removeAttribute("name");
        session.removeAttribute("email");
        session.removeAttribute("id");
        session.removeAttribute("role");
        return "member/login";
    }
}
