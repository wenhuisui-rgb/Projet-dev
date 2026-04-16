package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service gérant les relations d'amitié entre les utilisateurs.
 * <p>
 * Il permet la création de demandes d'amitié, leur acceptation ou refus,
 * et la récupération de la liste d'amis selon le statut (EN_ATTENTE, ACCEPTEE).
 */
@Service
public class AmitieService {

    private final AmitieRepository amitieRepository;

    public AmitieService(AmitieRepository amitieRepository) {
        this.amitieRepository = amitieRepository;
    }

    /**
     * Envoie une demande d'amitié d'un utilisateur à un autre.
     * Gère les cas où la demande a déjà été envoyée, acceptée ou si l'utilisateur s'ajoute lui-même.
     *
     * @param demandeur L'utilisateur qui initie la demande
     * @param receveur  L'utilisateur qui reçoit la demande
     * @return Un code sous forme de chaîne de caractères représentant le résultat de l'opération
     * ("SELF", "PENDING", "ALREADY_FRIEND", "SENT_AGAIN", "SENT")
     */
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

    /**
     * Accepte une demande d'amitié en attente.
     *
     * @param amitie L'entité amitié concernée
     * @return L'amitié mise à jour avec le statut ACCEPTEE
     */
    public Amitie accepterDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.ACCEPTEE);
        return amitieRepository.save(amitie);
    }

    /**
     * Refuse une demande d'amitié en attente.
     *
     * @param amitie L'entité amitié concernée
     * @return L'amitié mise à jour avec le statut REFUSEE
     */
    public Amitie refuserDemande(Amitie amitie) {
        amitie.setStatut(StatutAmitie.REFUSEE);
        return amitieRepository.save(amitie);
    }

    /**
     * Récupère une entité Amitie par son identifiant.
     *
     * @param id L'identifiant de la relation d'amitié
     * @return L'entité Amitie trouvée
     * @throws RuntimeException Si l'amitié n'existe pas en base
     */
    public Amitie getById(Long id) {
        return amitieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amitié introuvable"));
    }

    /**
     * Récupère la liste des demandes d'amitié reçues et en attente pour un utilisateur.
     *
     * @param user L'utilisateur receveur
     * @return Une liste d'amitiés avec le statut EN_ATTENTE
     */
    public List<Amitie> getDemandesRecues(Utilisateur user) {
        return amitieRepository.findByUtilisateurReceveurAndStatut(
                user,
                StatutAmitie.EN_ATTENTE
        );
    }

    /**
     * Récupère la liste des ID des utilisateurs à qui une demande a été envoyée et qui est en attente.
     *
     * @param user L'utilisateur demandeur
     * @return Une liste contenant les identifiants des receveurs
     */
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

    /**
     * Récupère la liste complète des amis (statut ACCEPTEE) d'un utilisateur,
     * qu'il soit à l'origine de la demande ou qu'il l'ait reçue.
     *
     * @param utilisateur L'utilisateur concerné
     * @return Une liste d'entités {@link Utilisateur} représentant ses amis
     */
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