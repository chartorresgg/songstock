package com.songstock.controller;

import com.songstock.dto.SongDTO;
import com.songstock.service.SongService;
import com.songstock.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@Tag(name = "Songs", description = "Gestión de Canciones")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    @Autowired
    private SongService songService;

    @GetMapping("/album/{albumId}")
    @Operation(summary = "Canciones por álbum", description = "Obtener todas las canciones de un álbum")
    public ResponseEntity<ApiResponse<List<SongDTO>>> getSongsByAlbum(@PathVariable Long albumId) {
        logger.info("REST request to get songs for album: {}", albumId);

        List<SongDTO> songs = songService.getSongsByAlbumId(albumId);

        return ResponseEntity.ok(ApiResponse.success("Canciones obtenidas exitosamente", songs));
    }
}