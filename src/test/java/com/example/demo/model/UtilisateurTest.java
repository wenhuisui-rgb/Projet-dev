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
}