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

    // Sonar Fix: 拆分为基本属性测试
    @Test
    void testConstructorsAndBasicFields() {
        List<TypeSport> preferences = new ArrayList<>();
        preferences.add(TypeSport.COURSE);
        List<ObtentionBadge> badges = new ArrayList<>();
        
        Utilisateur userFull = new Utilisateur(1L, "john_doe", "john@test.com", "password123", 
                Sexe.HOMME, 25, 1.80f, 75.0f, NiveauPratique.INTERMEDIAIRE, preferences, badges);

        assertEquals(1L, userFull.getId());
        assertEquals("john_doe", userFull.getPseudo());
        assertEquals("john@test.com", userFull.getEmail());
        assertEquals(Sexe.HOMME, userFull.getSexe());
        assertEquals(25, userFull.getAge());
        assertEquals(1.80f, userFull.getTaille());
        assertEquals(75.0f, userFull.getPoids());
        assertEquals(NiveauPratique.INTERMEDIAIRE, userFull.getNiveauPratique());

        utilisateur.setId(2L);
        assertEquals(2L, utilisateur.getId());
        utilisateur.setPseudo("jane_doe");
        assertEquals("jane_doe", utilisateur.getPseudo());
        utilisateur.setObjectifPersonnel("Perdre du poids");
        assertEquals("Perdre du poids", utilisateur.getObjectifPersonnel());
    }

    // Sonar Fix: 拆分为集合属性测试，避免单个测试断言过多
    @Test
    void testCollectionFields() {
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
        utilisateur.setTaille(2.0f);
        utilisateur.setPoids(80.0f);
        assertEquals(20.0f, utilisateur.calculerIMC(), 0.01f);

        utilisateur.setTaille(null);
        assertNull(utilisateur.calculerIMC());

        utilisateur.setTaille(1.8f);
        utilisateur.setPoids(null);
        assertNull(utilisateur.calculerIMC());

        utilisateur.setTaille(0f);
        utilisateur.setPoids(70.0f);
        assertNull(utilisateur.calculerIMC());
        
        utilisateur.setTaille(-1.5f);
        assertNull(utilisateur.calculerIMC());
    }

    @Test
    void testGetAmis() {
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

        List<Utilisateur> amis = utilisateur.getAmis();

        assertEquals(2, amis.size());
        assertTrue(amis.contains(ami1));
        assertTrue(amis.contains(ami2));
    }
}