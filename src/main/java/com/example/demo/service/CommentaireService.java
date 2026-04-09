package com.example.demo.service;

import com.example.demo.model.Commentaire;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.CommentaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentaireService {

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Transactional
    public Commentaire ajouterCommentaire(String contenu, Activite activite, Utilisateur auteur) {
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setDateCommentaire(LocalDateTime.now());
        commentaire.setActivite(activite);
        commentaire.setAuteur(auteur);
        
        Commentaire saved = commentaireRepository.save(commentaire);
        
        if (activite.getCommentaires() != null) {
            activite.getCommentaires().add(saved);
        }
        
        return saved;
    }

    public List<Commentaire> getCommentairesParActivite(Activite activite) {
        return commentaireRepository.findByActiviteOrderByDateCommentaireDesc(activite);
    }

    public List<Commentaire> getCommentairesParActiviteId(Long activiteId) {
        return commentaireRepository.findByActiviteId(activiteId);
    }

    public List<Commentaire> getCommentairesParAuteur(Utilisateur auteur) {
        return commentaireRepository.findByAuteurOrderByDateCommentaireDesc(auteur);
    }

    public Commentaire getCommentaireParId(Long id) {
        return commentaireRepository.findById(id).orElse(null);
    }

    @Transactional
    public void supprimerCommentaire(Long id) {
        Commentaire commentaire = getCommentaireParId(id);
        if (commentaire != null && commentaire.getActivite() != null) {
            commentaire.getActivite().getCommentaires().remove(commentaire);
        }
        commentaireRepository.deleteById(id);
    }

    @Transactional
    public void supprimerCommentairesParActivite(Activite activite) {
        List<Commentaire> commentaires = getCommentairesParActivite(activite);
        for (Commentaire c : commentaires) {
            commentaireRepository.deleteById(c.getId());
        }
        if (activite.getCommentaires() != null) {
            activite.getCommentaires().clear();
        }
    }

    @Transactional
    public Commentaire modifierCommentaire(Long id, String nouveauContenu) {
        Commentaire commentaire = getCommentaireParId(id);
        if (commentaire != null) {
            commentaire.setContenu(nouveauContenu);
            return commentaireRepository.save(commentaire);
        }
        return null;
    }

    public long getNombreCommentairesParActivite(Activite activite) {
        return commentaireRepository.findByActiviteOrderByDateCommentaireDesc(activite).size();
    }
}