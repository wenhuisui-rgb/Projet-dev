package com.example.demo.model;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;


@Entity
public class ObtentionBadge {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)

    private Long id;
    private LocalDateTime dateObtention;
    
    @ManyToOne
    @JoinColumn(name="badge_id")
    private Badge badge;

    @ManyToOne
    @JoinColumn(name="utilisateur_id")
    private Utilisateur utilisateur;
    
    public ObtentionBadge(){

    }
    
    //On veut savoir quel utilisateur obtient quel badge
    public ObtentionBadge(Utilisateur utilisateur,Badge badge) {
        this.utilisateur=utilisateur;
        this.badge=badge;
        this.dateObtention = LocalDateTime.now();

        
    }
    
    public Long getId() {
        return id;
    }

    public LocalDateTime getDateObtention() {
        return dateObtention;
    }

    public void setDateObtention(LocalDateTime dateObtention) {
        this.dateObtention = dateObtention;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
}
