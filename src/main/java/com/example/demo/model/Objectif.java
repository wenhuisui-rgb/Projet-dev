package com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

public class Objectif {
    private Long id;
    private String description;
    private TypeSport typeSport;
    private Float cible;
    private String unite;
    private LocalDate dateDebut;
    private String periode;

    public Objectif() {
    }

    public Objectif(Long id, String description, TypeSport typeSport, Float cible, 
                    String unite, LocalDate dateDebut, String periode) {
        this.id = id;
        this.description = description;
        this.typeSport = typeSport;
        this.cible = cible;
        this.unite = unite;
        this.dateDebut = dateDebut;
        this.periode = periode;
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

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public Float calculerProgression(List<Activite> activites) {
        return null;
    }

    public Boolean estAtteint() {
        return null;
    }

    public void prolongerObjectif(LocalDate nouvelleDate) {
    }
}
