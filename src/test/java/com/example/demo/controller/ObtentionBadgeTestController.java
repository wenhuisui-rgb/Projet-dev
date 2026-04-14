package com.example.demo.controller;

import com.example.demo.model.Badge;
import com.example.demo.model.ObtentionBadge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.BadgeService;
import com.example.demo.service.ObtentionBadgeService;
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

@WebMvcTest(ObtentionBadgeController.class)
class ObtentionBadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObtentionBadgeService obtentionBadgeService;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private UtilisateurService utilisateurService;

    @Test
    void testAttribuerBadge() throws Exception {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

       
        ObtentionBadge obtention = new ObtentionBadge(user, badge);

        when(utilisateurService.findById(1L)).thenReturn(user);
        when(badgeService.getBadgeById(10L)).thenReturn(badge);
        when(obtentionBadgeService.attribuerBadge(user, badge))
                .thenReturn(obtention);

        mockMvc.perform(post("/badges/obtention/attribuer")
                        .param("utilisateurId", "1")
                        .param("badgeId", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testAttribuerSiCondition() throws Exception {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

        ObtentionBadge obtention = new ObtentionBadge(user, badge);

        when(utilisateurService.findById(1L)).thenReturn(user);
        when(badgeService.getBadgeById(10L)).thenReturn(badge);
        when(obtentionBadgeService.attribuerBadgeSiCondition(
                badge, user, TypeSport.COURSE, 5f))
                .thenReturn(obtention);

        mockMvc.perform(post("/badges/obtention/attribuer-condition")
                        .param("utilisateurId", "1")
                        .param("badgeId", "10")
                        .param("sport", "COURSE")
                        .param("valeur", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBadgesUtilisateur() throws Exception {

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Badge badge = new Badge();
        badge.setId(10L);

        when(utilisateurService.findById(1L)).thenReturn(user);

        when(obtentionBadgeService.getBadgesUtilisateur(user))
                .thenReturn(List.of(new ObtentionBadge(user, badge)));

        mockMvc.perform(get("/badges/obtention/utilisateur/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}