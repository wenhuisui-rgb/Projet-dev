package com.example.demo.repository;

import com.example.demo.model.Commentaire;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Interface de repository pour l'entité {@link Commentaire}.
 * Gère l'accès aux données des commentaires laissés sur les activités sportives.
 */
@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    
    /**
     * Récupère tous les commentaires d'une entité Activite, triés du plus récent au plus ancien.
     *
     * @param activite L'activité concernée
     * @return La liste chronologique décroissante des commentaires
     */
    List<Commentaire> findByActiviteOrderByDateCommentaireDesc(Activite activite);
    
    /**
     * Récupère tous les commentaires d'une activité via son identifiant.
     *
     * @param activiteId L'ID de l'activité
     * @return La liste des commentaires
     */
    List<Commentaire> findByActiviteId(Long activiteId);
    
    /**
     * Récupère tous les commentaires rédigés par un auteur, triés du plus récent au plus ancien.
     *
     * @param auteur L'utilisateur ayant rédigé les commentaires
     * @return La liste de ses commentaires
     */
    List<Commentaire> findByAuteurOrderByDateCommentaireDesc(Utilisateur auteur);
    
    /**
     * Supprime tous les commentaires associés à une activité spécifique.
     * <p>
     * L'annotation {@code @Modifying(clearAutomatically = true)} indique à Spring Data JPA 
     * qu'il s'agit d'une requête de modification (DELETE) et qu'il faut vider le cache de premier niveau 
     * (EntityManager) pour éviter des incohérences de données.
     *
     * @param activiteId L'ID de l'activité dont on veut purger les commentaires
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteByActiviteId(Long activiteId);
    
    /**
     * Compte le nombre total de commentaires présents sur une activité.
     *
     * @param activiteId L'ID de l'activité
     * @return Le nombre de commentaires
     */
    long countByActiviteId(Long activiteId);
}