package com.example.demo.controller;

import com.example.demo.model.Activite;
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

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
public class ActiviteController {

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private MeteoService meteoService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        model.addAttribute("activites", activiteService.getActivitesParUtilisateur(utilisateur));
        model.addAttribute("stats", activiteService.getStatsDashboard(utilisateur));
        model.addAttribute("dernieresActivites", activiteService.getDernieresActivites(utilisateur));
        
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
        
        model.addAttribute("activite", activite);
        model.addAttribute("typesSport", TypeSport.values());
        return "modifierActivite";
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
            return "redirect:/profil"; // 注意这里：最好重定向回原本所在的页面
        }

        // 优化：在更新前，检查位置是否发生了变化，如果变化了再去请求外部 API 获取天气
        boolean isLocationChanged = (activiteModifiee.getLocalisation() != null && 
                                   !activiteModifiee.getLocalisation().equals(existante.getLocalisation()));
        
        if (isLocationChanged && !activiteModifiee.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activiteModifiee.getLocalisation());
            activiteModifiee.setMeteo(meteo); // 将新天气设置到待更新的对象中
        } else {
            // 如果位置没变，保留原来的天气
            activiteModifiee.setMeteo(existante.getMeteo()); 
        }
        
        // 我们需要稍微修改一下 activiteService.updateActivite 来接收 meteo 参数，
        // 或者直接在这里把属性赋给 existante 然后调用 sauvegarderActivite。
        // 为了最少改动你的 Service，可以这样做：
        
        existante.setTypeSport(activiteModifiee.getTypeSport());
        existante.setDateActivite(activiteModifiee.getDateActivite());
        existante.setDuree(activiteModifiee.getDuree());
        existante.setDistance(activiteModifiee.getDistance());
        existante.setLocalisation(activiteModifiee.getLocalisation());
        existante.setEvaluation(activiteModifiee.getEvaluation());
        existante.setMeteo(activiteModifiee.getMeteo()); // 更新天气
        
        // 计算卡路里并保存 (仅执行一次数据库操作)
        activiteService.sauvegarderActivite(existante, utilisateur.getPoids());
        
        redirectAttributes.addFlashAttribute("success", "Activité modifiée avec succès !");
        return "redirect:/profil"; // 注意这里：最好重定向回原本所在的页面
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