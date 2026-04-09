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

    
    //Vérifie si l'utilisateur possède déjà un badge
    
    public boolean utilisateurPossedeBadge(Utilisateur utilisateur, Badge badge) {
        return obtentionBadgeRepository.findByUtilisateurId(utilisateur.getId())
                .stream()
                .anyMatch(obt -> obt.getBadge().getId().equals(badge.getId()));
    }

    public ObtentionBadge attribuerBadgeSiCondition(Badge badge, Utilisateur utilisateur, 
                                                    TypeSport sportRealise, float valeur, BadgeService badgeService) {
        if (badgeService.verifierCondition(badge, utilisateur, sportRealise, valeur)) {
            return attribuerBadge(utilisateur, badge);
        }
        return null; 
    }


    public ObtentionBadge attribuerBadge(Utilisateur utilisateur, Badge badge) {
        ObtentionBadge obtention = new ObtentionBadge(utilisateur, badge);
        return obtentionBadgeRepository.save(obtention);
    }

    public List<ObtentionBadge> getBadgesUtilisateur(Utilisateur utilisateur) {
        return obtentionBadgeRepository.findByUtilisateurId(utilisateur.getId());
    }
}