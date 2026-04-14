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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtentionBadgeServiceTest {

    @Mock
    private ObtentionBadgeRepository obtentionBadgeRepository;

    @InjectMocks
    private ObtentionBadgeService obtentionBadgeService;

    @Test
    void testUtilisateurPossedeBadge_true() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(true);

        boolean result = obtentionBadgeService.utilisateurPossedeBadge(user, badge);

        assertTrue(result);
    }

    @Test
    void testUtilisateurPossedeBadge_false() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(false);

        boolean result = obtentionBadgeService.utilisateurPossedeBadge(user, badge);

        assertFalse(result);
    }

    @Test
    void testVerifierCondition_ok() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(false);

        boolean result = obtentionBadgeService.verifierCondition(
                badge, user, TypeSport.COURSE, 10f
        );

        assertTrue(result);
    }

    @Test
    void testVerifierCondition_badSport() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(false);

        boolean result = obtentionBadgeService.verifierCondition(
                badge, user, TypeSport.NATATION, 10f
        );

        assertFalse(result);
    }

    @Test
    void testVerifierCondition_badSeuil() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(10f);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(false);

        boolean result = obtentionBadgeService.verifierCondition(
                badge, user, TypeSport.COURSE, 5f
        );

        assertFalse(result);
    }

    @Test
    void testAttribuerBadge() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

        ObtentionBadge saved = new ObtentionBadge(user, badge);

        when(obtentionBadgeRepository.save(any(ObtentionBadge.class)))
                .thenReturn(saved);

        ObtentionBadge result = obtentionBadgeService.attribuerBadge(user, badge);

        assertNotNull(result);
        assertEquals(user, result.getUtilisateur());
        assertEquals(badge, result.getBadge());
    }

    @Test
    void testAttribuerBadgeSiCondition_ok() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(false);

        when(obtentionBadgeRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ObtentionBadge result = obtentionBadgeService.attribuerBadgeSiCondition(
                badge, user, TypeSport.COURSE, 10f
        );

        assertNotNull(result);
    }

    @Test
    void testAttribuerBadgeSiCondition_fail() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);

        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(1L, 10L))
                .thenReturn(true);

        ObtentionBadge result = obtentionBadgeService.attribuerBadgeSiCondition(
                badge, user, TypeSport.COURSE, 10f
        );

        assertNull(result);
    }

   
}