package com.example.demo.repository;

import com.example.demo.model.ParticipationChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Interface de repository pour l'entité {@link ParticipationChallenge}.
 * Gère les inscriptions des utilisateurs aux défis et le suivi de leurs scores respectifs.
 */
public interface ParticipationChallengeRepository extends JpaRepository<ParticipationChallenge, Long> {

    /**
     * Récupère le classement (leaderboard) d'un challenge spécifique.
     * Les participations sont triées par score de manière décroissante (du plus élevé au plus bas).
     *
     * @param challengeId L'identifiant du challenge
     * @return La liste ordonnée des participations
     */
    List<ParticipationChallenge> findByChallengeIdOrderByScoreActuelDesc(Long challengeId);

    /**
     * Recherche l'entité de participation d'un utilisateur spécifique à un challenge spécifique.
     * Permet de vérifier l'inscription ou de mettre à jour le score individuel.
     *
     * @param utilisateurId L'identifiant de l'utilisateur participant
     * @param challengeId   L'identifiant du challenge
     * @return La participation correspondante, ou {@code null} si non inscrit
     */
    ParticipationChallenge findByUtilisateurIdAndChallengeId(Long utilisateurId, Long challengeId);
}