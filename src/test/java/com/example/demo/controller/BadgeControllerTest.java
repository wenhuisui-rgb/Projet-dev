package com.example.demo.controller;

import com.example.demo.model.Badge;
import com.example.demo.model.TypeSport;
import com.example.demo.service.BadgeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BadgeController.class)
class BadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BadgeService badgeService;

    @Test
    void testCreerBadge() throws Exception {

        Badge badge = new Badge("Runner 5K", "Courir 5 km");
        badge.setId(1L);
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);

        when(badgeService.creerBadge("Runner 5K", "Courir 5 km", TypeSport.COURSE, 5f))
                .thenReturn(badge);

        mockMvc.perform(post("/badges")
                        .param("nom", "Runner 5K")
                        .param("description", "Courir 5 km")
                        .param("typeSport", "COURSE")
                        .param("seuil", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Runner 5K"))
                .andExpect(jsonPath("$.typeSport").value("COURSE"))
                .andExpect(jsonPath("$.seuil").value(5.0));
    }

    @Test
    void testListerTousLesBadges() throws Exception {

        Badge b1 = new Badge("5K", "Courir 5 km");
        b1.setId(1L);

        Badge b2 = new Badge("10K", "Courir 10 km");
        b2.setId(2L);

        when(badgeService.listerTousLesBadges()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("5K"))
                .andExpect(jsonPath("$[1].nom").value("10K"));
    }

    @Test
    void testGetBadgeById() throws Exception {

        Badge badge = new Badge("Marathon", "42 km");
        badge.setId(10L);

        when(badgeService.getBadgeById(10L)).thenReturn(badge);

        mockMvc.perform(get("/badges/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nom").value("Marathon"));
    }
}
