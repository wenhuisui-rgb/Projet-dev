package com.example.demo.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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
    void testConstructeurSimple() {
        Badge badge = new Badge("Nom", "Desc");

        assertEquals("Nom", badge.getNom());
        assertEquals("Desc", badge.getDescription());
    }

    @Test
    void testSetters() {
        Badge badge = new Badge();

        badge.setNom("Grimpeur");
        badge.setDescription("Atteindre 1000m de dénivelé");

        assertEquals("Grimpeur", badge.getNom());
        assertEquals("Atteindre 1000m de dénivelé", badge.getDescription());
    }

    @Test
    void testSeuil() {
        Badge badge = new Badge();
        badge.setSeuil(10f);

        assertEquals(10f, badge.getSeuil());
    }

    @Test
    void testId() {
        Badge badge = new Badge();
        badge.setId(10L);

        assertEquals(10L, badge.getId());
    }

    @Test
    void testObtentions() {
        Badge badge = new Badge();

        assertNull(badge.getObtentions());
    }
}