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

    /**
     * Afficher le tableau de bord
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        // Récupérer les activités de l'utilisateur
        model.addAttribute("activites", activiteService.getActivitesParUtilisateur(utilisateur));
        model.addAttribute("stats", activiteService.getStatsDashboard(utilisateur));
        model.addAttribute("dernieresActivites", activiteService.getDernieresActivites(utilisateur));
        
        return "dashboard";
    }

    /**
     * Afficher le formulaire pour créer une nouvelle activité
     */
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

    /**
     * Enregistrer une nouvelle activité
     */
    @PostMapping("/activites/sauvegarder")
    public String sauvegarderActivite(@ModelAttribute Activite activite,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        
        // Définir la date actuelle si non fournie
        if (activite.getDateActivite() == null) {
            activite.setDateActivite(LocalDateTime.now());
        }
        
        // Associer l'utilisateur
        activite.setUtilisateur(utilisateur);
        
        // Récupérer la météo (appel API externe)
        if (activite.getLocalisation() != null && !activite.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activite.getLocalisation());
            activite.setMeteo(meteo);
        }
        
        // Sauvegarder (le calcul des calories est fait automatiquement dans le service)
        activiteService.sauvegarderActivite(activite, utilisateur.getPoids());
        
        redirectAttributes.addFlashAttribute("success", "Activité enregistrée avec succès !");
        return "redirect:/dashboard";
    }

    /**
     * Consulter les détails d'une activité spécifique
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
            return "redirect:/dashboard";
        }
        
        // Vérifier que l'activité appartient à l'utilisateur
        if (!activite.getUtilisateur().getId().equals(utilisateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas accès à cette activité");
            return "redirect:/dashboard";
        }
        
        model.addAttribute("activite", activite);
        return "detailActivite";
    }

    /**
     * Afficher le formulaire de modification d'une activité
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

    /**
     * Mettre à jour une activité
     */
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
        
        // Mettre à jour
        activiteService.updateActivite(id, activiteModifiee, utilisateur.getPoids());
        
        // Mettre à jour la météo si la localisation a changé
        if (activiteModifiee.getLocalisation() != null && !activiteModifiee.getLocalisation().isEmpty()) {
            String meteo = meteoService.getMeteoParLocalisation(activiteModifiee.getLocalisation());
            Activite updated = activiteService.getActiviteParId(id);
            updated.setMeteo(meteo);
            activiteService.sauvegarderActivite(updated, utilisateur.getPoids());
        }
        
        redirectAttributes.addFlashAttribute("success", "Activité modifiée avec succès !");
        return "redirect:/activites/{id}";
    }

    /**
     * Supprimer une activité
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
            return "redirect:/dashboard";
        }
        
        activiteService.supprimerActivite(id);
        redirectAttributes.addFlashAttribute("success", "Activité supprimée avec succès !");
        return "redirect:/dashboard";
    }

    /**
     * API AJAX - Récupérer les statistiques pour les graphiques
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