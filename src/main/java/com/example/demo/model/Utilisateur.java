package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexe sexe;

    private Integer age;
    private Float taille;
    private Float poids;
    private String objectifPersonnel;

    @Enumerated(EnumType.STRING)
    private NiveauPratique niveauPratique;

    @ElementCollection(targetClass = TypeSport.class)
    @CollectionTable(name = "utilisateur_preferences_sports", joinColumns = @JoinColumn(name = "utilisateur_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type_sport")
    private List<TypeSport> preferencesSports = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activite> activites = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Objectif> objectifs = new ArrayList<>();
    
    @OneToMany(mappedBy = "demandeur")
    private List<Amitie> demandesEnvoyees = new ArrayList<>();
    
    @OneToMany(mappedBy = "receveur")
    private List<Amitie> demandesRecues = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilisateur")
    private List<ObtentionBadge> listBadges = new ArrayList<>();
    
    @OneToMany(mappedBy = "createur")
    private List<Challenge> challengesCrees = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilisateur")
    private List<ParticipationChallenge> participationsChallenge = new ArrayList<>();
    
    @OneToMany(mappedBy = "auteur")
    private List<Commentaire> commentaires = new ArrayList<>();
    
    @OneToMany(mappedBy = "auteur")
    private List<Reaction> reactions = new ArrayList<>();

    public Utilisateur() {
    }
    
    public Utilisateur(Long id, String pseudo, String email, String motDePasse, Sexe sexe, Integer age, 
                       Float taille, Float poids, NiveauPratique niveauPratique, 
                       List<TypeSport> preferencesSports, List<ObtentionBadge> listBadges) {
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
        this.listBadges = listBadges;
    }

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

    public String getObjectifPersonnel() {
        return objectifPersonnel;
    }

    public void setObjectifPersonnel(String objectifPersonnel) {
        this.objectifPersonnel = objectifPersonnel;
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

    public List<ObtentionBadge> getListBadges() {
        return listBadges;
    }

    public void setListBadges(List<ObtentionBadge> listBadges) {
        this.listBadges = listBadges;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    public List<Objectif> getObjectifs() {
        return objectifs;
    }

    public void setObjectifs(List<Objectif> objectifs) {
        this.objectifs = objectifs;
    }

    public List<Amitie> getDemandesEnvoyees() {
        return demandesEnvoyees;
    }

    public void setDemandesEnvoyees(List<Amitie> demandesEnvoyees) {
        this.demandesEnvoyees = demandesEnvoyees;
    }

    public List<Amitie> getDemandesRecues() {
        return demandesRecues;
    }

    public void setDemandesRecues(List<Amitie> demandesRecues) {
        this.demandesRecues = demandesRecues;
    }

    public List<ParticipationChallenge> getParticipationsChallenge() {
        return participationsChallenge;
    }

    public void setParticipationsChallenge(List<ParticipationChallenge> participationsChallenge) {
        this.participationsChallenge = participationsChallenge;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<Challenge> getChallengesCrees() {
        return challengesCrees;
    }

    public void setChallengesCrees(List<Challenge> challengesCrees) {
        this.challengesCrees = challengesCrees;
    }

    public Float calculerIMC() {
        if (this.taille != null && this.poids != null && this.taille > 0) {
            return this.poids / (this.taille * this.taille);
        }
        return null;
    }

}