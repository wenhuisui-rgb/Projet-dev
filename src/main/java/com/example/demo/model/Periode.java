package com.example.demo.model;

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