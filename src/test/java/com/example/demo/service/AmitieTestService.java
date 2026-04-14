package com.example.demo.service;

import com.example.demo.model.Amitie;
import com.example.demo.model.StatutAmitie;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.AmitieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmitieTestService {

    @Mock
    private AmitieRepository amitieRepository;

    @InjectMocks
    private AmitieService amitieService;

    @Test
    void testEnvoyerDemande_NouvelleDemande() {

        Utilisateur u1 = new Utilisateur();
        u1.setId(1L);

        Utilisateur u2 = new Utilisateur();
        u2.setId(2L);

        when(amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2))
                .thenReturn(Optional.empty());

        when(amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u2, u1))
                .thenReturn(Optional.empty());

        when(amitieRepository.save(any(Amitie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = amitieService.envoyerDemande(u1, u2);

        assertEquals("Demande envoyée avec succès !", result);
        verify(amitieRepository, times(1)).save(any(Amitie.class));
    }

    @Test
    void testEnvoyerDemande_DejaExistanteBloquee() {

        Utilisateur u1 = new Utilisateur();
        u1.setId(1L);

        Utilisateur u2 = new Utilisateur();
        u2.setId(2L);

        Amitie existing = new Amitie();
        existing.setStatut(StatutAmitie.EN_ATTENTE);

        when(amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2))
                .thenReturn(Optional.of(existing));

        String result = amitieService.envoyerDemande(u1, u2);

        assertTrue(result.contains("déjà une demande"));
        verify(amitieRepository, never()).save(any());
    }

    @Test
    void testAccepterDemande() {

        Amitie amitie = new Amitie();
        amitie.setStatut(StatutAmitie.EN_ATTENTE);

        when(amitieRepository.save(any(Amitie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Amitie result = amitieService.accepterDemande(amitie);

        assertEquals(StatutAmitie.ACCEPTEE, result.getStatut());
    }

    @Test
    void testRefuserDemande() {

        Amitie amitie = new Amitie();
        amitie.setStatut(StatutAmitie.EN_ATTENTE);

        when(amitieRepository.save(any(Amitie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Amitie result = amitieService.refuserDemande(amitie);

        assertEquals(StatutAmitie.REFUSEE, result.getStatut());
    }

    @Test
    void testAnnulerDemande() {

        Amitie amitie = new Amitie();

        when(amitieRepository.save(any(Amitie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Amitie result = amitieService.annulerDemande(amitie);

        assertEquals(StatutAmitie.ANNULEE, result.getStatut());
    }

    @Test
    void testRompreAmitie() {

        Amitie amitie = new Amitie();

        amitieService.rompreAmitie(amitie);

        verify(amitieRepository, times(1)).delete(amitie);
    }

    @Test
    void testGetById() {

        Amitie amitie = new Amitie();
        amitie.setAmitieID(1L);

        when(amitieRepository.findById(1L))
                .thenReturn(Optional.of(amitie));

        Amitie result = amitieService.getById(1L);

        assertEquals(1L, result.getAmitieID());
    }
}
