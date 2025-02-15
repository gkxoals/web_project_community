package com.example.Community.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public void saveComment(Comment comment) {
        if (comment.getParentComment() != null) {
            comment.setBoard(comment.getParentComment().getBoard());
        }
        commentRepository.save(comment);
    }

    public List<Comment> getCommentHierarchyByBoardId(Long boardId) {
        List<Comment> allComments = commentRepository.findByBoardId(boardId);
        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> rootComments = new ArrayList<>();

        for (Comment comment : allComments) {
            commentMap.put(comment.getId(), comment);
        }

        for (Comment comment : allComments) {
            if (comment.getParentComment() == null) {
                rootComments.add(comment);
            } else {
                Comment parent = commentMap.get(comment.getParentComment().getId());
                if (parent != null) {
                    if (!parent.getReplies().contains(comment)) {
                        parent.getReplies().add(comment);
                    }
                }
            }
        }

        return rootComments;
    }


    public long getCommentCountByBoardId(Long boardId) {
        if (boardId == null) {
            return 0;
        }
        return commentRepository.countByBoard_Id(boardId);
    }

}
