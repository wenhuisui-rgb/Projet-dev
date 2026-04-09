package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;


@Service
public class UtilisateurService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public void sInscrire(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public void envoyerDemandeAmi(Long demandeurId, Long receveurId) {
    }
}
