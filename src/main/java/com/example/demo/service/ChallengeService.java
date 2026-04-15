package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
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

    public Challenge creerChallenge(String titre,
                                    TypeSport typeSport,
                                    LocalDate dateDebut,
                                    LocalDate dateFin,
                                    Utilisateur createur,
                                    Unite unite,
                                    Float cible) {

        Challenge challenge = new Challenge(
                titre,
                typeSport,
                dateDebut,
                dateFin,
                createur,
                unite,
                cible
        );

        return challengeRepository.save(challenge);
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id).orElse(null);
    }

    public List<Challenge> findByTypeSport(TypeSport typeSport) {
        return challengeRepository.findByTypeSport(typeSport);
    }

    public List<Challenge> getChallengesByCreateur(Long createurId) {
        return challengeRepository.findByCreateurId(createurId);
    }

    public List<Challenge> findChallengesByUser(Utilisateur user) {
        return challengeRepository.findChallengesByUser(user);
    }

    
}