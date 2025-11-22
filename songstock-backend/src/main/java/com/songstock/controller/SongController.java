package com.songstock.controller;

import com.songstock.dto.ApiResponse;
import com.songstock.dto.SongDTO;
import com.songstock.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/songs") // SIN /api/v1 porque ya está en context-path
@Tag(name = "Songs", description = "Gestión de canciones y búsqueda")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    @Autowired
    private SongService songService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SongDTO>>> searchSongs(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(required = false) Long genreId) {

        logger.info("Búsqueda de canciones individuales - query: {}, genreId: {}", query, genreId);

        try {
            List<SongDTO> songs = songService.searchAvailableSongs(query, genreId);

            String message = songs.isEmpty()
                    ? "No se encontraron canciones disponibles"
                    : String.format("Se encontraron %d canciones disponibles", songs.size());

            return ResponseEntity.ok(ApiResponse.success(message, songs));
        } catch (Exception e) {
            logger.error("Error al buscar canciones", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al buscar canciones"));
        }
    }

    @GetMapping("/{id}/vinyls")
    @Operation(summary = "Vinilos disponibles", description = "Obtener vinilos que contienen esta canción")
    public ResponseEntity<ApiResponse<SongDTO>> getSongWithVinyls(@PathVariable Long id) {
        logger.info("Obteniendo vinilos disponibles para canción ID: {}", id);

        try {
            SongDTO songWithVinyls = songService.getSongWithAvailableVinyls(id);
            String message = songWithVinyls.getAvailableVinyls().isEmpty()
                    ? "Esta canción no está disponible en vinilo"
                    : String.format("Se encontraron %d vinilos disponibles",
                            songWithVinyls.getAvailableVinyls().size());

            return ResponseEntity.ok(ApiResponse.success(message, songWithVinyls));
        } catch (RuntimeException e) {
            logger.error("Error al obtener vinilos para canción", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error interno", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al obtener información de vinilos"));
        }
    }

    @GetMapping("/album/{albumId}")
    @Operation(summary = "Canciones del álbum", description = "Obtener todas las canciones de un álbum")
    public ResponseEntity<ApiResponse<List<SongDTO>>> getSongsByAlbum(@PathVariable Long albumId) {
        logger.info("Obteniendo canciones del álbum ID: {}", albumId);

        try {
            List<SongDTO> songs = songService.getSongsByAlbumId(albumId);
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Álbum con %d canciones", songs.size()),
                    songs));
        } catch (Exception e) {
            logger.error("Error al obtener canciones del álbum", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al obtener canciones"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Crear canción", description = "Crear una nueva canción asociada a un álbum")
    public ResponseEntity<ApiResponse<SongDTO>> createSong(@RequestBody SongDTO songDTO) {
        logger.info("Creando nueva canción: {}", songDTO.getTitle());

        try {
            SongDTO created = songService.createSong(songDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Canción creada exitosamente", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Actualizar canción", description = "Actualizar información de una canción")
    public ResponseEntity<ApiResponse<SongDTO>> updateSong(
            @PathVariable Long id,
            @RequestBody SongDTO songDTO) {
        logger.info("Actualizando canción ID: {}", id);

        try {
            SongDTO updated = songService.updateSong(id, songDTO);
            return ResponseEntity.ok(ApiResponse.success("Canción actualizada exitosamente", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Eliminar canción", description = "Desactivar una canción")
    public ResponseEntity<ApiResponse<Void>> deleteSong(@PathVariable Long id) {
        logger.info("Eliminando canción ID: {}", id);

        try {
            songService.deleteSong(id);
            return ResponseEntity.ok(ApiResponse.success("Canción eliminada exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Crear canciones en lote", description = "Crear múltiples canciones de una vez")
    public ResponseEntity<ApiResponse<List<SongDTO>>> createSongsBatch(@RequestBody List<SongDTO> songs) {
        logger.info("Creando {} canciones en lote", songs.size());

        try {
            List<SongDTO> created = songService.createSongsBatch(songs);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Canciones creadas exitosamente", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}