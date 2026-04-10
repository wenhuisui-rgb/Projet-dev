package com.example.demo.service;
import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Créer un challenge
     */
    public Challenge creerChallenge(String titre,
                                    TypeSport typeSport,
                                    LocalDate dateDebut,
                                    LocalDate dateFin,
                                    Utilisateur createur) {

        Challenge challenge = new Challenge(
                titre,
                typeSport,
                dateDebut,
                dateFin,
                createur
        );

        return challengeRepository.save(challenge);
    }

    /**
     * Récupérer tous les challenges
     */
    public List<Challenge> getTousLesChallenges() {
        return challengeRepository.findAll();
    }

    /**
     * Récupérer un challenge par ID
     */
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id).orElse(null);
    }

    /**
     * Récupérer les challenges créés par un utilisateur
     */
    public List<Challenge> getChallengesByCreateur(Long createurId) {
        return challengeRepository.findByCreateurId(createurId);
    }


    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }
}