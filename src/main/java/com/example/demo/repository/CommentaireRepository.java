package com.example.demo.repository;

import com.example.demo.model.Commentaire;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    
    List<Commentaire> findByActiviteOrderByDateCommentaireDesc(Activite activite);
    
    List<Commentaire> findByActiviteId(Long activiteId);
    
    List<Commentaire> findByAuteurOrderByDateCommentaireDesc(Utilisateur auteur);
    
    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteByActiviteId(Long activiteId);
    
    long countByActiviteId(Long activiteId);
}