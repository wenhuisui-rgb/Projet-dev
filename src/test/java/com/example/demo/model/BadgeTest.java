package com.example.demo.model;


import com.example.demo.repository.BadgeRepository;

import com.example.demo.service.BadgeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BadgeServiceTest {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Test
    void testCreerBadge() {
        Badge badge = badgeService.creerBadge(
                "Runner 5K",
                "Courir 5 km",
                TypeSport.COURSE,
                5.0f
        );

        assertNotNull(badge.getId());
        assertEquals("Runner 5K", badge.getNom());
        assertEquals(5.0f, badge.getSeuil());
    }

    @Test
    void testGetBadgeById() {
        Badge badge = new Badge("Test", "Desc");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(10f);

        badge = badgeRepository.save(badge);

        Badge found = badgeService.getBadgeById(badge.getId());

        assertNotNull(found);
        assertEquals(badge.getId(), found.getId());
    }

    @Test
    void testListerBadges() {
        assertNotNull(badgeService.listerTousLesBadges());
    }
}