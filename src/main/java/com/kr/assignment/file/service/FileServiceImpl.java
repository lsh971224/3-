package com.kr.assignment.file.service;


import com.kr.assignment.file.entity.File;
import com.kr.assignment.file.fileDto.FileDto;
import com.kr.assignment.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    @Transactional
    public void saveFile(String fileName) throws Exception{
        File file = File.builder().fileName(fileName).build();
        fileRepository.save(file);
    }

    @Override
    public FileDto getFile(Long fileId) {
        Optional<File> byId = fileRepository.findById(fileId);
        if(byId.isPresent()){
            FileDto fileDto = FileDto.builder()
                    .fileName(byId.get().getFileName())
                    .fileDownCnt(byId.get().getFileDownCnt())
                    .fileId(byId.get().getId())
                    .build();
            return fileDto;
        }
        return null;
    }
}
