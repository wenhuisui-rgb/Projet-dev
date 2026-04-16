package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Badge;
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
import com.example.demo.model.ObtentionBadge;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.ObtentionBadgeRepository;
import java.util.Objects;
import java.util.Optional;

/**
 * Service central gérant les activités sportives des utilisateurs.
 * <p>
 * Ce service est le cœur du suivi sportif. Il gère :
 * <ul>
 * <li>L'enregistrement et la mise à jour des activités (avec calcul automatique des calories).</li>
 * <li>Le déclenchement automatique de l'attribution des badges basés sur les jalons (milestones).</li>
 * <li>La génération de données statistiques et de graphiques (ChartData) pour le tableau de bord (Dashboard).</li>
 * </ul>
 */
@Service
public class ActiviteService {

    @Autowired
    private ActiviteRepository activiteRepository;

    @Autowired
    private ParticipationChallengeService participationChallengeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private ObtentionBadgeRepository obtentionBadgeRepository;

    /**
     * Sauvegarde une nouvelle activité sportive.
     * Calcule automatiquement les calories brûlées en fonction du poids de l'utilisateur,
     * puis déclenche la vérification des badges potentiellement débloqués.
     *
     * @param activite         L'activité à sauvegarder
     * @param poidsUtilisateur Le poids actuel de l'utilisateur (nécessaire pour le calcul des calories)
     * @return L'activité persistée
     */
    @Transactional
    public Activite sauvegarderActivite(Activite activite, Float poidsUtilisateur) {
        Float caloriesCalculees = activite.calculerCalories(poidsUtilisateur);
        activite.setCalories(caloriesCalculees);
        Activite saved = activiteRepository.save(activite);
        
        verifierEtAttribuerBadges(saved.getUtilisateur());
        
        return saved;
    }

    /**
     * Analyse l'historique complet des activités d'un utilisateur pour vérifier
     * s'il a atteint de nouveaux jalons (milestones) et lui attribuer les badges correspondants.
     *
     * @param utilisateur L'utilisateur à évaluer
     */
    private void verifierEtAttribuerBadges(Utilisateur utilisateur) {
        List<Activite> activites = activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur);
        
        Float totalDistance = activites.stream()
            .map(Activite::getDistance)
            .filter(Objects::nonNull)
            .reduce(0f, Float::sum);
        
        int totalActivites = activites.size();
        
        int totalMinutes = activites.stream()
            .map(Activite::getDuree)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();
        
        Map<TypeSport, Float> maxDistanceBySport = new HashMap<>();
        Map<TypeSport, Float> totalDistanceBySport = new HashMap<>();
        Map<TypeSport, Integer> totalActivitesBySport = new HashMap<>();
        
        for (TypeSport sport : TypeSport.values()) {
            maxDistanceBySport.put(sport, 0f);
            totalDistanceBySport.put(sport, 0f);
            totalActivitesBySport.put(sport, 0);
        }
        
        for (Activite a : activites) {
            if (a.getTypeSport() != null) {
                TypeSport sport = a.getTypeSport();
                totalActivitesBySport.put(sport, totalActivitesBySport.get(sport) + 1);
                
                if (a.getDistance() != null) {
                    if (a.getDistance() > maxDistanceBySport.get(sport)) {
                        maxDistanceBySport.put(sport, a.getDistance());
                    }
                    totalDistanceBySport.put(sport, totalDistanceBySport.get(sport) + a.getDistance());
                }
            }
        }
        
        // Jalons globaux
        checkAndAwardBadge(utilisateur, "PREMIER_PAS", totalActivites >= 1);
        checkAndAwardBadge(utilisateur, "SPORTIF_10", totalActivites >= 10);
        checkAndAwardBadge(utilisateur, "SPORTIF_50", totalActivites >= 50);
        checkAndAwardBadge(utilisateur, "TOTAL_100KM", totalDistance >= 100);
        checkAndAwardBadge(utilisateur, "TOTAL_500KM", totalDistance >= 500);
        checkAndAwardBadge(utilisateur, "TOTAL_1000MIN", totalMinutes >= 1000);
        checkAndAwardBadge(utilisateur, "TOTAL_5000MIN", totalMinutes >= 5000);
        
        // Jalons par sport : COURSE
        checkAndAwardBadge(utilisateur, "COURSE_5KM", maxDistanceBySport.get(TypeSport.COURSE) >= 5);
        checkAndAwardBadge(utilisateur, "COURSE_10KM", maxDistanceBySport.get(TypeSport.COURSE) >= 10);
        checkAndAwardBadge(utilisateur, "COURSE_SEMI", maxDistanceBySport.get(TypeSport.COURSE) >= 21.1);
        checkAndAwardBadge(utilisateur, "COURSE_MARATHON", maxDistanceBySport.get(TypeSport.COURSE) >= 42.2);
        checkAndAwardBadge(utilisateur, "COURSE_100KM", totalDistanceBySport.get(TypeSport.COURSE) >= 100);
        
