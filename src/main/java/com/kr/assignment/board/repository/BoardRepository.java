package com.kr.assignment.board.repository;

import com.kr.assignment.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long> {
    Page<Board> findByTitleContainingOrNameContaining(String name, String title, Pageable pageable);

    Board findByName(String name);
}
