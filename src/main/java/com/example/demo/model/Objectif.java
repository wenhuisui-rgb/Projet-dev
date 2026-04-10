package com.example.demo.model;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "objectifs")
public class Objectif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;

    private Float cible;

    private String unite;

    private LocalDate dateDebut;

    @Enumerated(EnumType.STRING)
    private Periode periode;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public Objectif() {
    }

    public Objectif(Long id, String description, TypeSport typeSport, Float cible,
                String unite, LocalDate dateDebut, Periode periode, Utilisateur utilisateur) {
    this.id = id;
    this.description = description;
    this.typeSport = typeSport;
    this.cible = cible;
    this.unite = unite;
    this.dateDebut = dateDebut;
    this.periode = periode;
    this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeSport getTypeSport() {
        return typeSport;
    }

    public void setTypeSport(TypeSport typeSport) {
        this.typeSport = typeSport;
    }

    public Float getCible() {
        return cible;
    }

    public void setCible(Float cible) {
        this.cible = cible;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Periode getPeriode() {
        return periode;
    }

    public void setPeriode(Periode periode) {
        this.periode = periode;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDate getDateFin() {
        if (dateDebut == null) return LocalDate.now();
        if (periode == null) return dateDebut.plusMonths(1);
        switch (periode) {
            case SEMAINE:
                return dateDebut.plusWeeks(1);
            case ANNEE:
                return dateDebut.plusYears(1);
            default:
                return dateDebut.plusMonths(1);
        }
    }

    public void prolongerObjectif(LocalDate nouvelleDate) {
        if (nouvelleDate != null && nouvelleDate.isAfter(dateDebut)) {
            this.dateDebut = nouvelleDate;
        }
    }

    @Override
    public String toString() {
        return "Objectif{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", typeSport=" + typeSport +
                ", cible=" + cible + " " + unite +
                ", periode=" + periode +
                '}';
    }
}