package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPseudo("john_doe");
        utilisateur.setEmail("john@example.com");
        utilisateur.setMotDePasse("password123");
        utilisateur.setSexe(Sexe.HOMME);
        utilisateur.setAge(25);
        utilisateur.setTaille(1.75f);
        utilisateur.setPoids(70f);
        utilisateur.setNiveauPratique(NiveauPratique.INTERMEDIAIRE);
        
        List<TypeSport> preferences = new ArrayList<>();
        preferences.add(TypeSport.COURSE);
        preferences.add(TypeSport.VELO);
        utilisateur.setPreferencesSports(preferences);
    }

    @Test
    void testNoArgConstructor() {
        Utilisateur user = new Utilisateur();
        assertNotNull(user);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, utilisateur.getId());
        assertEquals("john_doe", utilisateur.getPseudo());
        assertEquals("john@example.com", utilisateur.getEmail());
        assertEquals("password123", utilisateur.getMotDePasse());
        assertEquals(Sexe.HOMME, utilisateur.getSexe());
        assertEquals(25, utilisateur.getAge());
        assertEquals(1.75f, utilisateur.getTaille());
        assertEquals(70f, utilisateur.getPoids());
        assertEquals(NiveauPratique.INTERMEDIAIRE, utilisateur.getNiveauPratique());
        assertEquals(2, utilisateur.getPreferencesSports().size());
    }

    @Test
    void testCalculerIMC() {
        Float imc = utilisateur.calculerIMC();
        assertNotNull(imc);
        assertEquals(22.86f, imc, 0.01);
    }

    @Test
    void testCalculerIMCNull() {
        utilisateur.setTaille(null);
        Float imc = utilisateur.calculerIMC();
        assertNull(imc);
    }

    @Test
    void testObjectifPersonnel() {
        utilisateur.setObjectifPersonnel("Courir 50 km par mois");
        assertEquals("Courir 50 km par mois", utilisateur.getObjectifPersonnel());
    }

    @Test
    void testPreferencesSports() {
        List<TypeSport> sports = utilisateur.getPreferencesSports();
        assertTrue(sports.contains(TypeSport.COURSE));
        assertTrue(sports.contains(TypeSport.VELO));
    }

    @Test
    void testToString() {
        String result = utilisateur.toString();
        assertNotNull(result);
    }

    @Test
    void testAllArgsConstructor() {
        List<TypeSport> sports = new ArrayList<>();
        sports.add(TypeSport.COURSE);
        List<ObtentionBadge> badges = new ArrayList<>();
        
        Utilisateur user = new Utilisateur(2L, "jane_doe", "jane@example.com", "pass123", 
                                        Sexe.FEMME, 30, 1.65f, 60f, 
                                        NiveauPratique.EXPERT, sports, badges);
        
        assertEquals(2L, user.getId());
        assertEquals("jane_doe", user.getPseudo());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("pass123", user.getMotDePasse());
        assertEquals(Sexe.FEMME, user.getSexe());
        assertEquals(30, user.getAge());
        assertEquals(1.65f, user.getTaille());
        assertEquals(60f, user.getPoids());
        assertEquals(NiveauPratique.EXPERT, user.getNiveauPratique());
        assertEquals(1, user.getPreferencesSports().size());
        assertNotNull(user.getListBadges());
    }

    @Test
    void testGetSetListBadges() {
        List<ObtentionBadge> badges = new ArrayList<>();
        ObtentionBadge badge1 = new ObtentionBadge();
        ObtentionBadge badge2 = new ObtentionBadge();
        badges.add(badge1);
        badges.add(badge2);
        
        utilisateur.setListBadges(badges);
        assertNotNull(utilisateur.getListBadges());
        assertEquals(2, utilisateur.getListBadges().size());
    }

    @Test
    void testGetSetActivites() {
        List<Activite> activites = new ArrayList<>();
        Activite a1 = new Activite();
        Activite a2 = new Activite();
        activites.add(a1);
        activites.add(a2);
        
        utilisateur.setActivites(activites);
        assertNotNull(utilisateur.getActivites());
        assertEquals(2, utilisateur.getActivites().size());
    }

    @Test
    void testGetSetObjectifs() {
        List<Objectif> objectifs = new ArrayList<>();
        Objectif o1 = new Objectif();
        Objectif o2 = new Objectif();
        objectifs.add(o1);
        objectifs.add(o2);
        
        utilisateur.setObjectifs(objectifs);
        assertNotNull(utilisateur.getObjectifs());
        assertEquals(2, utilisateur.getObjectifs().size());
    }

    @Test
    void testGetSetDemandesEnvoyees() {
        List<Amitie> demandes = new ArrayList<>();
        Amitie a1 = new Amitie();
        Amitie a2 = new Amitie();
        demandes.add(a1);
        demandes.add(a2);
        
        utilisateur.setDemandesEnvoyees(demandes);
        assertNotNull(utilisateur.getDemandesEnvoyees());
        assertEquals(2, utilisateur.getDemandesEnvoyees().size());
    }

    @Test
    void testGetSetDemandesRecues() {
        List<Amitie> demandes = new ArrayList<>();
        Amitie a1 = new Amitie();
        Amitie a2 = new Amitie();
        demandes.add(a1);
        demandes.add(a2);
        
        utilisateur.setDemandesRecues(demandes);
        assertNotNull(utilisateur.getDemandesRecues());
        assertEquals(2, utilisateur.getDemandesRecues().size());
    }

    @Test
    void testGetSetParticipationsChallenge() {
        List<ParticipationChallenge> participations = new ArrayList<>();
        ParticipationChallenge p1 = new ParticipationChallenge();
        ParticipationChallenge p2 = new ParticipationChallenge();
        participations.add(p1);
        participations.add(p2);
        
        utilisateur.setParticipationsChallenge(participations);
        assertNotNull(utilisateur.getParticipationsChallenge());
        assertEquals(2, utilisateur.getParticipationsChallenge().size());
    }

    @Test
    void testGetSetCommentaires() {
        List<Commentaire> commentaires = new ArrayList<>();
        Commentaire c1 = new Commentaire();
        Commentaire c2 = new Commentaire();
        commentaires.add(c1);
        commentaires.add(c2);
        
        utilisateur.setCommentaires(commentaires);
        assertNotNull(utilisateur.getCommentaires());
        assertEquals(2, utilisateur.getCommentaires().size());
    }

    @Test
    void testGetSetReactions() {
        List<Reaction> reactions = new ArrayList<>();
        Reaction r1 = new Reaction();
        Reaction r2 = new Reaction();
        reactions.add(r1);
        reactions.add(r2);
        
        utilisateur.setReactions(reactions);
        assertNotNull(utilisateur.getReactions());
        assertEquals(2, utilisateur.getReactions().size());
    }

    @Test
    void testGetSetChallengesCrees() {
        List<Challenge> challenges = new ArrayList<>();
        Challenge c1 = new Challenge();
        Challenge c2 = new Challenge();
        challenges.add(c1);
        challenges.add(c2);
        
        utilisateur.setChallengesCrees(challenges);
        assertNotNull(utilisateur.getChallengesCrees());
        assertEquals(2, utilisateur.getChallengesCrees().size());
    }
}