package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ChallengeTest {

    private Challenge challenge;
    private Utilisateur createur;

    @BeforeEach
    void setUp() {
        createur = new Utilisateur();
        createur.setId(1L);
        challenge = new Challenge("Defi RUN", TypeSport.COURSE, LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10), createur);
    }

    @Test
    @DisplayName("Le challenge doit être actif si la date du jour est entre début et fin")
    void testEstActif_Succes() {
        
        challenge.setDateDebut(LocalDate.now().minusDays(1)); // Hier
        challenge.setDateFin(LocalDate.now().plusDays(1)); // Demain

        assertTrue(challenge.estActif(), "Le challenge devrait être actif");
    }

    @Test
    @DisplayName("Test de création d'un challenge avec une date de début passée")
    void testEstActif_Future() {
        challenge.setDateDebut(LocalDate.now().plusDays(1));
        challenge.setDateFin(LocalDate.now().plusDays(5));

        assertFalse(challenge.estActif(), "Le challenge ne devrait pas être actif (commence demain)");
    }

    @Test
    @DisplayName("Le challenge doit être inactif s'il est déjà terminé")
    void testEstActif_Passe() {
        challenge.setDateDebut(LocalDate.now().minusDays(10));
        challenge.setDateFin(LocalDate.now().minusDays(1));

        assertFalse(challenge.estActif(), "Le challenge ne devrait plus être actif (fini hier)");
    }

    @Test
    @DisplayName("getters et setters du challenge")
    void testGettersSetters() {
        assertEquals("Defi RUN", challenge.getTitre());
        assertEquals(TypeSport.COURSE, challenge.getTypeSport());
        assertEquals(createur, challenge.getCreateur());
    }

}
