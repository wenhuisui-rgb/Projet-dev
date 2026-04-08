package com.example.demo.model;
import java.util.Date;

public class ObtentionBadge {
    private Long id;
    private Date dateObtention;
    
    
    public ObtentionBadge(){
        
    }
    
    public ObtentionBadge(Long id,Date dateObtention){
        this.id=id;
        this.dateObtention=dateObtention;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getDateObtention() {
        return dateObtention;
    }
    
    public void setDateObtention(Date dateObtention) {
        this.dateObtention = dateObtention;
    }
}
