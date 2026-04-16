package com.example.demo.controller;

import com.example.demo.model.Activite;
import com.example.demo.model.Commentaire;
import com.example.demo.model.Reaction;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.AmitieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private AmitieService amitieService;

    @GetMapping("/feed")
    @Transactional
    public String feed(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        List<Utilisateur> amis = amitieService.getAmis(utilisateur);
        List<Activite> toutesActivites = new ArrayList<>();
        
        toutesActivites.addAll(activiteService.getActivitesParUtilisateur(utilisateur));
        
        for (Utilisateur ami : amis) {
            toutesActivites.addAll(activiteService.getActivitesParUtilisateur(ami));
        }
        
        toutesActivites.sort((a, b) -> b.getDateActivite().compareTo(a.getDateActivite()));
        
        // 预加载评论和反应
        for (Activite act : toutesActivites) {
            Hibernate.initialize(act.getCommentaires());
            Hibernate.initialize(act.getReactions());
        }
        
        model.addAttribute("activites", toutesActivites);
        model.addAttribute("utilisateur", utilisateur);
        
        return "feed";
    }
}