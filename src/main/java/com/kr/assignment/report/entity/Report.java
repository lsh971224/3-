package com.kr.assignment.report.entity;


import com.kr.assignment.board.entity.Board;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long report_id;

    private String reportContent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
}
