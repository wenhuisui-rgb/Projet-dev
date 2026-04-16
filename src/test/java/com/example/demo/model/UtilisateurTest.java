package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilisateurTest {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
    }

    @Test
    void testConstructorsAndGettersSetters() {
        List<TypeSport> preferences = new ArrayList<>();
        preferences.add(TypeSport.COURSE);
        
        List<ObtentionBadge> badges = new ArrayList<>();
        
        Utilisateur userFull = new Utilisateur(1L, "john_doe", "john@test.com", "password123", 
                Sexe.HOMME, 25, 1.80f, 75.0f, NiveauPratique.INTERMEDIAIRE, preferences, badges);

        assertEquals(1L, userFull.getId());
        assertEquals("john_doe", userFull.getPseudo());
        assertEquals("john@test.com", userFull.getEmail());
        assertEquals("password123", userFull.getMotDePasse());
        assertEquals(Sexe.HOMME, userFull.getSexe());
        assertEquals(25, userFull.getAge());
        assertEquals(1.80f, userFull.getTaille());
        assertEquals(75.0f, userFull.getPoids());
        assertEquals(NiveauPratique.INTERMEDIAIRE, userFull.getNiveauPratique());
        assertEquals(preferences, userFull.getPreferencesSports());
        assertEquals(badges, userFull.getListBadges());

        // Test Setters
        utilisateur.setId(2L);
        assertEquals(2L, utilisateur.getId());
        
        utilisateur.setPseudo("jane_doe");
        assertEquals("jane_doe", utilisateur.getPseudo());
        
        utilisateur.setEmail("jane@test.com");
        assertEquals("jane@test.com", utilisateur.getEmail());
        
        utilisateur.setMotDePasse("newpass");
        assertEquals("newpass", utilisateur.getMotDePasse());
        
        utilisateur.setSexe(Sexe.FEMME);
        assertEquals(Sexe.FEMME, utilisateur.getSexe());
        
        utilisateur.setAge(30);
        assertEquals(30, utilisateur.getAge());
        
        utilisateur.setTaille(1.65f);
        assertEquals(1.65f, utilisateur.getTaille());
        
        utilisateur.setPoids(60.0f);
        assertEquals(60.0f, utilisateur.getPoids());
        
        utilisateur.setObjectifPersonnel("Perdre du poids");
        assertEquals("Perdre du poids", utilisateur.getObjectifPersonnel());
        
        utilisateur.setNiveauPratique(NiveauPratique.DEBUTANT);
        assertEquals(NiveauPratique.DEBUTANT, utilisateur.getNiveauPratique());

        utilisateur.setPreferencesSports(new ArrayList<>());
        assertNotNull(utilisateur.getPreferencesSports());

        utilisateur.setActivites(new ArrayList<>());
        assertNotNull(utilisateur.getActivites());

        utilisateur.setObjectifs(new ArrayList<>());
        assertNotNull(utilisateur.getObjectifs());

        utilisateur.setDemandesEnvoyees(new ArrayList<>());
        assertNotNull(utilisateur.getDemandesEnvoyees());

        utilisateur.setDemandesRecues(new ArrayList<>());
        assertNotNull(utilisateur.getDemandesRecues());

        utilisateur.setListBadges(new ArrayList<>());
        assertNotNull(utilisateur.getListBadges());

        utilisateur.setChallengesCrees(new ArrayList<>());
        assertNotNull(utilisateur.getChallengesCrees());

        utilisateur.setParticipationsChallenge(new ArrayList<>());
        assertNotNull(utilisateur.getParticipationsChallenge());

        utilisateur.setCommentaires(new ArrayList<>());
        assertNotNull(utilisateur.getCommentaires());

        utilisateur.setReactions(new ArrayList<>());
        assertNotNull(utilisateur.getReactions());
    }

    @Test
    void testCalculerIMC() {
        // Normal case
        utilisateur.setTaille(2.0f);
        utilisateur.setPoids(80.0f);
        // IMC = 80 / (2 * 2) = 20.0
        assertEquals(20.0f, utilisateur.calculerIMC(), 0.01f);

        // Null checks
        utilisateur.setTaille(null);
        assertNull(utilisateur.calculerIMC());

        utilisateur.setTaille(1.8f);
        utilisateur.setPoids(null);
        assertNull(utilisateur.calculerIMC());

        // Division by zero check
        utilisateur.setTaille(0f);
        utilisateur.setPoids(70.0f);
        assertNull(utilisateur.calculerIMC());
        
        // Negative size check
        utilisateur.setTaille(-1.5f);
        assertNull(utilisateur.calculerIMC());
    }

    @Test
    void testGetAmis() {
        // Setup mock Amitie (Envoyées)
        Amitie amitieEnvoyeeAcceptee = mock(Amitie.class);
        when(amitieEnvoyeeAcceptee.getStatut()).thenReturn(StatutAmitie.ACCEPTEE);
        Utilisateur ami1 = mock(Utilisateur.class);
        when(amitieEnvoyeeAcceptee.getUtilisateurReceveur()).thenReturn(ami1);

        Amitie amitieEnvoyeeEnAttente = mock(Amitie.class);
        when(amitieEnvoyeeEnAttente.getStatut()).thenReturn(StatutAmitie.EN_ATTENTE);

        List<Amitie> envoyees = new ArrayList<>();
        envoyees.add(amitieEnvoyeeAcceptee);
        envoyees.add(amitieEnvoyeeEnAttente);
        utilisateur.setDemandesEnvoyees(envoyees);

        // Setup mock Amitie (Reçues)
        Amitie amitieRecueAcceptee = mock(Amitie.class);
        when(amitieRecueAcceptee.getStatut()).thenReturn(StatutAmitie.ACCEPTEE);
        Utilisateur ami2 = mock(Utilisateur.class);
        when(amitieRecueAcceptee.getUtilisateurDemandeur()).thenReturn(ami2);

        Amitie amitieRecueRefusee = mock(Amitie.class);
        when(amitieRecueRefusee.getStatut()).thenReturn(StatutAmitie.REFUSEE);

        List<Amitie> recues = new ArrayList<>();
        recues.add(amitieRecueAcceptee);
        recues.add(amitieRecueRefusee);
        utilisateur.setDemandesRecues(recues);

        // Execution
        List<Utilisateur> amis = utilisateur.getAmis();

        // Verification
        assertEquals(2, amis.size());
        assertTrue(amis.contains(ami1));
        assertTrue(amis.contains(ami2));
    }
}