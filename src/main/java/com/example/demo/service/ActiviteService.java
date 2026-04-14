package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class ActiviteService {

    @Autowired
    private ActiviteRepository activiteRepository;

    @Transactional
    public Activite sauvegarderActivite(Activite activite, Float poidsUtilisateur) {
        Float caloriesCalculees = activite.calculerCalories(poidsUtilisateur);
        activite.setCalories(caloriesCalculees);
        return activiteRepository.save(activite);
    }

    public Activite getActiviteParId(Long id) {
        return activiteRepository.findById(id).orElse(null);
    }

    public List<Activite> getActivitesParUtilisateur(Utilisateur utilisateur) {
        return activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur);
    }

    public List<Activite> getDernieresActivites(Utilisateur utilisateur) {
        return activiteRepository.findTop5ByUtilisateurOrderByDateActiviteDesc(utilisateur);
    }

    public List<Activite> getActivitesEntreDates(Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin) {
        return activiteRepository.findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(utilisateur, debut, fin);
    }

    public List<Activite> getActivitesDeLaSemaine(Utilisateur utilisateur) {
        LocalDateTime debutSemaine = LocalDateTime.now().minusDays(7);
        return getActivitesEntreDates(utilisateur, debutSemaine, LocalDateTime.now());
    }

    public List<Activite> getActivitesDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        return getActivitesEntreDates(utilisateur, debutMois, LocalDateTime.now());
    }

    public List<Activite> getActivitesParType(Utilisateur utilisateur, TypeSport typeSport) {
        return activiteRepository.findByUtilisateurAndTypeSportOrderByDateActiviteDesc(utilisateur, typeSport);
    }

    public Float getDistanceTotale(Utilisateur utilisateur) {
        return activiteRepository.getDistanceTotale(utilisateur);
    }

    public Integer getDureeTotale(Utilisateur utilisateur) {
        return activiteRepository.getDureeTotale(utilisateur);
    }

    public Float getCaloriesTotales(Utilisateur utilisateur) {
        return activiteRepository.getCaloriesTotales(utilisateur);
    }

    public Float getDistanceDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        return activiteRepository.getDistanceByPeriod(utilisateur, debutMois, LocalDateTime.now());
    }

    public Integer getNombreActivitesDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        return (int) activiteRepository.countByUtilisateurAndDateActiviteBetween(utilisateur, debutMois, LocalDateTime.now());
    }

    public Map<String, Object> getStatsDashboard(Utilisateur utilisateur) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalDistance", getDistanceTotale(utilisateur));
        stats.put("totalDuree", getDureeTotale(utilisateur));
        stats.put("totalCalories", getCaloriesTotales(utilisateur));
        stats.put("totalActivites", activiteRepository.countByUtilisateur(utilisateur));
        stats.put("distanceMois", getDistanceDuMois(utilisateur));
        stats.put("activitesMois", getNombreActivitesDuMois(utilisateur));
        stats.put("dernieresActivites", getDernieresActivites(utilisateur));
        
        return stats;
    }

    public Map<String, Float> getStatistiquesParSemaine(Utilisateur utilisateur) {
        LocalDateTime debutSemaine = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();
        
        Map<String, Float> statsSemaine = new HashMap<>();
        
        Float distance = activiteRepository.getDistanceByPeriod(utilisateur, debutSemaine, fin);
        Integer duree = activiteRepository.getDureeByPeriod(utilisateur, debutSemaine, fin);
        Float calories = activiteRepository.getCaloriesByPeriod(utilisateur, debutSemaine, fin);
        
        statsSemaine.put("distance", distance != null ? distance : 0f);
        statsSemaine.put("duree", duree != null ? duree.floatValue() : 0f);
        statsSemaine.put("calories", calories != null ? calories : 0f);
        
        return statsSemaine;
    }

    public Map<String, Float> getStatistiquesParMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime fin = LocalDateTime.now();
        
        Map<String, Float> statsMois = new HashMap<>();
        
        Float distance = activiteRepository.getDistanceByPeriod(utilisateur, debutMois, fin);
        Integer duree = activiteRepository.getDureeByPeriod(utilisateur, debutMois, fin);
        Float calories = activiteRepository.getCaloriesByPeriod(utilisateur, debutMois, fin);
        
        statsMois.put("distance", distance != null ? distance : 0f);
        statsMois.put("duree", duree != null ? duree.floatValue() : 0f);
        statsMois.put("calories", calories != null ? calories : 0f);
        
        return statsMois;
    }

    @Transactional
    public Activite updateActivite(Long id, Activite nouvelleActivite, Float poidsUtilisateur) {
        Activite existante = getActiviteParId(id);
        if (existante != null) {
            existante.setTypeSport(nouvelleActivite.getTypeSport());
            existante.setDateActivite(nouvelleActivite.getDateActivite());
            existante.setDuree(nouvelleActivite.getDuree());
            existante.setDistance(nouvelleActivite.getDistance());
            existante.setLocalisation(nouvelleActivite.getLocalisation());
            existante.setEvaluation(nouvelleActivite.getEvaluation());
            
            Float nouvellesCalories = existante.calculerCalories(poidsUtilisateur);
            existante.setCalories(nouvellesCalories);
            
            return activiteRepository.save(existante);
        }
        return null;
    }

    @Transactional
    public void supprimerActivite(Long id) {
        activiteRepository.deleteById(id);
    }

    public boolean aAtteintObjectifMensuel(Utilisateur utilisateur, Float objectifDistance) {
        if (objectifDistance == null) return false;
        Float distanceMois = getDistanceDuMois(utilisateur);
        return distanceMois >= objectifDistance;
    }

    public Page<Activite> getActivitesPaginees(Utilisateur utilisateur, int page, int size) {
        // 注意：Spring Data JPA 的页码是从 0 开始的 (0 代表第一页)
        Pageable pageable = PageRequest.of(page, size);
        return activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur, pageable);
    }
}