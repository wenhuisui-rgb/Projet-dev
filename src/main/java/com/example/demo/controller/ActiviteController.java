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

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false, defaultValue = "semaine") String periode,
                            HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

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
        
        // 添加图表数据 - 即使没有数据也給空列表
        List<String> chartLabels = new ArrayList<>();
        List<Integer> chartValues = new ArrayList<>();
        
        if (utilisateur.getActivites() != null && !utilisateur.getActivites().isEmpty()) {
            // 有活动时才计算图表数据
            List<ChartData> chartData = activiteService.getChartData(utilisateur, periode);
            for (ChartData data : chartData) {
                chartLabels.add(data.getLabel());
                chartValues.add(data.getValue());
            }
        } else {
            // 没有活动时给默认的7天数据（都是0）
            for (int i = 6; i >= 0; i--) {
                chartLabels.add(getDayName(i));
                chartValues.add(0);
            }
        }
        
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);
        
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

    @GetMapping("/api/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(@RequestParam String periode,
                                            @RequestParam(required = false) List<String> sports,
                                            HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return null;
        }
        
        // 重新加载用户，确保所有懒加载集合可用
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
                        // 忽略无效值
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
                        continue;
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

    private String getDayName(int daysAgo) {
        LocalDateTime date = LocalDateTime.now().minusDays(daysAgo);
        return date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.FRENCH);
    }


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
        
        if (activite.getLocalisation() != null && !activite.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activite.getLocalisation());
            activite.setMeteo(meteo);
        }
        
        activiteService.sauvegarderActivite(activite, utilisateur.getPoids());
        
        redirectAttributes.addFlashAttribute("success", "Activité enregistrée avec succès !");
        return "redirect:/profil";
    }

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

    @PostMapping("/activites/update/{id}")
public String updateActivite(@PathVariable Long id,
                              @ModelAttribute Activite activiteModifiee,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
    Utilisateur utilisateurSession = (Utilisateur) session.getAttribute("utilisateur");
    if (utilisateurSession == null) {
        return "redirect:/connexion";
    }
    
    // 1. 关键修复：从数据库重新拉取用户数据，确保能拿到用户的最新体重 (Poids)
    Utilisateur utilisateur = utilisateurService.findById(utilisateurSession.getId());

    Activite existante = activiteService.getActiviteParId(id);
    if (existante == null || !existante.getUtilisateur().getId().equals(utilisateur.getId())) {
        redirectAttributes.addFlashAttribute("error", "Impossible de modifier cette activité");
        return "redirect:/profil"; 
    }

    // 处理天气的逻辑保持不变
    boolean isLocationChanged = (activiteModifiee.getLocalisation() != null && 
                               !activiteModifiee.getLocalisation().equals(existante.getLocalisation()));
    if (isLocationChanged && !activiteModifiee.getLocalisation().isEmpty()) {
        String meteo = meteoService.getMeteoParLocalisation(activiteModifiee.getLocalisation());
        activiteModifiee.setMeteo(meteo); 
    } else {
        activiteModifiee.setMeteo(existante.getMeteo()); 
    }
    
    // 2. 关键修复：加一层非空判断。防止前端表单未提交这些关键字段导致它们变成 null
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
    
    // 调用 Service 进行保存。Service内部会自动调用 calculerCalories 重新计算并覆盖旧卡路里
    activiteService.sauvegarderActivite(existante, utilisateur.getPoids());
    
    redirectAttributes.addFlashAttribute("success", "Activité modifiée avec succès !");
    return "redirect:/profil";
}

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