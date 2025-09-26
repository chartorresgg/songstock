package com.songstock.util;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Clase genérica de respuesta para las APIs.
 * 
 * - Incluye información de éxito/fracaso.
 * - Permite retornar un mensaje, datos asociados y detalles de error.
 * - Se utiliza como wrapper para estandarizar las respuestas de la API.
 *
 * @param <T> tipo de dato que contendrá la respuesta
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora propiedades nulas en la serialización JSON
public class ApiResponse<T> {
    private boolean success; // Indica si la operación fue exitosa
    private String message; // Mensaje informativo o de error
    private T data; // Datos de la respuesta (si aplica)
    private String error; // Detalles del error (si aplica)

    // Constructor vacío (necesario para deserialización)
    public ApiResponse() {
    }

    // Constructor con valores iniciales
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Método de fábrica para crear respuestas exitosas.
     * 
     * @param message mensaje asociado
     * @param data    datos de la respuesta
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Método de fábrica para crear respuestas de error.
     * 
     * @param message mensaje asociado al error
     * @param error   detalle técnico o adicional del error
     */
    public static <T> ApiResponse<T> error(String message, String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setError(error);
        return response;
    }

    // ================= Getters y Setters =================
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
