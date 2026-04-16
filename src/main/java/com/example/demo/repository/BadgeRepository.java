package com.example.demo.repository;

import com.example.demo.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Interface de repository pour l'entité {@link Badge}.
 * Fournit les opérations de base pour consulter le catalogue des badges système.
 */
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    /**
     * Recherche un badge dans la base de données via son nom exact.
     *
     * @param nom Le nom du badge (ex: "TOTAL_100KM")
     * @return Un {@link Optional} contenant le badge s'il est trouvé
     */
    Optional<Badge> findByNom(String nom);
}