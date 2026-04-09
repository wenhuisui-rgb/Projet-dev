package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Activite> activites = getActivitesEntreDates(utilisateur, debutMois, LocalDateTime.now());
        return activites.stream().map(Activite::getDistance).reduce(0f, Float::sum);
    }

    public Integer getNombreActivitesDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        return getActivitesEntreDates(utilisateur, debutMois, LocalDateTime.now()).size();
    }

    public Map<String, Object> getStatsDashboard(Utilisateur utilisateur) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalDistance", getDistanceTotale(utilisateur));
        stats.put("totalDuree", getDureeTotale(utilisateur));
        stats.put("totalCalories", getCaloriesTotales(utilisateur));
        stats.put("totalActivites", activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur).size());
        stats.put("distanceMois", getDistanceDuMois(utilisateur));
        stats.put("activitesMois", getNombreActivitesDuMois(utilisateur));
        stats.put("dernieresActivites", getDernieresActivites(utilisateur));
        
        return stats;
    }

    public Map<String, Float> getStatistiquesParSemaine(Utilisateur utilisateur) {
        Map<String, Float> statsSemaine = new HashMap<>();
        List<Activite> activitesSemaine = getActivitesDeLaSemaine(utilisateur);
        
        float distanceSemaine = 0f;
        int dureeSemaine = 0;
        float caloriesSemaine = 0f;
        
        for (Activite a : activitesSemaine) {
            if (a.getDistance() != null) distanceSemaine += a.getDistance();
            if (a.getDuree() != null) dureeSemaine += a.getDuree();
            if (a.getCalories() != null) caloriesSemaine += a.getCalories();
        }
        
        statsSemaine.put("distance", distanceSemaine);
        statsSemaine.put("duree", (float) dureeSemaine);
        statsSemaine.put("calories", caloriesSemaine);
        
        return statsSemaine;
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
}