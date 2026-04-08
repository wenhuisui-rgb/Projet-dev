package com.example.demo.model;

import java.time.LocalDateTime;

public class Reaction {
    private Long id;
    private TypeReaction typeReaction;
    private LocalDateTime dateReaction;

    public void changerType(TypeReaction nouveauType) {
        // TODO: Implement logic
    }

    public void retirerReaction() {
        // TODO: Implement logic
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeReaction getTypeReaction() {
        return typeReaction;
    }

    public void setTypeReaction(TypeReaction typeReaction) {
        this.typeReaction = typeReaction;
    }

    public LocalDateTime getDateReaction() {
        return dateReaction;
    }

    public void setDateReaction(LocalDateTime dateReaction) {
        this.dateReaction = dateReaction;
    }

    
}