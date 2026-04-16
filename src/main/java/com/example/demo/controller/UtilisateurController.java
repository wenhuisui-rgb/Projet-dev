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

    // Sonar Fix: 提取重复使用的魔法字符串为常量
    private static final String ATTR_UTILISATEUR = "utilisateur";

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ObjectifService objectifService;

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private AmitieService amitieService;

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
            session.setAttribute(ATTR_UTILISATEUR, utilisateur);
            return "redirect:/profil";
        }

        model.addAttribute("erreur", "Email ou mot de passe incorrect.");
        return "connexion";
    }

    @GetMapping("/inscription")
    public String pageInscription(Model model) {
        model.addAttribute(ATTR_UTILISATEUR, new Utilisateur());
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

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/connexion";
    }

    @Transactional(readOnly = true)
    @GetMapping("/profil")
    public String afficherProfil(@RequestParam(required = false) Long userId,
                                 @RequestParam(defaultValue = "0") int page,
                                 HttpSession session,
                                 Model model) {

        Utilisateur currentUser = (Utilisateur) session.getAttribute(ATTR_UTILISATEUR);

        if (currentUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur profilUser = (userId == null || userId.equals(currentUser.getId()))
                ? utilisateurService.findById(currentUser.getId())
                : utilisateurService.findById(userId);

        if (profilUser == null) {
            return "redirect:/profil";
        }

        Map<Long, Float> progressions = new HashMap<>();
        if (profilUser.getObjectifs() != null) {
            for (Objectif obj : profilUser.getObjectifs()) {
                Float progression = objectifService.getPourcentageObjectif(obj, profilUser);
                progressions.put(obj.getId(), progression);
            }
        }

        Page<Activite> activitePage = activiteService.getActivitesPaginees(profilUser, page, 10);

        model.addAttribute(ATTR_UTILISATEUR, profilUser);
        model.addAttribute("objectifProgressions", progressions);
        model.addAttribute("isOwner", (userId == null || userId.equals(currentUser.getId())));
        model.addAttribute("activitePage", activitePage);

        return "profil";
    }

    @GetMapping("/profil/modifier")
    public String modifierProfilPage(HttpSession session, Model model) {

        Utilisateur user = (Utilisateur) session.getAttribute(ATTR_UTILISATEUR);

        if (user == null) {
            return "redirect:/connexion";
        }

        model.addAttribute(ATTR_UTILISATEUR, utilisateurService.findById(user.getId()));

        return "modifierProfil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(@ModelAttribute Utilisateur utilisateurModifie,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Utilisateur currentUser = (Utilisateur) session.getAttribute(ATTR_UTILISATEUR);

        if (currentUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur dbUser = utilisateurService.findById(currentUser.getId());

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

        session.setAttribute(ATTR_UTILISATEUR, dbUser);

        redirectAttributes.addFlashAttribute("success", "Profil mis à jour");
        return "redirect:/profil";
    }

    @GetMapping("/mesAmis")
    public String mesAmis(@RequestParam(required = false) String search,
                          HttpSession session,
                          Model model) {

        Utilisateur sessionUser = (Utilisateur) session.getAttribute(ATTR_UTILISATEUR);

        if (sessionUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur user = utilisateurService.findById(sessionUser.getId());

        model.addAttribute("amis", user.getAmis());
        model.addAttribute("demandesEnvoyees", amitieService.getDemandesEnvoyeesIds(user));
        model.addAttribute("demandesRecues", amitieService.getDemandesRecues(user));

        if (search != null && !search.isEmpty()) {
            model.addAttribute("resultats", utilisateurService.rechercherParPseudo(search, user.getId()));
        }

        return "mesAmis";
    }
}