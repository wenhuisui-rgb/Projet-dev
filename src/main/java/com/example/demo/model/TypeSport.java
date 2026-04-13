package com.example.demo.model;

public enum TypeSport {
    COURSE(9.8f),
    NATATION(8.0f),
    VELO(7.5f),
    MUSCULATION(6.0f),
    YOGA(3.0f),
    RANDONNEE(5.5f);

    private final float met;

    TypeSport(float met) {
        this.met = met;
    }

    public float getMet() {
        return met;
    }
}