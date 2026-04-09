package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPseudo("john_doe");
        utilisateur.setEmail("john@example.com");
        utilisateur.setMotDePasse("password123");
        utilisateur.setSexe(Sexe.HOMME);
        utilisateur.setAge(25);
        utilisateur.setTaille(1.75f);
        utilisateur.setPoids(70f);
        utilisateur.setNiveauPratique(NiveauPratique.INTERMEDIAIRE);
        
        List<TypeSport> preferences = new ArrayList<>();
        preferences.add(TypeSport.COURSE);
        preferences.add(TypeSport.VELO);
        utilisateur.setPreferencesSports(preferences);
    }

    @Test
    @DisplayName("Test constructeur sans paramètres")
    void testNoArgConstructor() {
        Utilisateur user = new Utilisateur();
        assertNotNull(user);
    }

    @Test
    @DisplayName("Test constructeur avec paramètres")
    void testAllArgsConstructor() {
        List<TypeSport> sports = Arrays.asList(TypeSport.COURSE, TypeSport.NATATION);
        List<ObtentionBadge> badges = new ArrayList<>();
        
        Utilisateur user = new Utilisateur(2L, "jane_doe", "jane@example.com", "pass123", 
                                           Sexe.FEMME, 30, 1.65f, 60f, 
                                           NiveauPratique.EXPERT, sports, badges);
        
        assertEquals(2L, user.getId());
        assertEquals("jane_doe", user.getPseudo());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("pass123", user.getMotDePasse());
        assertEquals(Sexe.FEMME, user.getSexe());
        assertEquals(30, user.getAge());
        assertEquals(1.65f, user.getTaille());
        assertEquals(60f, user.getPoids());
        assertEquals(NiveauPratique.EXPERT, user.getNiveauPratique());
        assertEquals(2, user.getPreferencesSports().size());
        assertTrue(user.getPreferencesSports().contains(TypeSport.COURSE));
        assertTrue(user.getPreferencesSports().contains(TypeSport.NATATION));
    }

    @Test
    @DisplayName("Test getters et setters")
    void testGettersAndSetters() {
        Utilisateur user = new Utilisateur();
        
        user.setId(10L);
        user.setPseudo("test_user");
        user.setEmail("test@test.com");
        user.setMotDePasse("secret");
        user.setSexe(Sexe.AUTRE);
        user.setAge(40);
        user.setTaille(1.80f);
        user.setPoids(75f);
        user.setNiveauPratique(NiveauPratique.DEBUTANT);
        
        List<TypeSport> sports = Arrays.asList(TypeSport.YOGA, TypeSport.MUSCULATION);
        user.setPreferencesSports(sports);
        
        assertEquals(10L, user.getId());
        assertEquals("test_user", user.getPseudo());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("secret", user.getMotDePasse());
        assertEquals(Sexe.AUTRE, user.getSexe());
        assertEquals(40, user.getAge());
        assertEquals(1.80f, user.getTaille());
        assertEquals(75f, user.getPoids());
        assertEquals(NiveauPratique.DEBUTANT, user.getNiveauPratique());
        assertEquals(2, user.getPreferencesSports().size());
    }

    @Test
    @DisplayName("Test calculerIMC - normal")
    void testCalculerIMCNormal() {
        utilisateur.setTaille(1.75f);
        utilisateur.setPoids(70f);
        
        Float imc = utilisateur.calculerIMC();
        
        assertNotNull(imc);
        assertEquals(22.86f, imc, 0.01);
    }

    @Test
    @DisplayName("Test calculerIMC - taille null")
    void testCalculerIMCTailleNull() {
        utilisateur.setTaille(null);
        utilisateur.setPoids(70f);
        
        Float imc = utilisateur.calculerIMC();
        
        assertNull(imc);
    }

    @Test
    @DisplayName("Test calculerIMC - poids null")
    void testCalculerIMCPoidsNull() {
        utilisateur.setTaille(1.75f);
        utilisateur.setPoids(null);
        
        Float imc = utilisateur.calculerIMC();
        
        assertNull(imc);
    }

    @Test
    @DisplayName("Test calculerIMC - taille 0")
    void testCalculerIMCTailleZero() {
        utilisateur.setTaille(0f);
        utilisateur.setPoids(70f);
        
        Float imc = utilisateur.calculerIMC();
        
        assertNull(imc);
    }

    @Test
    @DisplayName("Test calculerIMC - valeurs extrêmes")
    void testCalculerIMCValeursExtremes() {
        utilisateur.setTaille(2.0f);
        utilisateur.setPoids(150f);
        
        Float imc = utilisateur.calculerIMC();

        assertEquals(37.5f, imc, 0.01);
    }

    @Test
    @DisplayName("Test ajout et récupération des activités")
    void testAjoutActivites() {
        List<Activite> activites = new ArrayList<>();
        Activite a1 = new Activite();
        a1.setId(1L);
        a1.setTypeSport(TypeSport.COURSE);
        
        Activite a2 = new Activite();
        a2.setId(2L);
        a2.setTypeSport(TypeSport.VELO);
        
        activites.add(a1);
        activites.add(a2);
        
        if (utilisateur.getActivites() == null) {
            utilisateur.setActivites(new ArrayList<>());
        }
        utilisateur.getActivites().addAll(activites);
        
        assertNotNull(utilisateur.getActivites());
        assertEquals(2, utilisateur.getActivites().size());
    }

    @Test
    @DisplayName("Test ajout et récupération des objectifs")
    void testAjoutObjectifs() {
        List<Objectif> objectifs = new ArrayList<>();
        Objectif o1 = new Objectif();
        o1.setId(1L);
        o1.setDescription("Courir 50km");
        
        objectifs.add(o1);
        
        utilisateur.setObjectifs(objectifs);
        
        assertNotNull(utilisateur.getObjectifs());
        assertEquals(1, utilisateur.getObjectifs().size());
        assertEquals("Courir 50km", utilisateur.getObjectifs().get(0).getDescription());
    }

    @Test
    @DisplayName("Test obtenirListeAmis - retourne null actuellement")
    void testObtenirListeAmis() {
        List<Utilisateur> amis = utilisateur.obtenirListeAmis();
        // TODO: Implémenter la méthode
        assertNull(amis);
    }

    @Test
    @DisplayName("Test obtenirMesBadges - retourne null actuellement")
    void testObtenirMesBadges() {
        List<Badge> badges = utilisateur.obtenirMesBadges();
        // TODO: Implémenter la méthode
        assertNull(badges);
    }

    @Test
    @DisplayName("Test obtenirMesChallenges - retourne null actuellement")
    void testObtenirMesChallenges() {
        List<Challenge> challenges = utilisateur.obtenirMesChallenges();
        // TODO: Implémenter la méthode
        assertNull(challenges);
    }

    @Test
    @DisplayName("Test toString - vérifie que ça ne plante pas")
    void testToString() {
        String result = utilisateur.toString();
        // La classe n'a pas de toString, on vérifie juste que ça retourne quelque chose
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test liste préférences sports - ajout et modification")
    void testPreferencesSports() {
        List<TypeSport> newPreferences = Arrays.asList(
            TypeSport.NATATION, 
            TypeSport.RANDONNEE, 
            TypeSport.YOGA
        );
        
        utilisateur.setPreferencesSports(newPreferences);
        
        assertEquals(3, utilisateur.getPreferencesSports().size());
        assertTrue(utilisateur.getPreferencesSports().contains(TypeSport.NATATION));
        assertTrue(utilisateur.getPreferencesSports().contains(TypeSport.RANDONNEE));
        assertTrue(utilisateur.getPreferencesSports().contains(TypeSport.YOGA));
    }

    @Test
    @DisplayName("Test liste badges")
    void testListBadges() {
        List<ObtentionBadge> badges = new ArrayList<>();
        ObtentionBadge ob1 = new ObtentionBadge();
        ObtentionBadge ob2 = new ObtentionBadge();
        badges.add(ob1);
        badges.add(ob2);
        
        utilisateur.setListBadges(badges);
        
        assertNotNull(utilisateur.getListBadges());
        assertEquals(2, utilisateur.getListBadges().size());
    }

    @Test
    @DisplayName("Test valeurs null pour les champs optionnels")
    void testChampsOptionnelsNull() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        user.setPseudo("test");
        user.setEmail("test@test.com");
        user.setMotDePasse("pass");
        user.setSexe(Sexe.HOMME);
        
        assertNull(user.getAge());
        assertNull(user.getTaille());
        assertNull(user.getPoids());
        assertNull(user.getNiveauPratique());
    }

    @Test
    @DisplayName("Test différents niveaux de pratique")
    void testNiveauxPratique() {
        utilisateur.setNiveauPratique(NiveauPratique.DEBUTANT);
        assertEquals(NiveauPratique.DEBUTANT, utilisateur.getNiveauPratique());
        
        utilisateur.setNiveauPratique(NiveauPratique.INTERMEDIAIRE);
        assertEquals(NiveauPratique.INTERMEDIAIRE, utilisateur.getNiveauPratique());
        
        utilisateur.setNiveauPratique(NiveauPratique.EXPERT);
        assertEquals(NiveauPratique.EXPERT, utilisateur.getNiveauPratique());
    }

    @Test
    @DisplayName("Test différents sexes")
    void testSexes() {
        utilisateur.setSexe(Sexe.HOMME);
        assertEquals(Sexe.HOMME, utilisateur.getSexe());
        
        utilisateur.setSexe(Sexe.FEMME);
        assertEquals(Sexe.FEMME, utilisateur.getSexe());
        
        utilisateur.setSexe(Sexe.AUTRE);
        assertEquals(Sexe.AUTRE, utilisateur.getSexe());
    }

    @Test
    @DisplayName("Test méthodes à implémenter - ne plantent pas")
    void testMethodesAImplemter() {
        Utilisateur cible = new Utilisateur();
        
        assertDoesNotThrow(() -> utilisateur.envoyerDemandeAmi(cible));
        assertDoesNotThrow(() -> utilisateur.traiterDemande(null, true));
        assertDoesNotThrow(() -> utilisateur.supprimerAmi(cible));
    }
}