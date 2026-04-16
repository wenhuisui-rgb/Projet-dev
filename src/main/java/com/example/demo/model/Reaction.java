package com.example.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant une réaction (interaction sociale type "Like") laissée par un utilisateur sur une activité.
 * <p>
 * La contrainte d'unicité ({@code @UniqueConstraint}) garantit qu'un utilisateur
 * ne peut avoir qu'une seule réaction active par activité.
 */
@Entity
@Table(name = "reactions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"auteur_id", "activite_id"})
})
public class Reaction implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le type de la réaction (ex: BRAVO, SOUTIEN). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeReaction type;

    /** La date et l'heure exactes de la réaction. */
    @Column(nullable = false)
    private LocalDateTime dateReaction;

    /** L'utilisateur qui a réagi. */
    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    /** L'activité sportive qui a reçu la réaction. */
    @ManyToOne
    @JoinColumn(name = "activite_id", nullable = false)
    private Activite activite;

    public Reaction() {
    }

    public Reaction(TypeReaction type, LocalDateTime dateReaction, Utilisateur auteur, Activite activite) {
        this.type = type;
        this.dateReaction = dateReaction;
        this.auteur = auteur;
        this.activite = activite;
    }

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TypeReaction getType() { return type; }
    public void setType(TypeReaction type) { this.type = type; }
    public LocalDateTime getDateReaction() { return dateReaction; }
    public void setDateReaction(LocalDateTime dateReaction) { this.dateReaction = dateReaction; }
    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }
    public Activite getActivite() { return activite; }
    public void setActivite(Activite activite) { this.activite = activite; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reaction)) return false;
        Reaction reaction = (Reaction) o;
        return id != null && id.equals(reaction.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Reaction{" +
                "id=" + id +
                ", type=" + type +
                ", dateReaction=" + dateReaction +
                '}';
    }
}