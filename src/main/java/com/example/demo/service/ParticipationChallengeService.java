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

    /**
     * Rejoindre un challenge
     */
    public ParticipationChallenge rejoindreChallenge(Utilisateur utilisateur,
                                                     Challenge challenge) {

        ParticipationChallenge existing =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(),
                        challenge.getId()
                );

        if (existing != null) {
            return existing;
        }

        ParticipationChallenge participation =
                new ParticipationChallenge(utilisateur, challenge);

        return participationRepository.save(participation);
    }

    
    //Quitter un challenge
    
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

    
    //Mettre à jour le score d'un utilisateur
     
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

    
    //Classement du challenge
     
    public List<ParticipationChallenge> obtenirClassement(Long challengeId) {
        return participationRepository.findByChallengeIdOrderByScoreActuelDesc(challengeId);
    }

    
    //Vérifier si déjà inscrit
     
    public boolean estDejaInscrit(Long utilisateurId, Long challengeId) {
        return participationRepository.findByUtilisateurIdAndChallengeId(
                utilisateurId,
                challengeId
        ) != null;
    }
}