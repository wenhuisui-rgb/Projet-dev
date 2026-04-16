package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une activité sportive réalisée par un utilisateur.
 * <p>
 * C'est le cœur du système. Elle contient les métriques de l'effort (durée, distance, calories)
 * et sert de point d'ancrage pour les interactions sociales ({@link Commentaire}, {@link Reaction}).
 */
@Entity
@Table(name = "activites")
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le type de sport pratiqué (ex: COURSE, VELO). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSport typeSport;

    /** La date et l'heure exactes auxquelles l'activité a été réalisée. */
    @Column(nullable = false)
    private LocalDateTime dateActivite;

    /** La durée de l'activité en minutes. */
    @Column(nullable = false)
    private Integer duree;

    /** La distance parcourue, généralement en kilomètres (peut être null pour certains sports). */
    private Float distance;

    /** Le lieu où s'est déroulée l'activité. */
    private String localisation;

    /** La note ou l'évaluation de la difficulté ressentie par l'utilisateur (ex: sur 5 ou 10). */
    private Integer evaluation;

    /** Le nombre de calories brûlées (calculé automatiquement). */
    private Float calories;

    /** Les conditions météorologiques lors de l'activité. */
    @Column(length = 500)
    private String meteo;

    /** L'utilisateur qui a réalisé et enregistré cette activité. */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /** La liste des commentaires laissés par d'autres utilisateurs sur cette activité. */
    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires = new ArrayList<>();

    /** La liste des réactions (likes, etc.) sur cette activité. */
    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

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

    // Getters et Setters (Omis les commentaires pour les getters/setters standards)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }
    public LocalDateTime getDateActivite() { return dateActivite; }
    public void setDateActivite(LocalDateTime dateActivite) { this.dateActivite = dateActivite; }
    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }
    public Float getDistance() { return distance; }
    public void setDistance(Float distance) { this.distance = distance; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public Integer getEvaluation() { return evaluation; }
    public void setEvaluation(Integer evaluation) { this.evaluation = evaluation; }
    public Float getCalories() { return calories; }
    public void setCalories(Float calories) { this.calories = calories; }
    public String getMeteo() { return meteo; }
    public void setMeteo(String meteo) { this.meteo = meteo; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }
    public List<Reaction> getReactions() { return reactions; }
    public void setReactions(List<Reaction> reactions) { this.reactions = reactions; }

    /**
     * Ajoute un commentaire à l'activité et maintient la relation bidirectionnelle.
     */
    public void ajouterCommentaire(Commentaire commentaire) {
        commentaires.add(commentaire);
        commentaire.setActivite(this);
    }

    /**
     * Retire un commentaire de l'activité et rompt la relation bidirectionnelle.
     */
    public void retirerCommentaire(Commentaire commentaire) {
        commentaires.remove(commentaire);
        commentaire.setActivite(null);
    }

    /**
     * Ajoute une réaction à l'activité et maintient la relation bidirectionnelle.
     */
    public void ajouterReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setActivite(this);
    }

    /**
     * Retire une réaction de l'activité et rompt la relation bidirectionnelle.
     */
    public void retirerReaction(Reaction reaction) {
        reactions.remove(reaction);
        reaction.setActivite(null);
    }

    /**
     * Calcule et met à jour les calories brûlées en fonction du sport, de la durée et du poids.
     * Utilise l'équivalent métabolique (MET) défini dans {@link TypeSport}.
     *
     * @param poidsUtilisateur Le poids de l'utilisateur en kg
     * @return Les calories calculées et arrondies à une décimale
     */
    public Float calculerCalories(Float poidsUtilisateur) {
        if (typeSport == null || duree == null || poidsUtilisateur == null) {
            return 0f;
        }
        
        float met = typeSport.getMet();
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