package com.example.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité de liaison qui représente l'inscription d'un {@link Utilisateur} à un {@link Challenge}.
 * <p>
 * Elle permet de suivre la progression individuelle (score) de chaque participant 
 * tout au long du défi pour établir le classement (Leaderboard).
 */
@Entity
public class ParticipationChallenge implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** La date à laquelle l'utilisateur a rejoint le challenge. */
    private LocalDateTime dateInscription;

    /** La progression ou le score accumulé par l'utilisateur (ex: 25 km courus sur les 50 requis). */
    private float scoreActuel;

    /** L'utilisateur participant au défi. */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    /** Le défi auquel l'utilisateur est inscrit. */
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ParticipationChallenge() {}

    public ParticipationChallenge(Utilisateur utilisateur, Challenge challenge) {
        this.utilisateur = utilisateur;
        this.challenge = challenge;
        this.dateInscription = LocalDateTime.now();
        this.scoreActuel = 0f;
    }

    /**
     * Écrase le score actuel de l'utilisateur par une nouvelle valeur.
     *
     * @param nouveauScore La nouvelle valeur de progression
     */
    public void mettreAJourScore(float nouveauScore) {
        this.scoreActuel = nouveauScore;
    }

    /* ================= GETTERS / SETTERS ================= */
    public Long getId() { return id; }
    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
    public float getScoreActuel() { return scoreActuel; }
    public void setScoreActuel(float scoreActuel) { this.scoreActuel = scoreActuel; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}