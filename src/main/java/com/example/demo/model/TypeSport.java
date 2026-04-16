package com.example.demo.model;

/**
 * Énumération des sports disponibles sur la plateforme.
 * <p>
 * Chaque sport est associé à son Équivalent Métabolique (MET), 
 * une valeur utilisée pour calculer les calories brûlées lors de l'effort.
 */
public enum TypeSport {
    COURSE(9.8f),
    NATATION(8.0f),
    VELO(7.5f),
    MUSCULATION(6.0f),
    YOGA(3.0f),
    RANDONNEE(5.5f);

    /** Valeur MET (Metabolic Equivalent of Task) du sport. */
    private final float met;

    TypeSport(float met) {
        this.met = met;
    }

    public float getMet() {
        return met;
    }
}