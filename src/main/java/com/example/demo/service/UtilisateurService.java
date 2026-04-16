package com.example.demo.service;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

/**
 * Service gérant la logique métier liée aux utilisateurs.
 * <p>
 * Ce service est responsable de l'inscription (avec sécurisation des mots de passe),
 * de l'authentification, de la recherche et de la gestion globale des comptes.
 */
@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Inscrit un nouvel utilisateur dans le système.
     * Le mot de passe en clair est haché via BCrypt avant la sauvegarde en base.
     *
     * @param utilisateur L'objet utilisateur contenant les informations saisies (avec mot de passe en clair)
     * @return L'utilisateur sauvegardé avec le mot de passe encodé
     */
    @Transactional
    public Utilisateur inscrireUtilisateur(Utilisateur utilisateur) {
        String mdpEncode = passwordEncoder.encode(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(mdpEncode);
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Authentifie un utilisateur en vérifiant son email et son mot de passe.
     *
     * @param email      L'adresse email de l'utilisateur
     * @param motDePasse Le mot de passe en clair à vérifier
     * @return L'objet {@link Utilisateur} si l'authentification réussit, sinon {@code null}
     */
    public Utilisateur authentifier(String email, String motDePasse) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            if (passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
                return utilisateur; 
            }
        }
        return null; 
    }

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email L'adresse email à chercher
     * @return L'utilisateur correspondant, ou {@code null} s'il n'existe pas
     */
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    /**
     * Recherche un utilisateur par son identifiant technique (ID).
     *
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur correspondant, ou {@code null} s'il n'existe pas
     */
    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    /**
     * Vérifie si une adresse email est déjà utilisée dans le système.
     *
     * @param email L'adresse email à vérifier
     * @return {@code true} si l'email existe, {@code false} sinon
     */
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Vérifie si un pseudo est déjà utilisé dans le système.
     *
     * @param pseudo Le pseudo à vérifier
     * @return {@code true} si le pseudo existe, {@code false} sinon
     */
    public boolean pseudoExiste(String pseudo) {
        return utilisateurRepository.existsByPseudo(pseudo);
    }

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param utilisateur L'entité utilisateur avec les nouvelles données
     * @return L'utilisateur mis à jour
     */
    @Transactional
    public Utilisateur updateUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprime un compte utilisateur du système.
     *
     * @param id L'identifiant de l'utilisateur à supprimer
     */
    @Transactional
    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    /**
     * Recherche un utilisateur par son pseudo exact.
     *
     * @param pseudo Le pseudo à chercher
     * @return L'utilisateur correspondant, ou {@code null} s'il n'existe pas
     */
    public Utilisateur findByPseudo(String pseudo) {
        return utilisateurRepository.findByPseudo(pseudo).orElse(null);
    }

    /**
     * Obtient la liste des amis pour un utilisateur donné.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @return La liste de ses amis (Actuellement non implémenté, retourne null)
     */
    @Transactional(readOnly = true) 
    public List<Utilisateur> obtenirListeAmis(Long utilisateurId) {
        return null;
    }

    /**
     * Recherche des utilisateurs dont le pseudo contient la chaîne spécifiée,
     * en excluant l'utilisateur courant de la liste des résultats.
     *
     * @param search        La chaîne de caractères à rechercher (insensible à la casse)
     * @param currentUserId L'identifiant de l'utilisateur effectuant la recherche
     * @return Une liste d'utilisateurs correspondants
     */
    public List<Utilisateur> rechercherParPseudo(String search, Long currentUserId) {
        return utilisateurRepository.findByPseudoContainingIgnoreCaseAndIdNot(search, currentUserId);
    }
}