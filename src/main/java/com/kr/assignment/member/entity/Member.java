package com.kr.assignment.member.entity;


import com.kr.assignment.board.entity.Board;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="member2")
@Getter@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long member_id;
    @NonNull
    private String email;
    @NonNull
    private String pass;
    @NonNull
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;


}
