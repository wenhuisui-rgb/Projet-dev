package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActiviteService {

    @Autowired
    private ActiviteRepository activiteRepository;

    /**
     * 保存活动（自动计算卡路里）
     */
    @Transactional
    public Activite sauvegarderActivite(Activite activite, Float poidsUtilisateur) {
        // 自动计算卡路里
        Float caloriesCalculees = activite.calculerCalories(poidsUtilisateur);
        activite.setCalories(caloriesCalculees);
        return activiteRepository.save(activite);
    }

    /**
     * 根据ID获取活动
     */
    public Activite getActiviteParId(Long id) {
        return activiteRepository.findById(id).orElse(null);
    }

    /**
     * 获取用户的所有活动
     */
    public List<Activite> getActivitesParUtilisateur(Utilisateur utilisateur) {
        return activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur);
    }

    /**
     * 获取用户最近5条活动
     */
    public List<Activite> getDernieresActivites(Utilisateur utilisateur) {
        return activiteRepository.findTop5ByUtilisateurOrderByDateActiviteDesc(utilisateur);
    }

    /**
     * 获取用户在时间段内的活动
     */
    public List<Activite> getActivitesEntreDates(Utilisateur utilisateur, LocalDateTime debut, LocalDateTime fin) {
        return activiteRepository.findByUtilisateurAndDateActiviteBetweenOrderByDateActiviteDesc(utilisateur, debut, fin);
    }

    /**
     * 获取用户本周活动
     */
    public List<Activite> getActivitesDeLaSemaine(Utilisateur utilisateur) {
        LocalDateTime debutSemaine = LocalDateTime.now().minusDays(7);
        return getActivitesEntreDates(utilisateur, debutSemaine, LocalDateTime.now());
    }

    /**
     * 获取用户本月活动
     */
    public List<Activite> getActivitesDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        return getActivitesEntreDates(utilisateur, debutMois, LocalDateTime.now());
    }

    /**
     * 按运动类型筛选活动
     */
    public List<Activite> getActivitesParType(Utilisateur utilisateur, TypeSport typeSport) {
        return activiteRepository.findByUtilisateurAndTypeSportOrderByDateActiviteDesc(utilisateur, typeSport);
    }

    /**
     * 获取用户总距离
     */
    public Float getDistanceTotale(Utilisateur utilisateur) {
        return activiteRepository.getDistanceTotale(utilisateur);
    }

    /**
     * 获取用户总时长
     */
    public Integer getDureeTotale(Utilisateur utilisateur) {
        return activiteRepository.getDureeTotale(utilisateur);
    }

    /**
     * 获取用户总卡路里
     */
    public Float getCaloriesTotales(Utilisateur utilisateur) {
        return activiteRepository.getCaloriesTotales(utilisateur);
    }

    /**
     * 获取本月总距离
     */
    public Float getDistanceDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        List<Activite> activites = getActivitesEntreDates(utilisateur, debutMois, LocalDateTime.now());
        return activites.stream().map(Activite::getDistance).reduce(0f, Float::sum);
    }

    /**
     * 获取本月总活动次数
     */
    public Integer getNombreActivitesDuMois(Utilisateur utilisateur) {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        return getActivitesEntreDates(utilisateur, debutMois, LocalDateTime.now()).size();
    }

    /**
     * 获取统计仪表盘数据
     */
    public Map<String, Object> getStatsDashboard(Utilisateur utilisateur) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalDistance", getDistanceTotale(utilisateur));
        stats.put("totalDuree", getDureeTotale(utilisateur));
        stats.put("totalCalories", getCaloriesTotales(utilisateur));
        stats.put("totalActivites", activiteRepository.findByUtilisateurOrderByDateActiviteDesc(utilisateur).size());
        stats.put("distanceMois", getDistanceDuMois(utilisateur));
        stats.put("activitesMois", getNombreActivitesDuMois(utilisateur));
        stats.put("dernieresActivites", getDernieresActivites(utilisateur));
        
        return stats;
    }

    /**
     * 获取每周活动统计（用于图表）
     */
    public Map<String, Float> getStatistiquesParSemaine(Utilisateur utilisateur) {
        Map<String, Float> statsSemaine = new HashMap<>();
        List<Activite> activitesSemaine = getActivitesDeLaSemaine(utilisateur);
        
        float distanceSemaine = 0f;
        int dureeSemaine = 0;
        float caloriesSemaine = 0f;
        
        for (Activite a : activitesSemaine) {
            if (a.getDistance() != null) distanceSemaine += a.getDistance();
            if (a.getDuree() != null) dureeSemaine += a.getDuree();
            if (a.getCalories() != null) caloriesSemaine += a.getCalories();
        }
        
        statsSemaine.put("distance", distanceSemaine);
        statsSemaine.put("duree", (float) dureeSemaine);
        statsSemaine.put("calories", caloriesSemaine);
        
        return statsSemaine;
    }

    /**
     * 更新活动
     */
    @Transactional
    public Activite updateActivite(Long id, Activite nouvelleActivite, Float poidsUtilisateur) {
        Activite existante = getActiviteParId(id);
        if (existante != null) {
            existante.setTypeSport(nouvelleActivite.getTypeSport());
            existante.setDateActivite(nouvelleActivite.getDateActivite());
            existante.setDuree(nouvelleActivite.getDuree());
            existante.setDistance(nouvelleActivite.getDistance());
            existante.setLocalisation(nouvelleActivite.getLocalisation());
            existante.setEvaluation(nouvelleActivite.getEvaluation());
            
            // 重新计算卡路里
            Float nouvellesCalories = existante.calculerCalories(poidsUtilisateur);
            existante.setCalories(nouvellesCalories);
            
            return activiteRepository.save(existante);
        }
        return null;
    }

    /**
     * 删除活动
     */
    @Transactional
    public void supprimerActivite(Long id) {
        activiteRepository.deleteById(id);
    }

    /**
     * 检查用户是否达到月度目标
     */
    public boolean aAtteintObjectifMensuel(Utilisateur utilisateur, Float objectifDistance) {
        if (objectifDistance == null) return false;
        Float distanceMois = getDistanceDuMois(utilisateur);
        return distanceMois >= objectifDistance;
    }
}