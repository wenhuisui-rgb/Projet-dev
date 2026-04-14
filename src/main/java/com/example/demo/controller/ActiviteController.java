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
    public String dashboard(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
    
    // 重新加载用户信息（包含 objectifs）
    utilisateur = utilisateurService.findById(utilisateur.getId());
    
    model.addAttribute("activites", activiteService.getActivitesParUtilisateur(utilisateur));
    model.addAttribute("stats", activiteService.getStatsDashboard(utilisateur));
    model.addAttribute("dernieresActivites", activiteService.getDernieresActivites(utilisateur));
    
    // 添加 objectifs 数据
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

    @GetMapping("/activites/nouvelle")
    public String nouvelleActivite(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
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
        return "redirect:/dashboard";
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
            return "redirect:/dashboard";
        }
        
        if (!activite.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette activité");
            return "redirect:/dashboard";
        }
        
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
            return "redirect:/dashboard";
        }
        
        if (!activite.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette activité");
            return "redirect:/dashboard";
        }
        
        model.addAttribute("activite", activite);
        model.addAttribute("typesSport", TypeSport.values());
        return "editActivite";
    }

    @PostMapping("/activites/update/{id}")
    public String updateActivite(@PathVariable Long id,
                                  @ModelAttribute Activite activiteModifiee,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        Activite existante = activiteService.getActiviteParId(id);
        if (existante == null || !existante.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Impossible de modifier cette activité");
            return "redirect:/dashboard";
        }
        
   
        activiteService.updateActivite(id, activiteModifiee, utilisateur.getPoids());

        if (activiteModifiee.getLocalisation() != null && !activiteModifiee.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activiteModifiee.getLocalisation());
            Activite updated = activiteService.getActiviteParId(id);
            updated.setMeteo(meteo);
            activiteService.sauvegarderActivite(updated, utilisateur.getPoids());
        }
        
        redirectAttributes.addFlashAttribute("success", "Activité modifiée avec succès !");
        return "redirect:/activites/{id}";
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
            return "redirect:/dashboard";
        }
        
        activiteService.supprimerActivite(id);
        redirectAttributes.addFlashAttribute("success", "Activité supprimée avec succès !");
        return "redirect:/dashboard";
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