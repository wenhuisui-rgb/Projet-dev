package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ParticipationChallengeService;


import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/participation")
public class ParticipationChallengeController {
    @Autowired
    private ParticipationChallengeService participationService;
    @Autowired
    private ChallengeService challengeService;

    @PostMapping("/rejoindre/{challengeId}")
    public String rejoindreChallenge(@PathVariable Long challengeId, HttpSession session,
            RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        Challenge challenge = challengeService.getChallengeById(challengeId);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error", "Challenge non trouvé.");
            return "redirect:/challenges";
        }
        participationService.rejoindreChallenge(utilisateur, challenge);
        redirectAttributes.addFlashAttribute("success", "Vous avez rejoint le challenge !");
        return "redirect:/challenges";

    }

    @PostMapping("/quitter/{challengeId}")
    public String quitterChallenge(@PathVariable Long challengeId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        Challenge challenge = challengeService.getChallengeById(challengeId);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error", "Challenge non trouvé.");
            return "redirect:/challenges";
        }
        participationService.quitterChallenge(utilisateur, challenge);
        redirectAttributes.addFlashAttribute("success", "Vous avez quitté le challenge !");
        return "redirect:/challenges";
    }

    @PostMapping("/classement/{challengeId}")
    public String voirClassment(@PathVariable Long challengeId, Model model) {
        Challenge challenge = challengeService.getChallengeById(challengeId);
        if (challenge == null) {
            model.addAttribute("error", "Challenge non trouvé.");
            return "classement";
        }
        model.addAttribute("classement", participationService.obtenirClassement(challengeId));
        return "classement"; // nom du fichier html à créer pour afficher le classement

    }

}
