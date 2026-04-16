package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.BadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private ObtentionBadgeService obtentionBadgeService;

    @InjectMocks
    private BadgeService badgeService;

    @Test
    void testCreerBadge() {
        Badge badgeToSave = new Badge("Pro", "Desc");
        when(badgeRepository.save(any(Badge.class))).thenReturn(badgeToSave);

        Badge result = badgeService.creerBadge("Pro", "Desc", TypeSport.COURSE, 10f);
        assertNotNull(result);
        verify(badgeRepository).save(any(Badge.class));
    }

    @Test
    void testListerTousLesBadges() {
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(new Badge()));
        assertEquals(1, badgeService.listerTousLesBadges().size());
    }

    @Test
    void testGetBadgeById() {
        Badge badge = new Badge();
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(badge));
        assertEquals(badge, badgeService.getBadgeById(1L));

        when(badgeRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(badgeService.getBadgeById(2L));
    }

    @Test
    void testVerifierBadgePourUtilisateur() {
        Utilisateur user = new Utilisateur();
        
        Badge badge1 = new Badge("10km", "Course 10k", TypeSport.COURSE, 10f);
        Badge badge2 = new Badge("20km", "Course 20k", TypeSport.COURSE, 20f);
        Badge badge3 = new Badge("Velo10k", "Velo", TypeSport.VELO, 10f);

        when(badgeRepository.findAll()).thenReturn(Arrays.asList(badge1, badge2, badge3));

        // Test running 15km
        badgeService.verifierBadgePourUtilisateur(user, TypeSport.COURSE, 15f);

        // badge1 should be attributed (COURSE, 15 >= 10)
        verify(obtentionBadgeService, times(1)).attribuerBadge(user, badge1);
        // badge2 should NOT be attributed (COURSE, 15 < 20)
        verify(obtentionBadgeService, never()).attribuerBadge(user, badge2);
        // badge3 should NOT be attributed (Wrong sport)
        verify(obtentionBadgeService, never()).attribuerBadge(user, badge3);
    }
}