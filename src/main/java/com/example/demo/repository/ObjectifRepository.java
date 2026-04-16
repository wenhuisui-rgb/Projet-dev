package com.example.demo.repository;

import com.example.demo.model.Objectif;
import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interface de repository pour l'entité {@link Objectif}.
 * Gère la persistance des objectifs personnels fixés par les utilisateurs.
 */
@Repository
public interface ObjectifRepository extends JpaRepository<Objectif, Long> {

    /**
     * Récupère la liste complète de tous les objectifs d'un utilisateur.
     *
     * @param utilisateur L'utilisateur concerné
     * @return La liste de ses objectifs
     */
    List<Objectif> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Filtre les objectifs d'un utilisateur en fonction du type de sport.
     *
     * @param utilisateur L'utilisateur concerné
     * @param typeSport   Le sport ciblé (ex: COURSE, VELO)
     * @return La liste des objectifs correspondant à ce sport
     */
    List<Objectif> findByUtilisateurAndTypeSport(Utilisateur utilisateur, TypeSport typeSport);

    /**
     * Filtre les objectifs d'un utilisateur en fonction de leur périodicité.
     *
     * @param utilisateur L'utilisateur concerné
     * @param periode     La période ciblée (ex: HEBDOMADAIRE, MENSUEL)
     * @return La liste des objectifs correspondant à cette période
     */
    List<Objectif> findByUtilisateurAndPeriode(Utilisateur utilisateur, Periode periode);
}