        // Jalons par sport : NATATION
        checkAndAwardBadge(utilisateur, "NATATION_1KM", maxDistanceBySport.get(TypeSport.NATATION) >= 1);
        checkAndAwardBadge(utilisateur, "NATATION_2KM", maxDistanceBySport.get(TypeSport.NATATION) >= 2);
        checkAndAwardBadge(utilisateur, "NATATION_5KM", maxDistanceBySport.get(TypeSport.NATATION) >= 5);
        checkAndAwardBadge(utilisateur, "NATATION_10KM", maxDistanceBySport.get(TypeSport.NATATION) >= 10);
        
        // Jalons par sport : VELO
        checkAndAwardBadge(utilisateur, "VELO_50KM", maxDistanceBySport.get(TypeSport.VELO) >= 50);
        checkAndAwardBadge(utilisateur, "VELO_100KM", maxDistanceBySport.get(TypeSport.VELO) >= 100);
        checkAndAwardBadge(utilisateur, "VELO_200KM", maxDistanceBySport.get(TypeSport.VELO) >= 200);
 
        // Jalons par sport : MUSCULATION
        checkAndAwardBadge(utilisateur, "MUSCULATION_10", totalActivitesBySport.get(TypeSport.MUSCULATION) >= 10);
        checkAndAwardBadge(utilisateur, "MUSCULATION_50", totalActivitesBySport.get(TypeSport.MUSCULATION) >= 50);
        
        // Jalons par sport : YOGA
        checkAndAwardBadge(utilisateur, "YOGA_10", totalActivitesBySport.get(TypeSport.YOGA) >= 10);
        checkAndAwardBadge(utilisateur, "YOGA_50", totalActivitesBySport.get(TypeSport.YOGA) >= 50);
        
        // Jalons par sport : RANDONNEE
        checkAndAwardBadge(utilisateur, "RANDONNEE_10KM", maxDistanceBySport.get(TypeSport.RANDONNEE) >= 10);
        checkAndAwardBadge(utilisateur, "RANDONNEE_20KM", maxDistanceBySport.get(TypeSport.RANDONNEE) >= 20);
    }

    /**
     * Méthode utilitaire pour vérifier l'existence d'un badge et l'attribuer s'il n'est pas déjà possédé.
     */
    private void checkAndAwardBadge(Utilisateur utilisateur, String badgeNom, boolean condition) {
        if (condition) {
            Optional<Badge> badgeOpt = badgeRepository.findByNom(badgeNom);
            if (badgeOpt.isPresent()) {
                Badge badge = badgeOpt.get();
                boolean dejaObtenu = obtentionBadgeRepository.existsByUtilisateurIdAndBadgeId(utilisateur.getId(), badge.getId());
                if (!dejaObtenu) {
                    ObtentionBadge obtention = new ObtentionBadge();
                    obtention.setUtilisateur(utilisateur);
                    obtention.setBadge(badge);
                    obtention.setDateObtention(LocalDateTime.now());
                    obtentionBadgeRepository.save(obtention);
                    
                    System.out.println("Félicitations ! " + utilisateur.getPseudo() + " a obtenu le badge: " + badgeNom);
                }
            }
        }
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

    /**
     * Compile un dictionnaire de statistiques globales pour alimenter le tableau de bord (Dashboard).
     *
     * @param utilisateur L'utilisateur concerné
     * @return Une Map contenant différentes clés (totalDistance, totalDuree, etc.)
     */
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

    /**
     * Met à jour une activité existante, recalcule les calories, et met à jour
     * les scores des challenges actifs grâce à la nouvelle valeur.
     *
     * @param id                L'identifiant de l'activité
     * @param nouvelleActivite  L'objet contenant les nouvelles données
     * @param poidsUtilisateur  Le poids actuel de l'utilisateur
     * @return L'activité modifiée
     * @see ParticipationChallengeService#actualiserScoresApresActivite(Activite)
     */
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
            
            Activite savedActivite = activiteRepository.save(existante);
            
            // Actualise les challenges après une modification
            participationChallengeService.actualiserScoresApresActivite(savedActivite);
            
            return savedActivite;
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

    /**
     * Récupère les activités d'un utilisateur sous forme de page (Pagination).
     */
    public Page<Activite> getActivitesPaginees(Utilisateur utilisateur, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur, pageable);
    }

    /**
     * Prépare les données pour les graphiques du frontend en fonction de la période choisie.
     * Basé sur la durée de l'activité (en minutes).
     *
     * @param utilisateur L'utilisateur concerné
     * @param periode     "semaine", "mois", ou "annee"
     * @return Une liste d'objets {@link ChartData} formatés pour les graphes
     */
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

    /**
     * Génère les données de graphique filtrées par sports spécifiques et selon une métrique (calories ou durée).
     *
     * @param utilisateur L'utilisateur concerné
     * @param periode     "semaine", "mois", ou "annee"
     * @param sports      Liste des sports à inclure dans les calculs
     * @param metric      "calories" ou autre (par défaut, la durée en minutes)
     * @return Liste d'objets {@link ChartData}
     */
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