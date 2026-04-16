package com.example.demo.repository;

import com.example.demo.model.Reaction;
import com.example.demo.model.TypeReaction;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repository pour l'entité {@link Reaction}.
 * Gère les interactions (likes, bravos, etc.) des utilisateurs sur les activités sportives.
 */
@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * Récupère toutes les réactions d'une activité donnée, de la plus récente à la plus ancienne.
     *
     * @param activite L'activité concernée
     * @return La liste chronologique décroissante des réactions
     */
    List<Reaction> findByActiviteOrderByDateReactionDesc(Activite activite);

    /**
     * Récupère toutes les réactions d'une activité via son identifiant.
     *
     * @param activiteId L'identifiant de l'activité
     * @return La liste des réactions
     */
    List<Reaction> findByActiviteId(Long activiteId);

    /**
     * Récupère toutes les réactions postées par un utilisateur spécifique sur l'ensemble de la plateforme.
     *
     * @param auteur L'utilisateur ayant réagi
     * @return La liste de ses réactions
     */
    List<Reaction> findByAuteur(Utilisateur auteur);

    /**
     * Cherche la réaction unique d'un utilisateur sur une activité spécifique.
     *
     * @param auteur   L'utilisateur auteur de la réaction
     * @param activite L'activité ciblée
     * @return Un {@link Optional} contenant la réaction si elle existe
     */
    Optional<Reaction> findByAuteurAndActivite(Utilisateur auteur, Activite activite);

    /**
     * Vérifie rapidement si un utilisateur a déjà réagi à une activité.
     *
     * @param auteur   L'utilisateur à vérifier
     * @param activite L'activité ciblée
     * @return {@code true} si une réaction existe, {@code false} sinon
     */
    boolean existsByAuteurAndActivite(Utilisateur auteur, Activite activite);

    /**
     * Compte le nombre total de réactions pour une activité donnée.
     *
     * @param activiteId L'identifiant de l'activité
     * @return Le nombre total de réactions
     */
    long countByActiviteId(Long activiteId);

    /**
     * Compte le nombre de réactions d'un type spécifique (ex: nombre de KUDOS) pour une activité.
     *
     * @param activiteId L'identifiant de l'activité
     * @param type       Le type de réaction à compter (ex: {@link TypeReaction#KUDOS})
     * @return Le nombre de réactions de ce type
     */
    long countByActiviteIdAndType(Long activiteId, TypeReaction type);

    /**
     * Supprime la réaction d'un utilisateur sur une activité spécifique.
     *
     * @param auteur   L'utilisateur dont on veut retirer la réaction
     * @param activite L'activité concernée
     */
    void deleteByAuteurAndActivite(Utilisateur auteur, Activite activite);

}