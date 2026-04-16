package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
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

    @GetMapping("/{amitieID}/accepter")
    public String accepter(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        amitieService.accepterDemande(amitie);
        return "redirect:/mesAmis";
    }

    @GetMapping("/{amitieID}/refuser")
    public String refuser(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        amitieService.refuserDemande(amitie);
        return "redirect:/mesAmis";
    }

    @GetMapping("/mesAmis")
    public String mesAmis(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        List<Utilisateur> amis = amitieService.getAmis(utilisateur);
        model.addAttribute("amis", amis);
        
        List<Amitie> demandesRecues = amitieService.getDemandesRecues(utilisateur);
        model.addAttribute("demandesRecues", demandesRecues);
        
        List<Long> demandesEnvoyeesIds = amitieService.getDemandesEnvoyeesIds(utilisateur);
        model.addAttribute("demandesEnvoyees", demandesEnvoyeesIds);
        
        return "mesAmis";
    }
}