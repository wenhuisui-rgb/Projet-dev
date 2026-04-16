package com.example.demo.service;

import com.example.demo.model.Objectif;
import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ObjectifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service gérant les objectifs personnels des utilisateurs.
 * <p>
 * Permet de définir des cibles (ex: courir 50km ce mois-ci) et de calculer
 * dynamiquement la progression en interrogeant l'historique des {@link com.example.demo.model.Activite}.
 */
@Service
public class ObjectifService {

    @Autowired
    private ObjectifRepository objectifRepository;

    @Autowired
    private ActiviteRepository activiteRepository;

    /**
     * Crée un nouvel objectif et l'associe à un utilisateur.
     *
     * @param objectif    L'objet contenant les paramètres de l'objectif
     * @param utilisateur L'utilisateur propriétaire de l'objectif
     * @return L'objectif sauvegardé
     */
    @Transactional
    public Objectif creerObjectif(Objectif objectif, Utilisateur utilisateur) {
        objectif.setUtilisateur(utilisateur);
        return objectifRepository.save(objectif);
    }

    /**
     * Récupère la liste de tous les objectifs définis par un utilisateur.
     *
     * @param utilisateur L'utilisateur concerné
     * @return Une liste de ses objectifs
     */
    @Transactional(readOnly = true)
    public List<Objectif> getObjectifsParUtilisateur(Utilisateur utilisateur) {
        return objectifRepository.findByUtilisateur(utilisateur);
    }

    /**
     * Récupère un objectif spécifique par son identifiant.
     *
     * @param id L'identifiant de l'objectif
     * @return L'objectif correspondant, ou {@code null} s'il n'existe pas
     */
    @Transactional(readOnly = true)
    public Objectif getObjectifParId(Long id) {
        return objectifRepository.findById(id).orElse(null);
    }

    /**
     * Met à jour les paramètres d'un objectif existant.
     *
     * @param id              L'identifiant de l'objectif à modifier
     * @param objectifModifie L'objet contenant les nouvelles valeurs
     * @return L'objectif mis à jour, ou {@code null} si introuvable
     */
    @Transactional
    public Objectif updateObjectif(Long id, Objectif objectifModifie) {
        Objectif existant = getObjectifParId(id);
        if (existant != null) {
            existant.setDescription(objectifModifie.getDescription());
            existant.setTypeSport(objectifModifie.getTypeSport());
            existant.setCible(objectifModifie.getCible());
            existant.setUnite(objectifModifie.getUnite());
            existant.setPeriode(objectifModifie.getPeriode());
            return objectifRepository.save(existant);
        }
        return null;
    }

    /**
     * Supprime un objectif par son identifiant.
     *
     * @param id L'identifiant de l'objectif à supprimer
     */
    @Transactional
    public void supprimerObjectif(Long id) {
        objectifRepository.deleteById(id);
    }

    /**
     * Calcule la progression actuelle d'un objectif en fonction des activités réalisées.
     * Interroge la base de données selon l'unité (KM, MINUTES, KCAL), le sport et la période définis.
     *
     * @param objectif    L'objectif à évaluer
     * @param utilisateur L'utilisateur concerné
     * @return La valeur numérique de la progression actuelle (toujours >= 0)
     */
    @Transactional(readOnly = true)
    public Float getProgressionObjectif(Objectif objectif, Utilisateur utilisateur) {
        LocalDateTime debut = getDateDebutDateTime(objectif);
        LocalDateTime fin = getDateFinDateTime(objectif);
        Float resultat = 0f;

        if (Unite.KM.equals(objectif.getUnite())) { 
            if (objectif.getTypeSport() != null) {
                resultat = activiteRepository.getDistanceBySportAndPeriod(utilisateur, objectif.getTypeSport(), debut, fin);
            } else {
                resultat = activiteRepository.getDistanceByPeriod(utilisateur, debut, fin);
            }
        } else if (Unite.MINUTES.equals(objectif.getUnite())) {
            Integer duree;
            if (objectif.getTypeSport() != null) {
                duree = activiteRepository.getDureeBySportAndPeriod(utilisateur, objectif.getTypeSport(), debut, fin);
            } else {
                duree = activiteRepository.getDureeByPeriod(utilisateur, debut, fin);
            }
            resultat = (duree != null) ? duree.floatValue() : 0f;
        } else if (Unite.KCAL.equals(objectif.getUnite())) {
            if (objectif.getTypeSport() != null) {
                resultat = activiteRepository.getCaloriesBySportAndPeriod(utilisateur, objectif.getTypeSport(), debut, fin);
            } else {
                resultat = activiteRepository.getCaloriesByPeriod(utilisateur, debut, fin);
            }
        }
        
        return resultat != null ? resultat : 0f;
    }

    /**
     * Calcule le pourcentage d'accomplissement de l'objectif.
     *
     * @param objectif    L'objectif à évaluer
     * @param utilisateur L'utilisateur concerné
     * @return Le pourcentage complété (ex: 50.0 pour 50%). Retourne 0 si la cible est nulle.
     */
    @Transactional(readOnly = true)
    public Float getPourcentageObjectif(Objectif objectif, Utilisateur utilisateur) {
        Float progression = getProgressionObjectif(objectif, utilisateur);
        if (objectif.getCible() == null || objectif.getCible() == 0) {
            return 0f;
        }
        return (progression / objectif.getCible()) * 100;
    }

    /**
     * Vérifie si l'objectif a été entièrement atteint.
     *
     * @param objectif    L'objectif à évaluer
     * @param utilisateur L'utilisateur concerné
     * @return {@code true} si la progression est supérieure ou égale à la cible, {@code false} sinon
     */
    @Transactional(readOnly = true)
    public Boolean isObjectifAtteint(Objectif objectif, Utilisateur utilisateur) {
        Float progression = getProgressionObjectif(objectif, utilisateur);
        return progression >= objectif.getCible();
    }

    /**
     * Convertit la date de début de l'objectif en {@link LocalDateTime}.
     * Si la date n'est pas définie, utilise par défaut un mois en arrière.
     */
    private LocalDateTime getDateDebutDateTime(Objectif objectif) {
        if (objectif.getDateDebut() == null) {
            return LocalDateTime.now().minusMonths(1);
        }
        return objectif.getDateDebut().atStartOfDay();
    }

    /**
     * Convertit la date de fin de l'objectif en {@link LocalDateTime} calée à 23:59:59.
     */
    private LocalDateTime getDateFinDateTime(Objectif objectif) {
        return objectif.getDateFin().atTime(23, 59, 59);
    }
}