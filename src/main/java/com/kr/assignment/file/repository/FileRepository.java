package com.kr.assignment.file.repository;

import com.kr.assignment.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {
    File findByFileName(String fileName);
}
