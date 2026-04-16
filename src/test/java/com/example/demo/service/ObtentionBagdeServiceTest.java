package com.example.demo.service;

import com.example.demo.model.Badge;
import com.example.demo.model.ObtentionBadge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ObtentionBadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtentionBadgeServiceTest {

    @Mock
    private ObtentionBadgeRepository repository;

    @InjectMocks
    private ObtentionBadgeService service;

    @Test
    void testVerifierCondition() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        Badge badge = new Badge("Pro", "Desc", TypeSport.COURSE, 10f);
        badge.setId(1L);

        // Case 1: Already has badge
        when(repository.existsByUtilisateurIdAndBadgeId(1L, 1L)).thenReturn(true);
        assertFalse(service.verifierCondition(badge, user, TypeSport.COURSE, 15f));

        // Case 2: Wrong sport
        when(repository.existsByUtilisateurIdAndBadgeId(1L, 1L)).thenReturn(false);
        assertFalse(service.verifierCondition(badge, user, TypeSport.VELO, 15f));

        // Case 3: Value insufficient
        assertFalse(service.verifierCondition(badge, user, TypeSport.COURSE, 5f));

        // Case 4: Condition met
        assertTrue(service.verifierCondition(badge, user, TypeSport.COURSE, 15f));
    }

    @Test
    void testAttribuerBadgeSiCondition() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        Badge badge = new Badge("Pro", "Desc", TypeSport.COURSE, 10f);
        badge.setId(1L);

        when(repository.existsByUtilisateurIdAndBadgeId(1L, 1L)).thenReturn(false);
        when(repository.save(any())).thenReturn(new ObtentionBadge(user, badge));

        ObtentionBadge result = service.attribuerBadgeSiCondition(badge, user, TypeSport.COURSE, 15f);
        assertNotNull(result);

        // Fails condition
        assertNull(service.attribuerBadgeSiCondition(badge, user, TypeSport.COURSE, 5f));
    }
}