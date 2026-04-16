package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.ObtentionBadge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ObtentionBadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsable de la logique métier liée à l'obtention des badges par les utilisateurs.
 * <p>
 * Ce service agit comme un garde-fou pour éviter les doublons et s'assurer que les conditions
 * d'obtention sont strictement respectées avant de lier un {@link Badge} à un {@link Utilisateur}.
 */
@Service
public class ObtentionBadgeService {

    private final ObtentionBadgeRepository obtentionBadgeRepository;

    public ObtentionBadgeService(ObtentionBadgeRepository obtentionBadgeRepository) {
        this.obtentionBadgeRepository = obtentionBadgeRepository;
    }

    /**
     * Vérifie si un utilisateur a déjà débloqué un badge spécifique.
     *
     * @param utilisateur L'utilisateur concerné
     * @param badge       Le badge à vérifier
     * @return {@code true} si l'utilisateur possède déjà le badge, {@code false} sinon
     */
    public boolean utilisateurPossedeBadge(Utilisateur utilisateur, Badge badge) {
        return obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(
                utilisateur.getId(),
                badge.getId()
        );
    }

    /**
     * Évalue si les conditions pour obtenir un badge sont remplies par une activité.
     * Les conditions incluent la non-possession préalable, la correspondance du type de sport,
     * et l'atteinte du seuil requis.
     *
     * @param badge        Le badge dont on évalue les conditions
     * @param utilisateur  L'utilisateur réalisant l'activité
     * @param sportRealise Le type de sport pratiqué
     * @param valeur       La valeur réalisée (distance, temps, etc.) à comparer au seuil
     * @return {@code true} si toutes les conditions sont réunies, {@code false} sinon
     */
    public boolean verifierCondition(Badge badge,
                                     Utilisateur utilisateur,
                                     TypeSport sportRealise,
                                     float valeur) {

        if (utilisateurPossedeBadge(utilisateur, badge)) {
            return false;
        }

        if (!badge.getTypeSport().equals(sportRealise)) {
            return false;
        }

        return valeur >= badge.getSeuil();
    }

    /**
     * Attribue un badge à un utilisateur uniquement si toutes les conditions d'obtention sont remplies.
     *
     * @param badge        Le badge à attribuer potentiellement
     * @param utilisateur  L'utilisateur bénéficiaire potentiel
     * @param sportRealise Le type de sport pratiqué
     * @param valeur       La valeur réalisée
     * @return L'entité {@link ObtentionBadge} sauvegardée si l'attribution a réussi, {@code null} sinon
     */
    public ObtentionBadge attribuerBadgeSiCondition(Badge badge,
                                                    Utilisateur utilisateur,
                                                    TypeSport sportRealise,
                                                    float valeur) {

        if (verifierCondition(badge, utilisateur, sportRealise, valeur)) {
            return attribuerBadge(utilisateur, badge);
        }

        return null;
    }

    /**
     * Attribue directement un badge à un utilisateur et sauvegarde cette relation en base de données,
     * sans vérifier les conditions préalables.
     *
     * @param utilisateur L'utilisateur qui reçoit le badge
     * @param badge       Le badge accordé
     * @return L'entité de liaison {@link ObtentionBadge} nouvellement créée
     */
    public ObtentionBadge attribuerBadge(Utilisateur utilisateur, Badge badge) {
        ObtentionBadge obtention = new ObtentionBadge(utilisateur, badge);
        return obtentionBadgeRepository.save(obtention);
    }

    /**
     * Récupère l'historique complet de tous les badges débloqués par un utilisateur.
     *
     * @param utilisateur L'utilisateur concerné
     * @return Une liste des obtentions de badges pour cet utilisateur
     */
    public List<ObtentionBadge> getBadgesUtilisateur(Utilisateur utilisateur) {
        return obtentionBadgeRepository.findByUtilisateurId(utilisateur.getId());
    }
}