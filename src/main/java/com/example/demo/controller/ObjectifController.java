package com.example.demo.controller;

import com.example.demo.dto.ObjectifFormDTO;
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

/**
 * Contrôleur gérant les vues et les formulaires liés aux objectifs personnels.
 * <p>
 * Gère la création, l'édition, la suppression et la visualisation détaillée (progression)
 * des objectifs fixés par un utilisateur.
 */
@Controller
public class ObjectifController {

    @Autowired
    private ObjectifService objectifService;

    /**
     * Affiche le formulaire de création d'un nouvel objectif.
     *
     * @return La vue {@code "createObjectif"}
     */
    @GetMapping("/objectifs/nouveau")
    public String nouveauObjectif(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        // Sonar Fix: Envoyer DTO
        model.addAttribute("objectif", new ObjectifFormDTO());
        model.addAttribute("typesSport", TypeSport.values());
        model.addAttribute("periodes", Periode.values());
        return "createObjectif";
    }

    @PostMapping("/objectifs/sauvegarder")
    public String sauvegarderObjectif(@ModelAttribute("objectif") ObjectifFormDTO dto, // Sonar Fix
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        // Sonar Fix: Mapping DTO -> Entity
        Objectif objectif = new Objectif();
        objectif.setDescription(dto.getDescription());
        objectif.setCible(dto.getCible());
        objectif.setUnite(dto.getUnite());
        objectif.setPeriode(dto.getPeriode());
        objectif.setTypeSport(dto.getTypeSport());
        
        if (dto.getDateDebut() == null) {
            objectif.setDateDebut(LocalDate.now());
        } else {
            objectif.setDateDebut(dto.getDateDebut());
        }
        objectif.setDateFin(dto.getDateFin());

        objectifService.creerObjectif(objectif, utilisateur);
        redirectAttributes.addFlashAttribute("success", "Objectif créé avec succès !");
        return "redirect:/profil";
    }

    /**
     * Affiche la page de détails d'un objectif, incluant le calcul de sa progression actuelle.
     *
     * @return La vue {@code "detailObjectif"}
     */
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

    /**
     * Affiche la page de modification d'un objectif existant.
     * Réutilise le même template Thymeleaf que pour la création.
     *
     * @return La vue {@code "createObjectif"} pré-remplie
     */
    @GetMapping("/objectifs/modifier/{id}")
    public String editerObjectifPage(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Objectif objectif = objectifService.getObjectifParId(id);
        if (objectif == null || !objectif.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Objectif introuvable ou accès refusé.");
            return "redirect:/profil";
        }

        // Sonar Fix: Mapping Entity -> DTO pour affichage
        ObjectifFormDTO dto = new ObjectifFormDTO();
        dto.setDescription(objectif.getDescription());
        dto.setCible(objectif.getCible());
        dto.setUnite(objectif.getUnite());
        dto.setPeriode(objectif.getPeriode());
        dto.setTypeSport(objectif.getTypeSport());
        dto.setDateDebut(objectif.getDateDebut());
        dto.setDateFin(objectif.getDateFin());

        model.addAttribute("objectif", dto);
        model.addAttribute("typesSport", TypeSport.values());
        model.addAttribute("periodes", Periode.values());
        
        return "createObjectif"; 
    }

    @PostMapping("/objectifs/modifier/{id}")
    public String updateObjectifSave(@PathVariable Long id, 
                                     @ModelAttribute("objectif") ObjectifFormDTO dto, // Sonar Fix
                                     HttpSession session, 
                                     RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Objectif existant = objectifService.getObjectifParId(id);
        if (existant != null && existant.getUtilisateur().getId().equals(utilisateur.getId())) {
            
            // Sonar Fix: Update manuel et sécurisé de l'Entity
            Objectif objectifModifie = new Objectif();
            objectifModifie.setDescription(dto.getDescription());
            objectifModifie.setCible(dto.getCible());
            objectifModifie.setUnite(dto.getUnite());
            objectifModifie.setPeriode(dto.getPeriode());
            objectifModifie.setTypeSport(dto.getTypeSport());
            objectifModifie.setDateDebut(dto.getDateDebut());
            objectifModifie.setDateFin(dto.getDateFin());
            
            objectifService.updateObjectif(id, objectifModifie);
            redirectAttributes.addFlashAttribute("success", "Objectif mis à jour avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Échec de la modification.");
        }
        return "redirect:/profil";
    }

    /**
     * Traite la suppression d'un objectif.
     *
     * @return Une redirection vers la page de profil
     */
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