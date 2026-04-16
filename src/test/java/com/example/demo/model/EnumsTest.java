package com.example.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {

    @Test
    void testPeriode() {
        assertEquals(3, Periode.values().length);
        assertEquals(Periode.SEMAINE, Periode.valueOf("SEMAINE"));
        
        assertEquals("Semaine", Periode.SEMAINE.getLibelle());
        assertEquals("Mois", Periode.MOIS.getLibelle());
        assertEquals("Année", Periode.ANNEE.getLibelle());
    }

    @Test
    void testSexe() {
        assertEquals(3, Sexe.values().length);
        assertEquals(Sexe.HOMME, Sexe.valueOf("HOMME"));
        assertEquals(Sexe.FEMME, Sexe.valueOf("FEMME"));
        assertEquals(Sexe.AUTRE, Sexe.valueOf("AUTRE"));
    }

    @Test
    void testStatutAmitie() {
        assertEquals(4, StatutAmitie.values().length);
        assertEquals(StatutAmitie.EN_ATTENTE, StatutAmitie.valueOf("EN_ATTENTE"));
    }

    @Test
    void testStatutChallenge() {
        assertEquals(2, StatutChallenge.values().length);
        assertEquals(StatutChallenge.ACTIF, StatutChallenge.valueOf("ACTIF"));
        assertEquals(StatutChallenge.TERMINE, StatutChallenge.valueOf("TERMINE"));
    }

    @Test
    void testTypeReaction() {
        assertEquals(3, TypeReaction.values().length);
        assertEquals(TypeReaction.KUDOS, TypeReaction.valueOf("KUDOS"));
        assertEquals(TypeReaction.BRAVO, TypeReaction.valueOf("BRAVO"));
        assertEquals(TypeReaction.SOUTIEN, TypeReaction.valueOf("SOUTIEN"));
    }

    @Test
    void testTypeSport() {
        assertTrue(TypeSport.values().length > 0);
        assertEquals(TypeSport.COURSE, TypeSport.valueOf("COURSE"));
        
        assertEquals(9.8f, TypeSport.COURSE.getMet());
        assertEquals(8.0f, TypeSport.NATATION.getMet());
        assertEquals(7.5f, TypeSport.VELO.getMet());
    }

    @Test
    void testUnite() {
        assertEquals(3, Unite.values().length);
        assertEquals(Unite.KM, Unite.valueOf("KM"));

        assertEquals("km", Unite.KM.getCode());
        assertEquals("minutes", Unite.MINUTES.getCode());
        assertEquals("kcal", Unite.KCAL.getCode());

        // Test custom fromCode method
        assertEquals(Unite.KM, Unite.fromCode("km"));
        assertEquals(Unite.KM, Unite.fromCode("KM")); // Ignore case check
        assertEquals(Unite.MINUTES, Unite.fromCode("minutes"));
        assertEquals(Unite.KCAL, Unite.fromCode("KCAL"));
        
        // Test null return for invalid code
        assertNull(Unite.fromCode("invalide"));
        assertNull(Unite.fromCode(null));
    }
}