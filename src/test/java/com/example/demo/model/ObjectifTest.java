package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ObjectifTest {

    @Test
    void testGetDateFinLogic() {
        Objectif objectif = new Objectif();
        LocalDate today = LocalDate.now();

        // 1. dateFin n'est pas null
        objectif.setDateFin(today.plusDays(5));
        assertEquals(today.plusDays(5), objectif.getDateFin());

        // 2. dateFin est null, dateDebut est null -> renvoie aujourd'hui
        objectif.setDateFin(null);
        assertEquals(today, objectif.getDateFin());

        // 3. dateDebut est défini, periode est null -> + 1 mois
        LocalDate debut = LocalDate.of(2023, 1, 1);
        objectif.setDateDebut(debut);
        assertEquals(LocalDate.of(2023, 2, 1), objectif.getDateFin());

        // 4. Switch sur Periode
        // Mocking or assuming Periode SEMAINE, ANNEE, etc. exists.
        // Puisque Periode est un Enum, nous utilisons l'injection pour forcer les valeurs du switch si accessible.
        // *Note: On simule le comportement en configurant l'enum Periode.*
        // En supposant que Periode a SEMAINE, ANNEE, et potentiellement MOIS
        objectif.setPeriode(Periode.SEMAINE);
        assertEquals(debut.plusWeeks(1), objectif.getDateFin());

        objectif.setPeriode(Periode.ANNEE);
        assertEquals(debut.plusYears(1), objectif.getDateFin());
        
        // Pour couvrir le default (ex: MOIS)
        objectif.setPeriode(Periode.valueOf("MOIS")); // ou tout autre valeur
        assertEquals(debut.plusMonths(1), objectif.getDateFin());
    }

    @Test
    void testProlongerObjectif() {
        Objectif objectif = new Objectif();
        LocalDate initialFin = LocalDate.now().plusDays(5);
        objectif.setDateFin(initialFin);

        // Nouvelle date après -> mise à jour
        LocalDate nouvelleFinValide = LocalDate.now().plusDays(10);
        objectif.prolongerObjectif(nouvelleFinValide);
        assertEquals(nouvelleFinValide, objectif.getDateFin());

        // Nouvelle date avant -> refusé (reste l'ancienne)
        LocalDate nouvelleFinInvalide = LocalDate.now().plusDays(2);
        objectif.prolongerObjectif(nouvelleFinInvalide);
        assertEquals(nouvelleFinValide, objectif.getDateFin());

        // Nouvelle date null -> refusé
        objectif.prolongerObjectif(null);
        assertEquals(nouvelleFinValide, objectif.getDateFin());
    }

    @Test
    void testConstructorsGettersSettersAndToString() {
        TypeSport mockSport = mock(TypeSport.class);
        Unite mockUnite = mock(Unite.class);
        Utilisateur mockUser = mock(Utilisateur.class);
        
        Objectif obj = new Objectif(1L, "Desc", mockSport, 10f, mockUnite, LocalDate.now(), null, Periode.SEMAINE, mockUser);
        
        assertEquals(1L, obj.getId());
        obj.setId(2L);
        assertEquals(2L, obj.getId());

        assertEquals("Desc", obj.getDescription());
        obj.setDescription("New Desc");
        assertEquals("New Desc", obj.getDescription());

        assertEquals(mockSport, obj.getTypeSport());
        obj.setTypeSport(null);
        assertNull(obj.getTypeSport());

        assertEquals(10f, obj.getCible());
        obj.setCible(20f);
        assertEquals(20f, obj.getCible());

        assertEquals(mockUnite, obj.getUnite());
        obj.setUnite(null);
        assertNull(obj.getUnite());

        assertEquals(mockUser, obj.getUtilisateur());
        obj.setUtilisateur(null);
        assertNull(obj.getUtilisateur());

        assertTrue(obj.toString().contains("Objectif{id=2"));
    }
}