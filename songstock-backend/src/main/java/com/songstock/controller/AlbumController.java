package com.songstock.controller;

import com.songstock.dto.AlbumDTO;
import com.songstock.dto.AlbumFormatsDTO;
import com.songstock.service.AlbumService;
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

@RestController
@RequestMapping("/albums")
@Tag(name = "Albums", description = "Gestión de Álbumes")
public class AlbumController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);
    
    @Autowired
    private AlbumService albumService;
    
    /**
     * Crear un nuevo álbum
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Crear álbum", description = "Crear un nuevo álbum")
    public ResponseEntity<ApiResponse<AlbumDTO>> createAlbum(@Valid @RequestBody AlbumDTO albumDTO) {
        logger.info("REST request to create album: {}", albumDTO.getTitle());
        
        AlbumDTO createdAlbum = albumService.createAlbum(albumDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Álbum creado exitosamente", createdAlbum));
    }
    
    /**
     * Obtener todos los álbumes activos
     */
    @GetMapping
    @Operation(summary = "Listar álbumes", description = "Obtener todos los álbumes activos")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> getAllAlbums() {
        logger.info("REST request to get all active albums");
        
        List<AlbumDTO> albums = albumService.getAllActiveAlbums();
        
        return ResponseEntity.ok(ApiResponse.success("Álbumes obtenidos exitosamente", albums));
    }
    
    /**
     * Obtener álbumes con paginación
     */
    @GetMapping("/paginated")
    @Operation(summary = "Listar álbumes paginados", description = "Obtener álbumes con paginación")
    public ResponseEntity<ApiResponse<Page<AlbumDTO>>> getAlbums(Pageable pageable) {
        logger.info("REST request to get albums with pagination");
        
        Page<AlbumDTO> albumsPage = albumService.getAlbums(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Álbumes obtenidos exitosamente", albumsPage));
    }
    
    /**
     * Obtener álbum por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener álbum", description = "Obtener un álbum por su ID")
    public ResponseEntity<ApiResponse<AlbumDTO>> getAlbumById(@PathVariable Long id) {
        logger.info("REST request to get album by ID: {}", id);
        
        AlbumDTO album = albumService.getAlbumById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Álbum obtenido exitosamente", album));
    }
    
    /**
     * ENDPOINT PRINCIPAL PARA LA HISTORIA DE USUARIO:
     * Obtener todos los formatos disponibles de un álbum
     */
    @GetMapping("/{id}/formats")
    @Operation(
        summary = "Obtener formatos de álbum", 
        description = "Obtener todos los formatos disponibles (digital y vinilo) de un álbum específico - Historia de Usuario principal"
    )
    public ResponseEntity<ApiResponse<AlbumFormatsDTO>> getAlbumFormats(@PathVariable Long id) {
        logger.info("REST request to get album formats for ID: {}", id);
        
        AlbumFormatsDTO formats = albumService.getAlbumFormats(id);
        
        return ResponseEntity.ok(ApiResponse.success("Formatos del álbum obtenidos exitosamente", formats));
    }
    
    /**
     * Verificar si un álbum tiene versión en vinilo
     */
    @GetMapping("/{id}/has-vinyl")
    @Operation(summary = "Verificar versión vinilo", description = "Verificar si un álbum tiene versión en vinilo")
    public ResponseEntity<ApiResponse<Boolean>> hasVinylVersion(@PathVariable Long id) {
        logger.info("REST request to check if album {} has vinyl version", id);
        
        boolean hasVinyl = albumService.hasVinylVersion(id);
        
        return ResponseEntity.ok(ApiResponse.success("Verificación realizada", hasVinyl));
    }
    
    /**
     * Verificar si un álbum tiene versión digital
     */
    @GetMapping("/{id}/has-digital")
    @Operation(summary = "Verificar versión digital", description = "Verificar si un álbum tiene versión digital")
    public ResponseEntity<ApiResponse<Boolean>> hasDigitalVersion(@PathVariable Long id) {
        logger.info("REST request to check if album {} has digital version", id);
        
        boolean hasDigital = albumService.hasDigitalVersion(id);
        
        return ResponseEntity.ok(ApiResponse.success("Verificación realizada", hasDigital));
    }
    
    /**
     * Buscar álbumes por título
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar álbumes", description = "Buscar álbumes por título")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> searchAlbums(@RequestParam String title) {
        logger.info("REST request to search albums by title: {}", title);
        
        List<AlbumDTO> albums = albumService.searchAlbumsByTitle(title);
        
        return ResponseEntity.ok(ApiResponse.success("Búsqueda realizada exitosamente", albums));
    }
    
    /**
     * Obtener álbumes por artista
     */
    @GetMapping("/artist/{artistId}")
    @Operation(summary = "Álbumes por artista", description = "Obtener álbumes por artista")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> getAlbumsByArtist(@PathVariable Long artistId) {
        logger.info("REST request to get albums by artist ID: {}", artistId);
        
        List<AlbumDTO> albums = albumService.getAlbumsByArtist(artistId);
        
        return ResponseEntity.ok(ApiResponse.success("Álbumes obtenidos exitosamente", albums));
    }
    
    /**
     * Obtener álbumes por género
     */
    @GetMapping("/genre/{genreId}")
    @Operation(summary = "Álbumes por género", description = "Obtener álbumes por género")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> getAlbumsByGenre(@PathVariable Long genreId) {
        logger.info("REST request to get albums by genre ID: {}", genreId);
        
        List<AlbumDTO> albums = albumService.getAlbumsByGenre(genreId);
        
        return ResponseEntity.ok(ApiResponse.success("Álbumes obtenidos exitosamente", albums));
    }
    
    /**
     * Obtener álbumes por año
     */
    @GetMapping("/year/{year}")
    @Operation(summary = "Álbumes por año", description = "Obtener álbumes por año de lanzamiento")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> getAlbumsByYear(@PathVariable Integer year) {
        logger.info("REST request to get albums by year: {}", year);
        
        List<AlbumDTO> albums = albumService.getAlbumsByReleaseYear(year);
        
        return ResponseEntity.ok(ApiResponse.success("Álbumes obtenidos exitosamente", albums));
    }
    
    /**
     * Obtener álbumes que tienen ambos formatos
     */
    @GetMapping("/both-formats")
    @Operation(summary = "Álbumes con ambos formatos", description = "Obtener álbumes que tienen versión digital y vinilo")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> getAlbumsWithBothFormats() {
        logger.info("REST request to get albums with both formats");
        
        List<AlbumDTO> albums = albumService.getAlbumsWithBothFormats();
        
        return ResponseEntity.ok(ApiResponse.success("Álbumes obtenidos exitosamente", albums));
    }
    
    /**
     * Actualizar álbum
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Actualizar álbum", description = "Actualizar un álbum existente")
    public ResponseEntity<ApiResponse<AlbumDTO>> updateAlbum(@PathVariable Long id, @Valid @RequestBody AlbumDTO albumDTO) {
        logger.info("REST request to update album ID: {}", id);
        
        AlbumDTO updatedAlbum = albumService.updateAlbum(id, albumDTO);
        
        return ResponseEntity.ok(ApiResponse.success("Álbum actualizado exitosamente", updatedAlbum));
    }
    
    /**
     * Eliminar álbum
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar álbum", description = "Eliminar un álbum (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteAlbum(@PathVariable Long id) {
        logger.info("REST request to delete album ID: {}", id);
        
        albumService.deleteAlbum(id);
        
        return ResponseEntity.ok(ApiResponse.success("Álbum eliminado exitosamente", null));
    }
}