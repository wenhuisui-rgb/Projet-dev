package com.example.demo.controller;

import com.example.demo.model.Activite;
import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.MeteoService;
import com.example.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.service.ChallengeService;
import java.time.LocalDate;


import jakarta.servlet.http.HttpSession;

@Service
@Controller

public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

  

    @GetMapping("/challenges")
    public String listeChallenges(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        model.addAttribute("challenges", challengeService.getAllChallenges());
        model.addAttribute("utilisateur", utilisateur);
        return "challenges";
    }

    // Méthodes pour créer, rejoindre, quitter les challenges à implémenter ici
    @PostMapping("/challenges/creer")
    public String creerChallenge(@ModelAttribute("challenge") Challenge challenge, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        if(challenge.getDateDebut().isBefore(LocalDate.now()) || challenge.getDateFin().isBefore(challenge.getDateDebut())) {
            redirectAttributes.addFlashAttribute("error", "Les dates du challenge sont invalides.");
            return "redirect:/challenges";
        }
        challenge.setCreateur(utilisateur);
        challengeService.creerChallenge(challenge.getTitre(), challenge.getTypeSport(), challenge.getDateDebut(), challenge.getDateFin(), utilisateur);
        redirectAttributes.addFlashAttribute("success", "Challenge créé avec succès !");    
        return "redirect:/challenges";



}

    @PostMapping("/challenges/{id}/rejoindre")
    public String rejoindreChallenge(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        Challenge challenge = challengeService.getChallengeById(id);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error", "Challenge non trouvé.");
            return "redirect:/challenges";
        }
        // Logique pour rejoindre le challenge à implémenter ici
        redirectAttributes.addFlashAttribute("success", "Vous avez rejoint le challenge !");
        return "redirect:/challenges";
    }

    @PostMapping("/challenges/{id}/quitter")
    public String quitterChallenge(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        Challenge challenge = challengeService.getChallengeById(id);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error", "Challenge non trouvé.");
            return "redirect:/challenges";
        }
        // Logique pour quitter le challenge à implémenter ici
        redirectAttributes.addFlashAttribute("success", "Vous avez quitté le challenge !");
        return "redirect:/challenges";
    }

}


