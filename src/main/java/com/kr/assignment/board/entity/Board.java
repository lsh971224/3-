package com.kr.assignment.board.entity;

import com.kr.assignment.file.entity.File;
import com.kr.assignment.member.entity.Member;
import com.kr.assignment.report.entity.Report;
import lombok.*;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ColumnDefault("0")
    private int board_report;

    @ManyToOne(fetch = FetchType.LAZY)
    private File file;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Report> reports = new ArrayList<>();
    @CreationTimestamp
    public void setRegdate(LocalDateTime regdate) { // 위에 regdate에 걸어두면 값을 재할당을 못한다.
        if (this.regdate == null) {
            this.regdate = regdate;
        }
    }
}