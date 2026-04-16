package com.example.demo.repository;

import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface de repository pour l'entité {@link Challenge}.
 * Gère la persistance et les requêtes complexes liées aux défis sportifs.
 */
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    /**
     * Trouve tous les challenges créés par un utilisateur spécifique.
     *
     * @param createurId L'identifiant de l'utilisateur créateur
     * @return La liste des challenges organisés par cet utilisateur
     */
    List<Challenge> findByCreateurId(Long createurId);

    /**
     * Trouve tous les challenges filtrés par un type de sport spécifique.
     *
     * @param typeSport Le sport ciblé par le challenge
     * @return La liste des challenges correspondants
     */
    List<Challenge> findByTypeSport(TypeSport typeSport);

    /**
     * Récupère tous les challenges auxquels un utilisateur est actuellement inscrit.
     * Utilise une requête JPQL personnalisée pour effectuer une jointure avec la table des participations.
     *
     * @param user L'utilisateur participant
     * @return La liste des challenges rejoints par l'utilisateur
     */
    @Query("""
        SELECT c
        FROM Challenge c
        JOIN c.participations p
        WHERE p.utilisateur = :user
    """)
    List<Challenge> findChallengesByUser(@Param("user") Utilisateur user);

    /**
     * Recherche les challenges actifs pour un utilisateur à une date donnée.
     * Un challenge est considéré comme "actif" si l'utilisateur y participe ET 
     * que la date du jour se situe entre la date de début et la date de fin du challenge.
     *
     * @param userId L'identifiant de l'utilisateur participant
     * @param now    La date de référence (généralement la date du jour)
     * @return La liste des challenges actuellement en cours pour cet utilisateur
     */
    @Query("SELECT p.challenge FROM ParticipationChallenge p WHERE p.utilisateur.id = :userId AND p.challenge.dateDebut <= :now AND p.challenge.dateFin >= :now")
    List<Challenge> findActiveChallengesByUserId(@Param("userId") Long userId, @Param("now") LocalDate now);
    
}