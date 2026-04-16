package com.example.demo.repository;

import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Interface de repository pour l'entité {@link Utilisateur}.
 * Gère l'accès aux données des comptes utilisateurs, incluant l'authentification et la recherche.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    /**
     * Recherche un utilisateur par son adresse email exacte.
     * Utilisé principalement pour le processus de connexion (login).
     *
     * @param email L'adresse email à chercher
     * @return Un {@link Optional} contenant l'utilisateur s'il existe
     */
    Optional<Utilisateur> findByEmail(String email);
    
    /**
     * Recherche un utilisateur par son pseudo (nom d'utilisateur) exact.
     *
     * @param pseudo Le pseudo à chercher
     * @return Un {@link Optional} contenant l'utilisateur s'il existe
     */
    Optional<Utilisateur> findByPseudo(String pseudo);
    
    /**
     * Vérifie si une adresse email est déjà enregistrée dans la base de données.
     * Utilisé lors de l'inscription pour éviter les doublons.
     *
     * @param email L'adresse email à vérifier
     * @return {@code true} si l'email existe, {@code false} sinon
     */
    boolean existsByEmail(String email);
    
    /**
     * Vérifie si un pseudo est déjà pris par un autre utilisateur.
     *
     * @param pseudo Le pseudo à vérifier
     * @return {@code true} si le pseudo existe, {@code false} sinon
     */
    boolean existsByPseudo(String pseudo);

    /**
     * Recherche des utilisateurs dont le pseudo contient la chaîne de caractères spécifiée,
     * en ignorant la casse (majuscules/minuscules).
     *
     * @param pseudo La chaîne de recherche (partielle ou complète)
     * @return Une liste d'utilisateurs correspondants
     */
    List<Utilisateur> findByPseudoContainingIgnoreCase(String pseudo);

    /**
     * Recherche des utilisateurs par pseudo (recherche partielle insensible à la casse),
     * mais exclut un utilisateur spécifique des résultats (généralement l'utilisateur effectuant la recherche).
     * <p>
     * Très utile pour la fonctionnalité "Rechercher des amis" afin de ne pas se voir soi-même dans les résultats.
     *
     * @param pseudo La chaîne de recherche
     * @param id     L'identifiant de l'utilisateur à exclure
     * @return Une liste d'utilisateurs correspondants (sans l'utilisateur exclu)
     */
    List<Utilisateur> findByPseudoContainingIgnoreCaseAndIdNot(String pseudo, Long id);
}