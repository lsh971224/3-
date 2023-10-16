package com.kr.assignment.file.service;

import com.kr.assignment.file.fileDto.FileDto;
import org.springframework.transaction.annotation.Transactional;

public interface FileService {
    @Transactional
    void saveFile(String fileName) throws Exception;

    FileDto getFile(Long fileId);
}
