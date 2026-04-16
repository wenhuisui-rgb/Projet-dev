package com.example.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant un commentaire textuel laissé par un utilisateur
 * sous une activité sportive ({@link Activite}).
 */
@Entity
@Table(name = "commentaires")
public class Commentaire implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le texte du commentaire. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    /** La date et l'heure exactes de la publication du commentaire. */
    @Column(nullable = false)
    private LocalDateTime dateCommentaire;

    /** L'utilisateur qui a rédigé le commentaire. */
    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    /** L'activité sportive sur laquelle le commentaire a été posté. */
    @ManyToOne
    @JoinColumn(name = "activite_id", nullable = false)
    private Activite activite;

    public Commentaire() {
    }

    public Commentaire(String contenu, LocalDateTime dateCommentaire, Utilisateur auteur, Activite activite) {
        this.contenu = contenu;
        this.dateCommentaire = dateCommentaire;
        this.auteur = auteur;
        this.activite = activite;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateCommentaire() { return dateCommentaire; }
    public void setDateCommentaire(LocalDateTime dateCommentaire) { this.dateCommentaire = dateCommentaire; }
    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }
    public Activite getActivite() { return activite; }
    public void setActivite(Activite activite) { this.activite = activite; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commentaire)) return false;
        Commentaire that = (Commentaire) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", dateCommentaire=" + dateCommentaire +
                '}';
    }
}