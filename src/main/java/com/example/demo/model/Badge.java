package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Badge {

    public Badge(String nom, String description, TypeSport typeSport, float seuil) {
        this.nom = nom;
        this.description = description;
        this.typeSport = typeSport;
        this.seuil = seuil;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;

    private float seuil; // Valeur minimale pour obtenir le badge

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ObtentionBadge> obtentions;

    public Badge() {
    }

    public Badge(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public float getSeuil() {
        return seuil;
    }

    public void setSeuil(float seuil) {
        this.seuil = seuil;
    }

    public List<ObtentionBadge> getObtentions() {
        return obtentions;
    }

    public void setObtentions(List<ObtentionBadge> obtentions) {
        this.obtentions = obtentions;
    }
}