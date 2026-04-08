package com.example.demo.model;

import java.time.LocalDate; 

public class Challenge {
    private long id;
    private String titre;
    
    private TypeSport typeSport; 

    private LocalDate dateDebut;
    private LocalDate dateFin;


    public Challenge( long id, String titre, TypeSport typeSport, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.titre = titre;
        this.typeSport = typeSport;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

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

    public TypeSport getTypeSport() {
        return typeSport;
    }

    public void setTypeSport(TypeSport typeSport) {
        this.typeSport = typeSport;
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
