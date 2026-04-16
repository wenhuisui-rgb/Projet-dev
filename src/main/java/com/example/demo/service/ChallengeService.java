package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Service gérant le cycle de vie des défis (challenges).
 * <p>
 * Ce service permet de créer, consulter et supprimer des challenges.
 * La gestion des participants et de leurs scores est déléguée au {@link ParticipationChallengeService}.
 */
@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Crée un nouveau challenge et le sauvegarde en base de données.
     *
     * @param titre     Le nom du challenge
     * @param typeSport Le sport requis pour participer (peut être null pour tous les sports)
     * @param dateDebut La date de début du challenge
     * @param dateFin   La date de fin du challenge
     * @param createur  L'utilisateur qui organise le challenge
     * @param unite     L'unité de mesure (ex: KM, MINUTES, KCAL)
     * @param cible     L'objectif total à atteindre
     * @return Le challenge nouvellement créé
     */
    public Challenge creerChallenge(String titre,
                                    TypeSport typeSport,
                                    LocalDate dateDebut,
                                    LocalDate dateFin,
                                    Utilisateur createur,
                                    Unite unite,
                                    Float cible) {

        Challenge challenge = new Challenge(
                titre,
                typeSport,
                dateDebut,
                dateFin,
                createur,
                unite,
                cible
        );

        return challengeRepository.save(challenge);
    }

    /**
     * Supprime un challenge par son identifiant.
     * <p>
     * Note: Grâce au paramétrage CascadeType.ALL sur l'entité, la suppression du challenge
     * entraînera automatiquement la suppression de toutes les participations associées.
     *
     * @param id L'identifiant du challenge à supprimer
     */
    @Transactional
    public void supprimerChallenge(Long id) {
        challengeRepository.deleteById(id);
    }

    /**
     * Récupère la liste globale de tous les challenges existants.
     *
     * @return Une liste contenant tous les challenges
     */
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    /**
     * Récupère un challenge spécifique par son identifiant.
     *
     * @param id L'identifiant du challenge
     * @return Le challenge correspondant, ou {@code null} s'il n'est pas trouvé
     */
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id).orElse(null);
    }

    /**
     * Recherche les challenges filtrés par type de sport.
     *
     * @param typeSport Le type de sport ciblé
     * @return Une liste de challenges correspondants
     */
    public List<Challenge> findByTypeSport(TypeSport typeSport) {
        return challengeRepository.findByTypeSport(typeSport);
    }

    /**
     * Récupère la liste des challenges créés par un utilisateur spécifique.
     *
     * @param createurId L'identifiant de l'utilisateur créateur
     * @return Une liste des challenges organisés par cet utilisateur
     */
    public List<Challenge> getChallengesByCreateur(Long createurId) {
        return challengeRepository.findByCreateurId(createurId);
    }

    /**
     * Récupère la liste des challenges auxquels un utilisateur participe.
     *
     * @param user L'utilisateur participant
     * @return Une liste des challenges rejoints par l'utilisateur
     */
    public List<Challenge> findChallengesByUser(Utilisateur user) {
        return challengeRepository.findChallengesByUser(user);
    }
}