package com.kr.assignment.config;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {

    private String searchType; // 셀렉박스
    private String searchContent; // 내용
}
