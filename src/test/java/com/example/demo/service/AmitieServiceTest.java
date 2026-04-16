package com.example.demo.service;

import com.example.demo.model.Amitie;
import com.example.demo.model.StatutAmitie;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.AmitieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmitieServiceTest {

    @Mock
    private AmitieRepository amitieRepository;

    @InjectMocks
    private AmitieService amitieService;

    private Utilisateur user1;
    private Utilisateur user2;

    @BeforeEach
    void setUp() {
        user1 = new Utilisateur();
        user1.setId(1L);

        user2 = new Utilisateur();
        user2.setId(2L);
    }

    @Test
    void testEnvoyerDemande_Self() {
        assertEquals("SELF", amitieService.envoyerDemande(user1, user1));
    }

    @Test
    void testEnvoyerDemande_AlreadyPending() {
        Amitie existante = new Amitie(user1, user2);
        existante.setStatut(StatutAmitie.EN_ATTENTE);
        when(amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(user1, user2))
                .thenReturn(Optional.of(existante));

        assertEquals("PENDING", amitieService.envoyerDemande(user1, user2));
    }

    @Test
    void testEnvoyerDemande_AlreadyFriend() {
        Amitie existante = new Amitie(user1, user2);
        existante.setStatut(StatutAmitie.ACCEPTEE);
        when(amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(user1, user2))
                .thenReturn(Optional.of(existante));

        assertEquals("ALREADY_FRIEND", amitieService.envoyerDemande(user1, user2));
    }

    @Test
    void testEnvoyerDemande_SentAgain() {
        Amitie existante = new Amitie(user1, user2);
        existante.setStatut(StatutAmitie.REFUSEE);
        when(amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(user1, user2))
                .thenReturn(Optional.of(existante));

        assertEquals("SENT_AGAIN", amitieService.envoyerDemande(user1, user2));
        assertEquals(StatutAmitie.EN_ATTENTE, existante.getStatut());
        verify(amitieRepository).save(existante);
    }

    @Test
    void testEnvoyerDemande_New() {
        when(amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(user1, user2))
                .thenReturn(Optional.empty());

        assertEquals("SENT", amitieService.envoyerDemande(user1, user2));
        verify(amitieRepository).save(any(Amitie.class));
    }

    @Test
    void testAccepterDemande() {
        Amitie amitie = new Amitie();
        when(amitieRepository.save(amitie)).thenReturn(amitie);

        Amitie result = amitieService.accepterDemande(amitie);
        assertEquals(StatutAmitie.ACCEPTEE, result.getStatut());
    }

    @Test
    void testRefuserDemande() {
        Amitie amitie = new Amitie();
        when(amitieRepository.save(amitie)).thenReturn(amitie);

        Amitie result = amitieService.refuserDemande(amitie);
        assertEquals(StatutAmitie.REFUSEE, result.getStatut());
    }

    @Test
    void testGetById_Found() {
        Amitie amitie = new Amitie();
        when(amitieRepository.findById(1L)).thenReturn(Optional.of(amitie));
        assertEquals(amitie, amitieService.getById(1L));
    }

    @Test
    void testGetById_NotFound() {
        when(amitieRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> amitieService.getById(1L));
    }

    @Test
    void testGetDemandesRecues() {
        List<Amitie> list = Arrays.asList(new Amitie());
        when(amitieRepository.findByUtilisateurReceveurAndStatut(user1, StatutAmitie.EN_ATTENTE))
                .thenReturn(list);
        assertEquals(list, amitieService.getDemandesRecues(user1));
    }

    @Test
    void testGetDemandesEnvoyeesIds() {
        Amitie amitie = new Amitie(user1, user2);
        when(amitieRepository.findByUtilisateurDemandeurAndStatut(user1, StatutAmitie.EN_ATTENTE))
                .thenReturn(Arrays.asList(amitie));

        List<Long> ids = amitieService.getDemandesEnvoyeesIds(user1);
        assertEquals(1, ids.size());
        assertEquals(2L, ids.get(0));
    }

    @Test
    void testGetAmis() {
        Utilisateur user3 = new Utilisateur();
        user3.setId(3L);

        Amitie asDemandeur = new Amitie(user1, user2);
        asDemandeur.setStatut(StatutAmitie.ACCEPTEE);

        Amitie asReceveur = new Amitie(user3, user1);
        asReceveur.setStatut(StatutAmitie.ACCEPTEE);

        when(amitieRepository.findByUtilisateurDemandeurAndStatut(user1, StatutAmitie.ACCEPTEE))
                .thenReturn(Arrays.asList(asDemandeur));
        when(amitieRepository.findByUtilisateurReceveurAndStatut(user1, StatutAmitie.ACCEPTEE))
                .thenReturn(Arrays.asList(asReceveur));

        List<Utilisateur> amis = amitieService.getAmis(user1);
        assertEquals(2, amis.size());
        assertTrue(amis.contains(user2));
        assertTrue(amis.contains(user3));
    }
}