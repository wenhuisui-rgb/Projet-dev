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

    @GetMapping("/connexion")
    public String pageConnexion() {
        return "connexion";
    }

    @PostMapping("/connexion")
    public String connecter(@RequestParam String email, 
                            @RequestParam String motDePasse, 
                            HttpSession session, 
                            Model model) {
        Utilisateur utilisateur = utilisateurService.authentifier(email, motDePasse);
        if (utilisateur != null) {
            session.setAttribute("utilisateur", utilisateur); 
            return "redirect:/profil";
        } else {
            model.addAttribute("erreur", "Email ou mot de passe incorrect.");
            return "connexion";
        }
    }

    @GetMapping("/inscription")
    public String pageInscription(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(@ModelAttribute Utilisateur utilisateur, 
                           RedirectAttributes redirectAttributes, 
                           Model model) {
        if (utilisateurService.emailExiste(utilisateur.getEmail())) {
            model.addAttribute("erreur", "Cet email est déjà utilisé.");
            return "inscription";
        }
        if (utilisateurService.pseudoExiste(utilisateur.getPseudo())) {
            model.addAttribute("erreur", "Ce pseudo est déjà pris.");
            return "inscription";
        }

        utilisateurService.inscrireUtilisateur(utilisateur);
        redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Veuillez vous connecter.");
        return "redirect:/connexion";
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/connexion";
    }

    @GetMapping("/profil")
    public String afficherProfil(@RequestParam(required = false) Long userId,
                                  HttpSession session, 
                                  Model model) {
        Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");
        if (currentUser == null) {
            return "redirect:/connexion";
        }
        
        Utilisateur profilUser;
        boolean isOwner;
        
        if (userId != null && !userId.equals(currentUser.getId())) {
            profilUser = utilisateurService.findById(userId);
            isOwner = false;
        } else {
            profilUser = currentUser;
            isOwner = true;
        }
        
        if (profilUser == null) {
            return "redirect:/profil";
        }
        
        profilUser = utilisateurService.findById(profilUser.getId());
        
        Map<Long, Float> progressions = new HashMap<>();
        if (profilUser.getObjectifs() != null) {
            for (Objectif obj : profilUser.getObjectifs()) {
                Float progression = objectifService.getPourcentageObjectif(obj, profilUser);
                progressions.put(obj.getId(), progression);
            }
        }
        
        model.addAttribute("utilisateur", profilUser);
        model.addAttribute("objectifProgressions", progressions);
        model.addAttribute("isOwner", isOwner);
        return "profil";
    }
    
    @GetMapping("/profil/modifier")
    public String modifierProfilPage(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        model.addAttribute("utilisateur", utilisateur);
        return "modifierProfil";
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