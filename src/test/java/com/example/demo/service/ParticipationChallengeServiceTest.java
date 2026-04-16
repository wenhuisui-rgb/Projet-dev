package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.ParticipationChallengeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationChallengeServiceTest {

    @Mock
    private ParticipationChallengeRepository participationRepo;

    @Mock
    private ChallengeRepository challengeRepo;

    @InjectMocks
    private ParticipationChallengeService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "challengeRepository", challengeRepo);
    }

    @Test
    void testRejoindreChallenge() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        Challenge challenge = new Challenge();
        challenge.setId(1L);

        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(null);
        when(participationRepo.save(any())).thenReturn(new ParticipationChallenge(user, challenge));

        ParticipationChallenge result = service.rejoindreChallenge(user, challenge);
        assertNotNull(result);

        // Test already joined
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(result);
        assertThrows(RuntimeException.class, () -> service.rejoindreChallenge(user, challenge));
    }

    @Test
    void testActualiserScoresApresActivite() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        Activite activite = new Activite();
        activite.setUtilisateur(user);
        activite.setDateActivite(LocalDateTime.now());
        activite.setTypeSport(TypeSport.COURSE);
        activite.setDistance(10f);
        activite.setDuree(60);
        activite.setCalories(500f);

        Challenge challengeKm = new Challenge();
        challengeKm.setId(1L);
        challengeKm.setUnite(Unite.KM);
        challengeKm.setTypeSport(null); // Matches any sport

        Challenge challengeMinutes = new Challenge();
        challengeMinutes.setId(2L);
        challengeMinutes.setUnite(Unite.MINUTES);
        challengeMinutes.setTypeSport(TypeSport.COURSE);

        Challenge challengeWrongSport = new Challenge();
        challengeWrongSport.setId(3L);
        challengeWrongSport.setTypeSport(TypeSport.VELO); // Will be skipped

        when(challengeRepo.findActiveChallengesByUserId(eq(1L), any()))
                .thenReturn(Arrays.asList(challengeKm, challengeMinutes, challengeWrongSport));

        ParticipationChallenge pc1 = new ParticipationChallenge(user, challengeKm);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(pc1);

        ParticipationChallenge pc2 = new ParticipationChallenge(user, challengeMinutes);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 2L)).thenReturn(pc2);

        service.actualiserScoresApresActivite(activite);

        // Verify save was called for valid challenges
        verify(participationRepo, times(2)).save(any());
        assertEquals(10f, pc1.getScoreActuel()); // 0 + 10km
        assertEquals(60f, pc2.getScoreActuel()); // 0 + 60mins
    }

    @Test
    void testQuitterEtUpdate() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        Challenge challenge = new Challenge();
        challenge.setId(1L);

        ParticipationChallenge pc = new ParticipationChallenge(user, challenge);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(pc);

        service.quitterChallenge(user, challenge);
        verify(participationRepo).delete(pc);

        when(participationRepo.save(pc)).thenReturn(pc);
        ParticipationChallenge updated = service.mettreAJourScore(user, challenge, 100f);
        assertEquals(100f, updated.getScoreActuel());
    }

    @Test
    void testQuitterChallenge_NotFound() {
        Utilisateur user = new Utilisateur(); user.setId(1L);
        Challenge challenge = new Challenge(); challenge.setId(1L);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(null);
        
        service.quitterChallenge(user, challenge);
        verify(participationRepo, never()).delete(any()); // 找不到就不删
    }

    @Test
    void testAjouterScore() {
        Utilisateur user = new Utilisateur(); user.setId(1L);
        Challenge challenge = new Challenge(); challenge.setId(1L);
        ParticipationChallenge pc = new ParticipationChallenge();
        pc.setScoreActuel(10f);

        // 分支 1: 找到记录，累加分数
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(pc);
        when(participationRepo.save(pc)).thenReturn(pc);
        ParticipationChallenge result = service.ajouterScore(user, challenge, 5f);
        assertNotNull(result);
        assertEquals(15f, pc.getScoreActuel());

        // 分支 2: 找不到记录，返回 null
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 2L)).thenReturn(null);
        Challenge c2 = new Challenge(); c2.setId(2L);
        assertNull(service.ajouterScore(user, c2, 5f));
    }

    @Test
    void testMettreAJourScore_NotFound() {
        Utilisateur user = new Utilisateur(); user.setId(1L);
        Challenge challenge = new Challenge(); challenge.setId(1L);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(null);
        
        // 触发找不到记录抛出异常的分支
        assertThrows(RuntimeException.class, () -> service.mettreAJourScore(user, challenge, 50f));
    }

    @Test
    void testObtenirClassementEtEstDejaInscrit() {
        service.obtenirClassement(1L);
        verify(participationRepo).findByChallengeIdOrderByScoreActuelDesc(1L);

        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(new ParticipationChallenge());
        assertTrue(service.estDejaInscrit(1L, 1L));

        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 2L)).thenReturn(null);
        assertFalse(service.estDejaInscrit(1L, 2L));
    }

    @Test
    void testActualiserScores_BranchesNullEtSkip() {
        Utilisateur user = new Utilisateur(); user.setId(1L);
        Activite act = new Activite();
        act.setUtilisateur(user);
        act.setDateActivite(LocalDateTime.now());
        act.setTypeSport(TypeSport.VELO);
        
        // 全部设为 null 触发三元运算符的 : 0f 兜底分支
        act.setDistance(null); 
        act.setDuree(null); 
        act.setCalories(null); 

        // 挑战 1: 运动类型不符 (COURSE != VELO) -> 触发 continue 分支
        Challenge c1 = new Challenge(); c1.setTypeSport(TypeSport.COURSE);

        // 挑战 2: KM, 但是距离为 null -> valeur = 0
        Challenge c2 = new Challenge(); c2.setUnite(Unite.KM);

        // 挑战 3: MINUTES, 但是时长为 null -> valeur = 0
        Challenge c3 = new Challenge(); c3.setUnite(Unite.MINUTES);

        // 挑战 4: KCAL (覆盖遗漏的 switch KCAL 分支), 且 null -> valeur = 0
        Challenge c4 = new Challenge(); c4.setUnite(Unite.KCAL);

        when(challengeRepo.findActiveChallengesByUserId(eq(1L), any()))
                .thenReturn(java.util.Arrays.asList(c1, c2, c3, c4));

        service.actualiserScoresApresActivite(act);

        // 因为所有有效分数都是 0，或者被 continue 跳过，所以永远不会执行 save
        verify(participationRepo, never()).save(any());
    }

    @Test
    void testActualiserScores_NonNullValues_TernaryBranches() {
        Utilisateur user = new Utilisateur(); user.setId(1L);
        Activite act = new Activite();
        act.setUtilisateur(user);
        act.setDateActivite(LocalDateTime.now());
        act.setDistance(15f); // 非空
        act.setDuree(90);     // 非空
        act.setCalories(600f);// 非空

        Challenge cKm = new Challenge(); cKm.setId(1L); cKm.setUnite(Unite.KM);
        Challenge cMin = new Challenge(); cMin.setId(2L); cMin.setUnite(Unite.MINUTES);
        Challenge cKcal = new Challenge(); cKcal.setId(3L); cKcal.setUnite(Unite.KCAL);

        when(challengeRepo.findActiveChallengesByUserId(eq(1L), any()))
                .thenReturn(java.util.Arrays.asList(cKm, cMin, cKcal));

        ParticipationChallenge pKm = new ParticipationChallenge(); pKm.setScoreActuel(0f);
        ParticipationChallenge pMin = new ParticipationChallenge(); pMin.setScoreActuel(0f);
        ParticipationChallenge pKcal = new ParticipationChallenge(); pKcal.setScoreActuel(0f);

        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 1L)).thenReturn(pKm);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 2L)).thenReturn(pMin);
        when(participationRepo.findByUtilisateurIdAndChallengeId(1L, 3L)).thenReturn(pKcal);

        // 这会完美覆盖 KM, MINUTES, KCAL 提取非空值的分支
        service.actualiserScoresApresActivite(act);

        verify(participationRepo, times(3)).save(any());
        assertEquals(15f, pKm.getScoreActuel());
        assertEquals(90f, pMin.getScoreActuel());
        assertEquals(600f, pKcal.getScoreActuel());
    }
}