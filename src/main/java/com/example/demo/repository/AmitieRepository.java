package com.example.demo.repository;

import com.example.demo.model.Amitie;
import com.example.demo.model.StatutAmitie;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AmitieRepository extends JpaRepository<Amitie, Long> {

    // Récupérer toutes les amitiés d’un utilisateur (en tant que demandeur ou destinataire)
    List<Amitie> findByUtilisateurDemandeurOrUtilisateurReceveur(Utilisateur demandeur, Utilisateur receveur);

    // Rechercher une demande spécifique entre deux utilisateurs
    Optional<Amitie> findByUtilisateurDemandeurAndUtilisateurReceveur(Utilisateur demandeur, Utilisateur receveur);

    // Récupérer toutes les amitiés acceptées d’un utilisateur
    List<Amitie> findByStatutAndUtilisateurDemandeurOrUtilisateurReceveur(StatutAmitie statut, Utilisateur demandeur, Utilisateur receveur);
}
