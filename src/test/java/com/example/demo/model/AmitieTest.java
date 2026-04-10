package com.example.demo.model;

import com.example.demo.repository.AmitieRepository;
import com.example.demo.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demo.service.AmitieService;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AmitieServiceTest {

    @Autowired
    private AmitieService amitieService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AmitieRepository amitieRepository;

    
    @Test
    void testEnvoyerDemande() {

        Utilisateur u1 = new Utilisateur();
        u1.setPseudo("user1");
        u1 = utilisateurRepository.save(u1);

        Utilisateur u2 = new Utilisateur();
        u2.setPseudo("user2");
        u2 = utilisateurRepository.save(u2);

        String result = amitieService.envoyerDemande(u1, u2);

        assertEquals("Demande envoyée avec succès !", result);

        Amitie amitie = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2)
                .orElse(null);

        assertNotNull(amitie);
        assertEquals(StatutAmitie.EN_ATTENTE, amitie.getStatut());
    }

    @Test
    void testBloquerDoubleDemande() {

        Utilisateur u1 = new Utilisateur();
        u1.setPseudo("userA");
        u1 = utilisateurRepository.save(u1);

        Utilisateur u2 = new Utilisateur();
        u2.setPseudo("userB");
        u2 = utilisateurRepository.save(u2);

        amitieService.envoyerDemande(u1, u2);
        String result = amitieService.envoyerDemande(u1, u2);

        assertTrue(result.contains("déjà une demande"));
    }

    @Test
    void testAccepterDemande() {

        Utilisateur u1 = new Utilisateur();
        u1.setPseudo("userX");
        u1 = utilisateurRepository.save(u1);

        Utilisateur u2 = new Utilisateur();
        u2.setPseudo("userY");
        u2 = utilisateurRepository.save(u2);

        amitieService.envoyerDemande(u1, u2);

        Amitie amitie = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2)
                .orElseThrow();

        Amitie updated = amitieService.accepterDemande(amitie);

        assertEquals(StatutAmitie.ACCEPTEE, updated.getStatut());
    }

  
    @Test
    void testRefuserDemande() {

        Utilisateur u1 = new Utilisateur();
        u1.setPseudo("userR1");
        u1 = utilisateurRepository.save(u1);

        Utilisateur u2 = new Utilisateur();
        u2.setPseudo("userR2");
        u2 = utilisateurRepository.save(u2);

        amitieService.envoyerDemande(u1, u2);

        Amitie amitie = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2)
                .orElseThrow();

        Amitie updated = amitieService.refuserDemande(amitie);

        assertEquals(StatutAmitie.REFUSEE, updated.getStatut());
    }

    
    @Test
    void testRompreAmitie() {

        Utilisateur u1 = new Utilisateur();
        u1.setPseudo("userD1");
        u1 = utilisateurRepository.save(u1);

        Utilisateur u2 = new Utilisateur();
        u2.setPseudo("userD2");
        u2 = utilisateurRepository.save(u2);

        amitieService.envoyerDemande(u1, u2);

        Amitie amitie = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2)
                .orElseThrow();

        amitieService.rompreAmitie(amitie);

        boolean exists = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(u1, u2)
                .isPresent();

        assertFalse(exists);
    }
}