package com.example.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;
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
public class Activite implements Serializable{

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

    /**
     * Constructeur par défaut.
     */
    public Activite() {
    }

    /**
     * Constructeur avec tous les paramètres principaux.
     *
     * @param typeSport    Le sport pratiqué
     * @param dateActivite La date de l'activité
     * @param duree        La durée en minutes
     * @param distance     La distance en km
     * @param localisation Le lieu de l'activité
     * @param evaluation   L'évaluation personnelle
     * @param calories     Les calories brûlées
     * @param meteo        Les conditions météo
     * @param utilisateur  L'utilisateur auteur
     */
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

    /**
     * Obtient l'identifiant.
     * @return L'identifiant de l'activité
     */
    public Long getId() { return id; }

    /**
     * Définit l'identifiant.
     * @param id Le nouvel identifiant
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtient le type de sport.
     * @return Le type de sport
     */
    public TypeSport getTypeSport() { return typeSport; }

    /**
     * Définit le type de sport.
     * @param typeSport Le nouveau type de sport
     */
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }

    /**
     * Obtient la date de l'activité.
     * @return La date de l'activité
     */
    public LocalDateTime getDateActivite() { return dateActivite; }

    /**
     * Définit la date de l'activité.
     * @param dateActivite La nouvelle date
     */
    public void setDateActivite(LocalDateTime dateActivite) { this.dateActivite = dateActivite; }

    /**
     * Obtient la durée.
     * @return La durée en minutes
     */
    public Integer getDuree() { return duree; }

    /**
     * Définit la durée.
     * @param duree La nouvelle durée en minutes
     */
    public void setDuree(Integer duree) { this.duree = duree; }

    /**
     * Obtient la distance.
     * @return La distance
     */
    public Float getDistance() { return distance; }

    /**
     * Définit la distance.
     * @param distance La nouvelle distance
     */
    public void setDistance(Float distance) { this.distance = distance; }

    /**
     * Obtient la localisation.
     * @return La localisation
     */
    public String getLocalisation() { return localisation; }

    /**
     * Définit la localisation.
     * @param localisation La nouvelle localisation
     */
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    /**
     * Obtient l'évaluation.
     * @return L'évaluation
     */
    public Integer getEvaluation() { return evaluation; }

    /**
     * Définit l'évaluation.
     * @param evaluation La nouvelle évaluation
     */
    public void setEvaluation(Integer evaluation) { this.evaluation = evaluation; }

    /**
     * Obtient les calories.
     * @return Les calories
     */
    public Float getCalories() { return calories; }

    /**
     * Définit les calories.
     * @param calories Les nouvelles calories
     */
    public void setCalories(Float calories) { this.calories = calories; }

    /**
     * Obtient la météo.
     * @return La météo
     */
    public String getMeteo() { return meteo; }

    /**
     * Définit la météo.
     * @param meteo La nouvelle météo
     */
    public void setMeteo(String meteo) { this.meteo = meteo; }

    /**
     * Obtient l'utilisateur.
     * @return L'utilisateur
     */
    public Utilisateur getUtilisateur() { return utilisateur; }

    /**
     * Définit l'utilisateur.
     * @param utilisateur Le nouvel utilisateur
     */
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    /**
     * Obtient la liste des commentaires.
     * @return La liste des commentaires
     */
    public List<Commentaire> getCommentaires() { return commentaires; }

    /**
     * Définit la liste des commentaires.
     * @param commentaires La nouvelle liste
     */
    public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }

    /**
     * Obtient la liste des réactions.
     * @return La liste des réactions
     */
    public List<Reaction> getReactions() { return reactions; }

    /**
     * Définit la liste des réactions.
     * @param reactions La nouvelle liste
     */
    public void setReactions(List<Reaction> reactions) { this.reactions = reactions; }

    /**
     * Ajoute un commentaire à l'activité et maintient la relation bidirectionnelle.
     * @param commentaire Le commentaire à ajouter
     */
    public void ajouterCommentaire(Commentaire commentaire) {
        commentaires.add(commentaire);
        commentaire.setActivite(this);
    }

    /**
     * Retire un commentaire de l'activité et rompt la relation bidirectionnelle.
     * @param commentaire Le commentaire à retirer
     */
    public void retirerCommentaire(Commentaire commentaire) {
        commentaires.remove(commentaire);
        commentaire.setActivite(null);
    }

    /**
     * Ajoute une réaction à l'activité et maintient la relation bidirectionnelle.
     * @param reaction La réaction à ajouter
     */
    public void ajouterReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setActivite(this);
    }

    /**
     * Retire une réaction de l'activité et rompt la relation bidirectionnelle.
     * @param reaction La réaction à retirer
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

    /**
     * Retourne une représentation sous forme de chaîne de caractères de l'activité.
     * @return La chaîne descriptive
     */
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