package com.example.demo.service;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Transactional
    public Utilisateur inscrireUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur authentifier(String email, String motDePasse) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent() && utilisateur.get().getMotDePasse().equals(motDePasse)) {
            return utilisateur.get();
        }
        return null;
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }
    /**
     * Obtenir la liste d'amis (obtenirListeAmis)
     */
    @Transactional(readOnly = true) // Indique que cette méthode est en lecture seule, ce qui peut améliorer les performances
    public List<Utilisateur> obtenirListeAmis(Long utilisateurId) {
        // TODO: Implement logic
        return null;
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    public boolean pseudoExiste(String pseudo) {
        return utilisateurRepository.existsByPseudo(pseudo);
    }

    @Transactional
    public Utilisateur updateUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }
}