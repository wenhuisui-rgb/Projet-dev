package com.example.demo.service;

import com.example.demo.model.Objectif;
import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ObjectifRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ObjectifServiceTest {

    @Mock
    private ObjectifRepository objectifRepository;

    @Mock
    private ActiviteRepository activiteRepository;

    @InjectMocks
    private ObjectifService objectifService;

    private Utilisateur utilisateur;
    private Objectif objectif1;
    private Objectif objectif2;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPseudo("Dupont");

        objectif1 = new Objectif();
        objectif1.setId(1L);
        objectif1.setDescription("Courir 50km");
        objectif1.setTypeSport(TypeSport.COURSE);
        objectif1.setCible(50f);
        objectif1.setUnite(Unite.KM);
        objectif1.setPeriode(Periode.MOIS);
        objectif1.setDateDebut(LocalDate.now().minusMonths(1));
        objectif1.setDateFin(LocalDate.now());
        objectif1.setUtilisateur(utilisateur);

        objectif2 = new Objectif();
        objectif2.setId(2L);
        objectif2.setDescription("Velo 500km");
        objectif2.setTypeSport(TypeSport.VELO);
        objectif2.setCible(500f);
        objectif2.setUnite(Unite.KM);
        objectif2.setPeriode(Periode.ANNEE);
        objectif2.setDateDebut(LocalDate.now().minusMonths(3));
        objectif2.setDateFin(LocalDate.now());
        objectif2.setUtilisateur(utilisateur);
    }

    @Test
    void creerObjectif_ShouldSetUtilisateurAndSave() {
        when(objectifRepository.save(any(Objectif.class))).thenReturn(objectif1);
        
        Objectif result = objectifService.creerObjectif(objectif1, utilisateur);
        
        assertNotNull(result);
        assertEquals(utilisateur, result.getUtilisateur());
        verify(objectifRepository).save(objectif1);
    }

    @Test
    void getObjectifsParUtilisateur_ShouldReturnList() {
        List<Objectif> objectifs = Arrays.asList(objectif1, objectif2);
        when(objectifRepository.findByUtilisateur(utilisateur)).thenReturn(objectifs);
        
        List<Objectif> result = objectifService.getObjectifsParUtilisateur(utilisateur);
        
        assertEquals(2, result.size());
        verify(objectifRepository).findByUtilisateur(utilisateur);
    }

    @Test
    void getObjectifParId_WhenExists_ShouldReturnObjectif() {
        when(objectifRepository.findById(1L)).thenReturn(Optional.of(objectif1));
        
        Objectif result = objectifService.getObjectifParId(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Courir 50km", result.getDescription());
    }

    @Test
    void getObjectifParId_WhenNotExists_ShouldReturnNull() {
        when(objectifRepository.findById(99L)).thenReturn(Optional.empty());
        
        Objectif result = objectifService.getObjectifParId(99L);
        
        assertNull(result);
    }

    @Test
    void updateObjectif_WhenExists_ShouldUpdateAndReturn() {
        Objectif updatedInfo = new Objectif();
        updatedInfo.setDescription("Nouvel objectif");
        updatedInfo.setTypeSport(TypeSport.NATATION);
        updatedInfo.setCible(100f);
        updatedInfo.setUnite(Unite.MINUTES);
        updatedInfo.setPeriode(Periode.SEMAINE);
        
        when(objectifRepository.findById(1L)).thenReturn(Optional.of(objectif1));
        when(objectifRepository.save(any(Objectif.class))).thenReturn(objectif1);
        
        Objectif result = objectifService.updateObjectif(1L, updatedInfo);
        
        assertNotNull(result);
        verify(objectifRepository).save(objectif1);
    }

    @Test
    void updateObjectif_WhenNotExists_ShouldReturnNull() {
        when(objectifRepository.findById(99L)).thenReturn(Optional.empty());
        
        Objectif result = objectifService.updateObjectif(99L, objectif1);
        
        assertNull(result);
        verify(objectifRepository, never()).save(any());
    }

    @Test
    void supprimerObjectif_ShouldDeleteById() {
        objectifService.supprimerObjectif(1L);
        
        verify(objectifRepository).deleteById(1L);
    }

    @Test
    void getProgressionObjectif_ForKmWithSport_ShouldReturnDistance() {
        when(objectifRepository.findById(1L)).thenReturn(Optional.of(objectif1));
        when(activiteRepository.getDistanceBySportAndPeriod(any(), any(), any(), any()))
            .thenReturn(30.0f);
        
        Float result = objectifService.getProgressionObjectif(objectif1, utilisateur);
        
        assertEquals(30.0f, result);
    }

    @Test
    void getProgressionObjectif_ForKmWithoutSport_ShouldReturnDistance() {
        objectif1.setTypeSport(null);
        when(activiteRepository.getDistanceByPeriod(any(), any(), any()))
            .thenReturn(40.0f);
        
        Float result = objectifService.getProgressionObjectif(objectif1, utilisateur);
        
        assertEquals(40.0f, result);
    }

    @Test
    void getProgressionObjectif_ForMinutesWithSport_ShouldReturnDuree() {
        objectif1.setUnite(Unite.MINUTES);
        when(activiteRepository.getDureeBySportAndPeriod(any(), any(), any(), any()))
            .thenReturn(120);
        
        Float result = objectifService.getProgressionObjectif(objectif1, utilisateur);
        
        assertEquals(120.0f, result);
    }

    @Test
    void getProgressionObjectif_ForMinutesWithoutSport_ShouldReturnDuree() {
        objectif1.setUnite(Unite.MINUTES);
        objectif1.setTypeSport(null);
        when(activiteRepository.getDureeByPeriod(any(), any(), any()))
            .thenReturn(180);
        
        Float result = objectifService.getProgressionObjectif(objectif1, utilisateur);
        
        assertEquals(180.0f, result);
    }

    @Test
    void getProgressionObjectif_ForKcalWithSport_ShouldReturnCalories() {
        objectif1.setUnite(Unite.KCAL);
        when(activiteRepository.getCaloriesBySportAndPeriod(any(), any(), any(), any()))
            .thenReturn(1000.0f);
        
        Float result = objectifService.getProgressionObjectif(objectif1, utilisateur);
        
        assertEquals(1000.0f, result);
    }

    @Test
    void getProgressionObjectif_ForKcalWithoutSport_ShouldReturnCalories() {
        objectif1.setUnite(Unite.KCAL);
        objectif1.setTypeSport(null);
        when(activiteRepository.getCaloriesByPeriod(any(), any(), any()))
            .thenReturn(800.0f);
        
        Float result = objectifService.getProgressionObjectif(objectif1, utilisateur);
        
        assertEquals(800.0f, result);
    }

    @Test
    void getPourcentageObjectif_ShouldCalculatePercentage() {
        when(activiteRepository.getDistanceBySportAndPeriod(any(), any(), any(), any()))
            .thenReturn(25.0f);
        
        Float result = objectifService.getPourcentageObjectif(objectif1, utilisateur);
        
        assertEquals(50.0f, result);
    }

    @Test
    void getPourcentageObjectif_WhenCibleIsNull_ShouldReturnZero() {
        objectif1.setCible(null);
        
        Float result = objectifService.getPourcentageObjectif(objectif1, utilisateur);
        
        assertEquals(0f, result);
    }

    @Test
    void getPourcentageObjectif_WhenCibleIsZero_ShouldReturnZero() {
        objectif1.setCible(0f);
        
        Float result = objectifService.getPourcentageObjectif(objectif1, utilisateur);
        
        assertEquals(0f, result);
    }

    @Test
    void isObjectifAtteint_WhenProgressionGreaterOrEqual_ShouldReturnTrue() {
        when(activiteRepository.getDistanceBySportAndPeriod(any(), any(), any(), any()))
            .thenReturn(50.0f);
        
        Boolean result = objectifService.isObjectifAtteint(objectif1, utilisateur);
        
        assertTrue(result);
    }

    @Test
    void isObjectifAtteint_WhenProgressionLessThanCible_ShouldReturnFalse() {
        when(activiteRepository.getDistanceBySportAndPeriod(any(), any(), any(), any()))
            .thenReturn(30.0f);
        
        Boolean result = objectifService.isObjectifAtteint(objectif1, utilisateur);
        
        assertFalse(result);
    }
}