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

/**
 * Contrôleur gérant les réactions (interactions type "Like", "Bravo") sur les activités.
 * <p>
 * Fournit des routes classiques pour un fonctionnement sans JavaScript,
 * ainsi que des routes d'API REST (AJAX) pour une expérience utilisateur fluide et dynamique.
 */
@Controller
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private ActiviteService activiteService;

    /**
     * Route classique (MVC) : Ajoute ou retire une réaction via une soumission de formulaire classique.
     * Fait office de "Toggle" (bascule).
     *
     * @return Une redirection vers la page de détails de l'activité
     */
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

    /**
     * Route classique (MVC) : Supprime explicitement la réaction de l'utilisateur courant.
     *
     * @return Une redirection vers la page de l'activité
     */
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

    /**
     * API REST (AJAX) : Récupère le décompte des différentes réactions pour une activité donnée.
     * Utile pour rafraîchir dynamiquement les compteurs côté frontend.
     *
     * @param activiteId L'identifiant de l'activité
     * @return Un objet JSON contenant les totaux par type de réaction (ex: {"KUDOS": 5, "BRAVO": 2})
     */
    @GetMapping("/api/reactions/count")
    @ResponseBody
    public Map<String, Long> getReactionCounts(@RequestParam Long activiteId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("KUDOS", reactionService.getNombreReactionsParType(activiteId, TypeReaction.KUDOS));
        counts.put("BRAVO", reactionService.getNombreReactionsParType(activiteId, TypeReaction.BRAVO));
        counts.put("SOUTIEN", reactionService.getNombreReactionsParType(activiteId, TypeReaction.SOUTIEN));
        return counts;
    }

    /**
     * API REST (AJAX) : Indique si l'utilisateur courant a déjà réagi à une activité, et si oui, quel est son type de réaction.
     * Utilisé pour mettre en surbrillance (highlight) le bouton de réaction côté frontend.
     *
     * @return Un objet JSON contenant le type de réaction s'il existe (ex: {"type": "KUDOS"})
     */
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

    /**
     * API REST (AJAX) : Active ou désactive (Toggle) dynamiquement une réaction.
     * Effectue le changement en base de données et renvoie l'état mis à jour ainsi que les nouveaux compteurs.
     *
     * @param payload Le corps de la requête contenant "activiteId" et "type"
     * @return Un objet JSON avec l'état de la réaction de l'utilisateur ("userReaction") et les compteurs mis à jour ("counts")
     */
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
        
        // Logique de Toggle
        if (reactionService.aDejaReagi(utilisateur, activite)) {
            reactionService.supprimerReaction(utilisateur, activite);
            result.put("userReaction", null);
        } else {
            reactionService.ajouterReaction(type, activite, utilisateur);
            result.put("userReaction", typeStr);
        }
        
        // Récupère les nouveaux compteurs
        Map<String, Long> counts = new HashMap<>();
        counts.put("KUDOS", reactionService.getNombreReactionsParType(activiteId, TypeReaction.KUDOS));
        counts.put("BRAVO", reactionService.getNombreReactionsParType(activiteId, TypeReaction.BRAVO));
        counts.put("SOUTIEN", reactionService.getNombreReactionsParType(activiteId, TypeReaction.SOUTIEN));
        result.put("counts", counts);
        
        return result;
    }
}