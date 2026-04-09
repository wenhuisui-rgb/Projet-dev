package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifTest {

    private Objectif objectif;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        
        objectif = new Objectif();
        objectif.setId(1L);
        objectif.setDescription("Courir 50 km par mois");
        objectif.setTypeSport(TypeSport.COURSE);
        objectif.setCible(50f);
        objectif.setUnite("km");
        objectif.setDateDebut(LocalDate.of(2026, 4, 1));
        objectif.setPeriode("MOIS");
        objectif.setUtilisateur(utilisateur);
    }

    @Test
    @DisplayName("Test constructeur sans paramètres")
    void testNoArgConstructor() {
        Objectif obj = new Objectif();
        assertNotNull(obj);
    }

    @Test
    @DisplayName("Test constructeur avec paramètres")
    void testAllArgsConstructor() {
        LocalDate date = LocalDate.of(2026, 5, 1);
        Objectif obj = new Objectif(2L, "Nager 10 km", TypeSport.NATATION, 10f, "km", date, "MOIS", utilisateur);
        
        assertEquals(2L, obj.getId());
        assertEquals("Nager 10 km", obj.getDescription());
        assertEquals(TypeSport.NATATION, obj.getTypeSport());
        assertEquals(10f, obj.getCible());
        assertEquals("km", obj.getUnite());
        assertEquals(date, obj.getDateDebut());
        assertEquals("MOIS", obj.getPeriode());
        assertEquals(utilisateur, obj.getUtilisateur());
    }

    @Test
    @DisplayName("Test getters et setters")
    void testGettersAndSetters() {
        LocalDate date = LocalDate.of(2026, 6, 15);
        
        objectif.setId(5L);
        objectif.setDescription("Faire 20h de sport");
        objectif.setTypeSport(TypeSport.VELO);
        objectif.setCible(1200f);
        objectif.setUnite("minutes");
        objectif.setDateDebut(date);
        objectif.setPeriode("SEMAINE");
        
        assertEquals(5L, objectif.getId());
        assertEquals("Faire 20h de sport", objectif.getDescription());
        assertEquals(TypeSport.VELO, objectif.getTypeSport());
        assertEquals(1200f, objectif.getCible());
        assertEquals("minutes", objectif.getUnite());
        assertEquals(date, objectif.getDateDebut());
        assertEquals("SEMAINE", objectif.getPeriode());
    }

    @Test
    @DisplayName("Test calculerProgression - avec activités correspondantes")
    void testCalculerProgressionAvecActivites() {
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.COURSE);
        a1.setDistance(15f);
        a1.setDateActivite(LocalDate.of(2026, 4, 10).atStartOfDay());
        
        Activite a2 = new Activite();
        a2.setTypeSport(TypeSport.COURSE);
        a2.setDistance(20f);
        a2.setDateActivite(LocalDate.of(2026, 4, 20).atStartOfDay());
        
        Activite a3 = new Activite();
        a3.setTypeSport(TypeSport.VELO);
        a3.setDistance(30f);
        a3.setDateActivite(LocalDate.of(2026, 4, 15).atStartOfDay());
        
        activites.addAll(Arrays.asList(a1, a2, a3));
        
        Float progression = objectif.calculerProgression(activites);
        
        assertEquals(35f, progression);
    }

    @Test
    @DisplayName("Test calculerProgression - avec unité minutes")
    void testCalculerProgressionAvecMinutes() {
        objectif.setTypeSport(TypeSport.MUSCULATION);
        objectif.setCible(300f);
        objectif.setUnite("minutes");
        
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.MUSCULATION);
        a1.setDuree(60);
        a1.setDateActivite(LocalDate.of(2026, 4, 10).atStartOfDay());
        
        Activite a2 = new Activite();
        a2.setTypeSport(TypeSport.MUSCULATION);
        a2.setDuree(90);
        a2.setDateActivite(LocalDate.of(2026, 4, 20).atStartOfDay());
        
        activites.addAll(Arrays.asList(a1, a2));
        
        Float progression = objectif.calculerProgression(activites);
        
        assertEquals(150f, progression);
    }

    @Test
    @DisplayName("Test calculerProgression - liste d'activités vide")
    void testCalculerProgressionListeVide() {
        List<Activite> activites = new ArrayList<>();
        Float progression = objectif.calculerProgression(activites);
        assertEquals(0f, progression);
    }

    @Test
    @DisplayName("Test calculerProgression - cible null")
    void testCalculerProgressionCibleNull() {
        objectif.setCible(null);
        List<Activite> activites = new ArrayList<>();
        Float progression = objectif.calculerProgression(activites);
        assertEquals(0f, progression);
    }

    @Test
    @DisplayName("Test getPourcentageProgression")
    void testGetPourcentageProgression() {
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.COURSE);
        a1.setDistance(25f);
        a1.setDateActivite(LocalDate.of(2026, 4, 10).atStartOfDay());
        
        activites.add(a1);
        
        Float pourcentage = objectif.getPourcentageProgression(activites);
        
        assertEquals(50f, pourcentage);
    }

    @Test
    @DisplayName("Test getPourcentageProgression - cible null")
    void testGetPourcentageProgressionCibleNull() {
        objectif.setCible(null);
        List<Activite> activites = new ArrayList<>();
        Float pourcentage = objectif.getPourcentageProgression(activites);
        assertEquals(0f, pourcentage);
    }

    @Test
    @DisplayName("Test estAtteint - objectif atteint")
    void testEstAtteintVrai() {
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.COURSE);
        a1.setDistance(30f);
        a1.setDateActivite(LocalDate.of(2026, 4, 10).atStartOfDay());
        
        Activite a2 = new Activite();
        a2.setTypeSport(TypeSport.COURSE);
        a2.setDistance(30f);
        a2.setDateActivite(LocalDate.of(2026, 4, 20).atStartOfDay());
        
        activites.addAll(Arrays.asList(a1, a2));
        
        Boolean atteint = objectif.estAtteint(activites);
        
        assertTrue(atteint);
    }

    @Test
    @DisplayName("Test estAtteint - objectif non atteint")
    void testEstAtteintFaux() {
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.COURSE);
        a1.setDistance(20f);
        a1.setDateActivite(LocalDate.of(2026, 4, 10).atStartOfDay());
        
        activites.add(a1);
        
        Boolean atteint = objectif.estAtteint(activites);
        
        assertFalse(atteint);
    }

    @Test
    @DisplayName("Test prolongerObjectif")
    void testProlongerObjectif() {
        LocalDate nouvelleDate = LocalDate.of(2026, 5, 1);
        objectif.prolongerObjectif(nouvelleDate);
        assertEquals(nouvelleDate, objectif.getDateDebut());
    }

    @Test
    @DisplayName("Test prolongerObjectif - date antérieure ne change pas")
    void testProlongerObjectifDateAnterieure() {
        LocalDate dateOriginale = objectif.getDateDebut();
        LocalDate dateAnterieure = LocalDate.of(2026, 3, 1);
        objectif.prolongerObjectif(dateAnterieure);
        assertEquals(dateOriginale, objectif.getDateDebut());
    }

    @Test
    @DisplayName("Test toString")
    void testToString() {
        String result = objectif.toString();
        assertNotNull(result);
        assertTrue(result.contains("Courir 50 km par mois"));
        assertTrue(result.contains("COURSE"));
    }

    @Test
    @DisplayName("Test calculerProgression - hors période")
    void testCalculerProgressionHorsPeriode() {
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.COURSE);
        a1.setDistance(100f);
        a1.setDateActivite(LocalDate.of(2025, 12, 31).atStartOfDay()); // Avant date début
        
        activites.add(a1);
        
        Float progression = objectif.calculerProgression(activites);
        
        assertEquals(0f, progression);
    }

    @Test
    @DisplayName("Test calculerProgression - sport différent")
    void testCalculerProgressionSportDifferent() {
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.NATATION);
        a1.setDistance(40f);
        a1.setDateActivite(LocalDate.of(2026, 4, 15).atStartOfDay());
        
        activites.add(a1);
        
        Float progression = objectif.calculerProgression(activites);
        
        assertEquals(0f, progression);
    }

    @Test
    @DisplayName("Test getDateFin - période MOIS")
    void testGetDateFinMois() {
        objectif.setPeriode("MOIS");
        objectif.setDateDebut(LocalDate.of(2026, 4, 1));
        
        List<Activite> activites = new ArrayList<>();
        
        Activite a1 = new Activite();
        a1.setTypeSport(TypeSport.COURSE);
        a1.setDistance(30f);
        a1.setDateActivite(LocalDate.of(2026, 5, 15).atStartOfDay()); // Après la fin du mois
        
        activites.add(a1);
        
        Float progression = objectif.calculerProgression(activites);
        
        assertEquals(0f, progression);
    }
}