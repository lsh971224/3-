package com.kr.assignment.board.service;


import com.kr.assignment.board.dto.BoardDto;
import com.kr.assignment.board.entity.Board;
import com.kr.assignment.board.repository.BoardRepository;
import com.kr.assignment.config.SearchDTO;
import com.kr.assignment.file.entity.File;
import com.kr.assignment.file.fileDto.FileDto;
import com.kr.assignment.file.repository.FileRepository;
import com.kr.assignment.file.service.FileService;
import com.kr.assignment.member.dto.MemberDto;
import com.kr.assignment.member.entity.Member;
import com.kr.assignment.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    @Autowired
    private Environment environment;

    @Override
    public Page<BoardDto> selectAll(Pageable pageable) {
        Page<Board> boardList = boardRepository.findAll(pageable);
        List<BoardDto> boardDtoList = new ArrayList<>();
        for(Board board : boardList){
            BoardDto boardDtos = BoardDto.builder()
                    .board_cnt(board.getBoard_cnt())
                    .title(board.getTitle())
                    .id(board.getId())
                    .name(board.getName())
                    .regdate(board.getRegdate()).build();
            boardDtoList.add(boardDtos);
        }
        return new PageImpl<>(boardDtoList,pageable,boardList.getTotalElements());
    }

    public List<BoardDto> csvSelectAll(){
        List<Board> all = boardRepository.findAll();
        List<BoardDto> boardDtos = new ArrayList<>();
        for(Board b : all){
            BoardDto boardDto = BoardDto.builder().name(b.getMember().getName())
                    .title(b.getTitle()).content(b.getContent()).regdate(b.getRegdate()).build();
            boardDtos.add(boardDto);
        }
        return boardDtos;
    }

    @Override
    public Long selectBoardWriter(String name) {
        Member byName = memberRepository.findByName(name);
        Long member_id = 0L;
        if(byName!=null){
            member_id = byName.getMember_id();
        }
        return member_id;
    }

    @Transactional
    @Override
    public void csvInsertBoard(BoardDto boardDTO) throws Exception {
        Optional<Member> member = memberRepository.findById(boardDTO.getMemberId());
        if(member.isPresent()) {
            Board board = Board.builder().title(boardDTO.getTitle())
                            .regdate(boardDTO.getRegdate())
                            .name(boardDTO.getName())
                            .content(boardDTO.getContent())
                            .member(member.get()).build();
            boardRepository.save(board);
        }
    }

    @Override
    @Transactional
    public void excelinsertBoard(BoardDto data) {
        Member byName = memberRepository.findByName(data.getName());
        if (byName != null) {
            Board b = Board.builder().name(data.getName())
                    .member(byName)
                    .title(data.getTitle())
                    .content(data.getContent())
                    .regdate(data.getRegdate())
                    .build();
            boardRepository.save(b);
        }
    }

    @Override
    @Transactional
    public int insertBoard(BoardDto boardDto, MemberDto memberdto, String fileName) throws Exception{
        File file = null;
        if(fileName!=null) {
            file = fileRepository.findByFileName(fileName);
        }
        Optional<Member> byMember = memberRepository.findById(memberdto.getMember_id());
        Board board = Board.builder().title(boardDto.getTitle())
                    .member(byMember.get())
                    .content(boardDto.getContent())
                    .name(byMember.get().getName())
                    .file(file)
                    .regdate(boardDto.getRegdate())
                    .board_cnt(boardDto.getBoard_cnt()).build();
        if (boardDto.getRegdate() == null) {
            board.setRegdate(LocalDateTime.now());
        } else {
            board.setRegdate(boardDto.getRegdate());
        }
        Board save = boardRepository.save(board);
        if(save==null){
            return -1;
        }
        return 1;
    }
    @Override
    @Transactional
    public void updateBoardCnt(Long id) throws Exception{
        Optional<Board> byId = boardRepository.findById(id);
        byId.get().setBoard_cnt(byId.get().getBoard_cnt()+1);
        boardRepository.save(byId.get());
    }
    @Override
    public BoardDto selectBoardDto(Long id) {
        Optional<Board> byId = boardRepository.findById(id);
            Long fileId = byId.map(board -> board.getFile()).map(file -> file.getId()).orElse(null);
            BoardDto boardDto = BoardDto.builder()
                    .name(byId.get().getName())
                    .board_cnt(byId.get().getBoard_cnt())
                    .regdate(byId.get().getRegdate())
                    .content(byId.get().getContent())
                    .title(byId.get().getTitle())
                    .id(byId.get().getId())
                    .memberId(byId.get().getMember().getMember_id())
                    .fileId(fileId)
                    .build();
            return boardDto;
    }

    @Override
    @Transactional
    public String deleteBoard(Long boardId) throws Exception{
        Optional<Board> board = boardRepository.findById(boardId);
        if(board.isPresent()){
            if(board.get().getFile()!=null){ // 게시글에 업도르 파일이 존재한다면
                Optional<File> file = fileRepository.findById(board.get().getFile().getId());
                fileRepository.delete(file.get());
                String uploadPath = environment.getProperty("file.upload.dir"); //저장 파일 위치
                String filePath = uploadPath + java.io.File.separator+file.get().getFileName();
                java.io.File deleteFile = new java.io.File(filePath);
                deleteFile.delete();
            }
            boardRepository.delete(board.get());
            return "성공";
        }
        return "실패";
    }
    @Override
    @Transactional
    public String modifiedBoard(BoardDto boardDto, FileDto fileDto, MultipartFile uploadFile) throws Exception{
        //  1. 파일 없는 게시글 수정 2. 파일 있는 게시글 수정 (2-1. 파일 존재하는데 게시글만 수정 2-2. 파일 존재하는데 파일 없애고 게시글 수정해서 등록 2-3. 파일 변경해서 등록)
        Optional<Board> boardEntity = boardRepository.findById(boardDto.getId());
        String fileName = fileDto.getFileName();
        FileDto fileDto2 = new FileDto();
        File fileEntity = null;
        if(!uploadFile.isEmpty()) { // form에서 파일업로드를 했을때
            if(fileName==null) { // 기존 파일이 존재하지 않을때 즉 수정할떄 파일을 새로 넣음
                fileName = makeFile(uploadFile);
                fileDto2.setFileName(fileName);
            }else { // 기존 파일이 존재하는데 수정할때
                String uploadPath = environment.getProperty("file.upload.dir"); //저장 파일 위치
                String filePath = uploadPath + java.io.File.separator+fileName;
                java.io.File deleteFile = new java.io.File(filePath);
                deleteFile.delete();
                fileName = makeFile(uploadFile);
                fileDto2.setFileName(fileName);
            }
            fileEntity = File.builder().fileName(fileDto2.getFileName()).build();
            File save = fileRepository.save(fileEntity);
            boardEntity.ifPresent(t->{
                t.setTitle(boardDto.getTitle());
                t.setContent(boardDto.getContent());
                t.setFile(save);
                boardRepository.save(t);
            });
            return "성공";
        }else { //업로드 파일이 없을때
                if(boardEntity.get().getFile()!=null) {
                    String existingFile = boardEntity.get().getFile().getFileName(); // 게시판에 저장된 기존 파일
                    if (existingFile != null && fileName == null) {
                        String uploadPath = environment.getProperty("file.upload.dir"); //저장 파일 위치
                        String filePath = uploadPath + java.io.File.separator + existingFile;
                        java.io.File deleteFile = new java.io.File(filePath);
                        deleteFile.delete();
                        Board b = boardEntity.get();
                        fileRepository.delete(b.getFile());
                        b.setFile(null);
                        b.setTitle(boardDto.getTitle());
                        b.setContent(boardDto.getContent());
                        boardRepository.save(b);
                        return "성공";
                    }
                }
                boardEntity.ifPresent(t->{
                    t.setTitle(boardDto.getTitle());
                    t.setContent(boardDto.getContent());
                    boardRepository.save(t);
                });
                return "성공";
        }
        }
    @Override
    @Transactional
    public void updateFileDownCnt(String fileName) {
        File file = fileRepository.findByFileName(fileName);
        file.setFileDownCnt(file.getFileDownCnt()+1);
        fileRepository.save(file);
    }

    @Override
    public String makeFile(MultipartFile file) throws IllegalStateException, IOException {
        String fileName = null;
        MultipartFile uploadFile = file;
        if(!uploadFile.isEmpty()) {
            String originalFileName = uploadFile.getOriginalFilename();
            String ext = FilenameUtils.getExtension(originalFileName); //파일 뒤에 확장자 구하기
            UUID uuid = UUID.randomUUID(); //파일 이름 랜덤하게 생성
            fileName = uuid +"."+ext;
            String uploadPath = environment.getProperty("file.upload.dir"); //저장 파일 위치
            // 실제로 저장할 파일 객체 생성
            java.io.File saveFile = new java.io.File(uploadPath + fileName);
            // 파일을 저장
            uploadFile.transferTo(saveFile);
        }
        return fileName;
    }

    @Override
    public Page<BoardDto> searchAll(Pageable pageable, SearchDTO searchDTO) {
        String title = null;
        String name = null;
        if(searchDTO.getSearchType().equals("title")){
            title = searchDTO.getSearchContent();
        }else{
            name = searchDTO.getSearchContent();
        }
        Page<Board> boardList = boardRepository.findByTitleContainingOrNameContaining(title, name,pageable);
        List<BoardDto> boardDtoList = new ArrayList<>();
        for(Board board : boardList){
            BoardDto boardDtos = BoardDto.builder()
                    .board_cnt(board.getBoard_cnt())
                    .title(board.getTitle())
                    .id(board.getId())
                    .name(board.getName())
                    .regdate(board.getRegdate()).build();
            boardDtoList.add(boardDtos);
        }
        return new PageImpl<>(boardDtoList,pageable,boardList.getTotalElements());
    }
}
