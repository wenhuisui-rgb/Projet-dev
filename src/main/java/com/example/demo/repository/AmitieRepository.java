package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.*;

public interface AmitieRepository extends JpaRepository<Amitie, Long> {

    List<Amitie> findByUtilisateurReceveurAndStatut(
            Utilisateur receveur,
            StatutAmitie statut
    );

    List<Amitie> findByUtilisateurDemandeurAndStatut(
            Utilisateur demandeur,
            StatutAmitie statut
    );

    @Query("""
        SELECT a FROM Amitie a
        WHERE a.statut = 'ACCEPTEE'
        AND (a.utilisateurDemandeur.id = :id OR a.utilisateurReceveur.id = :id)
    """)
    List<Amitie> findAmis(@Param("id") Long id);

    Optional<Amitie> findByUtilisateurDemandeurAndUtilisateurReceveur(
            Utilisateur demandeur,
            Utilisateur receveur
    );

    Optional<Amitie> findTopByUtilisateurDemandeurAndUtilisateurReceveurOrderByAmitieIDDesc(
            Utilisateur demandeur,
            Utilisateur receveur
    );

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Amitie a
        WHERE (a.utilisateurDemandeur = :u1 AND a.utilisateurReceveur = :u2)
           OR (a.utilisateurDemandeur = :u2 AND a.utilisateurReceveur = :u1)
    """)
    void deleteRelation(@Param("u1") Utilisateur u1,
                        @Param("u2") Utilisateur u2);
}