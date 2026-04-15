package com.example.demo.controller;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.service.BadgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    public Badge creerBadge(@RequestParam String nom,@RequestParam String description,@RequestParam TypeSport typeSport,@RequestParam float seuil) {

        return badgeService.creerBadge(nom, description, typeSport, seuil);
    }

    @GetMapping
    public List<Badge> listerTousLesBadges() {
        return badgeService.listerTousLesBadges();
    }

    
    @GetMapping("/{id}")
    public Badge getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id);
    }

    
}