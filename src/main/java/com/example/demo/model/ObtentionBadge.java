package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité de liaison (table d'association) qui relie un {@link Utilisateur} à un {@link Badge}.
 * <p>
 * Elle permet de conserver l'historique et la date exacte à laquelle un utilisateur
 * a débloqué une récompense spécifique.
 */
@Entity
public class ObtentionBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** La date et l'heure exactes de l'obtention du badge. */
    private LocalDateTime dateObtention;

    /** Le badge qui a été débloqué. */
    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    /** L'utilisateur qui a gagné ce badge. */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnore
    private Utilisateur utilisateur;

    public ObtentionBadge() {
    }

    public ObtentionBadge(Utilisateur utilisateur, Badge badge) {
        this.utilisateur = utilisateur;
        this.badge = badge;
        this.dateObtention = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public LocalDateTime getDateObtention() { return dateObtention; }
    public void setDateObtention(LocalDateTime dateObtention) { this.dateObtention = dateObtention; }
    public Badge getBadge() { return badge; }
    public void setBadge(Badge badge) { this.badge = badge; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}