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

@Repository
public interface ActiviteRepository extends JpaRepository<Activite, Long> {

    List<Activite> findByUtilisateurOrderByDateActiviteDesc(Utilisateur utilisateur);

    List<Activite> findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(
        Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin);

    List<Activite> findByUtilisateurAndTypeSportOrderByDateActiviteDesc(
        Utilisateur utilisateur, TypeSport typeSport);

    List<Activite> findTop5ByUtilisateurOrderByDateActiviteDesc(Utilisateur utilisateur);

    long countByUtilisateur(Utilisateur utilisateur);

    long countByUtilisateurAndDateActiviteBetween(Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activite a WHERE a.utilisateur = :utilisateur")
    Float getDistanceTotale(@Param("utilisateur") Utilisateur utilisateur);

    @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a WHERE a.utilisateur = :utilisateur")
    Integer getDureeTotale(@Param("utilisateur") Utilisateur utilisateur);

    @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a WHERE a.utilisateur = :utilisateur")
    Float getCaloriesTotales(@Param("utilisateur") Utilisateur utilisateur);

    @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activite a " +
           "WHERE a.utilisateur = :utilisateur " +
           "AND a.typeSport = :typeSport " +
           "AND a.dateActivite BETWEEN :debut AND :fin")
    Float getDistanceBySportAndPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                       @Param("typeSport") TypeSport typeSport,
                                       @Param("debut") LocalDateTime debut,
                                       @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activite a " +
           "WHERE a.utilisateur = :utilisateur " +
           "AND a.dateActivite BETWEEN :debut AND :fin")
    Float getDistanceByPeriod(@Param("utilisateur") Utilisateur utilisateur,
                              @Param("debut") LocalDateTime debut,
                              @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a " +
           "WHERE a.utilisateur = :utilisateur " +
           "AND a.typeSport = :typeSport " +
           "AND a.dateActivite BETWEEN :debut AND :fin")
    Integer getDureeBySportAndPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                     @Param("typeSport") TypeSport typeSport,
                                     @Param("debut") LocalDateTime debut,
                                     @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(a.duree), 0) FROM Activite a " +
           "WHERE a.utilisateur = :utilisateur " +
           "AND a.dateActivite BETWEEN :debut AND :fin")
    Integer getDureeByPeriod(@Param("utilisateur") Utilisateur utilisateur,
                             @Param("debut") LocalDateTime debut,
                             @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a " +
           "WHERE a.utilisateur = :utilisateur " +
           "AND a.typeSport = :typeSport " +
           "AND a.dateActivite BETWEEN :debut AND :fin")
    Float getCaloriesBySportAndPeriod(@Param("utilisateur") Utilisateur utilisateur,
                                      @Param("typeSport") TypeSport typeSport,
                                      @Param("debut") LocalDateTime debut,
                                      @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(a.calories), 0) FROM Activite a " +
           "WHERE a.utilisateur = :utilisateur " +
           "AND a.dateActivite BETWEEN :debut AND :fin")
    Float getCaloriesByPeriod(@Param("utilisateur") Utilisateur utilisateur,
                              @Param("debut") LocalDateTime debut,
                              @Param("fin") LocalDateTime fin);
}