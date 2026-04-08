package com.example.demo.model;


import java.time.LocalDate; 

public class Challenge {
    private long id;
    private String titre;
    
    public enum TypeSport {
        COURSE, VELO, NATATION, YOGA, MUSCULATION
    }
    
    private TypeSport type; 

    private LocalDate dateDebut;
    private LocalDate dateFin;

    void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public TypeSport getType() {
        return type;
    }

    public void setType(TypeSport type) {
        this.type = type;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public void estActif() {
    }

    public void obtenirClassement() {
        // java.util.List<Participant> participants = new java.util.ArrayList<>();
    }

    public void listtoutLesChallenges() {
        // java.util.List<Challenge> challenges = new java.util.ArrayList<>();
    }

    



}
