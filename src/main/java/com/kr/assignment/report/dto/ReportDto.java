package com.kr.assignment.report.dto;


import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ReportDto {

    private Long report_Id;
    private String reportContent;
    private Long board_Id;



}
