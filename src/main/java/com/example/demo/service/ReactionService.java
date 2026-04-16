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

/**
 * Service gérant les réactions (ex: Like, Bravo, etc.) des utilisateurs sur les activités.
 * <p>
 * Ce service garantit qu'un utilisateur ne peut avoir qu'une seule réaction active
 * par {@link Activite}.
 */
@Service
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    /**
     * Ajoute une nouvelle réaction à une activité.
     * Si l'utilisateur a déjà réagi à cette activité, l'opération est ignorée.
     *
     * @param type     Le type de réaction (ex: LIKE)
     * @param activite L'activité ciblée
     * @param auteur   L'utilisateur qui réagit
     * @return La réaction sauvegardée, ou {@code null} si une réaction existe déjà
     */
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
        
        Reaction saved = reactionRepository.save(reaction);
        
        activite.ajouterReaction(saved);
        
        return saved;
    }

    /**
     * Supprime la réaction d'un utilisateur sur une activité spécifique.
     * Met également à jour l'entité {@link Activite} en retirant la réaction de sa liste.
     *
     * @param auteur   L'utilisateur dont on veut supprimer la réaction
     * @param activite L'activité concernée
     */
    @Transactional
    public void supprimerReaction(Utilisateur auteur, Activite activite) {
        Optional<Reaction> existing = reactionRepository.findByAuteurAndActivite(auteur, activite);
        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            activite.retirerReaction(reaction);
            reactionRepository.delete(reaction);
        }
    }

    /**
     * Modifie le type de réaction d'un utilisateur pour une activité donnée
     * (par exemple, passer d'un "Like" à un "Bravo").
     * Met à jour la date de la réaction.
     *
     * @param auteur      L'utilisateur concerné
     * @param activite    L'activité concernée
     * @param nouveauType Le nouveau type de réaction
     * @return La réaction mise à jour, ou {@code null} si aucune réaction préalable n'existe
     */
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

    /**
     * Récupère toutes les réactions d'une activité, de la plus récente à la plus ancienne.
     */
    public List<Reaction> getReactionsParActivite(Activite activite) {
        return reactionRepository.findByActiviteOrderByDateReactionDesc(activite);
    }

    /**
     * Récupère toutes les réactions d'une activité via son identifiant.
     */
    public List<Reaction> getReactionsParActiviteId(Long activiteId) {
        return reactionRepository.findByActiviteId(activiteId);
    }

    /**
     * Cherche la réaction spécifique d'un utilisateur pour une activité.
     */
    public Optional<Reaction> getReactionByAuteurEtActivite(Utilisateur auteur, Activite activite) {
        return reactionRepository.findByAuteurAndActivite(auteur, activite);
    }

    /**
     * Vérifie si un utilisateur a déjà laissé une réaction sur une activité.
     *
     * @return {@code true} si une réaction existe, {@code false} sinon
     */
    public boolean aDejaReagi(Utilisateur auteur, Activite activite) {
        return reactionRepository.existsByAuteurAndActivite(auteur, activite);
    }

    /**
     * Compte le nombre total de réactions pour une activité donnée.
     */
    public long getNombreReactionsParActivite(Long activiteId) {
        return reactionRepository.countByActiviteId(activiteId);
    }

    /**
     * Compte le nombre de réactions d'un type spécifique (ex: combien de LIKES) pour une activité.
     */
    public long getNombreReactionsParType(Long activiteId, TypeReaction type) {
        return reactionRepository.countByActiviteIdAndType(activiteId, type);
    }
}