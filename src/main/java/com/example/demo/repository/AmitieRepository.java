package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AmitieRepository extends JpaRepository<Amitie, Long> {

    List<Amitie> findByUtilisateurReceveurAndStatut(
            Utilisateur receveur,
            StatutAmitie statut
    );

    List<Amitie> findByUtilisateurDemandeurAndStatut(
            Utilisateur demandeur,
            StatutAmitie statut
    );

}