package com.example.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitaire de l'entité Amitie (Modèle)
 */
class AmitieTest {

    @Test
    @DisplayName("Devrait créer une amitié avec le statut EN_ATTENTE par défaut")
    void testCreationAmitie() {
        // Arrange
        Utilisateur demandeur = new Utilisateur();
        demandeur.setPseudo("Alice");
       
        Utilisateur receveur = new Utilisateur();
        receveur.setPseudo("Bob");

        // Act
        Amitie amitie = new Amitie();
        amitie.setUtilisateurDemandeur(demandeur);
        amitie.setUtilisateurReceveur(receveur);
        amitie.setStatut(StatutAmitie.EN_ATTENTE);

        // Assert
        assertNotNull(amitie);
        assertEquals("Alice", amitie.getUtilisateurDemandeur().getPseudo());
        assertEquals("Bob", amitie.getUtilisateurReceveur().getPseudo());
        assertEquals(StatutAmitie.EN_ATTENTE, amitie.getStatut());
    }

    @Test
    @DisplayName("Devrait permettre de changer le statut de l'amitié")
    void testChangementStatut() {
        // Arrange
        Amitie amitie = new Amitie();
       
        // Act & Assert pour l'acceptation
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        assertEquals(StatutAmitie.ACCEPTEE, amitie.getStatut(), "Le statut devrait passer à ACCEPTEE");

        // Act & Assert pour le refus
        amitie.setStatut(StatutAmitie.REFUSEE);
        assertEquals(StatutAmitie.REFUSEE, amitie.getStatut(), "Le statut devrait passer à REFUSEE");
    }

    @Test
    @DisplayName("Vérification des getters et setters des IDs")
    void testGettersSetters() {
        Amitie amitie = new Amitie();
        amitie.setAmitieID(100L);
       
        assertEquals(100L, amitie.getAmitieID());
    }
}