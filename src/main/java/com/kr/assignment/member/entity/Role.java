package com.kr.assignment.member.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER"),ADMIN("ROLE_ADMIN");
    private final String value;

    public static Role getRoleFromString(String role){
        if(role.equalsIgnoreCase("admin"))return Role.ADMIN;
        else return Role.USER;
    }
}
