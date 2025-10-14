package com.songstock.service;

import com.songstock.dto.SongDTO;
import com.songstock.entity.Song;
import com.songstock.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SongService {

    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    @Autowired
    private SongRepository songRepository;

    @Transactional(readOnly = true)
    public List<SongDTO> getSongsByAlbumId(Long albumId) {
        logger.info("Obteniendo canciones del álbum: {}", albumId);
        
        List<Song> songs = songRepository.findByAlbumIdAndIsActiveTrue(albumId);
        
        return songs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SongDTO convertToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setAlbumId(song.getAlbum().getId());
        dto.setAlbumTitle(song.getAlbum().getTitle());
        dto.setArtistName(song.getAlbum().getArtist().getName());
        dto.setTrackNumber(song.getTrackNumber());
        dto.setTitle(song.getTitle());
        dto.setDurationSeconds(song.getDurationSeconds());
        return dto;
    }
}