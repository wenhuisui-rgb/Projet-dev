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

    // Méthode utilitaire pour savoir si un statut bloque une nouvelle demande
    private boolean statutBloquant(StatutAmitie statut) {
        return statut == StatutAmitie.EN_ATTENTE || statut == StatutAmitie.ACCEPTEE;
    }

    // Envoyer une demande d'amitié
    public String envoyerDemande(Utilisateur demandeur, Utilisateur receveur) {
        // Vérifier si une demande existe déjà dans le sens demandeur -> receveur
        Optional<Amitie> existDirecte = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(demandeur, receveur);

        if (existDirecte.isPresent()) {
            Amitie amitieExistante = existDirecte.get();
            if (statutBloquant(amitieExistante.getStatut())) {
                return "Vous avez déjà une demande en attente ou acceptée.";
            } else {
                
                return "Votre précédente demande a été " + amitieExistante.getStatut().name() + 
                       ". Vous pouvez renvoyer une nouvelle demande.";
            }
        }

        // Vérifier si une demande existe dans le sens receveur -> demandeur
        Optional<Amitie> existInverse = amitieRepository
                .findByUtilisateurDemandeurAndUtilisateurReceveur(receveur, demandeur);

        if (existInverse.isPresent() && statutBloquant(existInverse.get().getStatut())) {
            return "L'utilisateur vous a déjà envoyé une demande d'amitié qui est en attente ou acceptée.";
        }

        // Créer la nouvelle demande si aucune condition bloquante
        Amitie amitie = new Amitie();
        amitie.setUtilisateurDemandeur(demandeur);
        amitie.setUtilisateurReceveur(receveur);
        amitie.setStatut(StatutAmitie.EN_ATTENTE);
        amitie.setDateDemande(java.time.LocalDate.now());
        amitieRepository.save(amitie);

        return "Demande envoyée avec succès !";
    }

   
    public Amitie accepterDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        return amitieRepository.save(amitie);
    }

    public Amitie refuserDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.REFUSEE);
        return amitieRepository.save(amitie);
    }

    public Amitie annulerDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ANNULEE);
        return amitieRepository.save(amitie);
    }

    public void rompreAmitie(Amitie amitie) {
        amitieRepository.delete(amitie);
    }

    public List<Amitie> getAmities(Utilisateur utilisateur) {
        return amitieRepository.findByUtilisateurDemandeurOrUtilisateurReceveur(utilisateur, utilisateur);
    }

    public List<Amitie> getAmitiesAcceptees(Utilisateur utilisateur) {
        return amitieRepository.findByStatutAndUtilisateurDemandeurOrUtilisateurReceveur(
                StatutAmitie.ACCEPTEE, utilisateur, utilisateur);
    }
}