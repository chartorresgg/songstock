package com.songstock.entity;

/**
 * Enum que representa el estado físico de un producto.
 * Se usa para clasificar vinilos, CDs u otros artículos usados.
 */
public enum ConditionType {
    NEW, // Nuevo
    LIKE_NEW, // Como nuevo
    VERY_GOOD, // Muy bueno
    GOOD, // Bueno
    FAIR, // Regular
    POOR // En mal estado
}
