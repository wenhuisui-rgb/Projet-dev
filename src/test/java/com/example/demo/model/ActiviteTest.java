package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActiviteTest {

    private Activite activite;
    private Utilisateur mockUtilisateur;
    private TypeSport mockTypeSport;

    @BeforeEach
    void setUp() {
        mockUtilisateur = mock(Utilisateur.class);
        mockTypeSport = mock(TypeSport.class);
        activite = new Activite(mockTypeSport, LocalDateTime.now(), 60, 5.0f, "Paris", 5, 0f, "Soleil", mockUtilisateur);
    }

    @Test
    void testConstructorsAndGettersSetters() {
        Activite emptyActivite = new Activite();
        emptyActivite.setId(1L);
        assertEquals(1L, emptyActivite.getId());

        activite.setDistance(10.0f);
        assertEquals(10.0f, activite.getDistance());
        
        activite.setDuree(120);
        assertEquals(120, activite.getDuree());

        activite.setLocalisation("Lyon");
        assertEquals("Lyon", activite.getLocalisation());

        activite.setEvaluation(4);
        assertEquals(4, activite.getEvaluation());

        activite.setMeteo("Pluie");
        assertEquals("Pluie", activite.getMeteo());

        activite.setUtilisateur(mockUtilisateur);
        assertEquals(mockUtilisateur, activite.getUtilisateur());
        
        LocalDateTime now = LocalDateTime.now();
        activite.setDateActivite(now);
        assertEquals(now, activite.getDateActivite());

        activite.setTypeSport(mockTypeSport);
        assertEquals(mockTypeSport, activite.getTypeSport());

        List<Commentaire> comments = new ArrayList<>();
        activite.setCommentaires(comments);
        assertEquals(comments, activite.getCommentaires());

        List<Reaction> reactions = new ArrayList<>();
        activite.setReactions(reactions);
        assertEquals(reactions, activite.getReactions());
    }

    @Test
    void testAjouterEtRetirerCommentaire() {
        Commentaire mockCommentaire = mock(Commentaire.class);
        activite.ajouterCommentaire(mockCommentaire);
        
        assertTrue(activite.getCommentaires().contains(mockCommentaire));
        verify(mockCommentaire).setActivite(activite);

        activite.retirerCommentaire(mockCommentaire);
        assertFalse(activite.getCommentaires().contains(mockCommentaire));
        verify(mockCommentaire).setActivite(null);
    }

    @Test
    void testAjouterEtRetirerReaction() {
        Reaction mockReaction = mock(Reaction.class);
        activite.ajouterReaction(mockReaction);

        assertTrue(activite.getReactions().contains(mockReaction));
        verify(mockReaction).setActivite(activite);

        activite.retirerReaction(mockReaction);
        assertFalse(activite.getReactions().contains(mockReaction));
        verify(mockReaction).setActivite(null);
    }

    @Test
    void testCalculerCalories() {
        // Test null conditions
        assertEquals(0f, activite.calculerCalories(null));
        
        activite.setDuree(null);
        assertEquals(0f, activite.calculerCalories(70f));
        
        activite.setDuree(60);
        activite.setTypeSport(null);
        assertEquals(0f, activite.calculerCalories(70f));

        // Test normal calculation (met * poids * (duree/60))
        activite.setTypeSport(mockTypeSport);
        when(mockTypeSport.getMet()).thenReturn(8.0f);
        activite.setDuree(90); // 1.5 hours
        
        // 8.0 * 70.0 * 1.5 = 840.0
        Float result = activite.calculerCalories(70.0f);
        assertEquals(840.0f, result);
        assertEquals(840.0f, activite.getCalories());
    }

    @Test
    void testToString() {
        activite.setId(1L);
        String toStringResult = activite.toString();
        assertTrue(toStringResult.contains("Activite{id=1"));
        assertTrue(toStringResult.contains("localisation='Paris'"));
    }
}