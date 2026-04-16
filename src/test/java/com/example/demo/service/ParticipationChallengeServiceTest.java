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
}