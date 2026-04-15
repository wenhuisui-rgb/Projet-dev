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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.model.ChartData;


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
        Pageable pageable = PageRequest.of(page, size);
        return activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur, pageable);
    }

    public List<ChartData> getChartData(Utilisateur utilisateur, String periode) {
        List<ChartData> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        if ("semaine".equals(periode)) {
            for (int i = 6; i >= 0; i--) {
                LocalDateTime day = now.minusDays(i);
                LocalDateTime start = day.withHour(0).withMinute(0);
                LocalDateTime end = day.withHour(23).withMinute(59);
                Integer minutes = activiteRepository.getDureeByPeriod(utilisateur, start, end);
                result.add(new ChartData(getDayName(i), minutes != null ? minutes : 0));
            }
        } else if ("mois".equals(periode)) {
            int daysInMonth = now.toLocalDate().lengthOfMonth();
            for (int i = 1; i <= daysInMonth; i++) {
                LocalDateTime day = LocalDateTime.now().withDayOfMonth(i);
                LocalDateTime start = day.withHour(0).withMinute(0);
                LocalDateTime end = day.withHour(23).withMinute(59);
                Integer minutes = activiteRepository.getDureeByPeriod(utilisateur, start, end);
                result.add(new ChartData(String.valueOf(i), minutes != null ? minutes : 0));
            }
        } else if ("annee".equals(periode)) {
            for (int i = 1; i <= 12; i++) {
                LocalDateTime start = LocalDateTime.now().withMonth(i).withDayOfMonth(1).withHour(0).withMinute(0);
                LocalDateTime end = start.withMonth(i).withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59);
                Integer minutes = activiteRepository.getDureeByPeriod(utilisateur, start, end);
                result.add(new ChartData(getMonthName(i), minutes != null ? minutes : 0));
            }
        }
        
        return result;
    }

    private String getDayName(int daysAgo) {
        LocalDateTime date = LocalDateTime.now().minusDays(daysAgo);
        return date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.FRENCH);
    }

    private String getMonthName(int month) {
        java.time.Month m = java.time.Month.of(month);
        return m.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.FRENCH);
    }

    public List<ChartData> getChartDataBySports(Utilisateur utilisateur, String periode, List<TypeSport> sports, String metric) {
    List<ChartData> result = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    
    if ("semaine".equals(periode)) {
        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = now.minusDays(i);
            LocalDateTime start = day.withHour(0).withMinute(0);
            LocalDateTime end = day.withHour(23).withMinute(59);
            Integer value;
            if ("calories".equals(metric)) {
                Float calories = activiteRepository.getCaloriesByPeriodAndSports(utilisateur, start, end, sports);
                value = calories != null ? calories.intValue() : 0;
            } else {
                value = activiteRepository.getDureeByPeriodAndSports(utilisateur, start, end, sports);
            }
            result.add(new ChartData(getDayName(i), value != null ? value : 0));
        }
    } else if ("mois".equals(periode)) {
        int daysInMonth = now.toLocalDate().lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDateTime day = now.withDayOfMonth(i);
            LocalDateTime start = day.withHour(0).withMinute(0);
            LocalDateTime end = day.withHour(23).withMinute(59);
            Integer value;
            if ("calories".equals(metric)) {
                Float calories = activiteRepository.getCaloriesByPeriodAndSports(utilisateur, start, end, sports);
                value = calories != null ? calories.intValue() : 0;
            } else {
                value = activiteRepository.getDureeByPeriodAndSports(utilisateur, start, end, sports);
            }
            result.add(new ChartData(String.valueOf(i), value != null ? value : 0));
        }
    } else if ("annee".equals(periode)) {
        for (int i = 1; i <= 12; i++) {
            LocalDateTime start = LocalDateTime.now().withMonth(i).withDayOfMonth(1).withHour(0).withMinute(0);
            LocalDateTime end = start.withMonth(i).withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59);
            Integer value;
            if ("calories".equals(metric)) {
                Float calories = activiteRepository.getCaloriesByPeriodAndSports(utilisateur, start, end, sports);
                value = calories != null ? calories.intValue() : 0;
            } else {
                value = activiteRepository.getDureeByPeriodAndSports(utilisateur, start, end, sports);
            }
            result.add(new ChartData(getMonthName(i), value != null ? value : 0));
        }
    }
    
    return result;
}

}