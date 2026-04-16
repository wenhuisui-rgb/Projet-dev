package com.example.demo.controller;

import com.example.demo.model.Activite;
import com.example.demo.model.Objectif;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.MeteoService;
import com.example.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import com.example.demo.service.ObjectifService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.demo.model.ChartData;

/**
 * Contrôleur principal gérant le tableau de bord (Dashboard) et le cycle de vie des activités sportives.
 * <p>
 * Ce contrôleur s'occupe de l'affichage des statistiques globales, de la préparation des données 
 * pour les graphiques (via des routes MVC et REST), ainsi que de la création, modification 
 * et suppression des enregistrements d'activités.
 */
@Controller
public class ActiviteController {

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ObjectifService objectifService;

    @Autowired
    private MeteoService meteoService;

    /**
     * Affiche le tableau de bord principal de l'utilisateur.
     * Injecte une grande quantité de données agrégées dans le modèle pour générer les KPI, 
     * les listes d'activités récentes, l'état d'avancement des objectifs et les données du graphique par défaut.
     *
     * @param periode La période de temps pour le graphique ("semaine", "mois", "annee")
     * @param session La session HTTP contenant l'utilisateur connecté
     * @param model   Le conteneur de données pour la vue
     * @return La vue Thymeleaf {@code "dashboard"}
     */
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false, defaultValue = "semaine") String periode,
                            HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        // Rechargement depuis la base pour garantir la fraîcheur des données
        utilisateur = utilisateurService.findById(utilisateur.getId());
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("activites", activiteService.getActivitesParUtilisateur(utilisateur));
        model.addAttribute("stats", activiteService.getStatsDashboard(utilisateur));
        model.addAttribute("dernieresActivites", activiteService.getDernieresActivites(utilisateur));
        
        Integer totalMinutes = activiteService.getDureeTotale(utilisateur);
        model.addAttribute("totalMinutes", totalMinutes);
        
        Map<String, Integer> tempsParSport = new HashMap<>();
        for (TypeSport sport : TypeSport.values()) {
            List<Activite> activites = activiteService.getActivitesParType(utilisateur, sport);
            int minutes = activites.stream().mapToInt(Activite::getDuree).sum();
            if (minutes > 0) {
                tempsParSport.put(sport.name(), minutes);
            }
        }
        model.addAttribute("tempsParSport", tempsParSport);
        
        // Préparation des données initiales pour le composant graphique (Chart.js ou autre)
        List<String> chartLabels = new ArrayList<>();
        List<Integer> chartValues = new ArrayList<>();
        
        if (utilisateur.getActivites() != null && !utilisateur.getActivites().isEmpty()) {
            List<ChartData> chartData = activiteService.getChartData(utilisateur, periode);
            for (ChartData data : chartData) {
                chartLabels.add(data.getLabel());
                chartValues.add(data.getValue());
            }
        } else {
            // Remplissage par défaut (0) si l'utilisateur n'a aucune activité
            for (int i = 6; i >= 0; i--) {
                chartLabels.add(getDayName(i));
                chartValues.add(0);
            }
        }
        
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);
        
        // Calcul de la progression des objectifs
        Map<Long, Float> objectifProgressions = new HashMap<>();
        if (utilisateur.getObjectifs() != null) {
            for (Objectif obj : utilisateur.getObjectifs()) {
                Float progression = objectifService.getPourcentageObjectif(obj, utilisateur);
                objectifProgressions.put(obj.getId(), progression != null ? (float) Math.round(progression) : 0f);
            }
        }
        model.addAttribute("objectifProgressions", objectifProgressions);
        
        List<Map<String, Object>> objectifsAvecProgression = new ArrayList<>();
        if (utilisateur.getObjectifs() != null) {
            for (Objectif obj : utilisateur.getObjectifs()) {
                Map<String, Object> objMap = new HashMap<>();
                objMap.put("id", obj.getId());
                objMap.put("description", obj.getDescription());
                objMap.put("cible", obj.getCible());
                objMap.put("unite", obj.getUnite());
                Float progression = objectifService.getPourcentageObjectif(obj, utilisateur);
                objMap.put("progression", progression != null ? Math.round(progression) : 0);
                objectifsAvecProgression.add(objMap);
            }
        }
        model.addAttribute("objectifs", objectifsAvecProgression);

        return "dashboard";
    }

    /**
     * API REST (AJAX) : Récupère les données actualisées pour les graphiques du tableau de bord.
     * Permet au frontend de rafraîchir dynamiquement les courbes (ex: filtrer par sport ou par période).
     *
     * @param periode La période ("semaine", "mois", "annee")
     * @param sports  Liste optionnelle des sports sélectionnés pour le filtre
     * @param session La session HTTP
     * @return Un objet JSON contenant les labels, et les valeurs en minutes et en calories
     */
    @GetMapping("/api/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(@RequestParam String periode,
                                            @RequestParam(required = false) List<String> sports,
                                            HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return null;
        }
        
        utilisateur = utilisateurService.findById(utilisateur.getId());
        
        Map<String, Object> result = new HashMap<>();
        
        List<TypeSport> sportTypes = null;
        if (sports != null && !sports.isEmpty()) {
            sportTypes = new ArrayList<>();
            for (String s : sports) {
                if (s != null && !s.isEmpty()) {
                    try {
                        sportTypes.add(TypeSport.valueOf(s));
                    } catch (IllegalArgumentException e) {
                        // Ignorer les valeurs invalides envoyées par le client
                    }
                }
            }
            if (sportTypes.isEmpty()) {
                sportTypes = null;
            }
        }
        
        List<ChartData> minutesData = activiteService.getChartDataBySports(utilisateur, periode, sportTypes, "minutes");
        List<ChartData> caloriesData = activiteService.getChartDataBySports(utilisateur, periode, sportTypes, "calories");
        
        List<String> labels = new ArrayList<>();
        List<Integer> minutesValues = new ArrayList<>();
        List<Integer> caloriesValues = new ArrayList<>();
        
        for (ChartData data : minutesData) {
            labels.add(data.getLabel());
            minutesValues.add(data.getValue());
        }
        for (ChartData data : caloriesData) {
            caloriesValues.add(data.getValue());
        }
        
        List<Map<String, Object>> filteredObjectifs = new ArrayList<>();
        if (utilisateur.getObjectifs() != null) {
            for (Objectif obj : utilisateur.getObjectifs()) {
                if (sportTypes != null && !sportTypes.isEmpty()) {
                    if (obj.getTypeSport() != null && !sportTypes.contains(obj.getTypeSport())) {
                        continue; // Exclure l'objectif si le filtre de sport ne correspond pas
                    }
                }
                Map<String, Object> objMap = new HashMap<>();
                objMap.put("description", obj.getDescription());
                objMap.put("cible", obj.getCible());
                objMap.put("unite", obj.getUnite());
                Float progression = objectifService.getPourcentageObjectif(obj, utilisateur);
                objMap.put("progression", progression != null ? Math.round(progression) : 0);
                filteredObjectifs.add(objMap);
            }
        }
        result.put("objectifs", filteredObjectifs);
        result.put("labels", labels);
        result.put("minutes", minutesValues);
        result.put("calories", caloriesValues);
        
        return result;
    }

    /**
     * Utilitaire : Récupère le nom du jour en français par rapport à la date actuelle.
     * @param daysAgo Le nombre de jours en arrière
     * @return Le nom du jour
     */
    private String getDayName(int daysAgo) {
        LocalDateTime date = LocalDateTime.now().minusDays(daysAgo);
        return date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.FRENCH);
    }

    /**
     * Affiche le formulaire d'enregistrement d'une nouvelle activité sportive.
     *
     * @param session La session HTTP
     * @param model Le modèle Thymeleaf
     * @return La vue {@code "createActivite"}
     */
    @GetMapping("/activites/nouvelle")
    public String nouvelleActivite(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("activite", new Activite());
        model.addAttribute("typesSport", TypeSport.values());
        return "createActivite";
    }

    /**
     * Traite la soumission du formulaire pour sauvegarder une nouvelle activité.
     * Fait également appel au service Météo externe si une localisation est renseignée.
     *
     * @param activite L'activité à sauvegarder
     * @param session La session HTTP
     * @param redirectAttributes Les attributs pour les messages flash
     * @return Une redirection vers la page de profil
     */
    @PostMapping("/activites/sauvegarder")
    public String sauvegarderActivite(@ModelAttribute Activite activite,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        if (activite.getDateActivite() == null) {
            activite.setDateActivite(LocalDateTime.now());
        }
        
        activite.setUtilisateur(utilisateur);
        
        // Appel à l'API tierce (Open-Meteo) pour récupérer la météo locale
        if (activite.getLocalisation() != null && !activite.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activite.getLocalisation());
            activite.setMeteo(meteo);
        }
        
        activiteService.sauvegarderActivite(activite, utilisateur.getPoids());
        
        redirectAttributes.addFlashAttribute("success", "Activité enregistrée avec succès !");
        return "redirect:/profil";
    }

    /**
     * Affiche les détails complets d'une activité spécifique.
     *
     * @param id L'identifiant de l'activité
     * @param session La session HTTP
     * @param model Le modèle Thymeleaf
     * @param redirectAttributes Les attributs pour les messages flash
     * @return La vue {@code "detailActivite"}
     */
    @GetMapping("/activites/{id}")
    public String detailActivite(@PathVariable Long id, 
                                  HttpSession session, 
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        Activite activite = activiteService.getActiviteParId(id);
        if (activite == null) {
            redirectAttributes.addFlashAttribute("error", "Activité non trouvée");
            return "redirect:/profil";
        }
        
        if (!activite.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette activité");
            return "redirect:/profil";
        }
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("activite", activite);
        return "detailActivite";
    }

    /**
     * Affiche le formulaire de modification pour une activité existante.
     *
     * @param id L'identifiant de l'activité
     * @param session La session HTTP
     * @param model Le modèle Thymeleaf
     * @param redirectAttributes Les attributs pour les messages flash
     * @return La vue {@code "modifierActivite"}
     */
    @GetMapping("/activites/edit/{id}")
    public String editActivite(@PathVariable Long id,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        Activite activite = activiteService.getActiviteParId(id);
        if (activite == null) {
            redirectAttributes.addFlashAttribute("error", "Activité non trouvée");
            return "redirect:/profil";
        }
        
        if (!activite.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette activité");
            return "redirect:/profil";
        }
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("activite", activite);
        model.addAttribute("typesSport", TypeSport.values());
        return "modifierActivite";
    }

    /**
     * Traite la soumission du formulaire de modification.
     * Assure la mise à jour des calories et le rafraîchissement potentiel des données météorologiques.
     *
     * @param id L'identifiant de l'activité
     * @param activiteModifiee L'activité avec les nouvelles données
     * @param session La session HTTP
     * @param redirectAttributes Les attributs pour les messages flash
     * @return Une redirection vers la page de profil
     */
    @PostMapping("/activites/update/{id}")
    public String updateActivite(@PathVariable Long id,
                                  @ModelAttribute Activite activiteModifiee,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateurSession = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateurSession == null) {
            return "redirect:/connexion";
        }
        
        Utilisateur utilisateur = utilisateurService.findById(utilisateurSession.getId());

        Activite existante = activiteService.getActiviteParId(id);
        if (existante == null || !existante.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Impossible de modifier cette activité");
            return "redirect:/profil"; 
        }

        // Met à jour la météo uniquement si la localisation a été changée par l'utilisateur
        boolean isLocationChanged = (activiteModifiee.getLocalisation() != null && 
                                   !activiteModifiee.getLocalisation().equals(existante.getLocalisation()));
        if (isLocationChanged && !activiteModifiee.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activiteModifiee.getLocalisation());
            activiteModifiee.setMeteo(meteo); 
        } else {
            activiteModifiee.setMeteo(existante.getMeteo()); 
        }
        
        if (activiteModifiee.getTypeSport() != null) {
            existante.setTypeSport(activiteModifiee.getTypeSport());
        }
        if (activiteModifiee.getDuree() != null) {
            existante.setDuree(activiteModifiee.getDuree());
        }

        existante.setDateActivite(activiteModifiee.getDateActivite());
        existante.setDistance(activiteModifiee.getDistance());
        existante.setLocalisation(activiteModifiee.getLocalisation());
        existante.setEvaluation(activiteModifiee.getEvaluation());
        existante.setMeteo(activiteModifiee.getMeteo()); 
        
        // La sauvegarde déclenchera le recalcul automatique des calories brûlées
        activiteService.sauvegarderActivite(existante, utilisateur.getPoids());
        
        redirectAttributes.addFlashAttribute("success", "Activité modifiée avec succès !");
        return "redirect:/profil";
    }

    /**
     * Supprime une activité spécifique après vérification des permissions.
     *
     * @param id L'identifiant de l'activité
     * @param session La session HTTP
     * @param redirectAttributes Les attributs pour les messages flash
     * @return Une redirection vers la page de profil
     */
    @GetMapping("/activites/delete/{id}")
    public String deleteActivite(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        Activite activite = activiteService.getActiviteParId(id);
        if (activite == null || !activite.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cette activité");
            return "redirect:/profil";
        }
        
        activiteService.supprimerActivite(id);
        redirectAttributes.addFlashAttribute("success", "Activité supprimée avec succès !");
        return "redirect:/profil";
    }

    /**
     * API REST (AJAX) : Récupère les statistiques globales du dashboard sous forme JSON.
     *
     * @param session La session HTTP
     * @return Les statistiques agrégées (formatées en JSON par Spring)
     */
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public Object getDashboardStats(HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return null;
        }
        
        return activiteService.getStatsDashboard(utilisateur);
    }
}