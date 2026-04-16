package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entité représentant un objectif personnel fixé par un utilisateur.
 * (ex: "Courir 50 km ce mois-ci" ou "Brûler 2000 calories par semaine").
 */
@Entity
@Table(name = "objectifs")
public class Objectif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le nom ou la description libre de l'objectif. */
    private String description;

    /** Le sport ciblé par cet objectif (null si l'objectif est multi-sports). */
    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;

    /** La valeur numérique à atteindre (la cible). */
    private Float cible;

    /** L'unité de mesure de la cible (KM, MINUTES, KCAL). */
    @Enumerated(EnumType.STRING)
    private Unite unite;

    /** La date de début de la période de l'objectif. */
    private LocalDate dateDebut;

    /** La date d'échéance de l'objectif. */
    private LocalDate dateFin;

    /** La récurrence temporelle de l'objectif (ex: SEMAINE, MOIS). */
    @Enumerated(EnumType.STRING)
    private Periode periode;

    /** L'utilisateur à qui appartient cet objectif. */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public Objectif() {
    }

    public Objectif(Long id, String description, TypeSport typeSport, Float cible,
                    Unite unite, LocalDate dateDebut, LocalDate dateFin, Periode periode, Utilisateur utilisateur) {
        this.id = id;
        this.description = description;
        this.typeSport = typeSport;
        this.cible = cible;
        this.unite = unite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.periode = periode;
        this.utilisateur = utilisateur;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }
    public Float getCible() { return cible; }
    public void setCible(Float cible) { this.cible = cible; }
    public Unite getUnite() { return unite; }
    public void setUnite(Unite unite) { this.unite = unite; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public Periode getPeriode() { return periode; }
    public void setPeriode(Periode periode) { this.periode = periode; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    /**
     * Calcule dynamiquement la date de fin si elle n'est pas explicitement définie,
     * en se basant sur la date de début et le type de période (semaine, mois, année).
     *
     * @return La date de fin calculée
     */
    public LocalDate getDateFin() {
        if (dateFin != null) {
            return dateFin;
        }
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

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * Prolonge la date de fin de l'objectif si la nouvelle date proposée est postérieure.
     */
    public void prolongerObjectif(LocalDate nouvelleDateFin) {
        if (nouvelleDateFin != null && nouvelleDateFin.isAfter(getDateFin())) {
            this.dateFin = nouvelleDateFin;
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