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

/**
 * Service gérant les commentaires laissés par les utilisateurs sur les activités sportives.
 * <p>
 * Ce service assure la persistance des commentaires et maintient la cohérence
 * bidirectionnelle avec l'entité {@link Activite}.
 */
@Service
public class CommentaireService {

    @Autowired
    private CommentaireRepository commentaireRepository;

    /**
     * Ajoute un nouveau commentaire à une activité spécifique.
     * Met également à jour la liste des commentaires de l'entité {@link Activite} en mémoire.
     *
     * @param contenu  Le texte du commentaire
     * @param activite L'activité sportive commentée
     * @param auteur   L'utilisateur qui rédige le commentaire
     * @return Le commentaire sauvegardé en base de données
     */
    @Transactional
    public Commentaire ajouterCommentaire(String contenu, Activite activite, Utilisateur auteur) {
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setDateCommentaire(LocalDateTime.now());
        commentaire.setActivite(activite);
        commentaire.setAuteur(auteur);
        
        Commentaire saved = commentaireRepository.save(commentaire);
        
        activite.ajouterCommentaire(saved);
        
        return saved;
    }

    /**
     * Récupère tous les commentaires d'une activité, triés par date décroissante (du plus récent au plus ancien).
     *
     * @param activite L'activité concernée
     * @return Une liste de commentaires
     */
    public List<Commentaire> getCommentairesParActivite(Activite activite) {
        return commentaireRepository.findByActiviteOrderByDateCommentaireDesc(activite);
    }

    /**
     * Récupère tous les commentaires d'une activité via son identifiant.
     *
     * @param activiteId L'identifiant de l'activité
     * @return Une liste de commentaires
     */
    public List<Commentaire> getCommentairesParActiviteId(Long activiteId) {
        return commentaireRepository.findByActiviteId(activiteId);
    }

    /**
     * Récupère tous les commentaires rédigés par un utilisateur spécifique, triés par date décroissante.
     *
     * @param auteur L'utilisateur auteur des commentaires
     * @return Une liste de commentaires
     */
    public List<Commentaire> getCommentairesParAuteur(Utilisateur auteur) {
        return commentaireRepository.findByAuteurOrderByDateCommentaireDesc(auteur);
    }

    /**
     * Récupère un commentaire spécifique par son identifiant.
     *
     * @param id L'identifiant du commentaire
     * @return Le commentaire correspondant, ou {@code null} s'il n'existe pas
     */
    public Commentaire getCommentaireParId(Long id) {
        return commentaireRepository.findById(id).orElse(null);
    }

    /**
     * Supprime un commentaire spécifique.
     * Assure également le retrait du commentaire de la liste de l'entité {@link Activite} parente.
     *
     * @param id L'identifiant du commentaire à supprimer
     */
    @Transactional
    public void supprimerCommentaire(Long id) {
        Commentaire commentaire = getCommentaireParId(id);
        if (commentaire != null && commentaire.getActivite() != null) {
            commentaire.getActivite().retirerCommentaire(commentaire);
        }
        commentaireRepository.deleteById(id);
    }

    /**
     * Supprime massivement tous les commentaires liés à une activité spécifique.
     *
     * @param activiteId L'identifiant de l'activité
     */
    @Transactional
    public void supprimerCommentairesParActivite(Long activiteId) {
        commentaireRepository.deleteByActiviteId(activiteId);
    }

    /**
     * Modifie le contenu d'un commentaire existant.
     * La date du commentaire n'est pas mise à jour dans cette implémentation.
     *
     * @param id             L'identifiant du commentaire à modifier
     * @param nouveauContenu Le nouveau texte du commentaire
     * @return Le commentaire mis à jour, ou {@code null} s'il est introuvable
     */
    @Transactional
    public Commentaire modifierCommentaire(Long id, String nouveauContenu) {
        Commentaire commentaire = getCommentaireParId(id);
        if (commentaire != null) {
            commentaire.setContenu(nouveauContenu);
            return commentaireRepository.save(commentaire);
        }
        return null;
    }

    /**
     * Compte le nombre total de commentaires pour une activité donnée.
     *
     * @param activiteId L'identifiant de l'activité
     * @return Le nombre de commentaires
     */
    public long getNombreCommentairesParActivite(Long activiteId) {
        return commentaireRepository.countByActiviteId(activiteId);
    }
}