package com.songstock.exception;

/**
 * Excepción específica para operaciones de inventario.
 * Permite identificar errores relacionados con stock, disponibilidad, etc.
 */
public class InventoryException extends RuntimeException {

    // Código de error asociado
    private String errorCode;

    // Campo específico que generó el error
    private String field;

    /**
     * Constructor con mensaje.
     *
     * @param message Mensaje descriptivo del error.
     */
    public InventoryException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y código de error.
     *
     * @param message   Mensaje descriptivo.
     * @param errorCode Código del error.
     */
    public InventoryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor con mensaje, código de error y campo relacionado.
     *
     * @param message   Mensaje descriptivo.
     * @param errorCode Código del error.
     * @param field     Campo que generó el error.
     */
    public InventoryException(String message, String errorCode, String field) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje descriptivo.
     * @param cause   Causa del error.
     */
    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con mensaje, causa y código de error.
     *
     * @param message   Mensaje descriptivo.
     * @param cause     Causa del error.
     * @param errorCode Código del error.
     */
    public InventoryException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    /**
     * Representación en texto de la excepción para depuración.
     *
     * @return String con detalles del error.
     */
    @Override
    public String toString() {
        return "InventoryException{" +
                "message='" + getMessage() + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}
