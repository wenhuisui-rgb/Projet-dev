package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pseudo;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexe sexe;

    private Integer age;
    private Float taille; // en mètres
    private Float poids; // en kilogrammes

    @Enumerated(EnumType.STRING)
    private NiveauPratique niveauPratique;


    private List<TypeSport> preferencesSports = new ArrayList<>();
    private List<ObtentionBadge> listBadge = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activite> activites = new ArrayList<>();
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Objectif> objectifs = new ArrayList<>();
    
    @OneToMany(mappedBy = "demandeur")
    private List<Amitie> demandesEnvoyees = new ArrayList<>(); // demander
    @OneToMany(mappedBy = "receveur")
    private List<Amitie> demandesRecues = new ArrayList<>();   // recevoir
    
    @OneToMany(mappedBy = "utilisateur")
    private List<ObtentionBadge> obtentionsBadges = new ArrayList<>();
    
    @OneToMany(mappedBy = "createur")
    private List<Challenge> challengesCrees = new ArrayList<>();
    @OneToMany(mappedBy = "utilisateur")
    private List<ParticipationChallenge> participationsChallenge = new ArrayList<>();
    
    @OneToMany(mappedBy = "auteur")
    private List<Commentaire> commentaires = new ArrayList<>();
    @OneToMany(mappedBy = "auteur")
    private List<Reaction> reactions = new ArrayList<>();

    // Constructeurs
    public Utilisateur() {
    }
    public Utilisateur(Long id, String pseudo, String email, String motDePasse, Sexe sexe, Integer age, 
                       Float taille, Float poids, NiveauPratique niveauPratique, List<TypeSport> preferencesSports, 
                       List<ObtentionBadge> listBadge) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasse = motDePasse;
        this.sexe = sexe;
        this.age = age;
        this.taille = taille;
        this.poids = poids;
        this.niveauPratique = niveauPratique;
        this.preferencesSports = preferencesSports;
        this.listBadge = listBadge;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getTaille() {
        return taille;
    }

    public void setTaille(Float taille) {
        this.taille = taille;
    }

    public Float getPoids() {
        return poids;
    }

    public void setPoids(Float poids) {
        this.poids = poids;
    }

    public NiveauPratique getNiveauPratique() {
        return niveauPratique;
    }

    public void setNiveauPratique(NiveauPratique niveauPratique) {
        this.niveauPratique = niveauPratique;
    }

    public List<TypeSport> getPreferencesSports() {
        return preferencesSports;
    }

    public void setPreferencesSports(List<TypeSport> preferencesSports) {
        this.preferencesSports = preferencesSports;
    }

    public List<ObtentionBadge> getListBadge() {
        return listBadge;
    }

    public void setListBadge(List<ObtentionBadge> listBadge) {
        this.listBadge = listBadge;
    }


    //methodes
    public void sInscrire() {
        // TODO: Implement logic
    }

    public Boolean seConnecter(String email, String mdp) {
        // TODO: Implement logic
        return false;
    }

    public void seDeconnecter() {
        // TODO: Implement logic
    }

    public void mettreAJourProfil() {
        // TODO: Implement logic
    }

    public Float calculerIMC() {
        // TODO: Implement logic
        return 0.0f;
    }

    public List<Utilisateur> obtenirListeAmis() {
        // TODO: Implement logic
        return null;
    }

    public List<Badge> obtenirMesBadges() {
        // TODO: Implement logic
        return null;
    }

    public List<Challenge> obtenirMesChallenges() {
        // TODO: Implement logic
        return null;
    }

    public void envoyerDemandeAmi(Utilisateur cible) {
        // TODO: Implement logic
    }

    public void traiterDemande(Amitie demande, Boolean accepter) {
        // TODO: Implement logic
    }

    public void supprimerAmi(Utilisateur ami) {
        // TODO: Implement logic
    }
}
