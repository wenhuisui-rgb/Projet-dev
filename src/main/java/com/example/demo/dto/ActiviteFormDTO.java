package com.example.demo.dto;

import com.example.demo.model.TypeSport;
import java.time.LocalDateTime;

/**
 * DTO pour sécuriser la réception des données du formulaire Activite.
 */
public class ActiviteFormDTO {

    private TypeSport typeSport;
    private Integer duree;
    private Float distance;
    private String localisation;
    private Integer evaluation;
    private LocalDateTime dateActivite;

    // Getters et Setters
    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Float getDistance() { return distance; }
    public void setDistance(Float distance) { this.distance = distance; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public Integer getEvaluation() { return evaluation; }
    public void setEvaluation(Integer evaluation) { this.evaluation = evaluation; }

    public LocalDateTime getDateActivite() { return dateActivite; }
    public void setDateActivite(LocalDateTime dateActivite) { this.dateActivite = dateActivite; }
}