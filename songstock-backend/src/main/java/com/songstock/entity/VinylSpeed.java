package com.songstock.entity;

/**
 * Enumeración que representa las velocidades de reproducción
 * más comunes de los vinilos.
 */
public enum VinylSpeed {
    RPM_33("33 RPM"), // 33 revoluciones por minuto
    RPM_45("45 RPM"), // 45 revoluciones por minuto
    RPM_78("78 RPM"); // 78 revoluciones por minuto

    /** Nombre legible de la velocidad del vinilo. */
    private final String displayName;

    VinylSpeed(String displayName) {
        this.displayName = displayName;
    }

    /** Devuelve el nombre que se mostrará en la UI. */
    public String getDisplayName() {
        return displayName;
    }
}
