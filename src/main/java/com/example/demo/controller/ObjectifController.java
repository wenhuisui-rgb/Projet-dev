package com.example.demo.controller;

import com.example.demo.model.Objectif;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ObjectifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ObjectifController {

    @Autowired
    private ObjectifService objectifService;

    @GetMapping("/objectifs")
    public String listObjectifs(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        List<Objectif> objectifs = objectifService.getObjectifsParUtilisateur(utilisateur);
        model.addAttribute("objectifs", objectifs);
        return "objectifs";
    }

    @GetMapping("/objectifs/nouveau")
    public String nouveauObjectif(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        model.addAttribute("objectif", new Objectif());
        model.addAttribute("typesSport", TypeSport.values());
        return "createObjectif";
    }

    @PostMapping("/objectifs/sauvegarder")
    public String sauvegarderObjectif(@ModelAttribute Objectif objectif,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        objectifService.creerObjectif(objectif, utilisateur);
        redirectAttributes.addFlashAttribute("success", "Objectif créé avec succès !");
        return "redirect:/objectifs";
    }

    @GetMapping("/objectifs/{id}")
    public String detailObjectif(@PathVariable Long id,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        Objectif objectif = objectifService.getObjectifParId(id);
        if (objectif == null) {
            redirectAttributes.addFlashAttribute("error", "Objectif non trouvé");
            return "redirect:/objectifs";
        }
        
        if (!objectif.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Accès non autorisé");
            return "redirect:/objectifs";
        }
        
        model.addAttribute("objectif", objectif);
        model.addAttribute("progression", objectifService.getProgressionObjectif(objectif, utilisateur));
        model.addAttribute("atteint", objectifService.isObjectifAtteint(objectif, utilisateur));
        
        return "detailObjectif";
    }

    @GetMapping("/objectifs/delete/{id}")
    public String deleteObjectif(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        Objectif objectif = objectifService.getObjectifParId(id);
        if (objectif != null && objectif.getUtilisateur().getId().equals(utilisateur.getId())) {
            objectifService.supprimerObjectif(id);
            redirectAttributes.addFlashAttribute("success", "Objectif supprimé");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer");
        }
        
        return "redirect:/objectifs";
    }
}