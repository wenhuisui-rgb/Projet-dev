package com.example.demo.controller;

import com.example.demo.model.Utilisateur;
import com.example.demo.model.Activite;
import com.example.demo.model.Objectif;
import com.example.demo.model.TypeSport;
import com.example.demo.service.ObjectifService;
import com.example.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.demo.service.ActiviteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private ObjectifService objectifService;

    @Autowired
    private ActiviteService activiteService;

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
        } else {
            model.addAttribute("erreur", "Email ou mot de passe incorrect.");
            return "connexion";
        }
    }

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
        redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Veuillez vous connecter.");
        return "redirect:/connexion";
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/connexion";
    }

    @Transactional(readOnly = true) // 修复懒加载异常
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
        boolean isOwner = (userId == null || userId.equals(currentUser.getId()));
        
        if (isOwner) {
            // 永远从数据库获取最新状态，防止 Session 数据过期
            profilUser = utilisateurService.findById(currentUser.getId());
        } else {
            profilUser = utilisateurService.findById(userId);
        }
        
        if (profilUser == null) {
            return "redirect:/profil";
        }
        
        Map<Long, Float> progressions = new HashMap<>();
        // 因为加了 @Transactional，这里调用 getObjectifs() 是安全的
        if (profilUser.getObjectifs() != null) {
            for (Objectif obj : profilUser.getObjectifs()) {
                Float progression = objectifService.getPourcentageObjectif(obj, profilUser);
                progressions.put(obj.getId(), progression);
            }
        }

        
        // 1. 获取分页的活动数据
        Page<Activite> activitePage = activiteService.getActivitesPaginees(profilUser, page, 10);
        
        model.addAttribute("utilisateur", profilUser);
        model.addAttribute("objectifProgressions", progressions);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("activitePage", activitePage);
        // 注意：前端如果需要最新的活动列表，这里还需要查询 dernièresActivités 并添加到 model
        return "profil"; 
    }
    
    @GetMapping("/profil/modifier")
    public String modifierProfilPage(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        // 保证表单渲染的是数据库中最新的数据
        model.addAttribute("utilisateur", utilisateurService.findById(utilisateur.getId()));
        return "modifierProfil";
    }
    
    @PostMapping("/profil/modifier")
    public String modifierProfil(@ModelAttribute Utilisateur utilisateurModifie,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");
        if (currentUser == null) {
            return "redirect:/connexion";
        }

        // 重新从数据库获取当前用户，避免操作游离态(Detached)对象
        Utilisateur utilisateurDb = utilisateurService.findById(currentUser.getId());

        // 1. 校验邮箱冲突 (如果邮箱改了，且新邮箱已存在)
        if (!utilisateurDb.getEmail().equals(utilisateurModifie.getEmail()) && 
            utilisateurService.emailExiste(utilisateurModifie.getEmail())) {
            redirectAttributes.addFlashAttribute("erreur", "Cet email est déjà utilisé par un autre compte.");
            return "redirect:/profil/modifier";
        }

        // 2. 校验昵称冲突
        if (!utilisateurDb.getPseudo().equals(utilisateurModifie.getPseudo()) && 
            utilisateurService.pseudoExiste(utilisateurModifie.getPseudo())) {
            redirectAttributes.addFlashAttribute("erreur", "Ce pseudo est déjà pris.");
            return "redirect:/profil/modifier";
        }

        // 2. 处理复选框逻辑：如果用户一个都没勾选，Spring 传过来的 list 可能是 null
        List<TypeSport> newSports = utilisateurModifie.getPreferencesSports();
        if (newSports == null) {
            newSports = new ArrayList<>();
        }

        // 3. 后端安全性校验：限制最多 4 个运动
        if (newSports.size() > 4) {
            redirectAttributes.addFlashAttribute("erreur", "Maximum 4 sports autorisés !");
            return "redirect:/profil/modifier";
        }
        
        // 执行更新
        utilisateurDb.setPseudo(utilisateurModifie.getPseudo());
        utilisateurDb.setEmail(utilisateurModifie.getEmail());
        utilisateurDb.setSexe(utilisateurModifie.getSexe());
        utilisateurDb.setAge(utilisateurModifie.getAge());
        utilisateurDb.setTaille(utilisateurModifie.getTaille());
        utilisateurDb.setPoids(utilisateurModifie.getPoids());
        utilisateurDb.setNiveauPratique(utilisateurModifie.getNiveauPratique());
        utilisateurDb.setPreferencesSports(newSports);
        
        utilisateurService.updateUtilisateur(utilisateurDb);
        
        // 更新 Session 中的简略信息（如果不放整个对象，能减轻内存负担，但这里维持你的逻辑）
        session.setAttribute("utilisateur", utilisateurDb);
        
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");
        return "redirect:/profil";
    }
}