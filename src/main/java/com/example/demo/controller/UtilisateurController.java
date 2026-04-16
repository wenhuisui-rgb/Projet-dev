package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import java.util.*;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ObjectifService objectifService;

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private AmitieService amitieService;

    // =========================
    // 🔐 CONNEXION
    // =========================
    @GetMapping("/connexion")
    public String pageConnexion() {
        return "connexion";
    }

    @PostMapping("/connexion")
    public String connecter(@RequestParam String email,
                            @RequestParam String motDePasse,
                            HttpSession session,
                            Model model) {

        Utilisateur utilisateur = utilisateurService.authentifier(email, motDePasse);

        if (utilisateur != null) {
            session.setAttribute("utilisateur", utilisateur);
            return "redirect:/profil";
        }

        model.addAttribute("erreur", "Email ou mot de passe incorrect.");
        return "connexion";
    }

    // =========================
    // 📝 INSCRIPTION
    // =========================
    @GetMapping("/inscription")
    public String pageInscription(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(@ModelAttribute Utilisateur utilisateur,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        if (utilisateurService.emailExiste(utilisateur.getEmail())) {
            model.addAttribute("erreur", "Cet email est déjà utilisé.");
            return "inscription";
        }

        if (utilisateurService.pseudoExiste(utilisateur.getPseudo())) {
            model.addAttribute("erreur", "Ce pseudo est déjà pris.");
            return "inscription";
        }

        utilisateurService.inscrireUtilisateur(utilisateur);

        redirectAttributes.addFlashAttribute("success", "Inscription réussie !");
        return "redirect:/connexion";
    }

    // =========================
    // 🚪 DECONNEXION
    // =========================
    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/connexion";
    }

    // =========================
    // 👤 PROFIL
    // =========================
    @Transactional(readOnly = true)
    @GetMapping("/profil")
    public String afficherProfil(@RequestParam(required = false) Long userId,
                                 @RequestParam(defaultValue = "0") int page,
                                 HttpSession session,
                                 Model model) {

        Utilisateur currentUser =
                (Utilisateur) session.getAttribute("utilisateur");

        if (currentUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur profilUser;

        boolean isOwner = (userId == null || userId.equals(currentUser.getId()));

        profilUser = isOwner
                ? utilisateurService.findById(currentUser.getId())
                : utilisateurService.findById(userId);

        if (profilUser == null) {
            return "redirect:/profil";
        }

        Map<Long, Float> progressions = new HashMap<>();

        if (profilUser.getObjectifs() != null) {
            for (Objectif obj : profilUser.getObjectifs()) {
                Float progression =
                        objectifService.getPourcentageObjectif(obj, profilUser);
                progressions.put(obj.getId(), progression);
            }
        }

        Page<Activite> activitePage =
                activiteService.getActivitesPaginees(profilUser, page, 10);

        model.addAttribute("utilisateur", profilUser);
        model.addAttribute("objectifProgressions", progressions);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("activitePage", activitePage);

        return "profil";
    }

    // =========================
    // ✏️ MODIFIER PROFIL
    // =========================
    @GetMapping("/profil/modifier")
    public String modifierProfilPage(HttpSession session, Model model) {

        Utilisateur user =
                (Utilisateur) session.getAttribute("utilisateur");

        if (user == null) {
            return "redirect:/connexion";
        }

        model.addAttribute("utilisateur",
                utilisateurService.findById(user.getId()));

        return "modifierProfil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(@ModelAttribute Utilisateur utilisateurModifie,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Utilisateur currentUser =
                (Utilisateur) session.getAttribute("utilisateur");

        if (currentUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur dbUser =
                utilisateurService.findById(currentUser.getId());

        List<TypeSport> sports = utilisateurModifie.getPreferencesSports();
        if (sports == null) sports = new ArrayList<>();

        if (sports.size() > 4) {
            redirectAttributes.addFlashAttribute("erreur", "Max 4 sports");
            return "redirect:/profil/modifier";
        }

        dbUser.setPseudo(utilisateurModifie.getPseudo());
        dbUser.setEmail(utilisateurModifie.getEmail());
        dbUser.setSexe(utilisateurModifie.getSexe());
        dbUser.setAge(utilisateurModifie.getAge());
        dbUser.setTaille(utilisateurModifie.getTaille());
        dbUser.setPoids(utilisateurModifie.getPoids());
        dbUser.setNiveauPratique(utilisateurModifie.getNiveauPratique());
        dbUser.setPreferencesSports(sports);

        utilisateurService.updateUtilisateur(dbUser);

        session.setAttribute("utilisateur", dbUser);

        redirectAttributes.addFlashAttribute("success", "Profil mis à jour");
        return "redirect:/profil";
    }

    // =========================
    // 👥 MES AMIS (FINAL PROPRE)
    // =========================
   @GetMapping("/mesAmis")
public String mesAmis(@RequestParam(required = false) String search,
                      HttpSession session,
                      Model model) {

    Utilisateur sessionUser =
            (Utilisateur) session.getAttribute("utilisateur");

    if (sessionUser == null) {
        return "redirect:/connexion";
    }

    Utilisateur user =
            utilisateurService.findById(sessionUser.getId());

    // ❤️ amis (ACCEPTÉS)
    model.addAttribute("amis", amitieService.getAmis(user));

    // ⏳ demandes envoyées (pour bouton dynamique)
    model.addAttribute("demandesEnvoyees",
            amitieService.getDemandesEnvoyeesIds(user));

    // 📩 demandes reçues
    model.addAttribute("demandesRecues",
            amitieService.getDemandesRecues(user));

    // 🔍 recherche utilisateurs
    if (search != null && !search.isEmpty()) {
        model.addAttribute("resultats",
                utilisateurService.rechercherParPseudo(search, user.getId()));
    }

    return "mesAmis";
}
}