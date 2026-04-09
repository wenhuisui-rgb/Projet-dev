package com.example.demo.repository;

import com.example.demo.model.ObtentionBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ObtentionBadgeRepository extends JpaRepository<ObtentionBadge, Long> {

    List<ObtentionBadge> findByUtilisateurId(Long utilisateurId);

    List<ObtentionBadge> findByBadgeId(Long badgeId);
}