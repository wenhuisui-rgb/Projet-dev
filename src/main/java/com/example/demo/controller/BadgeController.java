package com.example.demo.controller;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.service.BadgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST fournissant une API pour la gestion du catalogue des badges.
 * <p>
 * Note: Utilisant {@code @RestController}, les méthodes de cette classe sérialisent
 * automatiquement les objets retournés en format JSON pour le client (frontend/API),
 * au lieu de résoudre des vues Thymeleaf.
 */
@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    /**
     * Crée un nouveau badge dans le système via une requête HTTP POST.
     *
     * @param nom         Le nom du badge
     * @param description La description du badge
     * @param typeSport   Le sport concerné
     * @param seuil       Le seuil nécessaire pour l'obtenir
     * @return Le {@link Badge} créé, formaté en JSON
     */
    @PostMapping
    public Badge creerBadge(@RequestParam String nom,
                            @RequestParam String description,
                            @RequestParam TypeSport typeSport,
                            @RequestParam float seuil) {

        return badgeService.creerBadge(nom, description, typeSport, seuil);
    }

    /**
     * Expose la liste complète des badges disponibles dans le système.
     *
     * @return Une liste de {@link Badge} au format JSON
     */
    @GetMapping
    public List<Badge> listerTousLesBadges() {
        return badgeService.listerTousLesBadges();
    }

    /**
     * Récupère les détails d'un badge spécifique via son identifiant.
     *
     * @param id L'identifiant du badge à rechercher
     * @return Le {@link Badge} correspondant, au format JSON
     */
    @GetMapping("/{id}")
    public Badge getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id);
    }
}