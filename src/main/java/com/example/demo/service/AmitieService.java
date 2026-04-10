package com.example.demo.service;

import com.example.demo.model.Amitie;
import com.example.demo.model.StatutAmitie;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.AmitieRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AmitieService {

    private final AmitieRepository amitieRepository;

    public AmitieService(AmitieRepository amitieRepository) {
        this.amitieRepository = amitieRepository;
    }

    /**
     * Statuts qui bloquent toute nouvelle demande
     */
    private boolean statutBloquant(StatutAmitie statut) {
        return statut == StatutAmitie.EN_ATTENTE
                || statut == StatutAmitie.ACCEPTEE;
    }

    /**
     * Envoyer une demande d'amitié
     */
    public String envoyerDemande(Utilisateur demandeur, Utilisateur receveur) {

        if (demandeur == null || receveur == null) {
            return "Utilisateur introuvable.";
        }

        var existDirecte = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(demandeur, receveur);

        if (existDirecte.isPresent()) {
            Amitie amitie = existDirecte.get();

            if (statutBloquant(amitie.getStatut())) {
                return "Vous avez déjà une demande en attente ou acceptée.";
            }

            
            amitie.setStatut(StatutAmitie.EN_ATTENTE);
            amitie.setDateDemande(LocalDate.now());
            amitieRepository.save(amitie);

            return "Nouvelle demande envoyée avec succès !";
        }

       
        var existInverse = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(receveur, demandeur);

        if (existInverse.isPresent()) {
            Amitie amitieInverse = existInverse.get();

            if (statutBloquant(amitieInverse.getStatut())) {
                return "Cet utilisateur vous a déjà envoyé une demande en attente ou acceptée.";
            }

           
            amitieInverse.setStatut(StatutAmitie.EN_ATTENTE);
            amitieInverse.setDateDemande(LocalDate.now());
            amitieRepository.save(amitieInverse);

            return "Nouvelle demande envoyée avec succès !";
        }

        
        Amitie amitie = new Amitie();
        amitie.setUtilisateurDemandeur(demandeur);
        amitie.setUtilisateurReceveur(receveur);
        amitie.setStatut(StatutAmitie.EN_ATTENTE);
        amitie.setDateDemande(LocalDate.now());

        amitieRepository.save(amitie);

        return "Demande envoyée avec succès !";
    }

    /**
     * Accepter une demande
     */
    public Amitie accepterDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        return amitieRepository.save(amitie);
    }

    /**
     * Refuser une demande
     */
    public Amitie refuserDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.REFUSEE);
        return amitieRepository.save(amitie);
    }

    
    //Annuler une demande
    
    public Amitie annulerDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ANNULEE);
        return amitieRepository.save(amitie);
    }

    
    //Supprimer une amitié
    
    public void rompreAmitie(Amitie amitie) {
        amitieRepository.delete(amitie);
    }

    public List<Amitie> getAmities(Utilisateur utilisateur) {
        return amitieRepository.findByUtilisateurDemandeurOrUtilisateurReceveur(
                utilisateur,
                utilisateur
        );
    }

   
    public List<Amitie> getAmitiesAcceptees(Utilisateur utilisateur) {
        return amitieRepository.findAmitiesByStatutAndUtilisateur(
                StatutAmitie.ACCEPTEE,
                utilisateur
        );
    }

    
    public Amitie getById(Long id) {
        return amitieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amitié introuvable"));
    }
}