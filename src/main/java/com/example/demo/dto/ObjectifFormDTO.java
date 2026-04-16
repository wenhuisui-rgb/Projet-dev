package com.example.demo.dto;

import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import java.time.LocalDate;

/**
 * DTO pour sécuriser la création et modification des objectifs.
 */
public class ObjectifFormDTO {

    private String description;
    private Float cible;
    private Unite unite;
    private Periode periode;
    private TypeSport typeSport;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Getters et Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Float getCible() { return cible; }
    public void setCible(Float cible) { this.cible = cible; }

    public Unite getUnite() { return unite; }
    public void setUnite(Unite unite) { this.unite = unite; }

    public Periode getPeriode() { return periode; }
    public void setPeriode(Periode periode) { this.periode = periode; }

    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}