package com.example.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObtentionBadgeTest {

    @Test
    @DisplayName("Test constructeur métier ObtentionBadge")
    void testConstructeurMetier() {

        Utilisateur user = new Utilisateur();
        user.setPseudo("Runner31");

        Badge badge = new Badge("Marathonien", "A couru 42km");

        ObtentionBadge obtention = new ObtentionBadge(user, badge);

        assertEquals(user, obtention.getUtilisateur());
        assertEquals(badge, obtention.getBadge());
        assertNotNull(obtention.getDateObtention());
    }

    @Test
    @DisplayName("Test setters et date manuelle")
    void testSetters() {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

        LocalDateTime date = LocalDateTime.now();

        ObtentionBadge obtention = new ObtentionBadge();
        obtention.setUtilisateur(user);
        obtention.setBadge(badge);
        obtention.setDateObtention(date);

        assertEquals(user, obtention.getUtilisateur());
        assertEquals(badge, obtention.getBadge());
        assertEquals(date, obtention.getDateObtention());
    }

    @Test
    @DisplayName("Test relations IDs")
    void testRelationsIds() {

        Utilisateur user = new Utilisateur();
        user.setId(50L);

        Badge badge = new Badge();
        badge.setId(99L);

        ObtentionBadge obtention = new ObtentionBadge();
        obtention.setUtilisateur(user);
        obtention.setBadge(badge);

        assertAll(
            () -> assertEquals(50L, obtention.getUtilisateur().getId()),
            () -> assertEquals(99L, obtention.getBadge().getId())
        );

    }
}