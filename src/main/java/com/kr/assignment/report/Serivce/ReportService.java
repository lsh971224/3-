package com.kr.assignment.report.Serivce;


import com.kr.assignment.board.entity.Board;
import com.kr.assignment.board.repository.BoardRepository;
import com.kr.assignment.report.dto.ReportDto;
import com.kr.assignment.report.entity.Report;
import com.kr.assignment.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    @Transactional
    public String saveBoardReport(ReportDto reportDto) {
        Optional<Board> board = boardRepository.findById(reportDto.getBoard_Id());
        Report report = Report.builder()
                        .reportContent(reportDto.getReportContent())
                                .board(board.get())
                                        .build();
        reportRepository.save(report);
        board.get().setBoard_report(board.get().getBoard_report()+1);
        boardRepository.save(board.get());
        return "성공";
    }
}
