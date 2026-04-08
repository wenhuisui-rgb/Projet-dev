package com.example.demo.model;
import java.util.List;
public class Badge {

    private Long badgeID;
    
    private String badge_nom;
    
    private String badge_description;
    
    
    
    public Badge(){
        
    }

    public Badge(Long badgeID, String badge_nom, String badge_description){
        this.badgeID=badgeID;
        this.badge_nom=badge_nom;
        this.badge_description=badge_description;
    }
    
    
    public Long getBadgeID() {
        return badgeID;
    }
    
    public void setBadgeID(Long badgeID) {
        this.badgeID = badgeID;
    }
    
    
    public String getBadge_nom() {
        return badge_nom;
    }
    
    
    public void setBadge_nom(String badge_nom) {
        this.badge_nom = badge_nom;
    }


    public String getBadge_description() {
        return badge_description;
    }


    public void setBadge_description(String badge_description) {
        this.badge_description = badge_description;
    }

    public boolean verifierCondition(Utilisateur utilisateur){
        return false;
    }

    public static List<Badge> listerTousLesBadges() {
            //List<Badge> badges = new ArrayList<>();
            return null;
    }
}
