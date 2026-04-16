package com.example.demo.controller;

import com.example.demo.model.Utilisateur;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur simple dédié exclusivement au routage vers la page d'affichage des badges.
 */
@Controller
public class BadgesPageController {

    /**
     * Intercepte la requête pour afficher la vitrine des badges de l'utilisateur.
     * Vérifie la session et injecte l'utilisateur courant dans le modèle.
     *
     * @param session La session HTTP actuelle
     * @param model   Le conteneur de données pour la vue
     * @return Le nom de la vue Thymeleaf {@code "badges"} (ou redirection vers la connexion)
     */
    @GetMapping("/badges")
    public String badgesPage(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        // Injecte l'utilisateur dans le modèle pour que Thymeleaf puisse charger 
        // ses données personnelles (et potentiellement ses badges associés)
        model.addAttribute("utilisateur", utilisateur);
        
        return "badges";
    }
}