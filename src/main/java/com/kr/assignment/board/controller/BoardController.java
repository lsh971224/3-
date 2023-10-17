package com.kr.assignment.board.controller;

import com.kr.assignment.board.dto.BoardDto;
import com.kr.assignment.board.service.BoardService;
import com.kr.assignment.config.SearchDTO;
import com.kr.assignment.file.fileDto.FileDto;
import com.kr.assignment.file.service.FileService;
import com.kr.assignment.member.dto.MemberDto;
import com.kr.assignment.member.service.MemberService;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final FileService fileService;
    @Autowired
    private Environment environment;

    // 게시글 작성
    @GetMapping("/boardWrite.do")
    public String boardWrite(HttpSession session, HttpServletRequest req){
        String id = (String)session.getAttribute("email");
        if(id==null){
            return "redirect:/loginMember.do";
        }
        return "board/boardWrite";
    }
    @PostMapping("/boardWriteSave.do")
    public ResponseEntity<String> boardWriteSave(BoardDto boardDto, HttpSession session, MultipartFile uploadFile) throws Exception{
        String email = (String)session.getAttribute("email");
        MemberDto memberdto = memberService.selectMemberDto(email);
        MultipartFile uFile = uploadFile;
        String fileName = null;
        if(!uFile.isEmpty()){
            String originalFileName = uFile.getOriginalFilename();
            String ext = FilenameUtils.getExtension(originalFileName);
            UUID uuid = UUID.randomUUID();
            fileName = uuid+"."+ext;
            String uploadPath = environment.getProperty("file.upload.dir");
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdirs();
            }
            File saveFile = new File(uploadPath+fileName);
            uploadFile.transferTo(saveFile);
            fileService.saveFile(fileName);
        }
        int result = boardService.insertBoard(boardDto,memberdto,fileName);
        if(result==1){
            return ResponseEntity.ok("성공");
        }
        return ResponseEntity.ok("실패");
    }

    @GetMapping("/boardList.do")
    public String boardList(Model model,
                            @PageableDefault(page=0,size=10,sort="regdate",direction = Sort.Direction.DESC) Pageable pageable,SearchDTO searchDTO) throws Exception{
        Page<BoardDto> boardDtoList = null;
        if(searchDTO.getSearchContent()==null){
            boardDtoList = boardService.selectAll(pageable);
        }else{
            boardDtoList = boardService.searchAll(pageable,searchDTO);
        }
        boardDtoList.getPageable().getPageNumber();
        Map<String,String> searchMap = new HashMap<>();
        searchMap.put("type",searchDTO.getSearchType());
        searchMap.put("content",searchDTO.getSearchContent());
        model.addAttribute("searchMap",searchMap);
        model.addAttribute("boardList",boardDtoList);
        return "board/boardList";
    }
    //검색 페이징
    @GetMapping("/boardSearch.do")
    public String boardSearch(Model model,@PageableDefault(page=0,size=10,sort="id",direction = Sort.Direction.DESC) Pageable pageable,SearchDTO searchDTO) throws Exception{
        Page<BoardDto> boardDtos = boardService.searchAll(pageable,searchDTO);
        model.addAttribute("boardList",boardDtos);
        return null;
    }

    //게시글 상세보기
    @GetMapping("/boardDetail.do")
    public String boardShowDetail(Model model, @RequestParam("id") Long id,int page,HttpSession session) throws Exception{
        // 조회수 증가
        boardService.updateBoardCnt(id);
        BoardDto boardDto = boardService.selectBoardDto(id);
        Long memberId = boardDto.getMemberId();
        String member_role = (String)session.getAttribute("role");
        log.info("유저 권한 ==="+member_role);
        if(boardDto.getFileId()!=null){
            FileDto fileDto = fileService.getFile(boardDto.getFileId());
            int lastIndex = fileDto.getFileName().lastIndexOf(".");
            String ext = fileDto.getFileName().substring(lastIndex+1);
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("gif")) {
                model.addAttribute("ext",ext);
            }
            model.addAttribute("file",fileDto);
        }
        model.addAttribute("page",page);
        boardDto.setContent(boardDto.getContent().replaceAll("\n","<br>"));
        model.addAttribute("memberId",memberId);
        model.addAttribute("board",boardDto);
        model.addAttribute("role",member_role);
        return "board/boardDetail";
    }

    // 게시글 삭제
    @PostMapping("/deleteBoard.do")
    public ResponseEntity<String> deleteBoard(Long boardId) throws Exception{
        String result = boardService.deleteBoard(boardId);
        return ResponseEntity.ok(result);
    }
    // 게시글 수정화면 이동
    @GetMapping("/boardModify.do")
    public String boardModify(Long boardId,Model model,int page) throws Exception{
        BoardDto boardDto = boardService.selectBoardDto(boardId);
        if(boardDto.getFileId()!=null){
            FileDto fileDto = fileService.getFile(boardDto.getFileId());
            model.addAttribute("file",fileDto);
        }
        boardDto.setContent(boardDto.getContent().replaceAll("\n","<br>"));
        model.addAttribute("board",boardDto);
        model.addAttribute("page",page);
        return "board/boardModify";
    }

    // 게시글 수정하기
    @PostMapping("/boardModifySave.do")
    public ResponseEntity<String> boardModifySave(BoardDto boardDto,FileDto file,MultipartFile uploadFile) throws Exception{
        String result = boardService.modifiedBoard(boardDto,file,uploadFile);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/downloadFile.do")
    public void fileDownload(String fileName, HttpServletResponse res) throws Exception{
        String uploadPath = environment.getProperty("file.upload.dir");
        File f = new File(uploadPath,fileName);
        res.setContentType("application/download");
        res.setContentLength((int)f.length());
        res.setHeader("Content-disposition","attachment;fileName=\""+fileName+"\"");
        OutputStream os = res.getOutputStream();
        FileInputStream fis = new FileInputStream(f);
        FileCopyUtils.copy(fis, os);
        fis.close();
        os.close();
        boardService.updateFileDownCnt(fileName);
    }

    @GetMapping("/excelCsvDownload.do")
    public void csvDownload(HttpServletResponse response) throws Exception{
        response.setContentType("text/csv; charset=UTF-8");
        String fileName = URLEncoder.encode("jpaBoardList.csv","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=\""+fileName+"\"");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
        writer.write("\uFEFF");
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeAll(boardListString());
        csvWriter.close();
        writer.close();
    }

    @PostMapping("/csvBoardInsert.do")
    @ResponseBody
    public ModelAndView csvBoardInsert(MultipartFile csvfile) throws Exception{
        String fileName = csvfile.getOriginalFilename();
        int lastIndex = fileName.lastIndexOf(".");
        String ext = fileName.substring(lastIndex+1);
        Map<String,String> response = new HashMap<>();

        if(!ext.equals("csv")&& ext!="csv") {
            response.put("result", "fail");
        }else {
            BoardDto boardDTO = new BoardDto();
            BufferedReader br = new BufferedReader(new InputStreamReader(csvfile.getInputStream()));
            String line;
            boolean isFirstLine = true; // 첫 번째 행인지 여부를 나타내는 플래그
            if((line=br.readLine())!=null){
                while((line = br.readLine())!=null) {
                    String[] datalines = line.split(",");
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // csv파일에 첫번째 행은 속성명이기에 건너뛰기위함
                    }
                    String name = datalines[0]; // 작성자
                    String title = datalines[1]; // 제목
                    String content = datalines[2]; // 내용
                    String excelTime = datalines[3]; // 작성 시간

                    // 큰따옴표 제거
                    name = name.replaceAll("^\"|\"$", "");
                    title = title.replaceAll("^\"|\"$", "");
                    content = content.replaceAll("^\"|\"$", "");
                    excelTime = excelTime.replaceAll("^\"|\"$", "");
                    
                    // csv파일 시간을 변환해서 받아옴
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime regdate = LocalDateTime.parse(excelTime, formatter);

                    Long member_id = boardService.selectBoardWriter(name);

                    boardDTO.setName(name);
                    boardDTO.setMemberId(member_id);
                    boardDTO.setContent(content);
                    boardDTO.setTitle(title);
                    boardDTO.setRegdate(regdate);
                    boardService.csvInsertBoard(boardDTO);
                }
                br.close();
            }
            response.put("result", "ok");
        }
        ModelAndView modelAndView = new ModelAndView("jsonView",response);
        return modelAndView;
    }
    @GetMapping("/excelDownload.do")
    public void excelDownload(HttpServletResponse response,@RequestParam("exten") String extension) throws Exception{
        Workbook wb = null;
        if(extension.equals("excelxls")){
            wb = new HSSFWorkbook();
        }else{
            wb = new XSSFWorkbook();
        }
        Sheet sheet = wb.createSheet("첫번쨰 시트");
        Row row = null;
        Cell cell = null;
        int rowNum =0;
        List<BoardDto> boardDtos = boardService.csvSelectAll();
        // Header
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("작성자");
        cell = row.createCell(1);
        cell.setCellValue("제목");
        cell = row.createCell(2);
        cell.setCellValue("내용");
        cell = row.createCell(3);
        cell.setCellValue("등록일자");

        for(BoardDto b : boardDtos){
            b.setContent(b.getContent().replaceAll("<br>"," "));
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(b.getName());
            cell = row.createCell(1);
            cell.setCellValue(b.getTitle());
            cell = row.createCell(2);
            cell.setCellValue(b.getContent());
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(b.getRegdate()));
            log.info("게시판 등록일자 : "+b.getRegdate());
        }
        response.setContentType("application/vnd.ms-excel");
        String fileName = "excelBoardList_" + System.currentTimeMillis(); // 고유한 파일 이름을 만듭니다.
        if ("excelxls".equals(extension)) {
            fileName += ".xls";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        } else {
            fileName += ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        }
        wb.write(response.getOutputStream());
        wb.close();
    }

    @PostMapping("/excelBoardInsert.do")
    @ResponseBody
    public ModelAndView excelUpload(@RequestParam("excelfile") MultipartFile file) throws IOException{
        List<BoardDto> excelBoardList = new ArrayList<>();
        Map<String, String> response = new HashMap<>();
        ModelAndView modelAndView = null;
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(!extension.equals("xlsx")&&!extension.equals("xls")){
            response.put("result","fail");
            modelAndView = new ModelAndView("jsonView",response);
            return modelAndView;
        }
        Workbook wb = null;
        if(extension.equals("xlsx")){
            wb = new XSSFWorkbook(file.getInputStream());
        }else{
            wb = new HSSFWorkbook(file.getInputStream());
        }
        Sheet worksheet = wb.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            Row row = worksheet.getRow(i);
            BoardDto data = new BoardDto();
            data.setName(row.getCell(0).getStringCellValue());
            log.info("data.getName() = " + data.getName());
            data.setTitle(row.getCell(1).getStringCellValue());
            data.setContent(row.getCell(2).getStringCellValue());
            String dataString = row.getCell(3).getStringCellValue();
            log.info(dataString);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime regdate = LocalDateTime.parse(dataString, formatter);
            data.setRegdate(regdate);
            log.info("data.getRegdate(): " + data.getRegdate());
            boardService.excelinsertBoard(data);
        }
        response.put("result","ok");
        wb.close();
        modelAndView = new ModelAndView("jsonView",response);
        return modelAndView;
    }
    public List<String[]> boardListString() throws Exception{
        List<BoardDto> boardDtos = boardService.csvSelectAll();
        List<String[]> listStrings = new ArrayList<>();
        listStrings.add(new String[] {"작성자","제목","내용","등록일"});
        for(BoardDto boardDTO : boardDtos) {
            boardDTO.setContent(boardDTO.getContent().replaceAll("\n", " "));
            String[] rowData = new String[4];
            rowData[0] = boardDTO.getName();
            rowData[1] = boardDTO.getTitle();
            rowData[2] = boardDTO.getContent();
            rowData[3] = String.valueOf(boardDTO.getRegdate());
            listStrings.add(rowData);
        }
        return listStrings;
    }

    @GetMapping("/boardReport.do")
    public String boardReport(@RequestParam("boardId") Long boardId,Model model){
        model.addAttribute("boardId",boardId);
        return "board/boardReport";
    }


}
