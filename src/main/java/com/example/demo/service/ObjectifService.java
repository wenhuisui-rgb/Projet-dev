package com.example.demo.service;

import com.example.demo.model.Objectif;
import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ObjectifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ObjectifService {

    @Autowired
    private ObjectifRepository objectifRepository;

    @Autowired
    private ActiviteRepository activiteRepository;

    @Transactional
    public Objectif creerObjectif(Objectif objectif, Utilisateur utilisateur) {
        objectif.setUtilisateur(utilisateur);
        return objectifRepository.save(objectif);
    }

    @Transactional(readOnly = true)
    public List<Objectif> getObjectifsParUtilisateur(Utilisateur utilisateur) {
        return objectifRepository.findByUtilisateur(utilisateur);
    }

    @Transactional(readOnly = true)
    public Objectif getObjectifParId(Long id) {
        return objectifRepository.findById(id).orElse(null);
    }

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

    @Transactional
    public void supprimerObjectif(Long id) {
        objectifRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Float getProgressionObjectif(Objectif objectif, Utilisateur utilisateur) {
        LocalDateTime debut = getDateDebutDateTime(objectif);
        LocalDateTime fin = getDateFinDateTime(objectif);

        if ("km".equals(objectif.getUnite())) {
            if (objectif.getTypeSport() != null) {
                return activiteRepository.getDistanceBySportAndPeriod(
                    utilisateur, objectif.getTypeSport(), debut, fin);
            } else {
                return activiteRepository.getDistanceByPeriod(utilisateur, debut, fin);
            }
        } else if ("minutes".equals(objectif.getUnite())) {
            if (objectif.getTypeSport() != null) {
                Integer duree = activiteRepository.getDureeBySportAndPeriod(
                    utilisateur, objectif.getTypeSport(), debut, fin);
                return duree != null ? duree.floatValue() : 0f;
            } else {
                Integer duree = activiteRepository.getDureeByPeriod(utilisateur, debut, fin);
                return duree != null ? duree.floatValue() : 0f;
            }
        } else if ("kcal".equals(objectif.getUnite())) {
            if (objectif.getTypeSport() != null) {
                return activiteRepository.getCaloriesBySportAndPeriod(
                    utilisateur, objectif.getTypeSport(), debut, fin);
            } else {
                return activiteRepository.getCaloriesTotales(utilisateur);
            }
        }
        return 0f;
    }

    @Transactional(readOnly = true)
    public Float getPourcentageObjectif(Objectif objectif, Utilisateur utilisateur) {
        Float progression = getProgressionObjectif(objectif, utilisateur);
        if (objectif.getCible() == null || objectif.getCible() == 0) {
            return 0f;
        }
        return (progression / objectif.getCible()) * 100;
    }

    @Transactional(readOnly = true)
    public Boolean isObjectifAtteint(Objectif objectif, Utilisateur utilisateur) {
        Float progression = getProgressionObjectif(objectif, utilisateur);
        return progression >= objectif.getCible();
    }

    private LocalDateTime getDateDebutDateTime(Objectif objectif) {
        if (objectif.getDateDebut() == null) {
            return LocalDateTime.now().minusMonths(1);
        }
        return objectif.getDateDebut().atStartOfDay();
    }

    private LocalDateTime getDateFinDateTime(Objectif objectif) {
        LocalDate debut = objectif.getDateDebut() != null ? objectif.getDateDebut() : LocalDate.now();
        LocalDate fin;

        if (objectif.getPeriode() == null) {
            fin = debut.plusMonths(1);
        } else {
            switch (objectif.getPeriode()) {
                case SEMAINE:
                    fin = debut.plusWeeks(1);
                    break;
                case ANNEE:
                    fin = debut.plusYears(1);
                    break;
                default:
                    fin = debut.plusMonths(1);
            }
        }
        return fin.atTime(23, 59, 59);
    }
}