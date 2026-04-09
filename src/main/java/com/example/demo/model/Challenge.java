package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipationChallenge> participations = new ArrayList<>();

    public Challenge() {}

    public Challenge(String titre, TypeSport typeSport, LocalDate dateDebut, LocalDate dateFin, Utilisateur createur) {
        this.titre = titre;
        this.typeSport = typeSport;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.createur = createur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public List<ParticipationChallenge> getParticipations() {
        return participations;
    }

    public void ajouterParticipant(Utilisateur utilisateur) {
        ParticipationChallenge participation = new ParticipationChallenge(utilisateur, this);
        participations.add(participation);
    }

    public void retirerParticipant(Utilisateur utilisateur) {
        participations.removeIf(p -> p.getUtilisateur().equals(utilisateur));
    }

    public boolean estActif() {
        LocalDate aujourdHui = LocalDate.now();
        return !aujourdHui.isBefore(dateDebut) && !aujourdHui.isAfter(dateFin);
    }

}