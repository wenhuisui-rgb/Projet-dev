package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un défi (Challenge) organisé sur la plateforme.
 * Les utilisateurs peuvent s'y inscrire via l'entité {@link ParticipationChallenge}.
 */
@Entity
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le nom public du challenge. */
    private String titre;

    /** Le sport imposé pour ce challenge (null = tous sports confondus). */
    @Enumerated(EnumType.STRING)
    private TypeSport typeSport;

    /** La date de début de l'événement. */
    private LocalDate dateDebut;
    
    /** La date de fin de l'événement. */
    private LocalDate dateFin;

    /** Le statut du challenge (ex: ACTIF, TERMINE). */
    @Enumerated(EnumType.STRING)
    private StatutChallenge statut;

    /** L'unité de mesure pour l'objectif (ex: KM, MINUTES, KCAL). */
    @Enumerated(EnumType.STRING)
    private Unite unite;

    /** La valeur numérique totale à atteindre pour réussir le challenge. */
    private Float cible;

    /** L'utilisateur qui a organisé/créé ce défi. */
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    /** La liste des participants inscrits à ce challenge et leurs scores. */
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipationChallenge> participations = new ArrayList<>();

    public Challenge() {}

    public Challenge(String titre, TypeSport typeSport, LocalDate dateDebut,
                     LocalDate dateFin, Utilisateur createur, Unite unite, Float cible) {
        this.titre = titre;
        this.typeSport = typeSport;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.createur = createur;
        this.unite = unite;
        this.cible = cible;
        this.statut = StatutChallenge.ACTIF;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public TypeSport getTypeSport() { return typeSport; }
    public void setTypeSport(TypeSport typeSport) { this.typeSport = typeSport; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public StatutChallenge getStatut() { return statut; }
    public void setStatut(StatutChallenge statut) { this.statut = statut; }
    public Unite getUnite() { return unite; }
    public void setUnite(Unite unite) { this.unite = unite; }
    public Float getCible() { return cible; }
    public void setCible(Float cible) { this.cible = cible; }
    public Utilisateur getCreateur() { return createur; }
    public void setCreateur(Utilisateur createur) { this.createur = createur; }
    public List<ParticipationChallenge> getParticipations() { return participations; }
    public void setParticipations(List<ParticipationChallenge> participations) { this.participations = participations; }

    /**
     * Vérifie dynamiquement si le challenge est actuellement en cours,
     * en comparant la date d'aujourd'hui aux dates de début et de fin.
     *
     * @return {@code true} si la date actuelle est comprise entre le début et la fin.
     */
    public boolean estActif() {
        LocalDate aujourdHui = LocalDate.now();
        return !aujourdHui.isBefore(dateDebut)
                && !aujourdHui.isAfter(dateFin);
    }
}