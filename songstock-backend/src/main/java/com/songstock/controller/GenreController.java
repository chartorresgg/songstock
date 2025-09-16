package com.songstock.controller;

import com.songstock.dto.ApiResponse;
import com.songstock.entity.Genre;
import com.songstock.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Genre>>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success("Géneros obtenidos exitosamente", genres));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.ok(ApiResponse.success("Género encontrado", genre));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Genre>>> getActiveGenres() {
        List<Genre> genres = genreService.getActiveGenres();
        return ResponseEntity.ok(ApiResponse.success("Géneros activos obtenidos exitosamente", genres));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Genre>> createGenre(@Valid @RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.ok(ApiResponse.success("Género creado exitosamente", createdGenre));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Genre>> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody Genre genre) {
        Genre updatedGenre = genreService.updateGenre(id, genre);
        return ResponseEntity.ok(ApiResponse.success("Género actualizado exitosamente", updatedGenre));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success("Género eliminado exitosamente"));
    }
}
