package com.songstock.controller;

import com.songstock.dto.GenreDTO;
import com.songstock.service.GenreService;
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
@RequestMapping("/genres")
@Tag(name = "Genres", description = "Gestión de Géneros Musicales")
public class GenreController {
    
    private static final Logger logger = LoggerFactory.getLogger(GenreController.class);
    
    @Autowired
    private GenreService genreService;
    
    /**
     * Crear un nuevo género
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear género", description = "Crear un nuevo género musical")
    public ResponseEntity<ApiResponse<GenreDTO>> createGenre(@Valid @RequestBody GenreDTO genreDTO) {
        logger.info("REST request to create genre: {}", genreDTO.getName());
        
        GenreDTO createdGenre = genreService.createGenre(genreDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Género creado exitosamente", createdGenre));
    }
    
    /**
     * Obtener todos los géneros activos
     */
    @GetMapping
    @Operation(summary = "Listar géneros", description = "Obtener todos los géneros activos")
    public ResponseEntity<ApiResponse<List<GenreDTO>>> getAllGenres() {
        logger.info("REST request to get all active genres");
        
        List<GenreDTO> genres = genreService.getAllActiveGenres();
        
        return ResponseEntity.ok(ApiResponse.success("Géneros obtenidos exitosamente", genres));
    }
    
    /**
     * Obtener géneros con paginación
     */
    @GetMapping("/paginated")
    @Operation(summary = "Listar géneros paginados", description = "Obtener géneros con paginación")
    public ResponseEntity<ApiResponse<Page<GenreDTO>>> getGenres(Pageable pageable) {
        logger.info("REST request to get genres with pagination");
        
        Page<GenreDTO> genresPage = genreService.getGenres(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Géneros obtenidos exitosamente", genresPage));
    }
    
    /**
     * Obtener género por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener género", description = "Obtener un género por su ID")
    public ResponseEntity<ApiResponse<GenreDTO>> getGenreById(@PathVariable Long id) {
        logger.info("REST request to get genre by ID: {}", id);
        
        GenreDTO genre = genreService.getGenreById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Género obtenido exitosamente", genre));
    }
    
    /**
     * Buscar géneros por nombre
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar géneros", description = "Buscar géneros por nombre")
    public ResponseEntity<ApiResponse<List<GenreDTO>>> searchGenres(@RequestParam String name) {
        logger.info("REST request to search genres by name: {}", name);
        
        List<GenreDTO> genres = genreService.searchGenresByName(name);
        
        return ResponseEntity.ok(ApiResponse.success("Búsqueda realizada exitosamente", genres));
    }
    
    /**
     * Obtener géneros que tienen álbumes
     */
    @GetMapping("/with-albums")
    @Operation(summary = "Géneros con álbumes", description = "Obtener géneros que tienen álbumes")
    public ResponseEntity<ApiResponse<List<GenreDTO>>> getGenresWithAlbums() {
        logger.info("REST request to get genres with albums");
        
        List<GenreDTO> genres = genreService.getGenresWithAlbums();
        
        return ResponseEntity.ok(ApiResponse.success("Géneros obtenidos exitosamente", genres));
    }
    
    /**
     * Actualizar género
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar género", description = "Actualizar un género existente")
    public ResponseEntity<ApiResponse<GenreDTO>> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDTO genreDTO) {
        logger.info("REST request to update genre ID: {}", id);
        
        GenreDTO updatedGenre = genreService.updateGenre(id, genreDTO);
        
        return ResponseEntity.ok(ApiResponse.success("Género actualizado exitosamente", updatedGenre));
    }
    
    /**
     * Eliminar género
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar género", description = "Eliminar un género (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Long id) {
        logger.info("REST request to delete genre ID: {}", id);
        
        genreService.deleteGenre(id);
        
        return ResponseEntity.ok(ApiResponse.success("Género eliminado exitosamente", null));
    }
}