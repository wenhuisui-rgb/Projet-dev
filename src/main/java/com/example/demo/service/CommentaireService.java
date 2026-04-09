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
        return commentaireRepository.save(commentaire);
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
        commentaireRepository.deleteById(id);
    }

    @Transactional
    public void supprimerCommentairesParActivite(Long activiteId) {
        commentaireRepository.deleteByActiviteId(activiteId);
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