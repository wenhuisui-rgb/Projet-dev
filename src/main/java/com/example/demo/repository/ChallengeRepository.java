package com.example.demo.repository;

import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByCreateurId(Long createurId);

    List<Challenge> findByTypeSport(TypeSport typeSport);


    @Query("""
        SELECT c
        FROM Challenge c
        JOIN c.participations p
        WHERE p.utilisateur = :user
    """)
    List<Challenge> findChallengesByUser(@Param("user") Utilisateur user);

    @Query("SELECT p.challenge FROM ParticipationChallenge p WHERE p.utilisateur.id = :userId AND p.challenge.dateDebut <= :now AND p.challenge.dateFin >= :now")
    List<Challenge> findActiveChallengesByUserId(@Param("userId") Long userId, @Param("now") LocalDate now);
    
}