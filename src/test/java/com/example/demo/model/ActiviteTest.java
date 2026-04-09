package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActiviteTest {

    private Activite activite;

    @BeforeEach
    void setUp() {
        activite = new Activite();
        activite.setId(1L);
        activite.setTypeSport(TypeSport.COURSE);
        activite.setDateActivite(LocalDateTime.now());
        activite.setDuree(30);
        activite.setDistance(5.0f);
        activite.setLocalisation("Paris");
        activite.setEvaluation(4);
        activite.setCalories(300f);
        activite.setMeteo("Ensoleillé");
    }

    @Test
    void testNoArgConstructor() {
        Activite a = new Activite();
        assertNotNull(a);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.now();
        Activite a = new Activite(TypeSport.VELO, date, 60, 20f, "Lyon", 5, 500f, "Nuageux", null);
        
        assertEquals(TypeSport.VELO, a.getTypeSport());
        assertEquals(date, a.getDateActivite());
        assertEquals(60, a.getDuree());
        assertEquals(20f, a.getDistance());
        assertEquals("Lyon", a.getLocalisation());
        assertEquals(5, a.getEvaluation());
        assertEquals(500f, a.getCalories());
        assertEquals("Nuageux", a.getMeteo());
    }

    @Test
    void testAllGettersAndSetters() {
        LocalDateTime date = LocalDateTime.now();
        
        activite.setId(99L);
        activite.setTypeSport(TypeSport.NATATION);
        activite.setDateActivite(date);
        activite.setDuree(120);
        activite.setDistance(3.5f);
        activite.setLocalisation("Marseille");
        activite.setEvaluation(3);
        activite.setCalories(800f);
        activite.setMeteo("Pluie");
        
        assertEquals(99L, activite.getId());
        assertEquals(TypeSport.NATATION, activite.getTypeSport());
        assertEquals(date, activite.getDateActivite());
        assertEquals(120, activite.getDuree());
        assertEquals(3.5f, activite.getDistance());
        assertEquals("Marseille", activite.getLocalisation());
        assertEquals(3, activite.getEvaluation());
        assertEquals(800f, activite.getCalories());
        assertEquals("Pluie", activite.getMeteo());
    }

    @Test
    void testCalculerCaloriesCourse() {
        activite.setTypeSport(TypeSport.COURSE);
        activite.setDuree(30);
        Float result = activite.calculerCalories(70f);
        assertEquals(343.0f, result, 0.1);
    }

    @Test
    void testCalculerCaloriesNatation() {
        activite.setTypeSport(TypeSport.NATATION);
        activite.setDuree(45);
        Float result = activite.calculerCalories(65f);
        assertEquals(390.0f, result, 0.1);
    }

    @Test
    void testCalculerCaloriesVelo() {
        activite.setTypeSport(TypeSport.VELO);
        activite.setDuree(60);
        Float result = activite.calculerCalories(80f);
        assertEquals(600.0f, result, 0.1);
    }

    @Test
    void testCalculerCaloriesMusculation() {
        activite.setTypeSport(TypeSport.MUSCULATION);
        activite.setDuree(50);
        Float result = activite.calculerCalories(75f);
        assertEquals(375.0f, result, 0.1);
    }

    @Test
    void testCalculerCaloriesYoga() {
        activite.setTypeSport(TypeSport.YOGA);
        activite.setDuree(40);
        Float result = activite.calculerCalories(60f);
        assertEquals(120.0f, result, 0.1);
    }

    @Test
    void testCalculerCaloriesRandonnee() {
        activite.setTypeSport(TypeSport.RANDONNEE);
        activite.setDuree(90);
        Float result = activite.calculerCalories(68f);
        assertEquals(561.0f, result, 0.1);
    }

    @Test
    void testCalculerCaloriesTypeSportNull() {
        activite.setTypeSport(null);
        Float result = activite.calculerCalories(70f);
        assertEquals(0f, result);
    }

    @Test
    void testCalculerCaloriesDureeNull() {
        activite.setDuree(null);
        Float result = activite.calculerCalories(70f);
        assertEquals(0f, result);
    }

    @Test
    void testCalculerCaloriesPoidsNull() {
        Float result = activite.calculerCalories(null);
        assertEquals(0f, result);
    }

    @Test
    void testToString() {
        String result = activite.toString();
        assertNotNull(result);
        assertTrue(result.contains("COURSE"));
        assertTrue(result.contains("duree=30"));
    }

    @Test
    void testObjectNotNull() {
        assertNotNull(activite);
    }
}