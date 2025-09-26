package com.songstock.entity;

/**
 * Enumeración que define el estado de verificación de un proveedor.
 * - PENDING → En espera de verificación.
 * - VERIFIED → Proveedor verificado exitosamente.
 * - REJECTED → Proceso de verificación rechazado.
 */
public enum VerificationStatus {
    PENDING,
    VERIFIED,
    REJECTED
}
