package com.kr.assignment.board.dto;


import com.kr.assignment.report.dto.ReportDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime regdate;
    private String name;
    private Long memberId;
    private int board_cnt;
    private Long fileId;
    private int board_report;

    private Map<String,Integer> reportType;

}
