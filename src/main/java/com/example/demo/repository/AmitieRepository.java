package com.example.demo.repository;

import com.example.demo.model.Amitie;
import com.example.demo.model.StatutAmitie;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AmitieRepository extends JpaRepository<Amitie, Long> {

   
    List<Amitie> findByUtilisateurDemandeurOrUtilisateurReceveur(
            Utilisateur demandeur,
            Utilisateur receveur
    );

    Optional<Amitie> findByUtilisateurDemandeurAndUtilisateurReceveur(
            Utilisateur demandeur,
            Utilisateur receveur
    );

    @Query("SELECT a FROM Amitie a " +
           "WHERE a.statut = :statut " +
           "AND (a.utilisateurDemandeur = :user OR a.utilisateurReceveur = :user)")
    List<Amitie> findAmitiesByStatutAndUtilisateur(
            @Param("statut") StatutAmitie statut,
            @Param("user") Utilisateur user
    );
}