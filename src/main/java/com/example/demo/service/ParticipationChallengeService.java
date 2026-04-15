package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Challenge;
import com.example.demo.model.ParticipationChallenge;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.ParticipationChallengeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ParticipationChallengeService {

    private final ParticipationChallengeRepository participationRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

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
       MOTEUR DE CALCUL DES SCORES (第二阶段核心)
    ========================== */

    // 1. 累加积分的基础方法
    @Transactional
    public ParticipationChallenge ajouterScore(Utilisateur utilisateur, Challenge challenge, float valeurAjoutee) {
        ParticipationChallenge participation = participationRepository.findByUtilisateurIdAndChallengeId(
                utilisateur.getId(), challenge.getId());

        if (participation != null) {
            participation.setScoreActuel(participation.getScoreActuel() + valeurAjoutee);
            return participationRepository.save(participation);
        }
        return null;
    }

    // 2. 核心大脑：根据刚保存的运动记录，自动给各大挑战加分
    @Transactional
    public void actualiserScoresApresActivite(Activite activite) {
        Utilisateur user = activite.getUtilisateur();
        
        // 查找用户当天有哪些正在进行中的挑战
        List<Challenge> activeChallenges = challengeRepository.findActiveChallengesByUserId(
                user.getId(), 
                activite.getDateActivite().toLocalDate()
        );

        for (Challenge c : activeChallenges) {
            // 校验运动类型：如果挑战指定了运动类型，且与当前运动不符，则跳过。（假设 null 代表"所有运动"）
            if (c.getTypeSport() != null && c.getTypeSport() != activite.getTypeSport()) {
                continue; 
            }

            // 根据挑战设定的规则(Metrique)，提取对应的数值
            float valeurAajouter = 0f;
            
            switch (c.getUnite()) {
                case KM:
                    valeurAajouter = activite.getDistance() != null ? activite.getDistance() : 0f;
                    break;
                case MINUTES:
                    valeurAajouter = activite.getDuree() != null ? activite.getDuree().floatValue() : 0f;
                    break;
                case KCAL:
                    valeurAajouter = activite.getCalories() != null ? activite.getCalories() : 0f;
                    break;
            }

            // 如果这项运动产生了有效数据，就累加到挑战积分里
            if (valeurAajouter > 0) {
                ajouterScore(user, c, valeurAajouter);
            }
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