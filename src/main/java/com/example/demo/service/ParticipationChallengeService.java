package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipationChallengeService {

    private final ParticipationChallengeRepository participationRepository;
    private final ChallengeRepository challengeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ParticipationChallengeService(
            ParticipationChallengeRepository participationRepository,
            ChallengeRepository challengeRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.participationRepository = participationRepository;
        this.challengeRepository = challengeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public ParticipationChallenge rejoindreChallenge(Long userId, Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow();

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow();

        participationRepository
                .findByUtilisateurIdAndChallengeId(userId, challengeId)
                .ifPresent(p -> {
                    throw new RuntimeException("Déjà inscrit");
                });

        ParticipationChallenge participation =
                new ParticipationChallenge(utilisateur, challenge);

        return participationRepository.save(participation);
    }

    public void mettreAJourScore(Long participationId, float nouveauScore) {
        ParticipationChallenge participation = participationRepository
                .findById(participationId)
                .orElseThrow();

        participation.setScoreActuel(nouveauScore);

        participationRepository.save(participation);
    }

    public List<ParticipationChallenge> obtenirClassement(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow();

        return challenge.obtenirClassement();
    }

    public void quitterChallenge(Long participationId) {
        participationRepository.deleteById(participationId);
    }
}
