package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.model.Activite;

public interface ActiviteRepository extends JpaRepository<Activite, Long>{
    List<Activite> findByUserOrderByDateDesc(Utilisateur utilisateur);
}
