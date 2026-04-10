package com.example.demo.repository;

import com.example.demo.model.Reaction;
import com.example.demo.model.TypeReaction;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    List<Reaction> findByActiviteOrderByDateReactionDesc(Activite activite);

    List<Reaction> findByActiviteId(Long activiteId);

    List<Reaction> findByAuteur(Utilisateur auteur);

    Optional<Reaction> findByAuteurAndActivite(Utilisateur auteur, Activite activite);

    boolean existsByAuteurAndActivite(Utilisateur auteur, Activite activite);

    long countByActiviteId(Long activiteId);

    long countByActiviteIdAndType(Long activiteId, TypeReaction type);

    void deleteByAuteurAndActivite(Utilisateur auteur, Activite activite);

}