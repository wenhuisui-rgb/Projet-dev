package com.example.demo.model;

/**
 * Objet de Transfert de Données (DTO) utilisé pour formater les statistiques
 * afin de les afficher dans les graphiques du tableau de bord (frontend).
 */
public class ChartData {
    
    /** L'étiquette de l'axe des abscisses (X) : ex. "Lundi", "Janvier", ou "15". */
    private String label;
    
    /** La valeur numérique correspondante sur l'axe des ordonnées (Y) : ex. temps, distance, calories. */
    private int value;
    
    public ChartData() {}

    public ChartData(String label, int value) {
        this.label = label;
        this.value = value;
    }
    
    public String getLabel() { return label; }
    public int getValue() { return value; }

    public void setLabel(String label) { this.label = label; }
    public void setValue(int value) { this.value = value; }
}