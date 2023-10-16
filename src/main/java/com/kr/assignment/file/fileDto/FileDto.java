package com.kr.assignment.file.fileDto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {
    private Long fileId;
    private String fileName;
    private int fileDownCnt;
}
