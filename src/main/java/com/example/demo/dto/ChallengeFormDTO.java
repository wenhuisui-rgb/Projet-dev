package com.example.demo.dto;

import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import java.time.LocalDate;

/**
 * DTO pour sécuriser la création de défis (Challenges).
 */
public class ChallengeFormDTO {
    
    private String titre;
    private TypeSport typeSport;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Unite unite;
    private Float cible;

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Unite getUnite() { return unite; }
    public void setUnite(Unite unite) { this.unite = unite; }

    public Float getCible() { return cible; }
    public void setCible(Float cible) { this.cible = cible; }
}