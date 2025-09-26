package com.songstock.exception;

/**
 * Excepción personalizada para errores de negocio.
 * Se lanza cuando ocurre una regla de negocio que no se cumple.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje descriptivo del error.
     * 
     * @param message Mensaje de error.
     */
    public BusinessException(String message) {
        super(message);
    }
}
