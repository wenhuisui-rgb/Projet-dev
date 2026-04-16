package com.example.demo.model;

/**
 * Énumération représentant la périodicité ou la récurrence d'un objectif.
 */
public enum Periode {
    SEMAINE("Semaine"),
    MOIS("Mois"),
    ANNEE("Année");

    private String libelle;

    Periode(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}