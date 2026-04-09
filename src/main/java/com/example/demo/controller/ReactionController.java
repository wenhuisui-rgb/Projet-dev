package com.example.demo.controller;

import com.example.demo.model.TypeReaction;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private ActiviteService activiteService;

    @PostMapping("/reactions/ajouter")
    public String ajouterReaction(@RequestParam TypeReaction type,
                                   @RequestParam Long activiteId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Activite activite = activiteService.getActiviteParId(activiteId);
        if (activite == null) {
            redirectAttributes.addFlashAttribute("error", "Activité non trouvée");
            return "redirect:/dashboard";
        }

        if (reactionService.aDejaReagi(utilisateur, activite)) {
            reactionService.supprimerReaction(utilisateur, activite);
            redirectAttributes.addFlashAttribute("success", "Réaction retirée");
        } else {
            reactionService.ajouterReaction(type, activite, utilisateur);
            redirectAttributes.addFlashAttribute("success", "Réaction ajoutée");
        }

        return "redirect:/activites/" + activiteId;
    }

    @GetMapping("/reactions/supprimer/{activiteId}")
    public String supprimerReaction(@PathVariable Long activiteId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Activite activite = activiteService.getActiviteParId(activiteId);
        if (activite != null) {
            reactionService.supprimerReaction(utilisateur, activite);
            redirectAttributes.addFlashAttribute("success", "Réaction supprimée");
        }

        return "redirect:/activites/" + activiteId;
    }
}