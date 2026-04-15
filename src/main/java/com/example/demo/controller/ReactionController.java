package com.example.demo.controller;

import com.example.demo.model.TypeReaction;
import com.example.demo.model.Activite;
import com.example.demo.model.Reaction;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.ReactionService;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

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

    @GetMapping("/api/reactions/count")
    @ResponseBody
    public Map<String, Long> getReactionCounts(@RequestParam Long activiteId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("KUDOS", reactionService.getNombreReactionsParType(activiteId, TypeReaction.KUDOS));
        counts.put("BRAVO", reactionService.getNombreReactionsParType(activiteId, TypeReaction.BRAVO));
        counts.put("SOUTIEN", reactionService.getNombreReactionsParType(activiteId, TypeReaction.SOUTIEN));
        return counts;
    }

    @GetMapping("/api/reactions/user-reaction")
    @ResponseBody
    public Map<String, String> getUserReaction(@RequestParam Long activiteId, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        Map<String, String> result = new HashMap<>();
        if (utilisateur != null) {
            Optional<Reaction> reaction = reactionService.getReactionByAuteurEtActivite(utilisateur, activiteService.getActiviteParId(activiteId));
            if (reaction.isPresent()) {
                result.put("type", reaction.get().getType().toString());
            }
        }
        return result;
    }

    @PostMapping("/api/reactions/toggle")
    @ResponseBody
    public Map<String, Object> toggleReaction(@RequestBody Map<String, Object> payload, HttpSession session) {
        System.out.println("toggleReaction called");
        
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not logged in");
            return error;
        }
        
        Long activiteId = Long.valueOf(payload.get("activiteId").toString());
        String typeStr = payload.get("type").toString();
        TypeReaction type = TypeReaction.valueOf(typeStr);
        
        Map<String, Object> result = new HashMap<>();
        
        Activite activite = activiteService.getActiviteParId(activiteId);
        if (activite == null) {
            result.put("error", "Activity not found");
            return result;
        }
        
        if (reactionService.aDejaReagi(utilisateur, activite)) {
            reactionService.supprimerReaction(utilisateur, activite);
            result.put("userReaction", null);
        } else {
            reactionService.ajouterReaction(type, activite, utilisateur);
            result.put("userReaction", typeStr);
        }
        
        Map<String, Long> counts = new HashMap<>();
        counts.put("KUDOS", reactionService.getNombreReactionsParType(activiteId, TypeReaction.KUDOS));
        counts.put("BRAVO", reactionService.getNombreReactionsParType(activiteId, TypeReaction.BRAVO));
        counts.put("SOUTIEN", reactionService.getNombreReactionsParType(activiteId, TypeReaction.SOUTIEN));
        result.put("counts", counts);
        
        return result;
    }
}