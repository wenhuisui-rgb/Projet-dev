package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionTest {

    private Reaction reaction;
    private Utilisateur auteur;
    private Activite activite;

    @BeforeEach
    void setUp() {
        auteur = new Utilisateur();
        auteur.setId(1L);
        auteur.setPseudo("john_doe");

        activite = new Activite();
        activite.setId(1L);
        activite.setTypeSport(TypeSport.COURSE);

        reaction = new Reaction();
        reaction.setId(1L);
        reaction.setType(TypeReaction.KUDOS);
        reaction.setDateReaction(LocalDateTime.now());
        reaction.setAuteur(auteur);
        reaction.setActivite(activite);
    }

    @Test
    @DisplayName("Test constructeur sans paramètres")
    void testNoArgConstructor() {
        Reaction r = new Reaction();
        assertNotNull(r);
    }

    @Test
    @DisplayName("Test constructeur avec paramètres")
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.now();
        Reaction r = new Reaction(TypeReaction.BRAVO, date, auteur, activite);

        assertEquals(TypeReaction.BRAVO, r.getType());
        assertEquals(date, r.getDateReaction());
        assertEquals(auteur, r.getAuteur());
        assertEquals(activite, r.getActivite());
    }

    @Test
    @DisplayName("Test getters et setters")
    void testGettersAndSetters() {
        LocalDateTime newDate = LocalDateTime.of(2026, 4, 15, 10, 30);
        Utilisateur newAuteur = new Utilisateur();
        newAuteur.setId(2L);
        Activite newActivite = new Activite();
        newActivite.setId(2L);

        reaction.setId(10L);
        reaction.setType(TypeReaction.SOUTIEN);
        reaction.setDateReaction(newDate);
        reaction.setAuteur(newAuteur);
        reaction.setActivite(newActivite);

        assertEquals(10L, reaction.getId());
        assertEquals(TypeReaction.SOUTIEN, reaction.getType());
        assertEquals(newDate, reaction.getDateReaction());
        assertEquals(newAuteur, reaction.getAuteur());
        assertEquals(newActivite, reaction.getActivite());
    }

    @Test
    @DisplayName("Test tous les types de réaction")
    void testAllReactionTypes() {
        reaction.setType(TypeReaction.KUDOS);
        assertEquals(TypeReaction.KUDOS, reaction.getType());

        reaction.setType(TypeReaction.BRAVO);
        assertEquals(TypeReaction.BRAVO, reaction.getType());

        reaction.setType(TypeReaction.SOUTIEN);
        assertEquals(TypeReaction.SOUTIEN, reaction.getType());
    }

    @Test
    @DisplayName("Test dateReaction peut être nulle")
    void testDateReactionNullable() {
        reaction.setDateReaction(null);
        assertNull(reaction.getDateReaction());
    }

    @Test
    @DisplayName("Test auteur peut être nul")
    void testAuteurNullable() {
        reaction.setAuteur(null);
        assertNull(reaction.getAuteur());
    }

    @Test
    @DisplayName("Test activite peut être nulle")
    void testActiviteNullable() {
        reaction.setActivite(null);
        assertNull(reaction.getActivite());
    }

    @Test
    @DisplayName("Test égalité des IDs")
    void testEqualityById() {
        Reaction r1 = new Reaction();
        r1.setId(5L);
        Reaction r2 = new Reaction();
        r2.setId(5L);

        assertEquals(r1.getId(), r2.getId());
    }

    @Test
    @DisplayName("Test réaction avec différentes activités")
    void testReactionDifferentesActivites() {
        Activite activite2 = new Activite();
        activite2.setId(5L);
        activite2.setTypeSport(TypeSport.NATATION);

        reaction.setActivite(activite2);
        assertEquals(5L, reaction.getActivite().getId());
        assertEquals(TypeSport.NATATION, reaction.getActivite().getTypeSport());
    }
}