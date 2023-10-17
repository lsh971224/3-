package com.kr.assignment.member.dto;


import com.kr.assignment.member.entity.Role;
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

    private String role;
}
