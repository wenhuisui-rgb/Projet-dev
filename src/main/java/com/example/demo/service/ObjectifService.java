package com.example.demo.service;

import com.example.demo.model.Objectif;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ObjectifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.demo.model.Activite;

@Service
public class ObjectifService {

    @Autowired
    private ObjectifRepository objectifRepository;
    
    @Autowired
    private ActiviteRepository activiteRepository;

    /**
     * 创建新目标
     */
    @Transactional
    public Objectif creerObjectif(Objectif objectif, Utilisateur utilisateur) {
        objectif.setUtilisateur(utilisateur);
        return objectifRepository.save(objectif);
    }

    public List<Objectif> getObjectifsParUtilisateur(Utilisateur utilisateur) {
        return objectifRepository.findByUtilisateur(utilisateur);
    }

    /**
     * 根据ID获取目标
     */
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

    public Float getProgressionObjectif(Objectif objectif, Utilisateur utilisateur) {
        List<Activite> activites = activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur);
        return objectif.getPourcentageProgression(activites);
    }

    public Boolean isObjectifAtteint(Objectif objectif, Utilisateur utilisateur) {
        List<Activite> activites = activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur);
        return objectif.estAtteint(activites);
    }
}