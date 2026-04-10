package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public Challenge creerChallenge(String titre, com.example.demo.model.TypeSport typeSport,
                                    LocalDate dateDebut, LocalDate dateFin, Utilisateur createur) {
        Challenge challenge = new Challenge(titre, typeSport, dateDebut, dateFin, createur);
        return challengeRepository.save(challenge);
    }

    public List<Challenge> getChallengesByCreateur(Long createurId) {
        return challengeRepository.findByCreateurId(createurId);
    }


    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id).orElse(null);
    }
}