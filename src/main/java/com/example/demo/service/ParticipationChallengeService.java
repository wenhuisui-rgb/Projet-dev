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

    /* =========================
       JOIN CHALLENGE
    ========================== */
    public ParticipationChallenge rejoindreChallenge(Utilisateur utilisateur,
                                                     Challenge challenge) {

        ParticipationChallenge existing =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(),
                        challenge.getId()
                );

        if (existing != null) {
            throw new RuntimeException("Déjà inscrit à ce challenge");
        }

        ParticipationChallenge participation =
                new ParticipationChallenge(utilisateur, challenge);

        return participationRepository.save(participation);
    }

    /* =========================
       QUITTER CHALLENGE
    ========================== */
    public void quitterChallenge(Utilisateur utilisateur,
                                 Challenge challenge) {

        ParticipationChallenge participation =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(),
                        challenge.getId()
                );

        if (participation != null) {
            participationRepository.delete(participation);
        }
    }

    /* =========================
       UPDATE SCORE
    ========================== */
    public ParticipationChallenge mettreAJourScore(Utilisateur utilisateur,
                                                    Challenge challenge,
                                                    float score) {

        ParticipationChallenge participation =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(),
                        challenge.getId()
                );

        if (participation == null) {
            throw new RuntimeException("Participation introuvable");
        }

        participation.setScoreActuel(score);

        return participationRepository.save(participation);
    }

    /* =========================
       LEADERBOARD
    ========================== */
    public List<ParticipationChallenge> obtenirClassement(Long challengeId) {
        return participationRepository.findByChallengeIdOrderByScoreActuelDesc(challengeId);
    }

    /* =========================
       CHECK IF ALREADY JOINED
    ========================== */
    public boolean estDejaInscrit(Long utilisateurId, Long challengeId) {
        return participationRepository.findByUtilisateurIdAndChallengeId(
                utilisateurId,
                challengeId
        ) != null;
    }
}