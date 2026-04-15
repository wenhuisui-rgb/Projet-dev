package com.example.demo.service;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // management de compte utilisateur
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入加密器

    @Transactional
    public Utilisateur inscrireUtilisateur(Utilisateur utilisateur) {
        // 使用 BCrypt 将明文密码转换为哈希值
        String mdpEncode = passwordEncoder.encode(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(mdpEncode);
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur authentifier(String email, String motDePasse) {
    Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
    
    if (utilisateurOpt.isPresent()) {
        Utilisateur utilisateur = utilisateurOpt.get();
        // 比较输入的明文密码和数据库中的哈希值
        if (passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            return utilisateur; // 密码正确
        }
    }
    return null; // 邮箱不存在或密码错误
}

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
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

    public Utilisateur findByPseudo(String pseudo) {
    return utilisateurRepository.findByPseudo(pseudo).orElse(null);
}

    // gestion des amis
    /**
     * Obtenir la liste d'amis (obtenirListeAmis)
     */
    @Transactional(readOnly = true) // Indique que cette méthode est en lecture seule, ce qui peut améliorer les performances
    public List<Utilisateur> obtenirListeAmis(Long utilisateurId) {
        // TODO: Implement logic
        return null;
    }

    public List<Utilisateur> rechercherParPseudo(String search, Long currentUserId) {
    return utilisateurRepository.findByPseudoContainingIgnoreCaseAndIdNot(search, currentUserId);
}
}