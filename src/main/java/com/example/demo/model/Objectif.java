package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "objectifs")
public class Objectif {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;
    
    private Float cible;
    
    private String unite;
    
    private LocalDate dateDebut;
    
    private String periode; // SEMAINE, MOIS, ANNEE
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public Objectif() {
    }

    public Objectif(Long id, String description, TypeSport typeSport, Float cible, 
                    String unite, LocalDate dateDebut, String periode, Utilisateur utilisateur) {
        this.id = id;
        this.description = description;
        this.typeSport = typeSport;
        this.cible = cible;
        this.unite = unite;
        this.dateDebut = dateDebut;
        this.periode = periode;
        this.utilisateur = utilisateur;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Float getCible() {
        return cible;
    }

    public void setCible(Float cible) {
        this.cible = cible;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /**
     * 根据活动列表计算当前进度
     */
    public Float calculerProgression(List<Activite> activites) {
        if (activites == null || activites.isEmpty() || cible == null || cible == 0) {
            return 0f;
        }
        
        float total = 0f;
        LocalDate dateFin = getDateFin();
        
        for (Activite activite : activites) {
            if (typeSport != null && activite.getTypeSport() != typeSport) {
                continue;
            }
            LocalDate dateActivite = activite.getDateActivite().toLocalDate();
            if (!dateActivite.isBefore(dateDebut) && !dateActivite.isAfter(dateFin)) {
                if ("km".equals(unite) && activite.getDistance() != null) {
                    total += activite.getDistance();
                } else if ("minutes".equals(unite) && activite.getDuree() != null) {
                    total += activite.getDuree();
                } else if ("kcal".equals(unite) && activite.getCalories() != null) {
                    total += activite.getCalories();
                }
            }
        }
        
        return total;
    }

    /**
     * 获取进度百分比
     */
    public Float getPourcentageProgression(List<Activite> activites) {
        float progression = calculerProgression(activites);
        if (cible == null || cible == 0) {
            return 0f;
        }
        return (progression / cible) * 100;
    }

    /**
     * 判断目标是否达成
     */
    public Boolean estAtteint(List<Activite> activites) {
        float progression = calculerProgression(activites);
        return progression >= cible;
    }

    /**
     * 获取结束日期
     */
    private LocalDate getDateFin() {
        if (dateDebut == null) return LocalDate.now();
        
        switch (periode) {
            case "SEMAINE":
                return dateDebut.plusWeeks(1);
            case "MOIS":
                return dateDebut.plusMonths(1);
            case "ANNEE":
                return dateDebut.plusYears(1);
            default:
                return dateDebut.plusMonths(1);
        }
    }

    /**
     * 延长目标
     */
    public void prolongerObjectif(LocalDate nouvelleDate) {
        if (nouvelleDate != null && nouvelleDate.isAfter(dateDebut)) {
            this.dateDebut = nouvelleDate;
        }
    }

    @Override
    public String toString() {
        return "Objectif{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", typeSport=" + typeSport +
                ", cible=" + cible + " " + unite +
                ", periode=" + periode +
                '}';
    }
}