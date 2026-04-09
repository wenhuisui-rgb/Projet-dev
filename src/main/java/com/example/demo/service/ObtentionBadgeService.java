package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.ObtentionBadge;
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

    public ObtentionBadge attribuerBadge(Utilisateur utilisateur, Badge badge) {
        ObtentionBadge obtention = new ObtentionBadge(utilisateur, badge);
        return obtentionBadgeRepository.save(obtention);
    }

    public List<ObtentionBadge> getBadgesUtilisateur(Utilisateur utilisateur) {
        return obtentionBadgeRepository.findByUtilisateurId(utilisateur.getId());
    }
}