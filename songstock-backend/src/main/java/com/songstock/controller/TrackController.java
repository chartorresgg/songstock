package com.songstock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.songstock.dto.ProductDTO;
import com.songstock.entity.Track;
import com.songstock.service.TrackService;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    @Autowired
    private TrackService trackService;

    /**
     * Buscar pistas por título
     */
    @GetMapping
    public ResponseEntity<List<Track>> searchTracks(@RequestParam(name = "q", required = false) String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        List<Track> results = trackService.searchTracksByTitle(q);
        return ResponseEntity.ok(results);
    }

    /**
     * Obtener vinilos disponibles para una pista
     */
    @GetMapping("/{id}/vinyls")
    public ResponseEntity<List<ProductDTO>> getVinylsForTrack(@PathVariable("id") Long id) {
        List<ProductDTO> vinyls = trackService.getVinylsForTrack(id);
        return ResponseEntity.ok(vinyls);
    }
}
