package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ParticipationChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateInscription; // modifié pour stocker date + heure

    private float scoreActuel;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ParticipationChallenge() {}

    public ParticipationChallenge(Utilisateur utilisateur, Challenge challenge) {
        this.utilisateur = utilisateur;
        this.challenge = challenge;
        this.dateInscription = LocalDateTime.now(); // date + heure
        this.scoreActuel = 0;
    }

    public void mettreAJourScore(float nouveauScore) {
        this.scoreActuel = nouveauScore;
    }

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public float getScoreActuel() {
        return scoreActuel;
    }

    public void setScoreActuel(float scoreActuel) {
        this.scoreActuel = scoreActuel;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}