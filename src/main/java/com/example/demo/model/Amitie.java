package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Amitie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amitieID;

    // celui qui envoie la demande
    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Utilisateur utilisateurDemandeur; 

    // celui qui reçoit la demande
    @ManyToOne
    @JoinColumn(name = "receveur_id")
    private Utilisateur utilisateurReceveur;  

    @Enumerated(EnumType.STRING)
    private StatutAmitie statut;

    private LocalDate dateDemande;

    public Amitie() {
        this.statut = StatutAmitie.EN_ATTENTE;
        this.dateDemande = LocalDate.now();
    }

    public Amitie(Utilisateur demandeur, Utilisateur receveur) {
        this.utilisateurDemandeur = demandeur;
        this.utilisateurReceveur = receveur;
        this.statut = StatutAmitie.EN_ATTENTE;
        this.dateDemande = LocalDate.now();
    }

    // Getters / Setters
    public Long getAmitieID() {
        return amitieID;
    }

    public void setAmitieID(Long amitieID) {
        this.amitieID = amitieID;
    }

    public Utilisateur getUtilisateurDemandeur() {
        return utilisateurDemandeur;
    }

    public void setUtilisateurDemandeur(Utilisateur utilisateurDemandeur) {
        this.utilisateurDemandeur = utilisateurDemandeur;
    }

    public Utilisateur getUtilisateurReceveur() {
        return utilisateurReceveur;
    }

    public void setUtilisateurReceveur(Utilisateur utilisateurReceveur) {
        this.utilisateurReceveur = utilisateurReceveur;
    }

    public StatutAmitie getStatut() {
        return statut;
    }

    public void setStatut(StatutAmitie statut) {
        this.statut = statut;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }
}