package com.example.demo.dto;

import com.example.demo.model.NiveauPratique;
import com.example.demo.model.Sexe;
import com.example.demo.model.TypeSport;

import java.util.List;

/**
 * DTO pour sécuriser la réception des données formulaires.
 */
public class UtilisateurFormDTO {
    
    private String pseudo;
    private String email;
    private String motDePasse;
    private Sexe sexe;
    private Integer age;
    private Float taille;
    private Float poids;
    private NiveauPratique niveauPratique;
    private List<TypeSport> preferencesSports;

    // --- Générer les Getters et Setters ci-dessous ---
    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Float getTaille() { return taille; }
    public void setTaille(Float taille) { this.taille = taille; }
    public Float getPoids() { return poids; }
    public void setPoids(Float poids) { this.poids = poids; }
    public NiveauPratique getNiveauPratique() { return niveauPratique; }
    public void setNiveauPratique(NiveauPratique niveauPratique) { this.niveauPratique = niveauPratique; }
    public List<TypeSport> getPreferencesSports() { return preferencesSports; }
    public void setPreferencesSports(List<TypeSport> preferencesSports) { this.preferencesSports = preferencesSports; }
}