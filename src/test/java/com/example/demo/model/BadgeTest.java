package com.example.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitaire de l'entité Badge (Modèle)
 * Vérifie que les données du badge sont correctement stockées et accessibles.
 */
class BadgeTest {

    @Test
    @DisplayName("Devrait créer un badge avec toutes ses propriétés via le constructeur et setters")
    void testCreationBadge() {
        // 1. Arrange (Préparation)
        Badge badge = new Badge("Runner 5K", "Courir 5 km");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5.0f);

        // 2. Act (Action) - Ici on vérifie simplement les valeurs stockées
       
        // 3. Assert (Vérification)
        assertAll("Vérification des propriétés du Badge",
            () -> assertEquals("Runner 5K", badge.getNom(), "Le nom doit correspondre"),
            () -> assertEquals("Courir 5 km", badge.getDescription(), "La description doit correspondre"),
            () -> assertEquals(TypeSport.COURSE, badge.getTypeSport(), "Le type de sport doit correspondre"),
            () -> assertEquals(5.0f, badge.getSeuil(), "Le seuil doit être de 5.0")
        );
    }

    @Test
    @DisplayName("Devrait gérer correctement l'ID du badge")
    void testBadgeId() {
        Badge badge = new Badge();
        badge.setId(10L);
       
        assertEquals(10L, badge.getId(), "L'ID récupéré doit être celui que l'on a défini");
    }

    @Test
    @DisplayName("Vérifie les setters individuellement")
    void testSetters() {
        Badge badge = new Badge();
        badge.setNom("Grimpeur");
        badge.setDescription("Atteindre 1000m de dénivelé");
       
        assertEquals("Grimpeur", badge.getNom());
        assertTrue(badge.getDescription().contains("1000m"));
    }
}