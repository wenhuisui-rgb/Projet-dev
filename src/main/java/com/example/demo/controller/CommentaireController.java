package com.example.demo.controller;

import com.example.demo.model.Commentaire;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.CommentaireService;
import com.example.demo.service.ActiviteService;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentaireController {

    @Autowired
    private CommentaireService commentaireService;

    @Autowired
    private ActiviteService activiteService;

    @PostMapping("/commentaires/ajouter")
    public String ajouterCommentaire(@RequestParam String contenu,
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

        commentaireService.ajouterCommentaire(contenu, activite, utilisateur);
        redirectAttributes.addFlashAttribute("success", "Commentaire ajouté");
        return "redirect:/activites/" + activiteId;
    }

    @GetMapping("/commentaires/supprimer/{id}")
    public String supprimerCommentaire(@PathVariable Long id,
                                        @RequestParam Long activiteId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        Commentaire commentaire = commentaireService.getCommentaireParId(id);
        if (commentaire != null && commentaire.getAuteur().getId().equals(utilisateur.getId())) {
            commentaireService.supprimerCommentaire(id);
            redirectAttributes.addFlashAttribute("success", "Commentaire supprimé");
        } else {
            redirectAttributes.addFlashAttribute("error", "Action non autorisée");
        }

        return "redirect:/activites/" + activiteId;
    }

    @GetMapping("/api/commentaires/count")
    @ResponseBody
    public Map<String, Long> getCommentCount(@RequestParam Long activiteId) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", commentaireService.getNombreCommentairesParActivite(activiteId));
        return result;
    }

    @PostMapping("/api/commentaires/ajouter")
    @ResponseBody
    public Map<String, Object> ajouterCommentaire(@RequestBody Map<String, Object> payload, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        Long activiteId = Long.valueOf(payload.get("activiteId").toString());
        String contenu = payload.get("contenu").toString();
        Activite activite = activiteService.getActiviteParId(activiteId);
        Commentaire commentaire = commentaireService.ajouterCommentaire(contenu, activite, utilisateur);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", commentaire.getId());
        result.put("contenu", commentaire.getContenu());
        result.put("auteurPseudo", commentaire.getAuteur().getPseudo());
        result.put("date", commentaire.getDateCommentaire().toString());
        return result;
    }
}