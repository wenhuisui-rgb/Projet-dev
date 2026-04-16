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

/**
 * Contrôleur gérant les actions d'inscription et de désinscription aux défis (Challenges),
 * ainsi que l'affichage des classements.
 */
@Controller
@RequestMapping("/participation")
public class ParticipationChallengeController {

    @Autowired
    private ParticipationChallengeService participationService;

    @Autowired
    private ChallengeService challengeService;

    /**
     * Permet à l'utilisateur courant de rejoindre un challenge actif.
     *
     * @param challengeId        L'identifiant du challenge à rejoindre
     * @param session            La session HTTP
     * @param redirectAttributes Messages flash pour la vue
     * @return Une redirection vers la page de la liste des challenges ({@code "/challenges"})
     */
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

    /**
     * Permet à l'utilisateur courant de quitter un challenge auquel il était inscrit.
     *
     * @param challengeId        L'identifiant du challenge
     * @return Une redirection vers la page de la liste des challenges ({@code "/challenges"})
     */
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

    /**
     * Affiche la page du classement (Leaderboard) pour un challenge spécifique.
     * Injecte les données du challenge et la liste triée des participants dans le modèle.
     *
     * @param id    L'identifiant du challenge
     * @param model Le modèle Thymeleaf
     * @return La vue {@code "detailParticipationChallenge"} (ou {@code "classement"} en cas d'erreur)
     */
    @GetMapping("/classement/{id}")
    public String voirClassement(@PathVariable Long id,
                                 Model model,
                                 HttpSession session) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Challenge challenge = challengeService.getChallengeById(id);

        if (challenge == null) {
            model.addAttribute("error", "Challenge non trouvé.");
            return "classement";
        }

        model.addAttribute("challenge", challenge);
        model.addAttribute("classement", participationService.obtenirClassement(id));

        return "detailParticipationChallenge";
    }
}