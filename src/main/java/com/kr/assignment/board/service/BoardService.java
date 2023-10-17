package com.kr.assignment.board.service;

import com.kr.assignment.board.dto.BoardDto;
import com.kr.assignment.config.SearchDTO;
import com.kr.assignment.file.fileDto.FileDto;
import com.kr.assignment.member.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    Page<BoardDto> selectAll(Pageable pageable);

    @Transactional
    int insertBoard(BoardDto boardDto, MemberDto memberdto, String fileName) throws Exception;

    @Transactional
    void updateBoardCnt(Long id) throws Exception;

    BoardDto selectBoardDto(Long id);

    @Transactional
    String deleteBoard(Long boardId) throws Exception;

    @Transactional
    String modifiedBoard(BoardDto boardDto, FileDto fileDto, MultipartFile uploadFile) throws Exception;

    @Transactional
    void updateFileDownCnt(String fileName);

    String makeFile(MultipartFile file) throws IllegalStateException, IOException;

    Page<BoardDto> searchAll(Pageable pageable, SearchDTO searchDTO);

    List<BoardDto> csvSelectAll();

    Long selectBoardWriter(String name);

    void csvInsertBoard(BoardDto boardDTO) throws Exception;

    void excelinsertBoard(BoardDto data);

    List<BoardDto> selectReportAll();


    BoardDto reportBoardList(Long boardId);
}
