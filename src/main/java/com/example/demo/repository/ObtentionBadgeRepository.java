package com.example.demo.repository;

import com.example.demo.model.ObtentionBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Interface de repository pour l'entité {@link ObtentionBadge}.
 * Gère l'historique et les liaisons entre les utilisateurs et les badges qu'ils ont débloqués.
 */
public interface ObtentionBadgeRepository extends JpaRepository<ObtentionBadge, Long> {

    /**
     * Récupère la liste de tous les badges obtenus par un utilisateur spécifique.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @return La liste des obtentions de badges pour cet utilisateur
     */
    List<ObtentionBadge> findByUtilisateurId(Long utilisateurId);

    /**
     * Récupère la liste de toutes les obtentions pour un badge spécifique
     * (utile pour savoir qui a débloqué ce badge).
     *
     * @param badgeId L'identifiant du badge
     * @return La liste des utilisateurs (via l'entité de liaison) ayant ce badge
     */
    List<ObtentionBadge> findByBadgeId(Long badgeId);

    /**
     * Vérifie si un utilisateur a déjà obtenu un badge spécifique.
     * Utilisé comme garde-fou pour éviter d'attribuer le même badge plusieurs fois.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param badgeId       L'identifiant du badge
     * @return {@code true} si l'utilisateur possède déjà le badge, {@code false} sinon
     */
    boolean existsByUtilisateurIdAndBadgeId(Long utilisateurId, Long badgeId);
}