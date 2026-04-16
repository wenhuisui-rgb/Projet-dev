package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

/**
 * Entité représentant un badge (succès/récompense) disponible dans le système.
 * Il s'agit du modèle global du badge, et non de sa possession par un utilisateur.
 * La liaison avec les utilisateurs se fait via {@link ObtentionBadge}.
 */
@Entity
public class Badge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le nom de code ou l'identifiant lisible du badge (ex: "TOTAL_100KM"). */
    private String nom;

    /** La description affichée à l'utilisateur. */
    private String description;

    /** Le sport spécifique lié à ce badge (peut être null pour un badge général). */
    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;

    /** La valeur minimale (distance, nombre d'activités, etc.) requise pour débloquer ce badge. */
    private float seuil; 

    /** La liste de toutes les obtentions de ce badge par différents utilisateurs. */
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ObtentionBadge> obtentions;

    public Badge() {
    }

    public Badge(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Badge(String nom, String description, TypeSport typeSport, float seuil) {
        this.nom = nom;
        this.description = description;
        this.typeSport = typeSport;
        this.seuil = seuil;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }
    public float getSeuil() { return seuil; }
    public void setSeuil(float seuil) { this.seuil = seuil; }
    public List<ObtentionBadge> getObtentions() { return obtentions; }
    public void setObtentions(List<ObtentionBadge> obtentions) { this.obtentions = obtentions; }
}