package com.example.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;


class ObtentionBadgeTest {

    @Test
    @DisplayName("Devrait créer une instance d'ObtentionBadge avec les bonnes données")
    void testCreationObtention() {
        // 1. Préparation (Arrange)
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        user.setPseudo("Runner31");

        Badge badge = new Badge("Marathonien", "A couru 42km");
        badge.setId(10L);

        LocalDateTime maintenant = LocalDateTime.now();

        // 2. Action (Act)
        ObtentionBadge obtention = new ObtentionBadge();
        obtention.setUtilisateur(user);
        obtention.setBadge(badge);
        obtention.setDateObtention(maintenant);

        // 3. Vérification (Assert)
        assertNotNull(obtention, "L'objet obtention ne devrait pas être nul");
        assertEquals("Runner31", obtention.getUtilisateur().getPseudo(),
                "Le pseudo de l'utilisateur doit correspondre");
        assertEquals("Marathonien", obtention.getBadge().getNom(), "Le nom du badge doit correspondre");
        assertEquals(maintenant, obtention.getDateObtention(), "La date d'obtention doit être celle définie");
    }

    @Test
    @DisplayName("Vérifie que les IDs des relations sont bien accessibles")
    void testRelationsIds() {
        Utilisateur user = new Utilisateur();
        user.setId(50L);

        Badge badge = new Badge();
        badge.setId(99L);

        ObtentionBadge obtention = new ObtentionBadge();
        obtention.setUtilisateur(user);
        obtention.setBadge(badge);

        assertAll("Vérification des IDs",
                () -> assertEquals(50L, obtention.getUtilisateur().getId()),
                () -> assertEquals(99L, obtention.getBadge().getId()));
    }
}