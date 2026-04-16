package com.example.demo.model;

public class ChartData {
    private String label;
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