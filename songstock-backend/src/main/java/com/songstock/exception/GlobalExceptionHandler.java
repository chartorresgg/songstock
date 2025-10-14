package com.songstock.exception;

import com.songstock.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Captura y responde de manera controlada a los errores lanzados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger para registrar errores en la aplicación
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de tipo ResourceNotFoundException.
     * Retorna un código 404 (NOT_FOUND).
     *
     * @param ex Excepción capturada.
     * @return Respuesta con ApiResponse y mensaje de error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Recurso no encontrado", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        logger.error("Bad request: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error("Error de solicitud", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones de tipo DuplicateResourceException.
     * Retorna un código 409 (CONFLICT).
     *
     * @param ex Excepción capturada.
     * @return Respuesta con ApiResponse y mensaje de error.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        logger.error("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Recurso duplicado", ex.getMessage()));
    }

    /**
     * Maneja excepciones de tipo BusinessException.
     * Retorna un código 400 (BAD_REQUEST).
     *
     * @param ex Excepción capturada.
     * @return Respuesta con ApiResponse y mensaje de error.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        logger.error("Business exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error de negocio", ex.getMessage()));
    }

    /**
     * Maneja cualquier otra excepción no controlada.
     * Retorna un código 500 (INTERNAL_SERVER_ERROR).
     *
     * @param ex Excepción capturada.
     * @return Respuesta con ApiResponse indicando error interno.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor", "Ha ocurrido un error inesperado"));
    }
}
