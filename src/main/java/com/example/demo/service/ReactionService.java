package com.example.demo.service;

import com.example.demo.model.Reaction;
import com.example.demo.model.TypeReaction;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    @Transactional
    public Reaction ajouterReaction(TypeReaction type, Activite activite, Utilisateur auteur) {
        Optional<Reaction> existing = reactionRepository.findByAuteurAndActivite(auteur, activite);
        if (existing.isPresent()) {
            return null;
        }

        Reaction reaction = new Reaction();
        reaction.setType(type);
        reaction.setDateReaction(LocalDateTime.now());
        reaction.setActivite(activite);
        reaction.setAuteur(auteur);
        return reactionRepository.save(reaction);
    }

    @Transactional
    public void supprimerReaction(Utilisateur auteur, Activite activite) {
        reactionRepository.deleteByAuteurAndActivite(auteur, activite);
    }

    @Transactional
    public Reaction modifierReaction(Utilisateur auteur, Activite activite, TypeReaction nouveauType) {
        Optional<Reaction> existing = reactionRepository.findByAuteurAndActivite(auteur, activite);
        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            reaction.setType(nouveauType);
            reaction.setDateReaction(LocalDateTime.now());
            return reactionRepository.save(reaction);
        }
        return null;
    }

    public List<Reaction> getReactionsParActivite(Activite activite) {
        return reactionRepository.findByActiviteOrderByDateReactionDesc(activite);
    }

    public List<Reaction> getReactionsParActiviteId(Long activiteId) {
        return reactionRepository.findByActiviteId(activiteId);
    }

    public Optional<Reaction> getReactionByAuteurEtActivite(Utilisateur auteur, Activite activite) {
        return reactionRepository.findByAuteurAndActivite(auteur, activite);
    }

    public boolean aDejaReagi(Utilisateur auteur, Activite activite) {
        return reactionRepository.existsByAuteurAndActivite(auteur, activite);
    }

    public long getNombreReactionsParActivite(Long activiteId) {
        return reactionRepository.countByActiviteId(activiteId);
    }

    public long getNombreReactionsParType(Long activiteId, TypeReaction type) {
        return reactionRepository.countByActiviteIdAndType(activiteId, type);
    }
}