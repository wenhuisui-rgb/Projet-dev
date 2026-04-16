package com.example.demo.repository;

import com.example.demo.model.Activite;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface de repository pour l'entité {@link Activite}.
 * <p>
 * Gère l'accès aux données des activités sportives enregistrées par les utilisateurs.
 * Contient de nombreuses requêtes d'agrégation personnalisées (sommes) pour alimenter
 * les statistiques et les graphiques du tableau de bord.
 */
@Repository
public interface ActiviteRepository extends JpaRepository<Activite, Long> {

       /**
        * Récupère toutes les activités d'un utilisateur, de la plus récente à la plus ancienne.
        * @param utilisateur L'utilisateur ciblé
        * @return La liste des activités triées
        */
       List<Activite> findByUtilisateurOrderByDateActiviteDesc(Utilisateur utilisateur);

       /**
        * Récupère les activités d'un utilisateur sur une période précise, triées par date décroissante.
        * @param utilisateur L'utilisateur ciblé
        * @param debut Date de début
        * @param fin Date de fin
        * @return La liste des activités dans la période
        */
       List<Activite> findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(
              Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin);

       /**
        * Filtre les activités d'un utilisateur par type de sport, triées par date décroissante.
        * @param utilisateur L'utilisateur ciblé
        * @param typeSport Le sport spécifique
        * @return La liste des activités correspondantes
        */
       List<Activite> findByUtilisateurAndTypeSportOrderByDateActiviteDesc(
              Utilisateur utilisateur, TypeSport typeSport);

       /**
        * Récupère uniquement les 5 dernières activités d'un utilisateur (utile pour un aperçu rapide).
        * @param utilisateur L'utilisateur ciblé
        * @return Les 5 dernières activités
        */
       List<Activite> findTop5ByUtilisateurOrderByDateActiviteDesc(Utilisateur utilisateur);

       /**
        * Compte le nombre total d'activités enregistrées par un utilisateur.
        * @param utilisateur L'utilisateur ciblé
        * @return Le nombre total
        */
       long countByUtilisateur(Utilisateur utilisateur);

       /**
        * Compte le nombre d'activités enregistrées par un utilisateur sur une période précise.
        * @param utilisateur L'utilisateur ciblé
        * @param debut Date de début
        * @param fin Date de fin
        * @return Le nombre d'activités
        */
       long countByUtilisateurAndDateActiviteBetween(Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin);

       // =========================================================================================
       // REQUÊTES D'AGRÉGATION GLOBALEMENT (COALESCE évite de retourner null si aucune donnée)
       // =========================================================================================

       /**
        * Calcule la distance totale parcourue par l'utilisateur.
        * @param utilisateur L'utilisateur ciblé
        * @return La distance totale
        */
       @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activite a WHERE a.utilisateur = :utilisateur")
       Float getDistanceTotale(@Param("utilisateur") Utilisateur utilisateur);

       /**
        * Calcule la durée totale passée par l'utilisateur.
        * @param utilisateur L'utilisateur ciblé
        * @return La durée totale
        */
       @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a WHERE a.utilisateur = :utilisateur")
       Integer getDureeTotale(@Param("utilisateur") Utilisateur utilisateur);

       /**
        * Calcule les calories totales brûlées par l'utilisateur.
        * @param utilisateur L'utilisateur ciblé
        * @return Les calories totales
        */
       @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a WHERE a.utilisateur = :utilisateur")
       Float getCaloriesTotales(@Param("utilisateur") Utilisateur utilisateur);

       // =========================================================================================
       // REQUÊTES D'AGRÉGATION PAR PÉRIODE ET/OU PAR SPORT UNIQUE
       // =========================================================================================

