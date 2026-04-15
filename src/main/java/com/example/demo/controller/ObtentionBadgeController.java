package com.example.demo.controller;

import com.example.demo.model.Badge;
import com.example.demo.model.ObtentionBadge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.BadgeService;
import com.example.demo.service.ObtentionBadgeService;
import com.example.demo.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges/obtention")
public class ObtentionBadgeController {

    private final ObtentionBadgeService obtentionBadgeService;
    private final BadgeService badgeService;
    private final UtilisateurService utilisateurService;

    public ObtentionBadgeController(ObtentionBadgeService obtentionBadgeService, BadgeService badgeService,
            UtilisateurService utilisateurService) {
        this.obtentionBadgeService = obtentionBadgeService;
        this.badgeService = badgeService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/tester-ajout")
public ObtentionBadge testerAjout(@RequestParam Long utilisateurId, @RequestParam Long badgeId) {
    Utilisateur utilisateur = utilisateurService.findById(utilisateurId);
    Badge badge = badgeService.getBadgeById(badgeId);
    return obtentionBadgeService.attribuerBadge(utilisateur, badge);
}

    @PostMapping("/attribuer-condition")
    public ObtentionBadge attribuerSiCondition(@RequestParam Long utilisateurId, @RequestParam Long badgeId,
            @RequestParam TypeSport sport, @RequestParam float valeur) {

        Utilisateur utilisateur = utilisateurService.findById(utilisateurId);
        Badge badge = badgeService.getBadgeById(badgeId);

        return obtentionBadgeService.attribuerBadgeSiCondition(badge, utilisateur, sport, valeur);
    }

    @GetMapping("/utilisateur/{id}")
    public List<ObtentionBadge> getBadgesUtilisateur(@PathVariable Long id) {

        Utilisateur utilisateur = utilisateurService.findById(id);

        return obtentionBadgeService.getBadgesUtilisateur(utilisateur);
    }
}