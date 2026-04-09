package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.repository.BadgeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public Badge creerBadge(String nom, String description) {
        Badge badge = new Badge(nom, description);
        return badgeRepository.save(badge);
    }

    public List<Badge> listerTousLesBadges() {
        return badgeRepository.findAll();
    }

    public Badge getBadgeById(Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    public boolean verifierCondition(Badge badge, com.example.demo.model.Utilisateur utilisateur) {
        return true;
    }
}