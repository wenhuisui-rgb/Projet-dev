package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ParticipationChallengeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/participation")
public class ParticipationChallengeController {

    @Autowired
    private ParticipationChallengeService participationService;

    @Autowired
    private ChallengeService challengeService;

    /* =========================
       JOIN CHALLENGE
    ========================== */
    @PostMapping("/rejoindre/{challengeId}")
    public String rejoindreChallenge(@PathVariable Long challengeId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Challenge challenge = challengeService.getChallengeById(challengeId);

        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Challenge non trouvé.");
            return "redirect:/challenges";
        }

        if (!challenge.estActif()) {
            throw new RuntimeException("Ce challenge n'est pas actif ou est déjà terminé.");
        }

        try {
            participationService.rejoindreChallenge(utilisateur, challenge);

            redirectAttributes.addFlashAttribute("success",
                    "Vous avez rejoint le challenge !");

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());
        }

        return "redirect:/challenges";
    }

    /* =========================
       QUIT CHALLENGE
    ========================== */
    @PostMapping("/quitter/{challengeId}")
    public String quitterChallenge(@PathVariable Long challengeId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Challenge challenge = challengeService.getChallengeById(challengeId);

        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Challenge non trouvé.");
            return "redirect:/challenges";
        }

        participationService.quitterChallenge(utilisateur, challenge);

        redirectAttributes.addFlashAttribute("success",
                "Vous avez quitté le challenge !");

        return "redirect:/challenges";
    }

    /* =========================
       CLASSEMENT
    ========================== */
    @GetMapping("/classement/{challengeId}")
    public String voirClassement(@PathVariable Long challengeId,
                                 Model model,
                                 HttpSession session) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Challenge challenge = challengeService.getChallengeById(challengeId);

        if (challenge == null) {
            model.addAttribute("error", "Challenge non trouvé.");
            return "classement";
        }

        model.addAttribute("challenge", challenge);

        model.addAttribute("classement",
                participationService.obtenirClassement(challengeId));

        return "classement";
    }
}