package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReactionTest {

    @Test
    void testConstructorsAndGettersSetters() {
        Utilisateur mockAuteur = mock(Utilisateur.class);
        Activite mockActivite = mock(Activite.class);
        LocalDateTime now = LocalDateTime.now();

        Reaction reaction = new Reaction(TypeReaction.KUDOS, now, mockAuteur, mockActivite);
        
        assertNull(reaction.getId()); // Not set yet
        assertEquals(TypeReaction.KUDOS, reaction.getType());
        assertEquals(now, reaction.getDateReaction());
        assertEquals(mockAuteur, reaction.getAuteur());
        assertEquals(mockActivite, reaction.getActivite());

        reaction.setId(10L);
        assertEquals(10L, reaction.getId());

        reaction.setType(TypeReaction.BRAVO);
        assertEquals(TypeReaction.BRAVO, reaction.getType());

        LocalDateTime newTime = LocalDateTime.of(2023, 5, 1, 10, 0);
        reaction.setDateReaction(newTime);
        assertEquals(newTime, reaction.getDateReaction());

        Utilisateur newAuteur = mock(Utilisateur.class);
        reaction.setAuteur(newAuteur);
        assertEquals(newAuteur, reaction.getAuteur());

        Activite newActivite = mock(Activite.class);
        reaction.setActivite(newActivite);
        assertEquals(newActivite, reaction.getActivite());
    }

    @Test
    void testEqualsAndHashCode() {
        Reaction r1 = new Reaction();
        r1.setId(1L);

        Reaction r2 = new Reaction();
        r2.setId(1L);

        Reaction r3 = new Reaction();
        r3.setId(2L);

        Reaction r4 = new Reaction(); // id is null

        // test == 
        assertEquals(r1, r1);
        
        // test null or different class
        assertNotEquals(r1, null);
        assertNotEquals(r1, new Object());
        
        // test same ID
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        // test different ID
        assertNotEquals(r1, r3);

        // test null ID
        assertNotEquals(r1, r4);
        assertNotEquals(r4, r1);
    }

    @Test
    void testToString() {
        Reaction reaction = new Reaction();
        reaction.setId(5L);
        reaction.setType(TypeReaction.SOUTIEN);
        
        String str = reaction.toString();
        assertTrue(str.contains("Reaction{id=5"));
        assertTrue(str.contains("type=SOUTIEN"));
    }
}