package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AmitieService {

    private final AmitieRepository amitieRepository;

    public AmitieService(AmitieRepository amitieRepository) {
        this.amitieRepository = amitieRepository;
    }

    // ENVOYER DEMANDE
    public String envoyerDemande(Utilisateur demandeur, Utilisateur receveur) {

        if (demandeur == null || receveur == null) return "ERROR";

        if (demandeur.getId().equals(receveur.getId())) return "SELF";

        Optional<Amitie> existing =
                amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(demandeur, receveur);

        Optional<Amitie> inverse =
                amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(receveur, demandeur);

        Optional<Amitie> finalExisting = existing.isPresent() ? existing : inverse;

        if (finalExisting.isPresent()) {
            Amitie a = finalExisting.get();

            if (a.getStatut() == StatutAmitie.EN_ATTENTE) return "PENDING";
            if (a.getStatut() == StatutAmitie.ACCEPTEE) return "ALREADY_FRIEND";

            a.setStatut(StatutAmitie.EN_ATTENTE);
            amitieRepository.save(a);

            return "SENT_AGAIN";
        }

        Amitie amitie = new Amitie();
        amitie.setUtilisateurDemandeur(demandeur);
        amitie.setUtilisateurReceveur(receveur);
        amitie.setStatut(StatutAmitie.EN_ATTENTE);

        amitieRepository.save(amitie);

        return "SENT";
    }

    // ACCEPTER
    public void accepterDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        amitieRepository.save(amitie);
    }

    // REFUSER
    public void refuserDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.REFUSEE);
        amitieRepository.save(amitie);
    }

    // GET BY ID
    public Amitie getById(Long id) {
        return amitieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amitié introuvable"));
    }

    // DEMANDES RECUES
    public List<Amitie> getDemandesRecues(Utilisateur user) {
        return Optional.ofNullable(
                amitieRepository.findByUtilisateurReceveurAndStatut(user, StatutAmitie.EN_ATTENTE)
        ).orElse(new ArrayList<>());
    }

    // DEMANDES ENVOYEES IDS
    public List<Long> getDemandesEnvoyeesIds(Utilisateur user) {
        return amitieRepository
                .findByUtilisateurDemandeurAndStatut(user, StatutAmitie.EN_ATTENTE)
                .stream()
                .map(a -> a.getUtilisateurReceveur().getId())
                .toList();
    }

    // AMIS
    public List<Utilisateur> getAmis(Utilisateur user) {
        List<Amitie> relations = amitieRepository.findAmis(user.getId());

        return relations.stream()
                .map(a -> a.getUtilisateurDemandeur().getId().equals(user.getId())
                        ? a.getUtilisateurReceveur()
                        : a.getUtilisateurDemandeur())
                .toList();
    }

    // SUPPRESSION AMI
    public void supprimerAmi(Utilisateur u1, Utilisateur u2) {
        amitieRepository.deleteRelation(u1, u2);
    }
}