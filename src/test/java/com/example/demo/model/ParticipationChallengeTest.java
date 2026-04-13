package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ParticipationChallengeTest {

    private Utilisateur utilisateur;
    private Challenge challenge;
    private ParticipationChallenge participation;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        challenge = new Challenge();

        participation = new ParticipationChallenge(utilisateur, challenge);
    }

    @Test
    @DisplayName("Test de création d'une participation à un challenge")
    void testCreationParticipation() {
        assertNotNull(participation.getDateInscription(), "La date d'inscription ne devrait pas être nulle");
        assertEquals(0, participation.getScoreActuel(), "Le score initial devrait être 0");
        assertEquals(utilisateur, participation.getUtilisateur(), "L'utilisateur associé devrait être correct");
        assertEquals(challenge, participation.getChallenge(), "Le challenge associé devrait être correct");
    }

    @Test
    @DisplayName("Mise à jour du score : Le nouveau score doit être correctement enregistré")
    void testMettreAJourScore() {
        float nouveauScore = 150.5f;
        participation.mettreAJourScore(nouveauScore);

        assertEquals(nouveauScore, participation.getScoreActuel(), "Le score doit être mis à jour à 150.5");
    }

    @Test
    @DisplayName("Test des getters et setters de ParticipationChallenge")
    void testGettersSetters() {
        LocalDate dateTest = LocalDate.now();
        participation.setDateInscription(dateTest.atStartOfDay());
        participation.setScoreActuel(75.0f);

        assertEquals(dateTest.atStartOfDay(), participation.getDateInscription(), "La date d'inscription doit être mise à jour");
        assertEquals(75.0f, participation.getScoreActuel(), "Le score actuel doit être mis à jour à 75.0");
    }

}
