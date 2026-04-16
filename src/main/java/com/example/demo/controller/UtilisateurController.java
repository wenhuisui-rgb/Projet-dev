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

/**
 * Contrôleur principal gérant l'authentification et les profils utilisateurs.
 * <p>
 * Il prend en charge :
 * - L'inscription, la connexion et la déconnexion.
 * - L'affichage et la modification du profil (y compris le profil d'autres utilisateurs).
 * - L'affichage de la page de gestion des amis (recherche, liste, demandes).
 */
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
    // 🔐 CONNEXION (Connexion / Login)
    // =========================

    /**
     * Affiche la page de connexion.
     *
     * @return La vue Thymeleaf {@code "connexion"}
     */
    @GetMapping("/connexion")
    public String pageConnexion() {
        return "connexion";
    }

    /**
     * Traite la soumission du formulaire de connexion.
     *
     * @param email      L'adresse email saisie
     * @param motDePasse Le mot de passe saisi
     * @param session    La session HTTP pour y stocker l'utilisateur s'il est authentifié
     * @param model      Le modèle pour renvoyer des messages d'erreur si besoin
     * @return Une redirection vers le profil ({@code "/profil"}) en cas de succès, sinon recharge la vue {@code "connexion"}
     */
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
    // 📝 INSCRIPTION (Inscription / Register)
    // =========================

    /**
     * Affiche la page d'inscription.
     *
     * @param model Le conteneur de données pour injecter un objet Utilisateur vide
     * @return La vue Thymeleaf {@code "inscription"}
     */
    @GetMapping("/inscription")
    public String pageInscription(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "inscription";
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     * Vérifie l'unicité de l'email et du pseudo avant de sauvegarder.
     *
     * @param utilisateur        L'objet Utilisateur peuplé par le formulaire
     * @param redirectAttributes Pour afficher un message flash après redirection
     * @param model              Pour afficher les erreurs en direct sur la page
     * @return Une redirection vers la connexion en cas de succès, sinon recharge la vue {@code "inscription"}
     */
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
    // 🚪 DECONNEXION (Déconnexion / Logout)
    // =========================

    /**
     * Déconnecte l'utilisateur en invalidant sa session HTTP.
     *
     * @param session La session HTTP actuelle
     * @return Une redirection vers la page de connexion
     */
    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/connexion";
    }

    // =========================
    // 👤 PROFIL (Profil utilisateur)
    // =========================

    /**
     * Affiche la page de profil.
     * Peut afficher le profil de l'utilisateur connecté ou celui d'un autre utilisateur (via le paramètre userId).
     *
     * @param userId  L'identifiant de l'utilisateur ciblé (optionnel)
     * @param page    Le numéro de la page pour la pagination des activités
     * @param session La session HTTP
     * @param model   Le modèle Thymeleaf
     * @return La vue Thymeleaf {@code "profil"}
     */
    @Transactional(readOnly = true)
    @GetMapping("/profil")
    public String afficherProfil(@RequestParam(required = false) Long userId,
                                 @RequestParam(defaultValue = "0") int page,
                                 HttpSession session,
                                 Model model) {

        Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");

        if (currentUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur profilUser;

        // Détermine si on regarde son propre profil ou celui de quelqu'un d'autre
        boolean isOwner = (userId == null || userId.equals(currentUser.getId()));

        profilUser = isOwner
                ? utilisateurService.findById(currentUser.getId())
                : utilisateurService.findById(userId);

        if (profilUser == null) {
            return "redirect:/profil";
        }

        // Calcul dynamique de la progression des objectifs pour l'affichage
        Map<Long, Float> progressions = new HashMap<>();

        if (profilUser.getObjectifs() != null) {
            for (Objectif obj : profilUser.getObjectifs()) {
                Float progression = objectifService.getPourcentageObjectif(obj, profilUser);
                progressions.put(obj.getId(), progression);
            }
        }

        Page<Activite> activitePage = activiteService.getActivitesPaginees(profilUser, page, 10);

        model.addAttribute("utilisateur", profilUser);
        model.addAttribute("objectifProgressions", progressions);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("activitePage", activitePage);

        return "profil";
    }

    // =========================
    // ✏️ MODIFIER PROFIL
    // =========================

    /**
     * Affiche le formulaire de modification du profil de l'utilisateur courant.
     *
     * @return La vue Thymeleaf {@code "modifierProfil"}
     */
    @GetMapping("/profil/modifier")
    public String modifierProfilPage(HttpSession session, Model model) {

        Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");

        if (user == null) {
            return "redirect:/connexion";
        }

        model.addAttribute("utilisateur", utilisateurService.findById(user.getId()));

        return "modifierProfil";
    }

    /**
     * Traite la soumission du formulaire de modification de profil.
     * Vérifie notamment que l'utilisateur n'a pas sélectionné plus de 4 sports préférés.
     *
     * @return Une redirection vers la page de profil actualisée
     */
    @PostMapping("/profil/modifier")
    public String modifierProfil(@ModelAttribute Utilisateur utilisateurModifie,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");

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

        // Mise à jour manuelle des champs modifiables
        dbUser.setPseudo(utilisateurModifie.getPseudo());
        dbUser.setEmail(utilisateurModifie.getEmail());
        dbUser.setSexe(utilisateurModifie.getSexe());
        dbUser.setAge(utilisateurModifie.getAge());
        dbUser.setTaille(utilisateurModifie.getTaille());
        dbUser.setPoids(utilisateurModifie.getPoids());
        dbUser.setNiveauPratique(utilisateurModifie.getNiveauPratique());
        dbUser.setPreferencesSports(sports);

        utilisateurService.updateUtilisateur(dbUser);

        // Mettre à jour la session avec les nouvelles données
        session.setAttribute("utilisateur", dbUser);

        redirectAttributes.addFlashAttribute("success", "Profil mis à jour");
        return "redirect:/profil";
    }

    // =========================
    // 👥 MES AMIS
    // =========================

    /**
     * Affiche la page de gestion des amis (recherche, demandes en attente, liste de contacts).
     *
     * @param search  Le terme de recherche optionnel pour trouver de nouveaux amis
     * @param session La session HTTP
     * @param model   Le modèle Thymeleaf
     * @return La vue Thymeleaf {@code "mesAmis"}
     */
    @GetMapping("/mesAmis")
    public String mesAmis(@RequestParam(required = false) String search,
                          HttpSession session,
                          Model model) {

        Utilisateur sessionUser = (Utilisateur) session.getAttribute("utilisateur");

        if (sessionUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur user = utilisateurService.findById(sessionUser.getId());

        // ❤️ Liste des amis validés
        model.addAttribute("amis", user.getAmis());

        // ⏳ Liste des IDs pour savoir si on a déjà envoyé une demande (pour griser les boutons)
        model.addAttribute("demandesEnvoyees", amitieService.getDemandesEnvoyeesIds(user));

        // 📩 Demandes d'amis reçues (en attente d'acceptation/refus)
        model.addAttribute("demandesRecues", amitieService.getDemandesRecues(user));

        // 🔍 Résultats de recherche d'utilisateurs
        if (search != null && !search.isEmpty()) {
            model.addAttribute("resultats", utilisateurService.rechercherParPseudo(search, user.getId()));
        }

        return "mesAmis";
    }
}