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

/**
 * Contrôleur responsable de l'affichage du fil d'actualité (Feed).
 * <p>
 * Le feed agrège les activités sportives de l'utilisateur courant ainsi que
 * celles de l'ensemble de ses amis, puis les trie par ordre chronologique décroissant.
 */
@Controller
public class FeedController {

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private AmitieService amitieService;

    /**
     * Compile les données nécessaires à la vue du fil d'actualité.
     * L'annotation {@code @Transactional} permet de maintenir la session Hibernate ouverte 
     * pour forcer le chargement des collections paresseuses (Lazy Loading) via {@code Hibernate.initialize}.
     *
     * @param session La session HTTP
     * @param model   Le conteneur de données pour la vue
     * @return La vue Thymeleaf {@code "feed"}
     */
    @GetMapping("/feed")
    @Transactional
    public String feed(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        List<Utilisateur> amis = amitieService.getAmis(utilisateur);
        List<Activite> toutesActivites = new ArrayList<>();
        
        // Ajout des activités de l'utilisateur
        toutesActivites.addAll(activiteService.getActivitesParUtilisateur(utilisateur));
        
        // Ajout des activités de ses amis
        for (Utilisateur ami : amis) {
            toutesActivites.addAll(activiteService.getActivitesParUtilisateur(ami));
        }
        
        // Tri par date de la plus récente à la plus ancienne
        toutesActivites.sort((a, b) -> b.getDateActivite().compareTo(a.getDateActivite()));
        
        // Préchargement manuel (Fetching) des commentaires et réactions pour éviter 
        // l'erreur LazyInitializationException lors du rendu de la vue Thymeleaf.
        for (Activite act : toutesActivites) {
            Hibernate.initialize(act.getCommentaires());
            Hibernate.initialize(act.getReactions());
        }
        
        model.addAttribute("activites", toutesActivites);
        model.addAttribute("utilisateur", utilisateur);
        
        return "feed";
    }
}