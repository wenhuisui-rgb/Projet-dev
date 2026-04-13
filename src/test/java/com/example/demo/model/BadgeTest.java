package com.example.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class BadgeTest {

    @Test
    @DisplayName("Création badge complet")
    void testCreationBadge() {

        Badge badge = new Badge("Runner 5K", "Courir 5 km");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5.0f);

        assertAll(
            () -> assertEquals("Runner 5K", badge.getNom()),
            () -> assertEquals("Courir 5 km", badge.getDescription()),
            () -> assertEquals(TypeSport.COURSE, badge.getTypeSport()),
            () -> assertEquals(5.0f, badge.getSeuil())
        );
    }

    @Test
    @DisplayName("Constructeur simple fonctionne correctement")
    void testConstructeurSimple() {

        Badge badge = new Badge("Nom", "Desc");

        assertEquals("Nom", badge.getNom());
        assertEquals("Desc", badge.getDescription());
    }

    @Test
    @DisplayName("Test setters individuels")
    void testSetters() {

        Badge badge = new Badge();

        badge.setNom("Grimpeur");
        badge.setDescription("Atteindre 1000m de dénivelé");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(12.5f);

        assertEquals("Grimpeur", badge.getNom());
        assertEquals("Atteindre 1000m de dénivelé", badge.getDescription());
        assertEquals(TypeSport.COURSE, badge.getTypeSport());
        assertEquals(12.5f, badge.getSeuil());
    }

    @Test
    @DisplayName("Test ID badge")
    void testId() {

        Badge badge = new Badge();
        badge.setId(10L);

        assertEquals(10L, badge.getId());
    }

    @Test
    @DisplayName("Les obtentions doivent être null ou initialisables")
    void testObtentions() {

        Badge badge = new Badge();

        assertTrue(
            badge.getObtentions() == null || badge.getObtentions().isEmpty(),
            "La liste d'obtentions doit être null ou vide"
        );
    }
}