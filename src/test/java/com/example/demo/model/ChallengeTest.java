package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ChallengeTest {

    @Test
    void testConstructorsAndGettersSetters() {
        TypeSport mockTypeSport = mock(TypeSport.class);
        Utilisateur mockCreateur = mock(Utilisateur.class);
        Unite mockUnite = mock(Unite.class);

        Challenge challenge = new Challenge("100k", mockTypeSport, LocalDate.now(), LocalDate.now().plusDays(10), mockCreateur, mockUnite, 100f);
        
        challenge.setId(1L);
        assertEquals(1L, challenge.getId());

        assertEquals("100k", challenge.getTitre());
        challenge.setTitre("50k");
        assertEquals("50k", challenge.getTitre());

        challenge.setTypeSport(mockTypeSport);
        assertEquals(mockTypeSport, challenge.getTypeSport());

        challenge.setCreateur(mockCreateur);
        assertEquals(mockCreateur, challenge.getCreateur());

        challenge.setUnite(mockUnite);
        assertEquals(mockUnite, challenge.getUnite());

        challenge.setCible(200f);
        assertEquals(200f, challenge.getCible());

        StatutChallenge mockStatut = mock(StatutChallenge.class);
        challenge.setStatut(mockStatut);
        assertEquals(mockStatut, challenge.getStatut());

        List<ParticipationChallenge> participations = new ArrayList<>();
        challenge.setParticipations(participations);
        assertEquals(participations, challenge.getParticipations());
    }

    @Test
    void testEstActif() {
        Challenge challenge = new Challenge();
        LocalDate today = LocalDate.now();

        // Cas 1: Challenge en cours (Start = today, End = tomorrow)
        challenge.setDateDebut(today);
        challenge.setDateFin(today.plusDays(1));
        assertTrue(challenge.estActif());

        // Cas 2: Challenge pas encore commencé (Start = tomorrow)
        challenge.setDateDebut(today.plusDays(1));
        challenge.setDateFin(today.plusDays(5));
        assertFalse(challenge.estActif());

        // Cas 3: Challenge terminé (End = yesterday)
        challenge.setDateDebut(today.minusDays(5));
        challenge.setDateFin(today.minusDays(1));
        assertFalse(challenge.estActif());

        // Cas 4: Même jour
        challenge.setDateDebut(today);
        challenge.setDateFin(today);
        assertTrue(challenge.estActif());
    }

    @Test
    void testGetDatesMissing() {
        Challenge challenge = new Challenge();
        LocalDate debut = LocalDate.now();
        LocalDate fin = LocalDate.now().plusDays(5);
        
        challenge.setDateDebut(debut);
        challenge.setDateFin(fin);
        
        // 调用这两个被遗漏的 Getter
        assertEquals(debut, challenge.getDateDebut());
        assertEquals(fin, challenge.getDateFin());
    }
}