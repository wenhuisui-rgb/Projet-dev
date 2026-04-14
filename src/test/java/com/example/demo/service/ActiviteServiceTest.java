package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActiviteServiceTest {

    @Mock
    private ActiviteRepository activiteRepository;

    @InjectMocks
    private ActiviteService activiteService;

    private Utilisateur utilisateur;
    private Activite activite1;
    private Activite activite2;
    private Float poidsUtilisateur = 70.0f;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPseudo("Dupont");
        utilisateur.setPoids(poidsUtilisateur);

        activite1 = new Activite();
        activite1.setId(1L);
        activite1.setUtilisateur(utilisateur);
        activite1.setTypeSport(TypeSport.COURSE);
        activite1.setDateActivite(LocalDateTime.now().minusDays(2));
        activite1.setDuree(30);
        activite1.setDistance(5.0f);
        activite1.setLocalisation("Paris");
        activite1.setEvaluation(4);

        activite2 = new Activite();
        activite2.setId(2L);
        activite2.setUtilisateur(utilisateur);
        activite2.setTypeSport(TypeSport.VELO);
        activite2.setDateActivite(LocalDateTime.now().minusDays(5));
        activite2.setDuree(60);
        activite2.setDistance(15.0f);
        activite2.setLocalisation("Lyon");
        activite2.setEvaluation(5);
    }

    @Test
    void sauvegarderActivite_ShouldCalculateCaloriesAndSave() {
        when(activiteRepository.save(any(Activite.class))).thenReturn(activite1);
        
        Activite result = activiteService.sauvegarderActivite(activite1, poidsUtilisateur);
        
        assertNotNull(result);
        verify(activiteRepository).save(activite1);
    }

    @Test
    void getActiviteParId_WhenExists_ShouldReturnActivite() {
        when(activiteRepository.findById(1L)).thenReturn(Optional.of(activite1));
        
        Activite result = activiteService.getActiviteParId(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(TypeSport.COURSE, result.getTypeSport());
    }

    @Test
    void getActiviteParId_WhenNotExists_ShouldReturnNull() {
        when(activiteRepository.findById(99L)).thenReturn(Optional.empty());
        
        Activite result = activiteService.getActiviteParId(99L);
        
        assertNull(result);
    }

    @Test
    void getActivitesParUtilisateur_ShouldReturnList() {
        List<Activite> activites = Arrays.asList(activite1, activite2);
        when(activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur))
            .thenReturn(activites);
        
        List<Activite> result = activiteService.getActivitesParUtilisateur(utilisateur);
        
        assertEquals(2, result.size());
        verify(activiteRepository).findByUtilisateurOrderByDateActiviteDesc(utilisateur);
    }

    @Test
    void getDernieresActivites_ShouldReturnTop5() {
        List<Activite> dernieresActivites = Arrays.asList(activite1);
        when(activiteRepository.findTop5ByUtilisateurOrderByDateActiviteDesc(utilisateur))
            .thenReturn(dernieresActivites);
        
        List<Activite> result = activiteService.getDernieresActivites(utilisateur);
        
        assertEquals(1, result.size());
        verify(activiteRepository).findTop5ByUtilisateurOrderByDateActiviteDesc(utilisateur);
    }

    @Test
    void getActivitesEntreDates_ShouldReturnFilteredList() {
        LocalDateTime debut = LocalDateTime.now().minusDays(10);
        LocalDateTime fin = LocalDateTime.now();
        List<Activite> activites = Arrays.asList(activite1, activite2);
        
        when(activiteRepository.findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(
            eq(utilisateur), eq(debut), eq(fin)))
            .thenReturn(activites);
        
        List<Activite> result = activiteService.getActivitesEntreDates(utilisateur, debut, fin);
        
        assertEquals(2, result.size());
    }

    @Test
    void getActivitesDeLaSemaine_ShouldReturnLast7Days() {
        List<Activite> activites = Arrays.asList(activite1);
        when(activiteRepository.findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(
            eq(utilisateur), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(activites);
        
        List<Activite> result = activiteService.getActivitesDeLaSemaine(utilisateur);
        
        assertEquals(1, result.size());
    }

    @Test
    void getActivitesDuMois_ShouldReturnCurrentMonthActivities() {
        List<Activite> activites = Arrays.asList(activite1, activite2);
        when(activiteRepository.findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(
            eq(utilisateur), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(activites);
        
        List<Activite> result = activiteService.getActivitesDuMois(utilisateur);
        
        assertEquals(2, result.size());
    }

    @Test
    void getActivitesParType_ShouldReturnFilteredBySportType() {
        List<Activite> activitesCourse = Arrays.asList(activite1);
        when(activiteRepository.findByUtilisateurAndTypeSportOrderByDateActiviteDesc(
            utilisateur, TypeSport.COURSE))
            .thenReturn(activitesCourse);
        
        List<Activite> result = activiteService.getActivitesParType(utilisateur, TypeSport.COURSE);
        
        assertEquals(1, result.size());
        assertEquals(TypeSport.COURSE, result.get(0).getTypeSport());
    }

    @Test
    void getDistanceTotale_ShouldReturnTotalDistance() {
        when(activiteRepository.getDistanceTotale(utilisateur)).thenReturn(20.0f);
        
        Float result = activiteService.getDistanceTotale(utilisateur);
        
        assertEquals(20.0f, result);
    }

    @Test
    void getDureeTotale_ShouldReturnTotalDuration() {
        when(activiteRepository.getDureeTotale(utilisateur)).thenReturn(90);
        
        Integer result = activiteService.getDureeTotale(utilisateur);
        
        assertEquals(90, result);
    }

    @Test
    void getCaloriesTotales_ShouldReturnTotalCalories() {
        when(activiteRepository.getCaloriesTotales(utilisateur)).thenReturn(500.0f);
        
        Float result = activiteService.getCaloriesTotales(utilisateur);
        
        assertEquals(500.0f, result);
    }

    @Test
    void getDistanceDuMois_ShouldReturnMonthlyDistance() {
        when(activiteRepository.getDistanceByPeriod(eq(utilisateur), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(25.0f);
        
        Float result = activiteService.getDistanceDuMois(utilisateur);
        
        assertEquals(25.0f, result);
    }

    @Test
    void getNombreActivitesDuMois_ShouldReturnMonthlyActivityCount() {
        when(activiteRepository.countByUtilisateurAndDateActiviteBetween(
            eq(utilisateur), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(5L);
        
        Integer result = activiteService.getNombreActivitesDuMois(utilisateur);
        
        assertEquals(5, result);
    }

    @Test
    void getStatsDashboard_ShouldReturnCompleteStatistics() {
        when(activiteRepository.getDistanceTotale(utilisateur)).thenReturn(20.0f);
        when(activiteRepository.getDureeTotale(utilisateur)).thenReturn(90);
        when(activiteRepository.getCaloriesTotales(utilisateur)).thenReturn(500.0f);
        when(activiteRepository.countByUtilisateur(utilisateur)).thenReturn(2L);
        when(activiteRepository.getDistanceByPeriod(any(), any(), any())).thenReturn(25.0f);
        when(activiteRepository.countByUtilisateurAndDateActiviteBetween(any(), any(), any())).thenReturn(5L);
        when(activiteRepository.findTop5ByUtilisateurOrderByDateActiviteDesc(utilisateur))
            .thenReturn(Arrays.asList(activite1, activite2));
        
        Map<String, Object> stats = activiteService.getStatsDashboard(utilisateur);
        
        assertEquals(20.0f, stats.get("totalDistance"));
        assertEquals(90, stats.get("totalDuree"));
        assertEquals(500.0f, stats.get("totalCalories"));
        assertEquals(2L, stats.get("totalActivites"));
        assertEquals(25.0f, stats.get("distanceMois"));
        assertEquals(5, stats.get("activitesMois"));
        assertNotNull(stats.get("dernieresActivites"));
    }

    @Test
    void getStatistiquesParSemaine_ShouldReturnWeeklyStats() {
        when(activiteRepository.getDistanceByPeriod(any(), any(), any())).thenReturn(20.0f);
        when(activiteRepository.getDureeByPeriod(any(), any(), any())).thenReturn(90);
        when(activiteRepository.getCaloriesByPeriod(any(), any(), any())).thenReturn(400.0f);
        
        Map<String, Float> stats = activiteService.getStatistiquesParSemaine(utilisateur);
        
        assertEquals(20.0f, stats.get("distance"));
        assertEquals(90.0f, stats.get("duree"));
        assertEquals(400.0f, stats.get("calories"));
    }

    @Test
    void getStatistiquesParSemaine_WhenNoData_ShouldReturnZero() {
        when(activiteRepository.getDistanceByPeriod(any(), any(), any())).thenReturn(null);
        when(activiteRepository.getDureeByPeriod(any(), any(), any())).thenReturn(null);
        when(activiteRepository.getCaloriesByPeriod(any(), any(), any())).thenReturn(null);
        
        Map<String, Float> stats = activiteService.getStatistiquesParSemaine(utilisateur);
        
        assertEquals(0.0f, stats.get("distance"));
        assertEquals(0.0f, stats.get("duree"));
        assertEquals(0.0f, stats.get("calories"));
    }

    @Test
    void getStatistiquesParMois_ShouldReturnMonthlyStats() {
        when(activiteRepository.getDistanceByPeriod(any(), any(), any())).thenReturn(100.0f);
        when(activiteRepository.getDureeByPeriod(any(), any(), any())).thenReturn(300);
        when(activiteRepository.getCaloriesByPeriod(any(), any(), any())).thenReturn(1500.0f);
        
        Map<String, Float> stats = activiteService.getStatistiquesParMois(utilisateur);
        
        assertEquals(100.0f, stats.get("distance"));
        assertEquals(300.0f, stats.get("duree"));
        assertEquals(1500.0f, stats.get("calories"));
    }

    @Test
    void updateActivite_WhenExists_ShouldUpdateAndReturn() {
        Activite existingActivite = new Activite();
        existingActivite.setId(1L);
        existingActivite.setTypeSport(TypeSport.COURSE);
        existingActivite.setDuree(30);
        existingActivite.setDistance(5.0f);
        
        Activite updatedInfo = new Activite();
        updatedInfo.setTypeSport(TypeSport.NATATION);
        updatedInfo.setDateActivite(LocalDateTime.now());
        updatedInfo.setDuree(45);
        updatedInfo.setDistance(2.0f);
        updatedInfo.setLocalisation("Marseille");
        updatedInfo.setEvaluation(5);
        
        when(activiteRepository.findById(1L)).thenReturn(Optional.of(existingActivite));
        when(activiteRepository.save(any(Activite.class))).thenReturn(existingActivite);
        
        Activite result = activiteService.updateActivite(1L, updatedInfo, poidsUtilisateur);
        
        assertNotNull(result);
        assertEquals(TypeSport.NATATION, result.getTypeSport());
        assertEquals(45, result.getDuree());
        assertEquals(2.0f, result.getDistance());
        verify(activiteRepository).save(existingActivite);
    }

    @Test
    void updateActivite_WhenNotExists_ShouldReturnNull() {
        when(activiteRepository.findById(99L)).thenReturn(Optional.empty());
        
        Activite result = activiteService.updateActivite(99L, activite1, poidsUtilisateur);
        
        assertNull(result);
        verify(activiteRepository, never()).save(any());
    }

    @Test
    void supprimerActivite_ShouldDeleteById() {
        activiteService.supprimerActivite(1L);
        
        verify(activiteRepository).deleteById(1L);
    }

    @Test
    void aAtteintObjectifMensuel_WhenObjectiveReached_ShouldReturnTrue() {
        Float objectif = 20.0f;
        when(activiteRepository.getDistanceByPeriod(any(), any(), any())).thenReturn(25.0f);
        
        boolean result = activiteService.aAtteintObjectifMensuel(utilisateur, objectif);
        
        assertTrue(result);
    }

    @Test
    void aAtteintObjectifMensuel_WhenObjectiveNotReached_ShouldReturnFalse() {
        Float objectif = 50.0f;
        when(activiteRepository.getDistanceByPeriod(any(), any(), any())).thenReturn(25.0f);
        
        boolean result = activiteService.aAtteintObjectifMensuel(utilisateur, objectif);
        
        assertFalse(result);
    }

    @Test
    void aAtteintObjectifMensuel_WhenObjectiveIsNull_ShouldReturnFalse() {
        boolean result = activiteService.aAtteintObjectifMensuel(utilisateur, null);
        
        assertFalse(result);
        verify(activiteRepository, never()).getDistanceByPeriod(any(), any(), any());
    }
}