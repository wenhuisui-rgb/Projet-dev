package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Challenge;
import com.example.demo.model.ParticipationChallenge;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.ParticipationChallengeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service gérant l'inscription des utilisateurs aux défis et le suivi de leur progression.
 * <p>
 * Contient le moteur de calcul qui actualise automatiquement les scores des challenges
 * lorsqu'un utilisateur enregistre une nouvelle {@link Activite}.
 */
@Service
public class ParticipationChallengeService {

    private final ParticipationChallengeRepository participationRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    public ParticipationChallengeService(ParticipationChallengeRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    /**
     * Inscrit un utilisateur à un challenge.
     *
     * @param utilisateur L'utilisateur souhaitant participer
     * @param challenge   Le challenge à rejoindre
     * @return L'entité de participation créée
     * @throws RuntimeException Si l'utilisateur est déjà inscrit à ce challenge
     */
    public ParticipationChallenge rejoindreChallenge(Utilisateur utilisateur, Challenge challenge) {

        ParticipationChallenge existing =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(), challenge.getId());

        if (existing != null) {
            throw new RuntimeException("Déjà inscrit à ce challenge");
        }

        ParticipationChallenge participation = new ParticipationChallenge(utilisateur, challenge);
        return participationRepository.save(participation);
    }

    /**
     * Désinscrit un utilisateur d'un challenge.
     *
     * @param utilisateur L'utilisateur souhaitant quitter le challenge
     * @param challenge   Le challenge concerné
     */
    public void quitterChallenge(Utilisateur utilisateur, Challenge challenge) {
        ParticipationChallenge participation =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(), challenge.getId());

        if (participation != null) {
            participationRepository.delete(participation);
        }
    }

    /**
     * Ajoute une valeur spécifique au score actuel de l'utilisateur pour un challenge donné.
     *
     * @param utilisateur   L'utilisateur participant
     * @param challenge     Le challenge concerné
     * @param valeurAjoutee La valeur à additionner au score (ex: kilomètres parcourus)
     * @return La participation mise à jour, ou {@code null} si introuvable
     */
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

    /**
     * Moteur de calcul (Core) : Actualise automatiquement les scores de tous les challenges actifs
     * auxquels participe l'utilisateur, en fonction d'une nouvelle activité enregistrée.
     *
     * @param activite L'activité sportive venant d'être sauvegardée
     */
    @Transactional
    public void actualiserScoresApresActivite(Activite activite) {
        Utilisateur user = activite.getUtilisateur();
        
        List<Challenge> activeChallenges = challengeRepository.findActiveChallengesByUserId(
                user.getId(), 
                activite.getDateActivite().toLocalDate()
        );

        for (Challenge c : activeChallenges) {
            if (c.getTypeSport() != null && c.getTypeSport() != activite.getTypeSport()) {
                continue; 
            }

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

            if (valeurAajouter > 0) {
                ajouterScore(user, c, valeurAajouter);
            }
        }
    }

    /**
     * Écrase le score actuel par une nouvelle valeur (Utilisé pour les corrections manuelles).
     *
     * @param utilisateur L'utilisateur participant
     * @param challenge   Le challenge concerné
     * @param score       Le nouveau score absolu
     * @return La participation mise à jour
     * @throws RuntimeException Si la participation est introuvable
     */
    public ParticipationChallenge mettreAJourScore(Utilisateur utilisateur, Challenge challenge, float score) {
        ParticipationChallenge participation =
                participationRepository.findByUtilisateurIdAndChallengeId(
                        utilisateur.getId(), challenge.getId());

        if (participation == null) {
            throw new RuntimeException("Participation introuvable");
        }

        participation.setScoreActuel(score);
        return participationRepository.save(participation);
    }

    /**
     * Récupère le classement des participants pour un challenge donné, trié par score décroissant.
     *
     * @param challengeId L'identifiant du challenge
     * @return La liste des participations triées (Leaderboard)
     */
    public List<ParticipationChallenge> obtenirClassement(Long challengeId) {
        return participationRepository.findByChallengeIdOrderByScoreActuelDesc(challengeId);
    }

    /**
     * Vérifie si un utilisateur est déjà inscrit à un challenge spécifique.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param challengeId   L'identifiant du challenge
     * @return {@code true} si inscrit, {@code false} sinon
     */
    public boolean estDejaInscrit(Long utilisateurId, Long challengeId) {
        return participationRepository.findByUtilisateurIdAndChallengeId(
                utilisateurId, challengeId) != null;
    }
}