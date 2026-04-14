package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.repository.BadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeService badgeService;

    @Test
    void testCreerBadge() {

        Badge badgeMock = new Badge("Runner 5K", "Courir 5 km");
        badgeMock.setId(1L);
        badgeMock.setTypeSport(TypeSport.COURSE);
        badgeMock.setSeuil(5f);

        when(badgeRepository.save(any(Badge.class)))
                .thenReturn(badgeMock);

        Badge result = badgeService.creerBadge(
                "Runner 5K",
                "Courir 5 km",
                TypeSport.COURSE,
                5f
        );

        assertNotNull(result);
        assertEquals("Runner 5K", result.getNom());
        assertEquals("Courir 5 km", result.getDescription());
        assertEquals(TypeSport.COURSE, result.getTypeSport());
        assertEquals(5f, result.getSeuil());
    }

    @Test
    void testListerTousLesBadges() {

        Badge b1 = new Badge("5K", "Courir 5 km");
        Badge b2 = new Badge("10K", "Courir 10 km");

        when(badgeRepository.findAll())
                .thenReturn(List.of(b1, b2));

        List<Badge> result = badgeService.listerTousLesBadges();

        assertEquals(2, result.size());
        assertEquals("5K", result.get(0).getNom());
    }

    @Test
    void testGetBadgeById_Trouve() {

        Badge badge = new Badge("Marathon", "42 km");
        badge.setId(10L);

        when(badgeRepository.findById(10L))
                .thenReturn(Optional.of(badge));

        Badge result = badgeService.getBadgeById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testGetBadgeById_NullSiAbsent() {

        when(badgeRepository.findById(99L))
                .thenReturn(Optional.empty());

        Badge result = badgeService.getBadgeById(99L);

        assertNull(result);
    }
}
