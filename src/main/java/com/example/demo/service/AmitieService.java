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

    public String envoyerDemande(Utilisateur demandeur, Utilisateur receveur) {

        if (demandeur.getId().equals(receveur.getId())) {
            return "SELF";
        }

        Optional<Amitie> existing =
                amitieRepository.findByUtilisateurDemandeurAndUtilisateurReceveur(
                        demandeur, receveur);

        if (existing.isPresent()) {
            Amitie a = existing.get();

            if (a.getStatut() == StatutAmitie.EN_ATTENTE)
                return "PENDING";

            if (a.getStatut() == StatutAmitie.ACCEPTEE)
                return "ALREADY_FRIEND";

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

    public Amitie accepterDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        return amitieRepository.save(amitie);
    }

    public Amitie refuserDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.REFUSEE);
        return amitieRepository.save(amitie);
    }

    public Amitie getById(Long id) {
        return amitieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amitié introuvable"));
    }

    public List<Amitie> getDemandesRecues(Utilisateur user) {
        return amitieRepository.findByUtilisateurReceveurAndStatut(
                user,
                StatutAmitie.EN_ATTENTE
        );
    }

    public List<Long> getDemandesEnvoyeesIds(Utilisateur user) {
        return amitieRepository
                .findByUtilisateurDemandeurAndStatut(
                        user,
                        StatutAmitie.EN_ATTENTE
                )
                .stream()
                .map(a -> a.getUtilisateurReceveur().getId())
                .toList();
    }

    public List<Utilisateur> getAmis(Utilisateur utilisateur) {
        List<Utilisateur> amis = new ArrayList<>();
        
        List<Amitie> asDemandeur = amitieRepository.findByUtilisateurDemandeurAndStatut(utilisateur, StatutAmitie.ACCEPTEE);
        for (Amitie a : asDemandeur) {
            amis.add(a.getUtilisateurReceveur());
        }
        
        List<Amitie> asReceveur = amitieRepository.findByUtilisateurReceveurAndStatut(utilisateur, StatutAmitie.ACCEPTEE);
        for (Amitie a : asReceveur) {
            amis.add(a.getUtilisateurDemandeur());
        }
        
        return amis;
    }
}