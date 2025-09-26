package com.songstock.exception;

/**
 * Excepción lanzada cuando un recurso ya existe en el sistema.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo.
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
