package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.ParticipationChallenge;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.ParticipationChallengeRepository;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ParticipationChallengeRepository participationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ChallengeService(
            ChallengeRepository challengeRepository,
            ParticipationChallengeRepository participationRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Créer un challenge
    public Challenge creerChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    // Récupérer tous les challenges
    public List<Challenge> getTousLesChallenges() {
        return challengeRepository.findAll();
    }

    // Rejoindre un challenge
    public ParticipationChallenge rejoindreChallenge(
            Long utilisateurId,
            Long challengeId
    ) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge introuvable"));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        participationRepository
                .findByUtilisateurIdAndChallengeId(utilisateurId, challengeId)
                .ifPresent(p -> {
                    throw new RuntimeException("Utilisateur déjà inscrit");
                });

        ParticipationChallenge participation =
                new ParticipationChallenge(utilisateur, challenge);

        return participationRepository.save(participation);
    }

    // Classement challenge
    public List<ParticipationChallenge> obtenirClassement(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge introuvable"));

        return challenge.obtenirClassement();
    }
}