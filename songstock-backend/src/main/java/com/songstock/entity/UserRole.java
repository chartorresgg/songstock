package com.songstock.entity;

/**
 * Enumeración que define los roles disponibles para los usuarios del sistema.
 * - ADMIN → Administrador con privilegios completos.
 * - PROVIDER → Usuario que gestiona productos y ventas.
 * - CUSTOMER → Usuario comprador/consumidor.
 */
public enum UserRole {
    ADMIN,
    PROVIDER,
    CUSTOMER
}
