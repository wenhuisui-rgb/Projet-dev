package com.example.demo.model;

import java.time.LocalDate;

public class ParticipationChallenge {
    private long id;
    private LocalDate dateInscription;
    private float scoreActuel;

    public float getScoreActuel() {
        return scoreActuel;
    }

    public void setScoreActuel(float scoreActuel) {
        this.scoreActuel = scoreActuel;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void mettreAJourScore(float nouveauScore) {
        this.scoreActuel = nouveauScore;
    }

    public void verifierPosition() {
    }


}
