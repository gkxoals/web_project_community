package com.example.Community.board;

import com.example.Community.comment.Comment;
import com.example.Community.user.SiteUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/board")
@Controller
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("posts", boardService.getAllPost());
        return "board/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, HttpSession session, Model model) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");
        Board post = boardService.getPostId(id);

        if (post == null) {
            model.addAttribute("error", "게시글이 존재하지 않습니다.");
            return "error";
        }


        long commentCount = boardService.getCommentCountByPostId(id);

        
        List<Comment> comments = boardService.getCommentHierarchyByPostId(id);

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("post", post);
        model.addAttribute("count", commentCount);
        model.addAttribute("comments", comments);

        return "board/detail";
    }

    @PostMapping("/detail/{id}")
    public String addComment(@PathVariable Long id, @RequestParam String content, HttpSession session, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        boardService.createComment(id, content, loggedInUser);
        return "redirect:/board/detail/" + id;
    }

    @GetMapping("/create")
    public String create(HttpSession session, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        return "board/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title, @RequestParam String content, HttpSession session, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        boardService.createPost(title, content, loggedInUser);
        return "redirect:/board/list";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Board post = boardService.getPostId(id);

        if (!loggedInUser.getId().equals(post.getAuthor().getId()) && !loggedInUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "게시글 수정 권한이 없습니다.");
            return "redirect:/board/detail/" + id;
        }

        model.addAttribute("post", post);
        return "/board/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id, @RequestParam String title, @RequestParam String content, HttpSession session, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Board post = boardService.getPostId(id);

        if (!loggedInUser.getId().equals(post.getAuthor().getId()) && !loggedInUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "게시글 수정 권한이 없습니다.");
            return "redirect:/board/detail/" + id;
        }

        boardService.updatePost(id, title, content);
        return "redirect:/board/detail/" + id;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        Board post = boardService.getPostId(id);

        if (!loggedInUser.getId().equals(post.getAuthor().getId()) && !loggedInUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "게시글 삭제 권한이 없습니다.");
            return "redirect:/board/detail/" + id;
        }

        boardService.deletePost(id);
        return "redirect:/board/list";
    }
}
