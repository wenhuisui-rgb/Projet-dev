package com.example.demo.model;

import java.time.LocalDateTime;

public class Commentaire {
    
    private Long id;
    private String contenu;
    private LocalDateTime dateCreation;



    public Commentaire(Long id, String contenu, LocalDateTime dateCreation) {
        this.id = id;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void modifierContenu(String nouveauContenu) {
    }

    public void supprimer() {
    }

}
