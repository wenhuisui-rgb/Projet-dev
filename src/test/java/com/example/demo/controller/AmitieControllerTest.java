package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.AmitieService;
import com.example.demo.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AmitieController.class)
class AmitieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AmitieService amitieService;

    @MockBean
    private UtilisateurService utilisateurService;

    @Test
    void testEnvoyerDemande() throws Exception {

        Utilisateur u1 = new Utilisateur();
        u1.setId(1L);

        Utilisateur u2 = new Utilisateur();
        u2.setId(2L);

        when(utilisateurService.findById(1L)).thenReturn(u1);
        when(utilisateurService.findByPseudo("Bob")).thenReturn(u2);
        when(amitieService.envoyerDemande(u1, u2))
                .thenReturn("Demande envoyée avec succès !");

        mockMvc.perform(post("/amities/demande")
                        .param("demandeurID", "1")
                        .param("pseudoReceveur", "Bob"))
                .andExpect(status().isOk())
                .andExpect(content().string("Demande envoyée avec succès !"));
    }

    @Test
    void testAccepterDemande() throws Exception {

        Amitie amitie = new Amitie();
        amitie.setAmitieID(10L);
        amitie.setStatut(StatutAmitie.ACCEPTEE);

        when(amitieService.getById(10L)).thenReturn(amitie);
        when(amitieService.accepterDemande(amitie)).thenReturn(amitie);

        mockMvc.perform(put("/amities/10/accepter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"));
    }

    @Test
    void testRefuserDemande() throws Exception {

        Amitie amitie = new Amitie();
        amitie.setAmitieID(20L);
        amitie.setStatut(StatutAmitie.REFUSEE);

        when(amitieService.getById(20L)).thenReturn(amitie);
        when(amitieService.refuserDemande(amitie)).thenReturn(amitie);

        mockMvc.perform(put("/amities/20/refuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"));
    }

    @Test
    void testAnnulerDemande() throws Exception {

        Amitie amitie = new Amitie();
        amitie.setAmitieID(30L);

        when(amitieService.getById(30L)).thenReturn(amitie);
        when(amitieService.annulerDemande(amitie)).thenReturn(amitie);

        mockMvc.perform(put("/amities/30/annuler"))
                .andExpect(status().isOk());
    }

    @Test
    void testRompreAmitie() throws Exception {

        Amitie amitie = new Amitie();
        amitie.setAmitieID(40L);

        when(amitieService.getById(40L)).thenReturn(amitie);
        doNothing().when(amitieService).rompreAmitie(amitie);

        mockMvc.perform(delete("/amities/40"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amitié supprimée avec succès."));
    }

    @Test
    void testGetAmities() throws Exception {

        Utilisateur u = new Utilisateur();
        u.setId(1L);

        when(utilisateurService.findById(1L)).thenReturn(u);
        when(amitieService.getAmities(u)).thenReturn(List.of(new Amitie()));

        mockMvc.perform(get("/amities/utilisateurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}