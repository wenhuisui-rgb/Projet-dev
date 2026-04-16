package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BasicEntitiesTest {

    @Test
    void testChartData() {
        ChartData data = new ChartData("Label1", 100);
        assertEquals("Label1", data.getLabel());
        assertEquals(100, data.getValue());

        data.setLabel("Label2");
        data.setValue(200);
        assertEquals("Label2", data.getLabel());
        assertEquals(200, data.getValue());
        
        ChartData empty = new ChartData();
        assertNull(empty.getLabel());
    }

    @Test
    void testCommentaire() {
        Utilisateur mockUser = mock(Utilisateur.class);
        Activite mockActivite = mock(Activite.class);
        LocalDateTime now = LocalDateTime.now();

        Commentaire c1 = new Commentaire("Super!", now, mockUser, mockActivite);
        c1.setId(1L);

        assertEquals("Super!", c1.getContenu());
        assertEquals(now, c1.getDateCommentaire());
        assertEquals(mockUser, c1.getAuteur());
        assertEquals(mockActivite, c1.getActivite());
        assertEquals(1L, c1.getId());

        c1.setContenu("Bof");
        assertEquals("Bof", c1.getContenu());
        
        c1.setDateCommentaire(null);
        assertNull(c1.getDateCommentaire());

        c1.setAuteur(null);
        assertNull(c1.getAuteur());

        c1.setActivite(null);
        assertNull(c1.getActivite());

        // Test Equals and HashCode
        Commentaire c2 = new Commentaire();
        c2.setId(1L);
        Commentaire c3 = new Commentaire();
        c3.setId(2L);

        // Sonar Fix: 移除了毫无意义的 self-comparison assertEquals(c1, c1)
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, new Object());
        
        // Sonar Fix: 将 expected 放在前面
        assertEquals(c1.getClass().hashCode(), c1.hashCode());
        assertEquals(c1.hashCode(), c1.getClass().hashCode());

        // Test ToString
        assertTrue(c1.toString().contains("Commentaire{id=1"));
    }

    @Test
    void testNiveauPratiqueEnum() {
        // Pour couvrir 100% des Enums (values() et valueOf())
        NiveauPratique[] values = NiveauPratique.values();
        assertTrue(values.length > 0);
        
        NiveauPratique debutant = NiveauPratique.valueOf("DEBUTANT");
        assertEquals(NiveauPratique.DEBUTANT, debutant);
    }
}