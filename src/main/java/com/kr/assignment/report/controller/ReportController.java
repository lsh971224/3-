package com.kr.assignment.report.controller;


import com.kr.assignment.report.Serivce.ReportService;
import com.kr.assignment.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ReportController {

    private final ReportService reportSerivce;


    @PostMapping("/boardReportSave.do")
    @ResponseBody
    public ModelAndView reportSave(@RequestBody ReportDto reportDto){
        log.info("boardId===="+reportDto.getBoard_Id());
        ModelAndView modelAndView = null;
        Map<String,String> reportResult  = new HashMap<>();
        String result = reportSerivce.saveBoardReport(reportDto);
        if (result != null) {
            modelAndView = createModelAndView(result);
        } else {
            modelAndView = createModelAndView("실패");
        }
        return modelAndView;
    }

    private ModelAndView createModelAndView(String resultValue) {
        Map<String, Object> reportResult = new HashMap<>();
        reportResult.put("result", resultValue);
        return new ModelAndView("jsonView", reportResult);
    }
}
