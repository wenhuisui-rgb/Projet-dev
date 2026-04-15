package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final ObtentionBadgeService obtentionBadgeService;

    public BadgeService(BadgeRepository badgeRepository, ObtentionBadgeService obtentionBadgeService) {
        this.badgeRepository = badgeRepository;
        this.obtentionBadgeService = obtentionBadgeService;
    }

    public Badge creerBadge(String nom, String description, TypeSport typeSport, float seuil) {
        Badge badge = new Badge(nom, description);
        badge.setTypeSport(typeSport);
        badge.setSeuil(seuil);
        return badgeRepository.save(badge);
    }

    public List<Badge> listerTousLesBadges() {
        return badgeRepository.findAll();
    }

    public Badge getBadgeById(Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    public void verifierBadgePourUtilisateur(Utilisateur utilisateur, TypeSport typeSport, float distance) {
        // On récupère TOUS les badges de la base
        List<Badge> tousLesBadges = badgeRepository.findAll();

        // on parcourt tous les badges pour trouver ceux qui correspondent au type de sport et dont le seuil est atteint
       
        for (Badge badge : tousLesBadges) {
            if (badge.getTypeSport() == typeSport && distance >= badge.getSeuil()) {
                //on fait l'attribution du badge à l'utilisateur
                obtentionBadgeService.attribuerBadge(utilisateur, badge);
            }
        }
    }

}