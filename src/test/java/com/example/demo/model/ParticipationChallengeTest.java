package com.example.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ParticipationChallengeTest {
    @Test
    void testParticipationChallenge() {
        Utilisateur mockUser = mock(Utilisateur.class);
        Challenge mockChallenge = mock(Challenge.class);
        
        ParticipationChallenge pc = new ParticipationChallenge(mockUser, mockChallenge);
        
        assertNotNull(pc.getDateInscription());
        assertEquals(0f, pc.getScoreActuel());
        assertEquals(mockUser, pc.getUtilisateur());
        assertEquals(mockChallenge, pc.getChallenge());

        pc.mettreAJourScore(15.5f);
        assertEquals(15.5f, pc.getScoreActuel());

        pc.setDateInscription(null);
        assertNull(pc.getDateInscription());

        Utilisateur newUser = mock(Utilisateur.class);
        pc.setUtilisateur(newUser);
        assertEquals(newUser, pc.getUtilisateur());

        Challenge newChallenge = mock(Challenge.class);
        pc.setChallenge(newChallenge);
        assertEquals(newChallenge, pc.getChallenge());
    }

    @Test
    void testGetIdMissing() {
        ParticipationChallenge pc = new ParticipationChallenge();
        // 直接调用 getId() 覆盖红线
        assertNull(pc.getId());
    }
}