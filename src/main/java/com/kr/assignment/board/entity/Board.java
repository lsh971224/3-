package com.kr.assignment.board.entity;

import com.kr.assignment.file.entity.File;
import com.kr.assignment.member.entity.Member;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
@Table(name="board2")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    private String content;

    private String name;

    @Column(name = "regdate")
    private LocalDateTime regdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int board_cnt;

    @ManyToOne(fetch = FetchType.LAZY)
    private File file;

    @CreationTimestamp
    public void setRegdate(LocalDateTime regdate) { // 위에 regdate에 걸어두면 값을 재할당을 못한다.
        if (this.regdate == null) {
            this.regdate = regdate;
        }
    }
}