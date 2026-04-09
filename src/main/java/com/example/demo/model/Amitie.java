package com.example.demo.model;

import java.util.Date;

public class Amitie {

    private long amitieID;
    private StatutAmitie statut;
    private Date dateDemande;
    
    public Amitie() {
    }

    // Constructeur complet
    public Amitie(long amitieID, StatutAmitie statut, Date dateDemande) {
        this.amitieID = amitieID;
        this.statut = statut;
        this.dateDemande = dateDemande;
    }

    // Getters / Setters
    public long getAmitieID() {
        return amitieID;
    }

    public void setAmitieID(long amitieID) {
        this.amitieID = amitieID;
    }

    public StatutAmitie getStatut() {
        return statut;
    }

    public void setStatut(StatutAmitie statut) {
        this.statut = statut;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    public void accepterDemande(){

    }

    public void refuserDemande(){

    }

    public void annulerDemande(){

    }

    public void rompreAmitie(){

    }
}