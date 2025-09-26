package com.songstock.entity;

/**
 * Enumeración que representa los tamaños más comunes de vinilos.
 * Incluye un nombre para mostrar que será usado en la interfaz.
 */
public enum VinylSize {
    SEVEN_INCH("7\""), // Vinilo de 7 pulgadas
    TEN_INCH("10\""), // Vinilo de 10 pulgadas
    TWELVE_INCH("12\""); // Vinilo de 12 pulgadas

    /** Nombre legible del tamaño del vinilo. */
    private final String displayName;

    VinylSize(String displayName) {
        this.displayName = displayName;
    }

    /** Devuelve el nombre que se mostrará en la UI. */
    public String getDisplayName() {
        return displayName;
    }
}
