package com.songstock.service;

import com.songstock.dto.SongDTO;
import com.songstock.dto.VinylAvailabilityDTO;
import com.songstock.entity.Product;
import com.songstock.entity.Song;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.AlbumRepository;
import com.songstock.repository.ProductRepository;
import com.songstock.entity.Album;
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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public List<SongDTO> getSongsByAlbumId(Long albumId) {
        logger.info("Obteniendo canciones del álbum: {}", albumId);

        List<Song> songs = songRepository.findByAlbumIdAndIsActiveTrue(albumId);

        return songs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> searchSongs(String query) {
        logger.info("Buscando canciones con query: {}", query);

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        List<Song> songs = songRepository.searchByTitleOrAlbumOrArtist(query.trim());

        return songs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SongDTO getSongWithAvailableVinyls(Long songId) {
        logger.info("Obteniendo canción con vinilos disponibles: {}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Canción no encontrada con ID: " + songId));

        SongDTO dto = convertToDTO(song);

        // Obtener vinilos físicos del álbum
        List<Product> vinyls = productRepository.findPhysicalProductsByAlbumId(song.getAlbum().getId());

        // Convertir a DTO
        List<VinylAvailabilityDTO> vinylDTOs = vinyls.stream()
                .map(this::convertToVinylDTO)
                .collect(Collectors.toList());

        dto.setAvailableVinyls(vinylDTOs);

        return dto;
    }

    @Transactional
    public SongDTO createSong(SongDTO songDTO) {
        logger.info("Creando canción: {}", songDTO.getTitle());

        Album album = albumRepository.findById(songDTO.getAlbumId())
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado"));

        Song song = new Song();
        song.setAlbum(album);
        song.setTitle(songDTO.getTitle());
        song.setTrackNumber(songDTO.getTrackNumber());
        song.setDurationSeconds(songDTO.getDurationSeconds());
        song.setIsActive(true);

        Song saved = songRepository.save(song);
        return convertToDTO(saved);
    }

    @Transactional
    public SongDTO updateSong(Long id, SongDTO songDTO) {
        logger.info("Actualizando canción ID: {}", id);

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canción no encontrada"));

        if (songDTO.getTitle() != null) {
            song.setTitle(songDTO.getTitle());
        }
        if (songDTO.getTrackNumber() != null) {
            song.setTrackNumber(songDTO.getTrackNumber());
        }
        if (songDTO.getDurationSeconds() != null) {
            song.setDurationSeconds(songDTO.getDurationSeconds());
        }

        Song updated = songRepository.save(song);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteSong(Long id) {
        logger.info("Eliminando canción ID: {}", id);

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canción no encontrada"));

        song.setIsActive(false);
        songRepository.save(song);
    }

    @Transactional
    public List<SongDTO> createSongsBatch(List<SongDTO> songsDTO) {
        logger.info("Creando {} canciones en lote", songsDTO.size());

        if (songsDTO.isEmpty()) {
            return List.of();
        }

        Long albumId = songsDTO.get(0).getAlbumId();
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado"));

        List<Song> songs = songsDTO.stream()
                .map(dto -> {
                    Song song = new Song();
                    song.setAlbum(album);
                    song.setTitle(dto.getTitle());
                    song.setTrackNumber(dto.getTrackNumber());
                    song.setDurationSeconds(dto.getDurationSeconds());
                    song.setIsActive(true);
                    return song;
                })
                .collect(Collectors.toList());

        List<Song> saved = songRepository.saveAll(songs);
        return saved.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private VinylAvailabilityDTO convertToVinylDTO(Product product) {
        VinylAvailabilityDTO dto = new VinylAvailabilityDTO();
        dto.setProductId(product.getId());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setConditionType(product.getConditionType() != null ? product.getConditionType().name() : null);
        dto.setVinylSize(product.getVinylSize() != null ? product.getVinylSize().name() : null);
        dto.setVinylSpeed(product.getVinylSpeed() != null ? product.getVinylSpeed().name() : null);
        dto.setProviderName(product.getProvider().getBusinessName());
        dto.setProviderId(product.getProvider().getId());
        return dto;
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

    private SongDTO toDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setPrice(song.getPrice());
        dto.setFormat(song.getFormat());
        dto.setArtistName(
                song.getAlbum() != null && song.getAlbum().getArtist() != null ? song.getAlbum().getArtist().getName()
                        : "Unknown");
        dto.setAlbumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : "Single");
        return dto;
    }

    public List<SongDTO> searchAvailableSongs(String query, Long genreId) {
        List<Song> songs;

        if (query != null && !query.isEmpty()) {
            songs = songRepository.findByTitleContainingIgnoreCaseAndAvailableTrue(query);
        } else if (genreId != null) {
            songs = songRepository.findByAlbum_Genre_IdAndAvailableTrue(genreId);
        } else {
            songs = songRepository.findByAvailableTrue();
        }

        return songs.stream().map(this::toSongDTO).collect(Collectors.toList());
    }

    private SongDTO toSongDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setPrice(song.getPrice());
        dto.setAvailable(song.getAvailable());
        dto.setFormat(song.getFormat());

        if (song.getAlbum() != null) {
            dto.setAlbumTitle(song.getAlbum().getTitle());
            if (song.getAlbum().getArtist() != null) {
                dto.setArtistName(song.getAlbum().getArtist().getName());
            }
        }

        return dto;
    }
}