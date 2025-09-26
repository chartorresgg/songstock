package com.songstock.service;

import com.songstock.dto.ArtistDTO;
import com.songstock.entity.Artist;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.exception.DuplicateResourceException;
import com.songstock.mapper.ArtistMapper;
import com.songstock.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que maneja la lógica de negocio relacionada con los artistas.
 * Incluye creación, actualización, eliminación (soft delete) y búsquedas
 * personalizadas.
 */
@Service
@Transactional
public class ArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistMapper artistMapper;

    /**
     * Crear un nuevo artista, validando que no exista otro con el mismo nombre.
     */
    public ArtistDTO createArtist(ArtistDTO artistDTO) {
        logger.info("Creando nuevo artista: {}", artistDTO.getName());

        // Validar duplicados por nombre (case insensitive)
        Optional<Artist> existingArtist = artistRepository.findByNameIgnoreCase(artistDTO.getName());
        if (existingArtist.isPresent()) {
            throw new DuplicateResourceException("Ya existe un artista con el nombre: " + artistDTO.getName());
        }

        // Guardar nuevo artista
        Artist artist = artistMapper.toEntity(artistDTO);
        Artist savedArtist = artistRepository.save(artist);

        logger.info("Artista creado exitosamente con ID: {}", savedArtist.getId());
        return artistMapper.toDTO(savedArtist);
    }

    /**
     * Obtener todos los artistas activos (isActive = true).
     */
    @Transactional(readOnly = true)
    public List<ArtistDTO> getAllActiveArtists() {
        logger.info("Obteniendo todos los artistas activos");
        List<Artist> artists = artistRepository.findByIsActiveTrue();
        return artistMapper.toDTOList(artists);
    }

    /**
     * Obtener artistas con paginación.
     */
    @Transactional(readOnly = true)
    public Page<ArtistDTO> getArtists(Pageable pageable) {
        logger.info("Obteniendo artistas con paginación");
        Page<Artist> artistPage = artistRepository.findByIsActiveTrue(pageable);
        return artistPage.map(artistMapper::toDTO);
    }

    /**
     * Obtener artista por su ID.
     */
    @Transactional(readOnly = true)
    public ArtistDTO getArtistById(Long id) {
        logger.info("Obteniendo artista por ID: {}", id);
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con ID: " + id));
        return artistMapper.toDTO(artist);
    }

    /**
     * Buscar artistas cuyo nombre contenga una subcadena.
     */
    @Transactional(readOnly = true)
    public List<ArtistDTO> searchArtistsByName(String name) {
        logger.info("Buscando artistas por nombre: {}", name);
        List<Artist> artists = artistRepository.findByNameContainingIgnoreCase(name);
        return artistMapper.toDTOList(artists);
    }

    /**
     * Obtener artistas filtrados por país.
     */
    @Transactional(readOnly = true)
    public List<ArtistDTO> getArtistsByCountry(String country) {
        logger.info("Obteniendo artistas por país: {}", country);
        List<Artist> artists = artistRepository.findByCountryIgnoreCaseAndIsActiveTrue(country);
        return artistMapper.toDTOList(artists);
    }

    /**
     * Actualizar información de un artista.
     * Valida duplicados en caso de cambio de nombre.
     */
    public ArtistDTO updateArtist(Long id, ArtistDTO artistDTO) {
        logger.info("Actualizando artista con ID: {}", id);

        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con ID: " + id));

        // Validar duplicado de nombre
        if (!existingArtist.getName().equalsIgnoreCase(artistDTO.getName())) {
            Optional<Artist> duplicateArtist = artistRepository.findByNameIgnoreCase(artistDTO.getName());
            if (duplicateArtist.isPresent()) {
                throw new DuplicateResourceException("Ya existe un artista con el nombre: " + artistDTO.getName());
            }
        }

        // Mapear cambios y guardar
        artistMapper.updateEntity(existingArtist, artistDTO);
        Artist updatedArtist = artistRepository.save(existingArtist);

        logger.info("Artista actualizado exitosamente con ID: {}", updatedArtist.getId());
        return artistMapper.toDTO(updatedArtist);
    }

    /**
     * Eliminar un artista (soft delete: se marca como inactivo).
     */
    public void deleteArtist(Long id) {
        logger.info("Eliminando artista con ID: {}", id);

        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con ID: " + id));

        artist.setIsActive(false);
        artistRepository.save(artist);

        logger.info("Artista eliminado exitosamente con ID: {}", id);
    }

    /**
     * Obtener artistas que tengan al menos un álbum asociado.
     */
    @Transactional(readOnly = true)
    public List<ArtistDTO> getArtistsWithAlbums() {
        logger.info("Obteniendo artistas que tienen álbumes");
        List<Artist> artists = artistRepository.findArtistsWithAlbums();
        return artistMapper.toDTOList(artists);
    }
}
