package com.example.demo.model;
import java.util.List;
import java.time.LocalDate; 
import java.util.ArrayList;
import jakarta.persistence.*;


@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)

    private long id;

    private String titre;
    
    @Enumerated(EnumType.STRING)
    private TypeSport typeSport; 

    private LocalDate dateDebut;
    private LocalDate dateFin;
    
    @OneToMany(mappedBy = "challenge")
    private List<ParticipationChallenge>participations=new ArrayList<>();

    public Challenge(){

    }


    public Challenge(String titre, TypeSport typeSport, LocalDate dateDebut, LocalDate dateFin) {
        this.titre = titre;
        this.typeSport = typeSport;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public TypeSport getTypeSport() {
        return typeSport;
    }

    public void setTypeSport(TypeSport typeSport) {
        this.typeSport = typeSport;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

        public List<ParticipationChallenge> getParticipations() {
        return participations;
    }

    public void ajouterParticipant(Utilisateur utilisateur) {
        ParticipationChallenge participation = new ParticipationChallenge(utilisateur, this);

        participations.add(participation);
    
    }

    public void retirerParticipant(Utilisateur utilisateur){
        participations.removeIf(participations -> participations.getUtilisateur().equals(utilisateur) );
    }

    public boolean estActif() {
        LocalDate aujourdHui = LocalDate.now();
        return !aujourdHui.isBefore(dateDebut) && !aujourdHui.isAfter(dateFin);
    }

    //retourne la liste des participants sous forme d'un classement
    public List<ParticipationChallenge> obtenirClassement() {
        participations.sort(
                (p1, p2) -> Float.compare(
                        p2.getScoreActuel(),
                        p1.getScoreActuel()
                )
        );

        return participations;
    }

    //retourne la liste de tt les challenges 
    public List<Challenge> listtoutLesChallenges() {
        return new ArrayList<>();
}}
