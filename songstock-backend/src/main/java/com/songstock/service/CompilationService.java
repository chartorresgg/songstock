package com.songstock.service;

import com.songstock.dto.CompilationDTO;
import com.songstock.dto.SongDTO;
import com.songstock.entity.*;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.exception.BadRequestException;
import com.songstock.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompilationService {

    private static final Logger logger = LoggerFactory.getLogger(CompilationService.class);
    private static final int MAX_SONGS_PER_COMPILATION = 50;

    @Autowired
    private CompilationRepository compilationRepository;

    @Autowired
    private CompilationSongRepository compilationSongRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CompilationDTO> getMyCompilations(Long userId) {
        logger.info("Obteniendo compilaciones del usuario: {}", userId);

        List<Compilation> compilations = compilationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return compilations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompilationDTO getCompilationById(Long id, Long userId) {
        logger.info("Obteniendo compilación {} del usuario {}", id, userId);

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada con ID: " + id));

        if (!compilation.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para ver esta compilación");
        }

        return convertToDTOWithSongs(compilation);
    }

    @Transactional(readOnly = true)
    public List<CompilationDTO> getPublicCompilations(String name, Integer minSongs, Integer maxSongs) {
        logger.info("Obteniendo compilaciones públicas - name: {}, minSongs: {}, maxSongs: {}",
                name, minSongs, maxSongs);

        List<Compilation> compilations;

        if (name != null && !name.trim().isEmpty()) {
            compilations = compilationRepository
                    .findByIsPublicTrueAndNameContainingIgnoreCaseOrderByCreatedAtDesc(name.trim());
        } else {
            compilations = compilationRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        }

        List<CompilationDTO> result = compilations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Filtrar por cantidad de canciones
        if (minSongs != null || maxSongs != null) {
            result = result.stream()
                    .filter(dto -> {
                        int count = dto.getSongCount();
                        boolean matchMin = minSongs == null || count >= minSongs;
                        boolean matchMax = maxSongs == null || count <= maxSongs;
                        return matchMin && matchMax;
                    })
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public CompilationDTO getPublicCompilationById(Long id) {
        logger.info("Obteniendo compilación pública: {}", id);

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada con ID: " + id));

        if (!compilation.getIsPublic()) {
            throw new BadRequestException("Esta compilación no es pública");
        }

        return convertToDTOWithSongs(compilation);
    }

    public CompilationDTO cloneCompilation(Long id, Long userId) {
        logger.info("Clonando compilación {} para usuario {}", id, userId);

        Compilation original = compilationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada"));

        if (!original.getIsPublic()) {
            throw new BadRequestException("Solo se pueden clonar compilaciones públicas");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Compilation clone = new Compilation();
        clone.setUser(user);
        clone.setName(original.getName() + " (Copia)");
        clone.setDescription(original.getDescription());
        clone.setIsPublic(false);

        Compilation saved = compilationRepository.save(clone);

        // Copiar canciones
        for (CompilationSong originalSong : original.getCompilationSongs()) {
            CompilationSong clonedSong = new CompilationSong();
            clonedSong.setCompilation(saved);
            clonedSong.setSong(originalSong.getSong());
            clonedSong.setOrderPosition(originalSong.getOrderPosition());
            compilationSongRepository.save(clonedSong); // ← CORRECCIÓN: usar compilationSongRepository
        }

        logger.info("Compilación clonada con ID: {}", saved.getId());
        return convertToDTOWithSongs(saved);
    }

    public CompilationDTO createCompilation(CompilationDTO dto, Long userId) {
        logger.info("Creando compilación '{}' para usuario {}", dto.getName(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Compilation compilation = new Compilation();
        compilation.setUser(user);
        compilation.setName(dto.getName());
        compilation.setDescription(dto.getDescription());
        compilation.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false);

        Compilation saved = compilationRepository.save(compilation);
        logger.info("Compilación creada con ID: {}", saved.getId());

        return convertToDTO(saved);
    }

    public CompilationDTO updateCompilation(Long id, CompilationDTO dto, Long userId) {
        logger.info("Actualizando compilación {} del usuario {}", id, userId);

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada"));

        if (!compilation.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para modificar esta compilación");
        }

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            compilation.setName(dto.getName());
        }
        compilation.setDescription(dto.getDescription());
        if (dto.getIsPublic() != null) {
            compilation.setIsPublic(dto.getIsPublic());
        }

        Compilation updated = compilationRepository.save(compilation);
        return convertToDTO(updated);
    }

    public CompilationDTO addSongToCompilation(Long compilationId, Long songId, Long userId) {
        logger.info("Añadiendo canción {} a compilación {} del usuario {}", songId, compilationId, userId);

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada"));

        if (!compilation.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para modificar esta compilación");
        }

        long currentCount = compilationSongRepository.countByCompilationId(compilationId);
        if (currentCount >= MAX_SONGS_PER_COMPILATION) {
            throw new BadRequestException("La compilación ha alcanzado el límite máximo de "
                    + MAX_SONGS_PER_COMPILATION + " canciones");

        }

        if (compilationSongRepository.existsByCompilationIdAndSongId(compilationId, songId)) {
            throw new BadRequestException("La canción ya está en la compilación");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Canción no encontrada"));

        CompilationSong compilationSong = new CompilationSong();
        compilationSong.setCompilation(compilation);
        compilationSong.setSong(song);
        compilationSong.setOrderPosition((int) currentCount + 1);

        compilationSongRepository.save(compilationSong);
        logger.info("Canción añadida a la compilación");

        return convertToDTOWithSongs(compilation);
    }

    public void removeSongFromCompilation(Long compilationId, Long songId, Long userId) {
        logger.info("Eliminando canción {} de compilación {} del usuario {}", songId, compilationId, userId);

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada"));

        if (!compilation.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para modificar esta compilación");
        }

        CompilationSong compilationSong = compilationSongRepository.findByCompilationIdAndSongId(compilationId, songId)
                .orElseThrow(() -> new ResourceNotFoundException("La canción no está en esta compilación"));

        compilationSongRepository.delete(compilationSong);
        logger.info("Canción eliminada de la compilación");
    }

    public void deleteCompilation(Long id, Long userId) {
        logger.info("Eliminando compilación {} del usuario {}", id, userId);

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compilación no encontrada"));

        if (!compilation.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para eliminar esta compilación");
        }

        compilationRepository.delete(compilation);
        logger.info("Compilación eliminada");
    }

    private CompilationDTO convertToDTO(Compilation compilation) {
        CompilationDTO dto = new CompilationDTO();
        dto.setId(compilation.getId());
        dto.setUserId(compilation.getUser().getId());
        dto.setCreatorUsername(compilation.getUser().getUsername());
        dto.setName(compilation.getName());
        dto.setDescription(compilation.getDescription());
        dto.setIsPublic(compilation.getIsPublic());
        dto.setSongCount(compilation.getCompilationSongs().size());
        dto.setCreatedAt(compilation.getCreatedAt());
        dto.setUpdatedAt(compilation.getUpdatedAt());
        return dto;
    }

    private CompilationDTO convertToDTOWithSongs(Compilation compilation) {
        CompilationDTO dto = convertToDTO(compilation);

        List<SongDTO> songDTOs = compilation.getCompilationSongs().stream()
                .map(cs -> convertSongToDTO(cs.getSong()))
                .collect(Collectors.toList());

        dto.setSongs(songDTOs);
        return dto;
    }

    private SongDTO convertSongToDTO(Song song) {
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