package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.ObtentionBadge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ObtentionBadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObtentionBadgeService {

    private final ObtentionBadgeRepository obtentionBadgeRepository;

    public ObtentionBadgeService(ObtentionBadgeRepository obtentionBadgeRepository) {
        this.obtentionBadgeRepository = obtentionBadgeRepository;
    }

    /**
     * Vérifie si l'utilisateur possède déjà ce badge
     */
    public boolean utilisateurPossedeBadge(Utilisateur utilisateur, Badge badge) {
        return obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(
                utilisateur.getId(),
                badge.getId()
        );
    }

    /**
     * Vérifie si les conditions d'obtention du badge sont remplies
     */
    public boolean verifierCondition(Badge badge,
                                     Utilisateur utilisateur,
                                     TypeSport sportRealise,
                                     float valeur) {

        // Badge déjà obtenu
        if (utilisateurPossedeBadge(utilisateur, badge)) {
            return false;
        }

        // Mauvais type de sport
        if (!badge.getTypeSport().equals(sportRealise)) {
            return false;
        }

        // Valeur insuffisante
        return valeur >= badge.getSeuil();
    }

    /**
     * Attribue le badge si les conditions sont remplies
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
     * Attribue directement un badge à un utilisateur
     */
    public ObtentionBadge attribuerBadge(Utilisateur utilisateur, Badge badge) {
        ObtentionBadge obtention = new ObtentionBadge(utilisateur, badge);
        return obtentionBadgeRepository.save(obtention);
    }

    /**
     * Récupère tous les badges obtenus par un utilisateur
     */
    public List<ObtentionBadge> getBadgesUtilisateur(Utilisateur utilisateur) {
        return obtentionBadgeRepository.findByUtilisateurId(utilisateur.getId());
    }
}