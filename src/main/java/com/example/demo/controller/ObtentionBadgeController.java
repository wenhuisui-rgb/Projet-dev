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

/**
 * Contrôleur REST gérant les requêtes liées à l'attribution et la consultation des badges obtenus.
 * <p>
 * Toutes les méthodes de ce contrôleur renvoient des données au format JSON,
 * idéales pour des appels asynchrones (AJAX/Fetch) depuis le frontend.
 */
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

    /**
     * API de test : Attribue directement un badge à un utilisateur sans vérifier les conditions.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param badgeId       L'identifiant du badge à attribuer
     * @return L'entité {@link ObtentionBadge} créée, formatée en JSON
     */
    @GetMapping("/tester-ajout")
    public ObtentionBadge testerAjout(@RequestParam Long utilisateurId, @RequestParam Long badgeId) {
        Utilisateur utilisateur = utilisateurService.findById(utilisateurId);
        Badge badge = badgeService.getBadgeById(badgeId);
        return obtentionBadgeService.attribuerBadge(utilisateur, badge);
    }

    /**
     * API REST : Attribue un badge à un utilisateur uniquement si les conditions sportives sont remplies.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param badgeId       L'identifiant du badge visé
     * @param sport         Le type de sport pratiqué
     * @param valeur        La valeur accomplie (distance, temps, etc.)
     * @return L'entité {@link ObtentionBadge} si succès, ou null/vide si les conditions ne sont pas réunies (JSON)
     */
    @PostMapping("/attribuer-condition")
    public ObtentionBadge attribuerSiCondition(@RequestParam Long utilisateurId, @RequestParam Long badgeId,
            @RequestParam TypeSport sport, @RequestParam float valeur) {

        Utilisateur utilisateur = utilisateurService.findById(utilisateurId);
        Badge badge = badgeService.getBadgeById(badgeId);

        return obtentionBadgeService.attribuerBadgeSiCondition(badge, utilisateur, sport, valeur);
    }

    /**
     * API REST : Récupère la liste complète des badges obtenus par un utilisateur spécifique.
     *
     * @param id L'identifiant de l'utilisateur
     * @return Une liste d'{@link ObtentionBadge} au format JSON
     */
    @GetMapping("/utilisateur/{id}")
    public List<ObtentionBadge> getBadgesUtilisateur(@PathVariable Long id) {

        Utilisateur utilisateur = utilisateurService.findById(id);

        return obtentionBadgeService.getBadgesUtilisateur(utilisateur);
    }
}