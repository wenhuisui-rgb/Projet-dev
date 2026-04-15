package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/amities")
public class AmitieController {

    private final AmitieService amitieService;
    private final UtilisateurService utilisateurService;

    public AmitieController(AmitieService amitieService,
                            UtilisateurService utilisateurService) {
        this.amitieService = amitieService;
        this.utilisateurService = utilisateurService;
    }

    // AJOUTER AMI
    @GetMapping("/ajouter/{id}")
    public String ajouter(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        Utilisateur sessionUser =
                (Utilisateur) session.getAttribute("utilisateur");

        if (sessionUser == null) return "redirect:/connexion";

        Utilisateur receveur = utilisateurService.findById(id);

        String result = amitieService.envoyerDemande(sessionUser, receveur);

        redirectAttributes.addFlashAttribute("friendStatus", result);

        return "redirect:/mesAmis";
    }

    // ACCEPTER
    @PostMapping("/{id}/accepter")
    public String accepter(@PathVariable Long id) {
        Amitie amitie = amitieService.getById(id);
        amitieService.accepterDemande(amitie);
        return "redirect:/mesAmis";
    }

    // REFUSER
    @PostMapping("/{id}/refuser")
    public String refuser(@PathVariable Long id) {
        Amitie amitie = amitieService.getById(id);
        amitieService.refuserDemande(amitie);
        return "redirect:/mesAmis";
    }

    // SUPPRIMER AMI (PROPRE VERSION UNIQUE)
    @PostMapping("/supprimer/{userId}")
    public String supprimer(@PathVariable Long userId,
                            HttpSession session) {

        Utilisateur sessionUser =
                (Utilisateur) session.getAttribute("utilisateur");

        if (sessionUser == null) return "redirect:/connexion";

        Utilisateur ami = utilisateurService.findById(userId);

        amitieService.supprimerAmi(sessionUser, ami);

        return "redirect:/mesAmis";
    }
}