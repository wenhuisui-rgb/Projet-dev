package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AmitieTest {

    @Test
    void testDefaultConstructor() {
        Amitie amitie = new Amitie();
        assertNotNull(amitie.getDateDemande());
        assertEquals(LocalDate.now(), amitie.getDateDemande());
        // Assume StatutAmitie is an Enum, we check its string equivalent or existence
        assertNotNull(amitie.getStatut()); 
    }

    @Test
    void testParameterizedConstructorAndGettersSetters() {
        Utilisateur demandeur = mock(Utilisateur.class);
        Utilisateur receveur = mock(Utilisateur.class);

        Amitie amitie = new Amitie(demandeur, receveur);
        
        amitie.setAmitieID(10L);
        assertEquals(10L, amitie.getAmitieID());

        assertEquals(demandeur, amitie.getUtilisateurDemandeur());
        assertEquals(receveur, amitie.getUtilisateurReceveur());

        Utilisateur newDemandeur = mock(Utilisateur.class);
        amitie.setUtilisateurDemandeur(newDemandeur);
        assertEquals(newDemandeur, amitie.getUtilisateurDemandeur());

        Utilisateur newReceveur = mock(Utilisateur.class);
        amitie.setUtilisateurReceveur(newReceveur);
        assertEquals(newReceveur, amitie.getUtilisateurReceveur());

        // Test Enum Setter
        StatutAmitie mockStatut = mock(StatutAmitie.class);
        amitie.setStatut(mockStatut);
        assertEquals(mockStatut, amitie.getStatut());

        LocalDate newDate = LocalDate.of(2023, 1, 1);
        amitie.setDateDemande(newDate);
        assertEquals(newDate, amitie.getDateDemande());
    }
}