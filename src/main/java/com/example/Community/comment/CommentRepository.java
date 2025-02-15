package com.example.Community.comment;

import com.example.Community.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoardId(Long boardId);

    void deleteByBoardId(Long boardId);


    long countByBoard_Id(Long boardId);
}
