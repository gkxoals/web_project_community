package com.example.Community;

import com.example.Community.user.SiteUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(HttpSession session, Model model){
        SiteUser loggedInUser = (SiteUser) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser",loggedInUser);

        return "main";
    }

}
