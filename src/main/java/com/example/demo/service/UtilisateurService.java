package com.example.demo.service;

import com.example.demo.model.Amitie;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional // Garantit que les méthodes s'exécutent dans une transaction. En cas d'exception, un rollback automatique est effectué
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // ==========================================
    // 1. Gestion du compte utilisateur
    // ==========================================

    /**
     * Mettre à jour le profil de l'utilisateur (mettreAJourProfil)
     */
    public Utilisateur mettreAJourProfil(Long utilisateurId, Utilisateur donneesMisesAJour) {
        Utilisateur utilisateurExistant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Mettre à jour uniquement les champs autorisés
        utilisateurExistant.setAge(donneesMisesAJour.getAge());
        utilisateurExistant.setTaille(donneesMisesAJour.getTaille());
        utilisateurExistant.setPoids(donneesMisesAJour.getPoids());
        utilisateurExistant.setNiveauPratique(donneesMisesAJour.getNiveauPratique());
        utilisateurExistant.setPreferencesSports(donneesMisesAJour.getPreferencesSports());

        // La méthode save mettra automatiquement à jour l'entité existante
        return utilisateurRepository.save(utilisateurExistant);
    }

    // ==========================================
    // 2. Système d'amitié et interactions sociales
    // ==========================================

    /**
     * Envoyer une demande d'ami (envoyerDemandeAmi)
     */
    public Amitie envoyerDemandeAmi(Long demandeurId, Long cibleId) {
        return null;
    }

    /**
     * Traiter une demande d'ami (traiterDemande)
     */
    public void traiterDemande(Long amitieId, Long receveurId, Boolean accepter) {

    }

    /**
     * Obtenir la liste d'amis (obtenirListeAmis)
     */
    public List<Utilisateur> obtenirListeAmis(Long utilisateurId) {

        return null;
    }
}