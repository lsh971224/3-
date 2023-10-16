package com.kr.assignment.file.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="BOARDFILE")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String fileName;
    @Column(columnDefinition = "NUMBER(10, 0) default 0")
    private int fileDownCnt;

}
