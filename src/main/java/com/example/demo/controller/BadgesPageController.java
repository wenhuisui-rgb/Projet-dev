package com.example.demo.controller;

import com.example.demo.model.Utilisateur;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BadgesPageController {

    @GetMapping("/badges")
    public String badgesPage(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        model.addAttribute("utilisateur", utilisateur);
        return "badges";
    }
}