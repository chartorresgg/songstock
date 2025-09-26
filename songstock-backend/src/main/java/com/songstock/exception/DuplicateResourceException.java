package com.songstock.exception;

/**
 * Excepción personalizada para cuando un recurso duplicado es detectado.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo.
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
