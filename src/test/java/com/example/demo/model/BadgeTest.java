package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BadgeTest {

    @Test
    void testBadge() {
        TypeSport mockTypeSport = mock(TypeSport.class);
        Badge badge = new Badge("Marathonien", "Courir 42km", mockTypeSport, 42.0f);
        
        assertEquals("Marathonien", badge.getNom());
        assertEquals("Courir 42km", badge.getDescription());
        assertEquals(mockTypeSport, badge.getTypeSport());
        assertEquals(42.0f, badge.getSeuil());

        badge.setId(1L);
        assertEquals(1L, badge.getId());

        badge.setNom("Sprinter");
        assertEquals("Sprinter", badge.getNom());

        badge.setDescription("100m rapide");
        assertEquals("100m rapide", badge.getDescription());

        TypeSport newType = mock(TypeSport.class);
        badge.setTypeSport(newType);
        assertEquals(newType, badge.getTypeSport());

        badge.setSeuil(10.0f);
        assertEquals(10.0f, badge.getSeuil());

        List<ObtentionBadge> obtentions = new ArrayList<>();
        badge.setObtentions(obtentions);
        assertEquals(obtentions, badge.getObtentions());

        Badge defaultBadge = new Badge("Test", "Test Desc");
        assertEquals("Test", defaultBadge.getNom());
    }
}