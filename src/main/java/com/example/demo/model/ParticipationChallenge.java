package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ParticipationChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateInscription;

    private float scoreActuel;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ParticipationChallenge() {
    }

    public ParticipationChallenge(Utilisateur utilisateur, Challenge challenge) {
        this.utilisateur = utilisateur;
        this.challenge = challenge;
        this.dateInscription = LocalDate.now();
        this.scoreActuel = 0;
    }

    public void mettreAJourScore(float nouveauScore) {
        this.scoreActuel = nouveauScore;
    }


    public Long getId() {
        return id;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
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