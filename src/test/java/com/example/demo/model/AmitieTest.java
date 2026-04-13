package com.example.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AmitieTest {

    @Test
    @DisplayName("Devrait créer une amitié avec statut EN_ATTENTE et date auto")
    void testCreationAmitie() {

        Utilisateur demandeur = new Utilisateur();
        demandeur.setPseudo("Alice");

        Utilisateur receveur = new Utilisateur();
        receveur.setPseudo("Bob");

        Amitie amitie = new Amitie(demandeur, receveur);

        assertNotNull(amitie);
        assertEquals("Alice", amitie.getUtilisateurDemandeur().getPseudo());
        assertEquals("Bob", amitie.getUtilisateurReceveur().getPseudo());

        assertEquals(StatutAmitie.EN_ATTENTE, amitie.getStatut());
        assertNotNull(amitie.getDateDemande());
    }

    @Test
    @DisplayName("Devrait changer le statut de l'amitié")
    void testChangementStatut() {

        Amitie amitie = new Amitie();

        amitie.setStatut(StatutAmitie.EN_ATTENTE);
        assertEquals(StatutAmitie.EN_ATTENTE, amitie.getStatut());

        amitie.setStatut(StatutAmitie.ACCEPTEE);
        assertEquals(StatutAmitie.ACCEPTEE, amitie.getStatut());

        amitie.setStatut(StatutAmitie.REFUSEE);
        assertEquals(StatutAmitie.REFUSEE, amitie.getStatut());
    }

    @Test
    @DisplayName("Vérification ID")
    void testGettersSetters() {

        Amitie amitie = new Amitie();
        amitie.setAmitieID(100L);

        assertEquals(100L, amitie.getAmitieID());
    }
}