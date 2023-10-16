package com.kr.assignment.member.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long member_id;
    private String email;
    private String pass;
    private String name;
    private boolean remember_id;
}
