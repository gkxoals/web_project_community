package com.example.Community.board;

import com.example.Community.comment.CommentService;
import com.example.Community.comment.Comment;
import com.example.Community.comment.CommentRepository;
import com.example.Community.user.SiteUser;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    public BoardService(BoardRepository boardRepository, CommentRepository commentRepository, CommentService commentService) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    public void createPost(String title, String content, SiteUser siteUser) {
        Board post = new Board();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(siteUser);
        post.setCreateAt(LocalDateTime.now());
        post.setUpdateAt(LocalDateTime.now());
        boardRepository.save(post);
    }

    public List<Board> getAllPost() {
        return boardRepository.findAll();
    }

    public Board getPostId(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public void updatePost(Long id, String title, String content) {
        Board post = getPostId(id);
        post.setTitle(title);
        post.setContent(content);
        post.setUpdateAt(LocalDateTime.now());
        boardRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Board board = getPostId(id);
        commentRepository.deleteByBoardId(id);
        boardRepository.delete(board);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByBoardId(postId);
    }

    @Transactional
    public void createComment(Long id, String content, SiteUser loggedInUser) {
        if (loggedInUser == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용이 비어있을 수 없습니다.");
        }

        Board board = getPostId(id);
        Comment comment = new Comment(board, loggedInUser, content);
        commentRepository.save(comment);
    }

    public Board getBoardById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        return board.orElse(null);
    }

    public long getCommentCountByPostId(Long postId) {
        return commentRepository.countByBoard_Id(postId);
    }

    public List<Comment> getCommentHierarchyByPostId(Long postId) {
        return commentService.getCommentHierarchyByBoardId(postId);
    }
}
