package com.example.demo.controller;

import com.example.demo.model.Objectif;
import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ObjectifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
public class ObjectifController {

    @Autowired
    private ObjectifService objectifService;

    @GetMapping("/objectifs/nouveau")
    public String nouveauObjectif(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        model.addAttribute("objectif", new Objectif());
        model.addAttribute("typesSport", TypeSport.values());
        model.addAttribute("periodes", Periode.values());
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

        if (objectif.getDateDebut() == null) {
            objectif.setDateDebut(LocalDate.now());
        }

        objectifService.creerObjectif(objectif, utilisateur);
        redirectAttributes.addFlashAttribute("success", "Objectif créé avec succès !");
        return "redirect:/profil";
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
            return "redirect:/profil";
        }

        if (!objectif.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Accès non autorisé");
            return "redirect:/profil";
        }

        model.addAttribute("objectif", objectif);
        model.addAttribute("progression", objectifService.getProgressionObjectif(objectif, utilisateur));
        model.addAttribute("pourcentage", objectifService.getPourcentageObjectif(objectif, utilisateur));
        model.addAttribute("atteint", objectifService.isObjectifAtteint(objectif, utilisateur));

        return "detailObjectif";
    }

    // 渲染修改目标的页面
    @GetMapping("/objectifs/modifier/{id}")
    public String editerObjectifPage(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Objectif objectif = objectifService.getObjectifParId(id);
        if (objectif == null || !objectif.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Objectif introuvable ou accès refusé.");
            return "redirect:/profil";
        }

        model.addAttribute("objectif", objectif);
        model.addAttribute("typesSport", TypeSport.values());
        model.addAttribute("periodes", Periode.values());
        // 注意：你可以复用 "createObjectif.html" 模板，只需在前端稍微根据是否有 id 调整文字
        return "createObjectif"; 
    }

    // 接收修改表单并保存
    @PostMapping("/objectifs/modifier/{id}")
    public String updateObjectifSave(@PathVariable Long id, 
                                     @ModelAttribute Objectif objectifModifie, 
                                     HttpSession session, 
                                     RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Objectif existant = objectifService.getObjectifParId(id);
        if (existant != null && existant.getUtilisateur().getId().equals(utilisateur.getId())) {
            objectifService.updateObjectif(id, objectifModifie);
            redirectAttributes.addFlashAttribute("success", "Objectif mis à jour avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Échec de la modification.");
        }
        return "redirect:/profil";
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
            redirectAttributes.addFlashAttribute("success", "Objectif supprimé avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cet objectif.");
        }

        return "redirect:/profil"; 
    }
}