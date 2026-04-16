package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.ParticipationChallenge;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ParticipationChallengeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.Set;

/**
 * Contrôleur gérant les vues et les formulaires liés aux défis (Challenges).
 * <p>
 * Permet d'afficher la liste des défis, d'en créer de nouveaux, de les supprimer,
 * et de consulter les détails d'un défi spécifique (incluant le classement).
 */
@Controller
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private ParticipationChallengeService participationService;

    /**
     * Affiche la page globale recensant tous les challenges disponibles.
     * Injecte dans le modèle la liste des challenges (éventuellement filtrée par sport)
     * et l'ensemble des IDs des challenges que l'utilisateur a déjà rejoints.
     *
     * @param typeSport Le filtre optionnel par sport
     * @param session   La session HTTP
     * @param model     Le modèle Thymeleaf
     * @return La vue {@code "challenges"}
     */
    @GetMapping("/challenges")
    public String listeChallenges(@RequestParam(required = false) TypeSport typeSport,
                                  HttpSession session,
                                  Model model) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        List<Challenge> allChallenges = (typeSport != null) 
            ? challengeService.findByTypeSport(typeSport) 
            : challengeService.getAllChallenges();

        List<Challenge> userJoined = challengeService.findChallengesByUser(utilisateur);
        Set<Long> joinedChallengeIds = userJoined.stream()
                .map(Challenge::getId)
                .collect(Collectors.toSet());

        model.addAttribute("challenges", allChallenges);
        model.addAttribute("joinedChallengeIds", joinedChallengeIds); 
        model.addAttribute("types", TypeSport.values());
        model.addAttribute("selectedType", typeSport);
        model.addAttribute("utilisateur", utilisateur);

        return "challenges";
    }

    /**
     * Affiche le formulaire de création d'un nouveau challenge.
     *
     * @return La vue {@code "createChallenge"}
     */
    @GetMapping("/challenges/create")
    public String createPage(HttpSession session, Model model) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        model.addAttribute("challenge", new Challenge());
        model.addAttribute("types", TypeSport.values());
        model.addAttribute("unites", Unite.values());
        model.addAttribute("utilisateur", utilisateur);

        return "createChallenge";
    }

    /**
     * Traite la soumission du formulaire de création de challenge.
     * Inscrit automatiquement le créateur au challenge nouvellement créé.
     *
     * @return Une redirection vers {@code "/challenges"} en cas de succès, ou vers la page de création en cas d'erreur de date.
     */
    @PostMapping("/challenges/creer")
    public String creerChallenge(@ModelAttribute("challenge") Challenge challenge,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        if (challenge.getDateDebut().isBefore(LocalDate.now())
                || challenge.getDateFin().isBefore(challenge.getDateDebut())) {

            redirectAttributes.addFlashAttribute("error", "Les dates du challenge sont invalides.");
            return "redirect:/challenges/create";
        }

        Challenge savedChallenge = challengeService.creerChallenge(
                challenge.getTitre(),
                challenge.getTypeSport(),
                challenge.getDateDebut(),
                challenge.getDateFin(),
                utilisateur,
                challenge.getUnite(),
                challenge.getCible()
        );
        participationService.rejoindreChallenge(utilisateur, savedChallenge);

        redirectAttributes.addFlashAttribute("success", "Challenge créé avec succès !");
        return "redirect:/challenges";
    }

    /**
     * Traite la suppression d'un challenge.
     * Vérifie par sécurité que l'utilisateur qui supprime est bien le créateur.
     *
     * @return Une redirection vers la liste des challenges ({@code "/challenges"})
     */
    @PostMapping("/challenges/supprimer/{id}")
    public String supprimerChallenge(@PathVariable Long id, 
                                    HttpSession session, 
                                    RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Challenge challenge = challengeService.getChallengeById(id);
        
        if (challenge != null && challenge.getCreateur().getId().equals(utilisateur.getId())) {
            challengeService.supprimerChallenge(id);
            redirectAttributes.addFlashAttribute("success", "Challenge supprimé avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Vous n'avez pas l'autorisation de supprimer ce challenge.");
        }

        return "redirect:/challenges";
    }

    /**
     * Affiche la page listant uniquement les challenges auxquels l'utilisateur courant participe.
     *
     * @return La vue {@code "challenge"}
     */
    @GetMapping("/challenge")
    public String myChallenges(@RequestParam(required = false) TypeSport typeSport,
                           HttpSession session, 
                           Model model) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        List<Challenge> userChallenges = challengeService.findChallengesByUser(utilisateur);
        
        if (typeSport != null) {
            userChallenges = userChallenges.stream()
                    .filter(c -> c.getTypeSport() == typeSport)
                    .collect(Collectors.toList());
        }

        model.addAttribute("challenges", userChallenges);
        model.addAttribute("types", TypeSport.values());
        model.addAttribute("selectedType", typeSport);
        model.addAttribute("utilisateur", utilisateur);

        return "challenge";
    }

    /**
     * Affiche la page de détails d'un challenge spécifique, incluant le classement complet.
     *
     * @return La vue {@code "detailChallenge"}
     */
    @GetMapping("/challenges/detail/{id}")
    public String detailChallenge(@PathVariable Long id,
                                HttpSession session,
                                Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";
        
        Challenge challenge = challengeService.getChallengeById(id);
        if (challenge == null) {
            return "redirect:/challenges";
        }
       
        List<ParticipationChallenge> classement = participationService.obtenirClassement(id);
        boolean estInscrit = participationService.estDejaInscrit(utilisateur.getId(), id);
        
        model.addAttribute("challenge", challenge);
        model.addAttribute("classement", classement);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("estInscrit", estInscrit);
        
        return "detailChallenge";
    }
}