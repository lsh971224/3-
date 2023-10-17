package com.kr.assignment.admin;


import com.kr.assignment.board.dto.BoardDto;
import com.kr.assignment.board.service.BoardService;
import com.kr.assignment.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final BoardService boardService;
    @GetMapping("/adminCare.do")
    public String adminPage(Model model, HttpSession session){
        List<BoardDto> reportList = boardService.selectReportAll();
        model.addAttribute("reportList",reportList);

        String user_role = (String)session.getAttribute("role");
        if(user_role==null||!user_role.equals("ROLE_ADMIN")){
            return "/access/block";
        }
        return "/board/arc";
    }

    @GetMapping("/boardReportDetail")
    public String boardReportDetail(@RequestParam("boardId")Long boardId,Model model){
        // 내가 여기에 전달해줄거는 그 게시판에 해당하는 신고 개수들
        // 1. 게시판에 신고 갯수들을 받아와야 돼
        BoardDto boardDto = boardService.reportBoardList(boardId);
        model.addAttribute("result",boardDto);
        return null;
    }


}
