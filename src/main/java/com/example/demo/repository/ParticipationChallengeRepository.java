package com.example.demo.repository;

import com.example.demo.model.ParticipationChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipationChallengeRepository
        extends JpaRepository<ParticipationChallenge, Long> {
                
//verifie si un utilisateur participe déjà à un challenge avant de s'inscrire
    Optional<ParticipationChallenge> findByUtilisateurIdAndChallengeId(
            Long utilisateurId,
            Long challengeId
    );
}