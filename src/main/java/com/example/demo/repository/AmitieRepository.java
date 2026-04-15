package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AmitieRepository extends JpaRepository<Amitie, Long> {

    Optional<Amitie> findByUtilisateurDemandeurAndUtilisateurReceveur(
            Utilisateur demandeur,
            Utilisateur receveur
    );

    List<Amitie> findByUtilisateurReceveurAndStatut(
            Utilisateur receveur,
            StatutAmitie statut
    );

    List<Amitie> findByUtilisateurDemandeurAndStatut(
            Utilisateur demandeur,
            StatutAmitie statut
    );
}