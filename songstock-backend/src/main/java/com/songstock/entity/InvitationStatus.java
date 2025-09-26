package com.songstock.entity;

/**
 * Enum que representa el estado de una invitación a un proveedor.
 * 
 * - PENDING: invitación enviada, esperando respuesta.
 * - COMPLETED: el proveedor completó el registro.
 * - EXPIRED: la invitación venció sin ser aceptada.
 * - CANCELLED: un administrador canceló la invitación.
 */
public enum InvitationStatus {
    PENDING, // Invitación enviada, esperando respuesta
    COMPLETED, // Proveedor completó registro
    EXPIRED, // Invitación expirada
    CANCELLED // Invitación cancelada por admin
}
