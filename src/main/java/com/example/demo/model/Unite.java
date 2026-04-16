package com.example.demo.model;

/**
 * Énumération des unités de mesure utilisées pour quantifier les objectifs et les défis.
 */
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

    /**
     * Retrouve l'énumération correspondante à partir de son code textuel.
     *
     * @param code Le code de l'unité (ex: "km")
     * @return L'unité correspondante, ou {@code null} si introuvable
     */
    public static Unite fromCode(String code) {
        for (Unite u : values()) {
            if (u.code.equalsIgnoreCase(code)) {
                return u;
            }
        }
        return null;
    }
}