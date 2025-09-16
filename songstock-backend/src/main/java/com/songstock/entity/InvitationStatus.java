package com.songstock.entity;

public enum InvitationStatus {
    PENDING, // Invitación enviada, esperando respuesta
    COMPLETED, // Proveedor completó registro
    EXPIRED, // Invitación expirada
    CANCELLED // Invitación cancelada por admin
}