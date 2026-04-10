package com.example.demo.controller;

import com.example.demo.model.Utilisateur;
import com.example.demo.model.Objectif;
import com.example.demo.service.ObjectifService;
import com.example.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private ObjectifService objectifService;

    @GetMapping("/profil")
    public String afficherProfil(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        utilisateur = utilisateurService.findById(utilisateur.getId());
        
        Map<Long, Float> progressions = new HashMap<>();
        if (utilisateur.getObjectifs() != null) {
            for (Objectif obj : utilisateur.getObjectifs()) {
                Float progression = objectifService.getPourcentageObjectif(obj, utilisateur);
                progressions.put(obj.getId(), progression);
            }
        }
        
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("objectifProgressions", progressions);
        return "profil";
    }
    
    @PostMapping("/profil/modifier")
    public String modifierProfil(@ModelAttribute Utilisateur utilisateurModifie,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        utilisateur.setPseudo(utilisateurModifie.getPseudo());
        utilisateur.setEmail(utilisateurModifie.getEmail());
        utilisateur.setSexe(utilisateurModifie.getSexe());
        utilisateur.setAge(utilisateurModifie.getAge());
        utilisateur.setTaille(utilisateurModifie.getTaille());
        utilisateur.setPoids(utilisateurModifie.getPoids());
        utilisateur.setNiveauPratique(utilisateurModifie.getNiveauPratique());
        utilisateur.setPreferencesSports(utilisateurModifie.getPreferencesSports());
        
        utilisateurService.updateUtilisateur(utilisateur);
        session.setAttribute("utilisateur", utilisateur);
        
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");
        return "redirect:/profil";
    }
}