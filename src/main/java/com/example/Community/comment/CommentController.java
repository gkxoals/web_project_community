package com.example.Community.comment;

import com.example.Community.board.Board;
import com.example.Community.board.BoardService;
import com.example.Community.user.SiteUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/comments")
@Controller
public class CommentController {
    private final CommentService commentService;
    private final BoardService boardService;

    public CommentController(CommentService commentService, BoardService boardService) {
        this.commentService = commentService;
        this.boardService = boardService;
    }

    @PostMapping("/reply")
    public String replyToComment(
            @RequestParam(value = "boardId", required = false) Long boardId,
            @RequestParam("content") String content,
            @RequestParam(value = "commentId", required = false) Long commentId,
            HttpSession session, RedirectAttributes redirectAttributes) {

        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Board board = null;
        Comment parentComment = null;

        if (boardId != null) {
            board = boardService.getBoardById(boardId);
            if (board == null) {
                redirectAttributes.addFlashAttribute("error", "게시글이 존재하지 않습니다.");
                return "redirect:/board/list";
            }
        }

        if (commentId != null) {
            parentComment = commentService.getCommentById(commentId);
            if (parentComment == null) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 댓글입니다.");
                return "redirect:/board/list";
            }
        }

        Comment newComment = new Comment();
        newComment.setAuthor(loggedInUser);
        newComment.setContent(content);

        if (parentComment != null) {
            newComment.setParent(parentComment);
            newComment.setBoard(parentComment.getBoard());
            parentComment.getReplies().add(newComment);
        } else {
            newComment.setBoard(board);
        }

        commentService.saveComment(newComment);

        return "redirect:/board/detail/" +
                (parentComment != null && parentComment.getBoard() != null
                        ? parentComment.getBoard().getId()
                        : (board != null ? board.getId() : ""));
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Comment comment = commentService.getCommentById(id);

        if (comment == null) {
            redirectAttributes.addFlashAttribute("error", "존재하지 않는 댓글입니다.");
            return "redirect:/board/list";
        }

        if (!loggedInUser.getId().equals(comment.getAuthor().getId()) && !loggedInUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "댓글 삭제 권한이 없습니다.");
            return "redirect:/board/detail/" + comment.getBoard().getId();
        }

        commentService.deleteComment(id);
        return "redirect:/board/detail/" + comment.getBoard().getId();
    }

}
