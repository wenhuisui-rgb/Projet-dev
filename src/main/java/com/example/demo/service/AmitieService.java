package com.example.demo.service;

import com.example.demo.model.Amitie;
import com.example.demo.model.StatutAmitie;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.AmitieRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AmitieService {

    private final AmitieRepository amitieRepository;

    public AmitieService(AmitieRepository amitieRepository) {
        this.amitieRepository = amitieRepository;
    }

    // Envoyer une demande d'amitié
    public Amitie envoyerDemande(Utilisateur demandeur, Utilisateur receveur) {
        Optional<Amitie> exist = amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(demandeur, receveur);
        if (exist.isPresent()) {
            return exist.get(); 
        }
        Amitie amitie = new Amitie();
        amitie.setUtilisateurDemandeur(demandeur);
        amitie.setUtilisateurReceveur(receveur);
        amitie.setStatut(StatutAmitie.EN_ATTENTE);
        amitie.setDateDemande(java.time.LocalDate.now());
        return amitieRepository.save(amitie);
    }

    // Accepter une demande
    public Amitie accepterDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        return amitieRepository.save(amitie);
    }

    // Refuser une demande
    public Amitie refuserDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.REFUSEE);
        return amitieRepository.save(amitie);
    }

    // Annuler une demande
    public Amitie annulerDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ANNULEE);
        return amitieRepository.save(amitie);
    }

    // Rompre une amitié existante
    public void rompreAmitie(Amitie amitie) {
        amitieRepository.delete(amitie);
    }

    // Lister toutes les amitiés d’un utilisateur
    public List<Amitie> getAmities(Utilisateur utilisateur) {
        return amitieRepository.findByUtilisateurDemandeurOrUtilisateurReceveur(utilisateur, utilisateur);
    }

    // Lister uniquement les amitiés acceptées
    public List<Amitie> getAmitiesAcceptees(Utilisateur utilisateur) {
        return amitieRepository.findByStatutAndUtilisateurDemandeurOrUtilisateurReceveur(
                StatutAmitie.ACCEPTEE, utilisateur, utilisateur);
    }
}
