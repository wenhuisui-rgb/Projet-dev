package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

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
        objectif.setPeriode(Periode.MOIS);
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
        Objectif obj = new Objectif(2L, "Nager 10 km", TypeSport.NATATION, 10f, "km", date, Periode.MOIS, utilisateur);
        
        assertEquals(2L, obj.getId());
        assertEquals("Nager 10 km", obj.getDescription());
        assertEquals(TypeSport.NATATION, obj.getTypeSport());
        assertEquals(10f, obj.getCible());
        assertEquals("km", obj.getUnite());
        assertEquals(date, obj.getDateDebut());
        assertEquals(Periode.MOIS, obj.getPeriode());
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
        objectif.setPeriode(Periode.SEMAINE);
        
        assertEquals(5L, objectif.getId());
        assertEquals("Faire 20h de sport", objectif.getDescription());
        assertEquals(TypeSport.VELO, objectif.getTypeSport());
        assertEquals(1200f, objectif.getCible());
        assertEquals("minutes", objectif.getUnite());
        assertEquals(date, objectif.getDateDebut());
        assertEquals(Periode.SEMAINE, objectif.getPeriode());
    }

    @Test
    @DisplayName("Test getDateFin")
    void testGetDateFin() {
        objectif.setDateDebut(LocalDate.of(2026, 4, 1));
        
        objectif.setPeriode(Periode.SEMAINE);
        assertEquals(LocalDate.of(2026, 4, 8), objectif.getDateFin());
        
        objectif.setPeriode(Periode.MOIS);
        assertEquals(LocalDate.of(2026, 5, 1), objectif.getDateFin());
        
        objectif.setPeriode(Periode.ANNEE);
        assertEquals(LocalDate.of(2027, 4, 1), objectif.getDateFin());
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
}