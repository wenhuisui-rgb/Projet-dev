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

    
    //Vérifie si un utilisateur peut obtenir le badge selon le sport et le seuil
    
    public boolean verifierCondition(Badge badge, Utilisateur utilisateur, TypeSport sportRealise, float valeur) {
        
        if (obtentionBadgeService.utilisateurPossedeBadge(utilisateur, badge)) {
            return false;
        }
        
        if (!badge.getTypeSport().equals(sportRealise)) {
            return false;
        }

        return valeur >= badge.getSeuil();
    }
}