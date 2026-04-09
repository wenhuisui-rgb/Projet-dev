package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentaireTest {

    private Commentaire commentaire;
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

        commentaire = new Commentaire();
        commentaire.setId(1L);
        commentaire.setContenu("Super performance !");
        commentaire.setDateCommentaire(LocalDateTime.now());
        commentaire.setAuteur(auteur);
        commentaire.setActivite(activite);
    }

    @Test
    @DisplayName("Test constructeur sans paramètres")
    void testNoArgConstructor() {
        Commentaire c = new Commentaire();
        assertNotNull(c);
    }

    @Test
    @DisplayName("Test constructeur avec paramètres")
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.now();
        Commentaire c = new Commentaire("Félicitations !", date, auteur, activite);

        assertEquals("Félicitations !", c.getContenu());
        assertEquals(date, c.getDateCommentaire());
        assertEquals(auteur, c.getAuteur());
        assertEquals(activite, c.getActivite());
    }

    @Test
    @DisplayName("Test getters et setters")
    void testGettersAndSetters() {
        LocalDateTime newDate = LocalDateTime.of(2026, 4, 15, 14, 30);
        Utilisateur newAuteur = new Utilisateur();
        newAuteur.setId(2L);
        Activite newActivite = new Activite();
        newActivite.setId(2L);

        commentaire.setId(10L);
        commentaire.setContenu("Très beau parcours !");
        commentaire.setDateCommentaire(newDate);
        commentaire.setAuteur(newAuteur);
        commentaire.setActivite(newActivite);

        assertEquals(10L, commentaire.getId());
        assertEquals("Très beau parcours !", commentaire.getContenu());
        assertEquals(newDate, commentaire.getDateCommentaire());
        assertEquals(newAuteur, commentaire.getAuteur());
        assertEquals(newActivite, commentaire.getActivite());
    }

    @Test
    @DisplayName("Test contenu long")
    void testContenuLong() {
        String longText = "Ceci est un très long commentaire pour tester la capacité de la base de données à stocker des textes de grande longueur. ".repeat(10);
        commentaire.setContenu(longText);
        assertEquals(longText, commentaire.getContenu());
    }

    @Test
    @DisplayName("Test contenu vide")
    void testContenuVide() {
        commentaire.setContenu("");
        assertEquals("", commentaire.getContenu());
    }

    @Test
    @DisplayName("Test contenu null")
    void testContenuNull() {
        commentaire.setContenu(null);
        assertNull(commentaire.getContenu());
    }

    @Test
    @DisplayName("Test dateCommentaire peut être nulle")
    void testDateCommentaireNullable() {
        commentaire.setDateCommentaire(null);
        assertNull(commentaire.getDateCommentaire());
    }

    @Test
    @DisplayName("Test auteur peut être nul")
    void testAuteurNullable() {
        commentaire.setAuteur(null);
        assertNull(commentaire.getAuteur());
    }

    @Test
    @DisplayName("Test activite peut être nulle")
    void testActiviteNullable() {
        commentaire.setActivite(null);
        assertNull(commentaire.getActivite());
    }

    @Test
    @DisplayName("Test commentaires différents utilisateurs")
    void testCommentairesDifferentAuteurs() {
        Utilisateur autreAuteur = new Utilisateur();
        autreAuteur.setId(5L);
        autreAuteur.setPseudo("jane_doe");

        commentaire.setAuteur(autreAuteur);
        assertEquals(5L, commentaire.getAuteur().getId());
        assertEquals("jane_doe", commentaire.getAuteur().getPseudo());
    }

    @Test
    @DisplayName("Test commentaires différentes activités")
    void testCommentairesDifferentActivites() {
        Activite autreActivite = new Activite();
        autreActivite.setId(10L);
        autreActivite.setTypeSport(TypeSport.VELO);

        commentaire.setActivite(autreActivite);
        assertEquals(10L, commentaire.getActivite().getId());
        assertEquals(TypeSport.VELO, commentaire.getActivite().getTypeSport());
    }

    @Test
    @DisplayName("Test égalité des IDs")
    void testEqualityById() {
        Commentaire c1 = new Commentaire();
        c1.setId(7L);
        Commentaire c2 = new Commentaire();
        c2.setId(7L);

        assertEquals(c1.getId(), c2.getId());
    }

    @Test
    @DisplayName("Test dateCommentaire dans le passé")
    void testDateCommentairePasse() {
        LocalDateTime pastDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        commentaire.setDateCommentaire(pastDate);
        assertTrue(commentaire.getDateCommentaire().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Test dateCommentaire dans le futur")
    void testDateCommentaireFutur() {
        LocalDateTime futureDate = LocalDateTime.of(2030, 1, 1, 0, 0);
        commentaire.setDateCommentaire(futureDate);
        assertTrue(commentaire.getDateCommentaire().isAfter(LocalDateTime.now()));
    }
}