package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.ParticipationChallenge;
import com.example.demo.model.TypeSport;
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

@Controller
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private ParticipationChallengeService participationService;

    @GetMapping("/challenges")
    public String listeChallenges(@RequestParam(required = false) TypeSport typeSport,
                                  HttpSession session,
                                  Model model) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        model.addAttribute("challenges",
                typeSport != null
                        ? challengeService.findByTypeSport(typeSport)
                        : challengeService.getAllChallenges()
        );

        model.addAttribute("types", TypeSport.values());
        model.addAttribute("selectedType", typeSport);
        model.addAttribute("utilisateur", utilisateur);

        return "challenges";
    }

    @GetMapping("/challenges/create")
    public String createPage(HttpSession session, Model model) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        model.addAttribute("challenge", new Challenge());
        model.addAttribute("types", TypeSport.values());
        model.addAttribute("utilisateur", utilisateur);

        return "createChallenge";
    }

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

        challengeService.creerChallenge(
                challenge.getTitre(),
                challenge.getTypeSport(),
                challenge.getDateDebut(),
                challenge.getDateFin(),
                utilisateur
        );

        redirectAttributes.addFlashAttribute("success", "Challenge créé avec succès !");
        return "redirect:/challenges";
    }

    @PostMapping("/challenges/{id}/rejoindre")
    public String rejoindreChallenge(@PathVariable Long id,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Challenge challenge = challengeService.getChallengeById(id);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error", "Challenge non trouvé.");
            return "redirect:/challenges";
        }

        participationService.rejoindreChallenge(utilisateur, challenge);

        redirectAttributes.addFlashAttribute("success", "Vous avez rejoint le challenge !");
        return "redirect:/challenges";
    }

    @PostMapping("/challenges/{id}/quitter")
    public String quitterChallenge(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) return "redirect:/connexion";

        Challenge challenge = challengeService.getChallengeById(id);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("error", "Challenge non trouvé.");
            return "redirect:/challenges";
        }

        participationService.quitterChallenge(utilisateur, challenge);

        redirectAttributes.addFlashAttribute("success", "Vous avez quitté le challenge !");
        return "redirect:/challenges";
    }

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
        
        // 获取参与者排名
        List<ParticipationChallenge> classement = participationService.obtenirClassement(id);
        
        model.addAttribute("challenge", challenge);
        model.addAttribute("classement", classement);
        model.addAttribute("utilisateur", utilisateur);
        
        return "detailChallenge";
    }
}