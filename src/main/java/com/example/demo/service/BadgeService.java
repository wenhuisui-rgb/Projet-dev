package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service gérant le catalogue des badges (création, consultation).
 * <p>
 * Il parcourt les règles métier pour déterminer si une activité permet de débloquer un badge
 * et délègue l'attribution effective à {@link ObtentionBadgeService}.
 */
@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final ObtentionBadgeService obtentionBadgeService;

    public BadgeService(BadgeRepository badgeRepository, ObtentionBadgeService obtentionBadgeService) {
        this.badgeRepository = badgeRepository;
        this.obtentionBadgeService = obtentionBadgeService;
    }

    /**
     * Crée et enregistre un nouveau badge dans le système.
     *
     * @param nom         Le nom du badge (ex: "Marathonien")
     * @param description La description du badge
     * @param typeSport   Le sport ciblé par ce badge
     * @param seuil       La valeur à atteindre pour débloquer le badge (ex: distance de 42.195)
     * @return Le badge créé et sauvegardé
     */
    public Badge creerBadge(String nom, String description, TypeSport typeSport, float seuil) {
        Badge badge = new Badge(nom, description);
        badge.setTypeSport(typeSport);
        badge.setSeuil(seuil);
        return badgeRepository.save(badge);
    }

    /**
     * Récupère la liste de tous les badges disponibles dans le système.
     *
     * @return Une liste contenant tous les badges
     */
    public List<Badge> listerTousLesBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Récupère un badge spécifique par son identifiant.
     *
     * @param id L'identifiant du badge
     * @return Le badge correspondant, ou {@code null} s'il n'est pas trouvé
     */
    public Badge getBadgeById(Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    /**
     * Vérifie si un utilisateur remplit les conditions pour obtenir de nouveaux badges
     * suite à la réalisation d'une activité sportive, et les lui attribue le cas échéant.
     *
     * @param utilisateur L'utilisateur à vérifier
     * @param typeSport   Le type de sport de l'activité venant d'être réalisée
     * @param distance    La distance (ou métrique) réalisée lors de l'activité
     * @see ObtentionBadgeService#attribuerBadge(Utilisateur, Badge)
     */
    public void verifierBadgePourUtilisateur(Utilisateur utilisateur, TypeSport typeSport, float distance) {
        List<Badge> tousLesBadges = badgeRepository.findAll();
       
        for (Badge badge : tousLesBadges) {
            if (badge.getTypeSport() == typeSport && distance >= badge.getSeuil()) {
                obtentionBadgeService.attribuerBadge(utilisateur, badge);
            }
        }
    }
}