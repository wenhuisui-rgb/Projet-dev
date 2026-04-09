package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.ParticipationChallenge;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ParticipationChallengeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParticipationChallengeService {

    private final ParticipationChallengeRepository participationRepository;

    public ParticipationChallengeService(ParticipationChallengeRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public ParticipationChallenge rejoindreChallenge(Utilisateur utilisateur, Challenge challenge) {
        // Vérifier si l'utilisateur est déjà inscrit 
        ParticipationChallenge existing = participationRepository
                .findByUtilisateurIdAndChallengeId(utilisateur.getId(), challenge.getId());
        if (existing != null) return existing;

        ParticipationChallenge participation = new ParticipationChallenge(utilisateur, challenge);
        return participationRepository.save(participation);
    }

    public void retirerParticipant(Utilisateur utilisateur, Challenge challenge) {
        ParticipationChallenge participation = participationRepository
                .findByUtilisateurIdAndChallengeId(utilisateur.getId(), challenge.getId());
        if (participation != null) {
            participationRepository.delete(participation);
        }
    }

    public ParticipationChallenge mettreAJourScore(Utilisateur utilisateur, Challenge challenge, float score) {
        ParticipationChallenge participation = participationRepository
                .findByUtilisateurIdAndChallengeId(utilisateur.getId(), challenge.getId());
        if (participation != null) {
            participation.setScoreActuel(score);
            return participationRepository.save(participation);
        }
        return null;
    }

    public List<ParticipationChallenge> obtenirClassement(Long challengeId) {
        return participationRepository.findByChallengeIdOrderByScoreActuelDesc(challengeId);
    }
}