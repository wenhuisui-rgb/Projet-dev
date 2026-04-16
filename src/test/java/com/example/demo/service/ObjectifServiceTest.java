package com.example.demo.service;

import com.example.demo.model.Objectif;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ObjectifRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjectifServiceTest {

    @Mock
    private ObjectifRepository objectifRepository;

    @Mock
    private ActiviteRepository activiteRepository;

    @InjectMocks
    private ObjectifService objectifService;

    @Test
    void testCreerObjectif() {
        Objectif obj = new Objectif();
        Utilisateur user = new Utilisateur();
        when(objectifRepository.save(obj)).thenReturn(obj);

        Objectif saved = objectifService.creerObjectif(obj, user);
        assertEquals(user, saved.getUtilisateur());
        verify(objectifRepository).save(obj);
    }

    @Test
    void testUpdateObjectif() {
        Objectif existant = new Objectif();
        existant.setId(1L);
        existant.setDescription("Old");

        Objectif modifie = new Objectif();
        modifie.setDescription("New");
        modifie.setUnite(Unite.KM);

        when(objectifRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(objectifRepository.save(existant)).thenReturn(existant);

        Objectif result = objectifService.updateObjectif(1L, modifie);
        assertEquals("New", result.getDescription());
        assertEquals(Unite.KM, result.getUnite());

        assertNull(objectifService.updateObjectif(2L, modifie));
    }

    @Test
    void testGetProgressionObjectif_KM() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.KM);
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now().plusDays(5));
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getDistanceByPeriod(eq(user), any(), any())).thenReturn(15.5f);

        Float progression = objectifService.getProgressionObjectif(obj, user);
        assertEquals(15.5f, progression);
    }

    @Test
    void testGetProgressionObjectif_MinutesWithSport() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.MINUTES);
        obj.setTypeSport(TypeSport.COURSE);
        // Null dateDebut -> should test the minusMonths(1) branch
        obj.setDateDebut(null); 
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getDureeBySportAndPeriod(eq(user), eq(TypeSport.COURSE), any(), any())).thenReturn(120);

        Float progression = objectifService.getProgressionObjectif(obj, user);
        assertEquals(120f, progression);
    }

    @Test
    void testGetPourcentageObjectifEtIsAtteint() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.KCAL);
        obj.setCible(1000f);
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getCaloriesByPeriod(eq(user), any(), any())).thenReturn(500f);

        assertEquals(50f, objectifService.getPourcentageObjectif(obj, user));
        assertFalse(objectifService.isObjectifAtteint(obj, user));

        // Test objective zero or null
        obj.setCible(0f);
        assertEquals(0f, objectifService.getPourcentageObjectif(obj, user));
    }
}