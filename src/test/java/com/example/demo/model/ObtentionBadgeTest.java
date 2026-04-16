package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ObtentionBadgeTest {
    @Test
    void testObtentionBadge() {
        Utilisateur mockUtilisateur = mock(Utilisateur.class);
        Badge mockBadge = mock(Badge.class);

        ObtentionBadge obtention = new ObtentionBadge(mockUtilisateur, mockBadge);
        assertNotNull(obtention.getDateObtention());
        
        obtention.setDateObtention(LocalDateTime.of(2023, 1, 1, 12, 0));
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), obtention.getDateObtention());

        assertEquals(mockUtilisateur, obtention.getUtilisateur());
        assertEquals(mockBadge, obtention.getBadge());

        Utilisateur newUser = mock(Utilisateur.class);
        obtention.setUtilisateur(newUser);
        assertEquals(newUser, obtention.getUtilisateur());

        Badge newBadge = mock(Badge.class);
        obtention.setBadge(newBadge);
        assertEquals(newBadge, obtention.getBadge());
    }

    @Test
    void testGetIdMissing() {
        ObtentionBadge ob = new ObtentionBadge();
        // 直接调用 getId() 覆盖红线
        assertNull(ob.getId());
    }
}