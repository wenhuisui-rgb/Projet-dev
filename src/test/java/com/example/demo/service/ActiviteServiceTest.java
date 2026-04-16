package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.ObtentionBadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActiviteServiceTest {

    @Mock
    private ActiviteRepository activiteRepository;

    @Mock
    private ParticipationChallengeService participationChallengeService;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private ObtentionBadgeRepository obtentionBadgeRepository;

    @InjectMocks
    private ActiviteService activiteService;

    @Test
    void testSauvegarderActiviteEtBadges() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        
        // Nouvelle activité
        Activite newActivite = new Activite();
        newActivite.setUtilisateur(user);
        newActivite.setTypeSport(TypeSport.COURSE);
        newActivite.setDuree(60);
        newActivite.setDistance(15f); // Pour trigger COURSE_10KM
        
        when(activiteRepository.save(any(Activite.class))).thenReturn(newActivite);

        // Simulation de l'historique pour les badges
        Activite hist1 = new Activite();
        hist1.setTypeSport(TypeSport.COURSE);
        hist1.setDistance(15f);
        hist1.setDuree(60);

        when(activiteRepository.findByUtilisateurOrderByDateActiviteDesc(user))
                .thenReturn(Arrays.asList(hist1));

        // Mock Badge check (Pour le badge "COURSE_10KM" et "PREMIER_PAS")
        Badge fakeBadge = new Badge();
        fakeBadge.setId(100L);
        when(badgeRepository.findByNom(anyString())).thenReturn(Optional.of(fakeBadge));
        when(obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(anyLong(), anyLong()))
                .thenReturn(false);

        // Appel
        Activite result = activiteService.sauvegarderActivite(newActivite, 70f);

        assertNotNull(result);
        assertNotNull(result.getCalories()); // A été calculé
        verify(activiteRepository).save(newActivite);
        // verifierEtAttribuerBadges a dû appeler save pour plusieurs badges (car mockés à "non obtenus")
        verify(obtentionBadgeRepository, atLeastOnce()).save(any(ObtentionBadge.class));
    }

    @Test
    void testUpdateActivite() {
        Utilisateur user = new Utilisateur();
        Activite existante = new Activite();
        existante.setId(1L);
        existante.setDuree(30);

        Activite modifiee = new Activite();
        modifiee.setTypeSport(TypeSport.VELO);
        modifiee.setDuree(120);

        when(activiteRepository.findById(1L)).thenReturn(Optional.of(existante));
        when(activiteRepository.save(existante)).thenReturn(existante);

        Activite result = activiteService.updateActivite(1L, modifiee, 75f);
        
        assertNotNull(result);
        assertEquals(TypeSport.VELO, result.getTypeSport());
        assertEquals(120, result.getDuree());
        // Verify participation updated
        verify(participationChallengeService).actualiserScoresApresActivite(existante);

        assertNull(activiteService.updateActivite(2L, modifiee, 75f));
    }

    @Test
    void testSupprimerActivite() {
        activiteService.supprimerActivite(1L);
        verify(activiteRepository).deleteById(1L);
    }

    @Test
    void testStatsDashboardAndGetters() {
        Utilisateur user = new Utilisateur();
        
        when(activiteRepository.getDistanceTotale(user)).thenReturn(100f);
        when(activiteRepository.getDureeTotale(user)).thenReturn(500);
        when(activiteRepository.getCaloriesTotales(user)).thenReturn(2000f);
        when(activiteRepository.countByUtilisateur(user)).thenReturn(10L);
        when(activiteRepository.getDistanceByPeriod(eq(user), any(), any())).thenReturn(50f);
        when(activiteRepository.countByUtilisateurAndDateActiviteBetween(eq(user), any(), any())).thenReturn(5L);
        when(activiteRepository.findTop5ByUtilisateurOrderByDateActiviteDesc(user)).thenReturn(Arrays.asList(new Activite()));

        Map<String, Object> stats = activiteService.getStatsDashboard(user);

        assertEquals(100f, stats.get("totalDistance"));
        assertEquals(500, stats.get("totalDuree"));
        assertEquals(2000f, stats.get("totalCalories"));
        assertEquals(10L, stats.get("totalActivites"));
        assertEquals(50f, stats.get("distanceMois"));
        assertEquals(5, stats.get("activitesMois"));
        assertNotNull(stats.get("dernieresActivites"));
        
        // Test aAtteintObjectifMensuel
        assertTrue(activiteService.aAtteintObjectifMensuel(user, 40f));
        assertFalse(activiteService.aAtteintObjectifMensuel(user, 100f));
        assertFalse(activiteService.aAtteintObjectifMensuel(user, null));
    }

    @Test
    void testGetStatistiquesSemaineMois() {
        Utilisateur user = new Utilisateur();
        when(activiteRepository.getDistanceByPeriod(eq(user), any(), any())).thenReturn(null); // test null safety
        when(activiteRepository.getDureeByPeriod(eq(user), any(), any())).thenReturn(null);
        when(activiteRepository.getCaloriesByPeriod(eq(user), any(), any())).thenReturn(null);

        Map<String, Float> statsSemaine = activiteService.getStatistiquesParSemaine(user);
        assertEquals(0f, statsSemaine.get("distance"));
        assertEquals(0f, statsSemaine.get("duree"));
        assertEquals(0f, statsSemaine.get("calories"));

        Map<String, Float> statsMois = activiteService.getStatistiquesParMois(user);
        assertEquals(0f, statsMois.get("distance"));
    }

    @Test
    void testGetActivitesPaginees() {
        Utilisateur user = new Utilisateur();
        Page<Activite> page = new PageImpl<>(Arrays.asList(new Activite()));
        when(activiteRepository.findByUtilisateurOrderByDateActiviteDesc(eq(user), any(PageRequest.class)))
                .thenReturn(page);

        assertEquals(1, activiteService.getActivitesPaginees(user, 0, 10).getContent().size());
    }

    @Test
    void testChartData() {
        Utilisateur user = new Utilisateur();
        // Just mock the generic period methods to return a fixed value
        when(activiteRepository.getDureeByPeriod(eq(user), any(), any())).thenReturn(30);

        List<ChartData> semaine = activiteService.getChartData(user, "semaine");
        assertEquals(7, semaine.size());

        List<ChartData> mois = activiteService.getChartData(user, "mois");
        assertTrue(mois.size() >= 28 && mois.size() <= 31); // Depends on current month

        List<ChartData> annee = activiteService.getChartData(user, "annee");
        assertEquals(12, annee.size());
    }

    @Test
    void testChartDataBySports() {
        Utilisateur user = new Utilisateur();
        List<TypeSport> sports = Arrays.asList(TypeSport.COURSE);

        // Test Metric: Duree
        when(activiteRepository.getDureeByPeriodAndSports(eq(user), any(), any(), eq(sports))).thenReturn(45);
        List<ChartData> semaineDuree = activiteService.getChartDataBySports(user, "semaine", sports, "duree");
        assertEquals(7, semaineDuree.size());
        assertEquals(45, semaineDuree.get(0).getValue());

        // Test Metric: Calories
        when(activiteRepository.getCaloriesByPeriodAndSports(eq(user), any(), any(), eq(sports))).thenReturn(300f);
        List<ChartData> moisCalories = activiteService.getChartDataBySports(user, "mois", sports, "calories");
        assertTrue(moisCalories.size() >= 28);
        assertEquals(300, moisCalories.get(0).getValue());

        List<ChartData> anneeCalories = activiteService.getChartDataBySports(user, "annee", sports, "calories");
        assertEquals(12, anneeCalories.size());
    }
}