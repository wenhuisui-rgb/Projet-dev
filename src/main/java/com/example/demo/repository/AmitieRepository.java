package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repository pour l'entité {@link Amitie}.
 * Gère l'accès aux données concernant les relations d'amitié entre utilisateurs.
 */
public interface AmitieRepository extends JpaRepository<Amitie, Long> {

    /**
     * Recherche une relation d'amitié spécifique entre un demandeur et un receveur.
     *
     * @param demandeur L'utilisateur ayant initié la demande
     * @param receveur  L'utilisateur cible de la demande
     * @return Un {@link Optional} contenant l'amitié si elle existe, sinon vide
     */
    Optional<Amitie> findByUtilisateurDemandeurAndUtilisateurReceveur(
            Utilisateur demandeur,
            Utilisateur receveur
    );

    /**
     * Récupère toutes les demandes d'amitié reçues par un utilisateur avec un statut spécifique.
     * (ex: trouver toutes les demandes EN_ATTENTE pour un utilisateur donné)
     *
     * @param receveur L'utilisateur qui a reçu la demande
     * @param statut   Le statut de l'amitié (ex: {@link StatutAmitie#EN_ATTENTE})
     * @return Une liste des relations d'amitié correspondantes
     */
    List<Amitie> findByUtilisateurReceveurAndStatut(
            Utilisateur receveur,
            StatutAmitie statut
    );

    /**
     * Récupère toutes les demandes d'amitié envoyées par un utilisateur avec un statut spécifique.
     *
     * @param demandeur L'utilisateur ayant initié la demande
     * @param statut    Le statut de l'amitié
     * @return Une liste des relations d'amitié correspondantes
     */
    List<Amitie> findByUtilisateurDemandeurAndStatut(
            Utilisateur demandeur,
            StatutAmitie statut
    );

}