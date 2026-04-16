package com.example.demo.controller;

import com.example.demo.dto.UtilisateurFormDTO;
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
        // 使用 DTO 替代 Entity 传给前端
        model.addAttribute(ATTR_UTILISATEUR, new UtilisateurFormDTO());
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(@ModelAttribute UtilisateurFormDTO dto, // Sonar Fix: 使用 DTO
                           RedirectAttributes redirectAttributes,
                           Model model) {

        if (utilisateurService.emailExiste(dto.getEmail())) {
            model.addAttribute("erreur", "Cet email est déjà utilisé.");
            return "inscription";
        }

        if (utilisateurService.pseudoExiste(dto.getPseudo())) {
            model.addAttribute("erreur", "Ce pseudo est déjà pris.");
            return "inscription";
        }

        // 核心安全操作：把 DTO 的安全数据，手动转移到真正的 Entity 里
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setPseudo(dto.getPseudo());
        nouvelUtilisateur.setEmail(dto.getEmail());
        nouvelUtilisateur.setMotDePasse(dto.getMotDePasse());

        utilisateurService.inscrireUtilisateur(nouvelUtilisateur);

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

        Utilisateur dbUser = utilisateurService.findById(user.getId());
        
        // 提取数据库数据放到 DTO 里给前端展示
        UtilisateurFormDTO dto = new UtilisateurFormDTO();
        dto.setPseudo(dbUser.getPseudo());
        dto.setEmail(dbUser.getEmail());
        dto.setSexe(dbUser.getSexe());
        dto.setAge(dbUser.getAge());
        dto.setTaille(dbUser.getTaille());
        dto.setPoids(dbUser.getPoids());
        dto.setNiveauPratique(dbUser.getNiveauPratique());
        dto.setPreferencesSports(dbUser.getPreferencesSports());

        model.addAttribute(ATTR_UTILISATEUR, dto);

        return "modifierProfil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(@ModelAttribute UtilisateurFormDTO dto, // Sonar Fix: 使用 DTO
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Utilisateur currentUser = (Utilisateur) session.getAttribute(ATTR_UTILISATEUR);
        if (currentUser == null) {
            return "redirect:/connexion";
        }

        Utilisateur dbUser = utilisateurService.findById(currentUser.getId());

        List<TypeSport> sports = dto.getPreferencesSports();
        if (sports == null) sports = new java.util.ArrayList<>();

        if (sports.size() > 4) {
            redirectAttributes.addFlashAttribute("erreur", "Max 4 sports");
            return "redirect:/profil/modifier";
        }

        // 把前端传来的安全数据更新到 Entity 中
        dbUser.setPseudo(dto.getPseudo());
        dbUser.setEmail(dto.getEmail());
        dbUser.setSexe(dto.getSexe());
        dbUser.setAge(dto.getAge());
        dbUser.setTaille(dto.getTaille());
        dbUser.setPoids(dto.getPoids());
        dbUser.setNiveauPratique(dto.getNiveauPratique());
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