package com.songstock.controller;

// Importaciones necesarias para el controlador
import com.songstock.dto.ArtistDTO;
import com.songstock.service.ArtistService;
import com.songstock.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de artistas en la aplicación.
 * Expone los endpoints relacionados con CRUD, búsqueda y consultas específicas.
 */
@RestController // Define que esta clase es un controlador REST
@RequestMapping("/artists") // Prefijo común para todos los endpoints relacionados con artistas
@Tag(name = "Artists", description = "Gestión de Artistas") // Documentación para Swagger/OpenAPI
public class ArtistController {

    // Logger para registrar las operaciones y facilitar la depuración
    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    // Inyección del servicio que contiene la lógica de negocio para artistas
    @Autowired
    private ArtistService artistService;

    /**
     * Crear un nuevo artista.
     * Solo disponible para usuarios con rol ADMIN o PROVIDER.
     *
     * @param artistDTO datos del artista a crear
     * @return respuesta con el artista creado y estado HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Crear artista", description = "Crear un nuevo artista")
    public ResponseEntity<ApiResponse<ArtistDTO>> createArtist(@Valid @RequestBody ArtistDTO artistDTO) {
        logger.info("REST request to create artist: {}", artistDTO.getName());

        // Llamada al servicio para crear el artista
        ArtistDTO createdArtist = artistService.createArtist(artistDTO);

        // Respuesta con estado 201 (CREATED) y objeto creado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Artista creado exitosamente", createdArtist));
    }

    /**
     * Obtener todos los artistas activos.
     *
     * @return lista de artistas activos
     */
    @GetMapping
    @Operation(summary = "Listar artistas", description = "Obtener todos los artistas activos")
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getAllArtists() {
        logger.info("REST request to get all active artists");

        List<ArtistDTO> artists = artistService.getAllActiveArtists();

        return ResponseEntity.ok(ApiResponse.success("Artistas obtenidos exitosamente", artists));
    }

    /**
     * Obtener artistas con paginación.
     *
     * @param pageable parámetros de paginación (page, size, sort)
     * @return página de artistas
     */
    @GetMapping("/paginated")
    @Operation(summary = "Listar artistas paginados", description = "Obtener artistas con paginación")
    public ResponseEntity<ApiResponse<Page<ArtistDTO>>> getArtists(Pageable pageable) {
        logger.info("REST request to get artists with pagination");

        Page<ArtistDTO> artistsPage = artistService.getArtists(pageable);

        return ResponseEntity.ok(ApiResponse.success("Artistas obtenidos exitosamente", artistsPage));
    }

    /**
     * Obtener artista por su ID.
     *
     * @param id identificador del artista
     * @return artista correspondiente
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener artista", description = "Obtener un artista por su ID")
    public ResponseEntity<ApiResponse<ArtistDTO>> getArtistById(@PathVariable Long id) {
        logger.info("REST request to get artist by ID: {}", id);

        ArtistDTO artist = artistService.getArtistById(id);

        return ResponseEntity.ok(ApiResponse.success("Artista obtenido exitosamente", artist));
    }

    /**
     * Buscar artistas por nombre.
     *
     * @param name nombre (o parte del nombre) del artista
     * @return lista de artistas que coinciden con el criterio
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar artistas", description = "Buscar artistas por nombre")
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> searchArtists(@RequestParam String name) {
        logger.info("REST request to search artists by name: {}", name);

        List<ArtistDTO> artists = artistService.searchArtistsByName(name);

        return ResponseEntity.ok(ApiResponse.success("Búsqueda realizada exitosamente", artists));
    }

    /**
     * Obtener artistas por país.
     *
     * @param country país de origen
     * @return lista de artistas pertenecientes al país indicado
     */
    @GetMapping("/country/{country}")
    @Operation(summary = "Artistas por país", description = "Obtener artistas por país")
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getArtistsByCountry(@PathVariable String country) {
        logger.info("REST request to get artists by country: {}", country);

        List<ArtistDTO> artists = artistService.getArtistsByCountry(country);

        return ResponseEntity.ok(ApiResponse.success("Artistas obtenidos exitosamente", artists));
    }

    /**
     * Obtener artistas que tienen al menos un álbum registrado.
     *
     * @return lista de artistas con álbumes
     */
    @GetMapping("/with-albums")
    @Operation(summary = "Artistas con álbumes", description = "Obtener artistas que tienen álbumes")
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getArtistsWithAlbums() {
        logger.info("REST request to get artists with albums");

        List<ArtistDTO> artists = artistService.getArtistsWithAlbums();

        return ResponseEntity.ok(ApiResponse.success("Artistas obtenidos exitosamente", artists));
    }

    /**
     * Actualizar un artista existente.
     * Solo disponible para ADMIN o PROVIDER.
     *
     * @param id        identificador del artista
     * @param artistDTO datos actualizados del artista
     * @return artista actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Actualizar artista", description = "Actualizar un artista existente")
    public ResponseEntity<ApiResponse<ArtistDTO>> updateArtist(@PathVariable Long id,
            @Valid @RequestBody ArtistDTO artistDTO) {
        logger.info("REST request to update artist ID: {}", id);

        ArtistDTO updatedArtist = artistService.updateArtist(id, artistDTO);

        return ResponseEntity.ok(ApiResponse.success("Artista actualizado exitosamente", updatedArtist));
    }

    /**
     * Eliminar un artista (soft delete).
     * Solo disponible para ADMIN.
     *
     * @param id identificador del artista
     * @return confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar artista", description = "Eliminar un artista (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteArtist(@PathVariable Long id) {
        logger.info("REST request to delete artist ID: {}", id);

        artistService.deleteArtist(id);

        return ResponseEntity.ok(ApiResponse.success("Artista eliminado exitosamente", null));
    }
}