       /**
        * Calcule la distance pour un sport et une période.
        * @param utilisateur L'utilisateur ciblé
        * @param typeSport Le sport
        * @param debut Date de début
        * @param fin Date de fin
        * @return La distance calculée
        */
       @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.typeSport = :typeSport " +
              "AND a.dateActivite BETWEEN :debut AND :fin")
       Float getDistanceBySportAndPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                          @Param("typeSport") TypeSport typeSport,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin);

       /**
        * Calcule la distance totale pour une période.
        * @param utilisateur L'utilisateur ciblé
        * @param debut Date de début
        * @param fin Date de fin
        * @return La distance calculée
        */
       @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.dateActivite BETWEEN :debut AND :fin")
       Float getDistanceByPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                   @Param("debut") LocalDateTime debut,
                                   @Param("fin") LocalDateTime fin);

       /**
        * Calcule la durée pour un sport et une période.
        * @param utilisateur L'utilisateur ciblé
        * @param typeSport Le sport
        * @param debut Date de début
        * @param fin Date de fin
        * @return La durée calculée
        */
       @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.typeSport = :typeSport " +
              "AND a.dateActivite BETWEEN :debut AND :fin")
       Integer getDureeBySportAndPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                          @Param("typeSport") TypeSport typeSport,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin);

       /**
        * Calcule la durée totale pour une période.
        * @param utilisateur L'utilisateur ciblé
        * @param debut Date de début
        * @param fin Date de fin
        * @return La durée calculée
        */
       @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.dateActivite BETWEEN :debut AND :fin")
       Integer getDureeByPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                   @Param("debut") LocalDateTime debut,
                                   @Param("fin") LocalDateTime fin);

       /**
        * Calcule les calories pour un sport et une période.
        * @param utilisateur L'utilisateur ciblé
        * @param typeSport Le sport
        * @param debut Date de début
        * @param fin Date de fin
        * @return Les calories calculées
        */
       @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.typeSport = :typeSport " +
              "AND a.dateActivite BETWEEN :debut AND :fin")
       Float getCaloriesBySportAndPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                          @Param("typeSport") TypeSport typeSport,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin);

       /**
        * Calcule les calories totales pour une période.
        * @param utilisateur L'utilisateur ciblé
        * @param debut Date de début
        * @param fin Date de fin
        * @return Les calories calculées
        */
       @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.dateActivite BETWEEN :debut AND :fin")
       Float getCaloriesByPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                   @Param("debut") LocalDateTime debut,
                                   @Param("fin") LocalDateTime fin);
       
       /**
        * Récupère les activités d'un utilisateur sous forme paginée (pour optimiser le chargement des listes longues).
        *
        * @param utilisateur L'utilisateur concerné
        * @param pageable    Les paramètres de pagination (numéro de page, taille de la page)
        * @return Une page contenant la liste des activités
        */
       Page<Activite> findByUtilisateurOrderByDateActiviteDesc(Utilisateur utilisateur, Pageable pageable);

       // =========================================================================================
       // REQUÊTES D'AGRÉGATION MULTI-SPORTS (Utilisation de la clause IN)
       // =========================================================================================

       /**
        * Calcule la durée totale des activités d'un utilisateur sur une période, filtrée par une liste de sports.
        * Si la liste de sports est nulle, inclut tous les sports.
        * @param utilisateur L'utilisateur
        * @param debut Date de début
        * @param fin Date de fin
        * @param sports La liste des sports
        * @return La durée
        */
       @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a " +
       "WHERE a.utilisateur = :utilisateur " +
       "AND a.dateActivite BETWEEN :debut AND :fin " +
       "AND ((:sports) IS NULL OR a.typeSport IN :sports)")
       Integer getDureeByPeriodAndSports(@Param("utilisateur") Utilisateur utilisateur,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin,
                                          @Param("sports") List<TypeSport> sports);

       /**
        * Calcule les calories totales brûlées par un utilisateur sur une période, filtrées par une liste de sports.
        * Si la liste de sports est nulle, inclut tous les sports.
        * @param utilisateur L'utilisateur
        * @param debut Date de début
        * @param fin Date de fin
        * @param sports La liste des sports
        * @return Les calories
        */
       @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a " +
              "WHERE a.utilisateur = :utilisateur " +
              "AND a.dateActivite BETWEEN :debut AND :fin " +
              "AND ((:sports) IS NULL OR a.typeSport IN :sports)")
       Float getCaloriesByPeriodAndSports(@Param("utilisateur") Utilisateur utilisateur,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin,
                                          @Param("sports") List<TypeSport> sports);
}