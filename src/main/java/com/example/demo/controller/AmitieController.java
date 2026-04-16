package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur gérant les vues et les actions liées au système d'amitié.
 * <p>
 * Il intercepte les requêtes web pour envoyer des demandes d'amis, les accepter ou les refuser,
 * et prépare les données nécessaires pour la vue Thymeleaf correspondante.
 */
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

    /**
     * Traite l'envoi d'une demande d'amitié vers un autre utilisateur.
     *
     * @param id                 L'identifiant de l'utilisateur à qui on souhaite envoyer la demande
     * @param session            La session HTTP pour récupérer l'utilisateur connecté
     * @param redirectAttributes Permet de passer des messages flash ("friendStatus") à la vue après la redirection
     * @return Une redirection vers la page "/mesAmis" (ou vers "/connexion" si non connecté)
     */
    @GetMapping("/ajouter/{id}")
    public String ajouter(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        Utilisateur sessionUser =
                (Utilisateur) session.getAttribute("utilisateur");

        if (sessionUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur receveur = utilisateurService.findById(id);

        String result =
                amitieService.envoyerDemande(sessionUser, receveur);

        redirectAttributes.addFlashAttribute("friendStatus", result);

        return "redirect:/mesAmis";
    }

    /**
     * Traite l'acceptation d'une demande d'amitié reçue.
     *
     * @param amitieID L'identifiant de la relation d'amitié à accepter
     * @return Une redirection vers la page "/mesAmis"
     */
    @GetMapping("/{amitieID}/accepter")
    public String accepter(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        amitieService.accepterDemande(amitie);
        return "redirect:/mesAmis";
    }

    /**
     * Traite le refus d'une demande d'amitié reçue.
     *
     * @param amitieID L'identifiant de la relation d'amitié à refuser
     * @return Une redirection vers la page "/mesAmis"
     */
    @GetMapping("/{amitieID}/refuser")
    public String refuser(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        amitieService.refuserDemande(amitie);
        return "redirect:/mesAmis";
    }

    /**
     * Affiche la page principale de gestion des amis.
     * Injecte dans le modèle la liste des amis validés, les demandes reçues et envoyées.
     *
     * @param session La session HTTP actuelle
     * @param model   Le conteneur de données pour la vue Thymeleaf
     * @return Le nom de la vue Thymeleaf {@code "mesAmis"}
     */
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