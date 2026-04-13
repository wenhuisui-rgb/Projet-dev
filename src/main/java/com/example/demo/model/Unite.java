package com.example.demo.model;

public enum Unite {
    KM("km"),
    MINUTES("minutes"),
    KCAL("kcal");

    private String code;

    Unite(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Unite fromCode(String code) {
        for (Unite u : values()) {
            if (u.code.equalsIgnoreCase(code)) {
                return u;
            }
        }
        return null;
    }
}