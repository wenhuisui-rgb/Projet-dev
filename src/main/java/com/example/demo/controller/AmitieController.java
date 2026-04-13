package com.example.demo.controller;

import com.example.demo.model.Amitie;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.AmitieService;
import com.example.demo.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amities")
public class AmitieController {

    private final AmitieService amitieService;
    private final UtilisateurService utilisateurService;

    public AmitieController(AmitieService amitieService,
                            UtilisateurService utilisateurService) {
        this.amitieService = amitieService;
        this.utilisateurService = utilisateurService;
    }

    
    //Envoyer une demande d'amitié
     
    @PostMapping("/demande")
    public String envoyerDemande(@RequestParam Long demandeurID,
                                 @RequestParam String pseudoReceveur) {

        // récupération du demandeur via ID
        Utilisateur demandeur = utilisateurService.findById(demandeurID);

        // conversion pseudo → utilisateur
        Utilisateur receveur = utilisateurService.findByPseudo(pseudoReceveur);

        if (demandeur == null || receveur == null) {
            return "Utilisateur introuvable.";
        }

        // sécurité : éviter de s'ajouter soi-même
        if (demandeur.equals(receveur)) {
            return "Vous ne pouvez pas vous ajouter vous-même.";
        }

        return amitieService.envoyerDemande(demandeur, receveur);
    }

    /**
     * Accepter une demande
     */
    @PutMapping("/{amitieID}/accepter")
    public Amitie accepterDemande(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        return amitieService.accepterDemande(amitie);
    }

    /**
     * Refuser une demande
     */
    @PutMapping("/{amitieID}/refuser")
    public Amitie refuserDemande(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        return amitieService.refuserDemande(amitie);
    }

    /**
     * Annuler une demande
     */
    @PutMapping("/{amitieID}/annuler")
    public Amitie annulerDemande(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        return amitieService.annulerDemande(amitie);
    }

    /**
     * Rompre une amitié
     */
    @DeleteMapping("/{amitieID}")
    public String rompreAmitie(@PathVariable Long amitieID) {
        Amitie amitie = amitieService.getById(amitieID);
        amitieService.rompreAmitie(amitie);
        return "Amitié supprimée avec succès.";
    }

    /**
     * Liste complète des amitiés d’un utilisateur
     */
    @GetMapping("/utilisateurs/{id}")
    public List<Amitie> getAmitieUtilisateur(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.findById(id);
        return amitieService.getAmities(utilisateur);
    }

    /**
     * Liste des amitiés acceptées
     */
    @GetMapping("/utilisateurs/{id}/acceptees")
    public List<Amitie> getAmitiesAcceptees(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.findById(id);
        return amitieService.getAmitiesAcceptees(utilisateur);
    }
}