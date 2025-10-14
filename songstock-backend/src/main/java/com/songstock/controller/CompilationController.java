package com.songstock.controller;

import com.songstock.dto.CompilationDTO;
import com.songstock.security.UserDetailsImpl;
import com.songstock.service.CompilationService;
import com.songstock.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Tag(name = "Compilations", description = "Gestión de Recopilaciones de Canciones")
public class CompilationController {

    private static final Logger logger = LoggerFactory.getLogger(CompilationController.class);

    @Autowired
    private CompilationService compilationService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Mis compilaciones", description = "Obtener todas las compilaciones del usuario autenticado")
    public ResponseEntity<ApiResponse<List<CompilationDTO>>> getMyCompilations(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("REST request to get compilations for user: {}", userDetails.getId());

        List<CompilationDTO> compilations = compilationService.getMyCompilations(userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success("Compilaciones obtenidas exitosamente", compilations));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Detalle de compilación", description = "Obtener una compilación por ID con sus canciones")
    public ResponseEntity<ApiResponse<CompilationDTO>> getCompilationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("REST request to get compilation: {}", id);

        CompilationDTO compilation = compilationService.getCompilationById(id, userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success("Compilación obtenida exitosamente", compilation));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Crear compilación", description = "Crear una nueva compilación vacía")
    public ResponseEntity<ApiResponse<CompilationDTO>> createCompilation(
            @Valid @RequestBody CompilationDTO compilationDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("REST request to create compilation: {}", compilationDTO.getName());

        CompilationDTO created = compilationService.createCompilation(compilationDTO, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Compilación creada exitosamente", created));
    }

    @PostMapping("/{id}/songs/{songId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Añadir canción", description = "Añadir una canción a la compilación")
    public ResponseEntity<ApiResponse<CompilationDTO>> addSongToCompilation(
            @PathVariable Long id,
            @PathVariable Long songId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("REST request to add song {} to compilation {}", songId, id);

        CompilationDTO updated = compilationService.addSongToCompilation(id, songId, userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success("Canción añadida exitosamente", updated));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Eliminar canción", description = "Eliminar una canción de la compilación")
    public ResponseEntity<ApiResponse<Void>> removeSongFromCompilation(
            @PathVariable Long id,
            @PathVariable Long songId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("REST request to remove song {} from compilation {}", songId, id);

        compilationService.removeSongFromCompilation(id, songId, userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success("Canción eliminada exitosamente", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Eliminar compilación", description = "Eliminar una compilación completa")
    public ResponseEntity<ApiResponse<Void>> deleteCompilation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("REST request to delete compilation: {}", id);

        compilationService.deleteCompilation(id, userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success("Compilación eliminada exitosamente", null));
    }
}