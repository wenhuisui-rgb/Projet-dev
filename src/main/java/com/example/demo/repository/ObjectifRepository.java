package com.example.demo.repository;

import com.example.demo.model.Objectif;
import com.example.demo.model.Periode;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ObjectifRepository extends JpaRepository<Objectif, Long> {

    List<Objectif> findByUtilisateur(Utilisateur utilisateur);

    List<Objectif> findByUtilisateurAndTypeSport(Utilisateur utilisateur, TypeSport typeSport);

    List<Objectif> findByUtilisateurAndPeriode(Utilisateur utilisateur, Periode periode);
}