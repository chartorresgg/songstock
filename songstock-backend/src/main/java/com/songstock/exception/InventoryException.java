package com.songstock.exception;

/**
 * Excepción específica para operaciones de inventario
 */
public class InventoryException extends RuntimeException {

    private String errorCode;
    private String field;

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InventoryException(String message, String errorCode, String field) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }

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

    @Override
    public String toString() {
        return "InventoryException{" +
                "message='" + getMessage() + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}