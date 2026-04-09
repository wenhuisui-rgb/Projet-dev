package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.demo.model.TypeSport;

@Entity
@Table(name = "activites")
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSport typeSport;

    @Column(nullable = false)
    private LocalDateTime dateActivite;

    @Column(nullable = false)
    private Integer duree;//minute

    private Float distance; //km

    private String localisation;

    private Integer evaluation; // 1-5 etoiles

    private Float calories; 

    @Column(length = 500)
    private String meteo;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public Activite() {
    }

    public Activite(TypeSport typeSport, LocalDateTime dateActivite, Integer duree,
                    Float distance, String localisation, Integer evaluation,
                    Float calories, String meteo, Utilisateur utilisateur) {
        this.typeSport = typeSport;
        this.dateActivite = dateActivite;
        this.duree = duree;
        this.distance = distance;
        this.localisation = localisation;
        this.evaluation = evaluation;
        this.calories = calories;
        this.meteo = meteo;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeSport getTypeSport() {
        return typeSport;
    }

    public void setTypeSport(TypeSport typeSport) {
        this.typeSport = typeSport;
    }

    public LocalDateTime getDateActivite() {
        return dateActivite;
    }

    public void setDateActivite(LocalDateTime dateActivite) {
        this.dateActivite = dateActivite;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Integer getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Integer evaluation) {
        this.evaluation = evaluation;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public String getMeteo() {
        return meteo;
    }

    public void setMeteo(String meteo) {
        this.meteo = meteo;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Float calculerCalories(Float poidsUtilisateur) {
        if (typeSport == null || duree == null || poidsUtilisateur == null) {
            return 0f;
        }

        float met = 0f;
        switch (typeSport) {
            case COURSE:
                met = 9.8f;
                break;
            case NATATION:
                met = 8.0f;
                break;
            case VELO:
                met = 7.5f;
                break;
            case MUSCULATION:
                met = 6.0f;
                break;
            case YOGA:
                met = 3.0f;
                break;
            case RANDONNEE:
                met = 5.5f;
                break;
            default:
                met = 5.0f;
        }
        
        float heures = duree / 60.0f;
        float caloriesCalculees = met * poidsUtilisateur * heures;
        
        this.calories = Math.round(caloriesCalculees * 10) / 10.0f;
        return this.calories;
    }

    @Override
    public String toString() {
        return "Activite{" +
                "id=" + id +
                ", typeSport=" + typeSport +
                ", dateActivite=" + dateActivite +
                ", duree=" + duree +
                ", distance=" + distance +
                ", localisation='" + localisation + '\'' +
                ", evaluation=" + evaluation +
                ", calories=" + calories +
                ", meteo='" + meteo + '\'' +
                '}';
    }
}