package com.example.demo.repository;

import com.example.demo.model.ParticipationChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipationChallengeRepository extends JpaRepository<ParticipationChallenge, Long> {

    List<ParticipationChallenge> findByChallengeIdOrderByScoreActuelDesc(Long challengeId);

    ParticipationChallenge findByUtilisateurIdAndChallengeId(Long utilisateurId, Long challengeId);
}