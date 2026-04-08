package com.example.demo.model;

import java.time.LocalDateTime;

public class Activite {

    private Long id;
    private TypeSport typeSport;
    private LocalDateTime dateActivite;
    private Integer duree;
    private Float distance;
    private String localisation;
    private Integer evaluation;
    private Float calories;
    private String meteo;

    public Activite() {
    }

    public Activite(Long id, TypeSport typeSport, LocalDateTime dateActivite, Integer duree, 
                    Float distance, String localisation, Integer evaluation, Float calories, String meteo) {
        this.id = id;
        this.typeSport = typeSport;
        this.dateActivite = dateActivite;
        this.duree = duree;
        this.distance = distance;
        this.localisation = localisation;
        this.evaluation = evaluation;
        this.calories = calories;
        this.meteo = meteo;
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

     public Float calculerCalories() {
        return calories;
    }

    public String analyserPerformance() {
        return meteo;
    }

    public void modifierActivite() {
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
