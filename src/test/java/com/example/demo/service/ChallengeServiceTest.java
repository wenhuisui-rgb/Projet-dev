package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    void testCreerChallenge() {
        Utilisateur creator = new Utilisateur();
        Challenge mockSaved = new Challenge();
        when(challengeRepository.save(any(Challenge.class))).thenReturn(mockSaved);

        Challenge result = challengeService.creerChallenge("Titre", TypeSport.COURSE, 
                LocalDate.now(), LocalDate.now().plusDays(5), creator, Unite.KM, 100f);

        assertNotNull(result);
        verify(challengeRepository).save(any(Challenge.class));
    }

    @Test
    void testSupprimerChallenge() {
        challengeService.supprimerChallenge(1L);
        verify(challengeRepository).deleteById(1L);
    }

    @Test
    void testGetAllChallenges() {
        when(challengeRepository.findAll()).thenReturn(Arrays.asList(new Challenge()));
        assertEquals(1, challengeService.getAllChallenges().size());
    }

    @Test
    void testGetChallengeById() {
        Challenge c = new Challenge();
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(c));
        assertEquals(c, challengeService.getChallengeById(1L));

        when(challengeRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(challengeService.getChallengeById(2L));
    }

    @Test
    void testFindByTypeSport() {
        when(challengeRepository.findByTypeSport(TypeSport.COURSE)).thenReturn(Arrays.asList(new Challenge()));
        assertEquals(1, challengeService.findByTypeSport(TypeSport.COURSE).size());
    }

    @Test
    void testGetChallengesByCreateur() {
        when(challengeRepository.findByCreateurId(1L)).thenReturn(Arrays.asList(new Challenge()));
        assertEquals(1, challengeService.getChallengesByCreateur(1L).size());
    }

    @Test
    void testFindChallengesByUser() {
        Utilisateur u = new Utilisateur();
        when(challengeRepository.findChallengesByUser(u)).thenReturn(Arrays.asList(new Challenge()));
        assertEquals(1, challengeService.findChallengesByUser(u).size());
    }
}