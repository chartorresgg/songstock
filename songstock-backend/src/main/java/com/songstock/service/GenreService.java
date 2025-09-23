package com.songstock.service;

import com.songstock.dto.GenreDTO;
import com.songstock.entity.Genre;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.exception.DuplicateResourceException;
import com.songstock.mapper.GenreMapper;
import com.songstock.repository.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GenreService {
    
    private static final Logger logger = LoggerFactory.getLogger(GenreService.class);
    
    @Autowired
    private GenreRepository genreRepository;
    
    @Autowired
    private GenreMapper genreMapper;
    
    /**
     * Crear un nuevo género
     */
    public GenreDTO createGenre(GenreDTO genreDTO) {
        logger.info("Creando nuevo género: {}", genreDTO.getName());
        
        // Verificar si ya existe un género con el mismo nombre
        Optional<Genre> existingGenre = genreRepository.findByNameIgnoreCase(genreDTO.getName());
        if (existingGenre.isPresent()) {
            throw new DuplicateResourceException("Ya existe un género con el nombre: " + genreDTO.getName());
        }
        
        Genre genre = genreMapper.toEntity(genreDTO);
        Genre savedGenre = genreRepository.save(genre);
        
        logger.info("Género creado exitosamente con ID: {}", savedGenre.getId());
        return genreMapper.toDTO(savedGenre);
    }
    
    /**
     * Obtener todos los géneros activos
     */
    @Transactional(readOnly = true)
    public List<GenreDTO> getAllActiveGenres() {
        logger.info("Obteniendo todos los géneros activos");
        List<Genre> genres = genreRepository.findByIsActiveTrue();
        return genreMapper.toDTOList(genres);
    }
    
    /**
     * Obtener géneros con paginación
     */
    @Transactional(readOnly = true)
    public Page<GenreDTO> getGenres(Pageable pageable) {
        logger.info("Obteniendo géneros con paginación");
        Page<Genre> genrePage = genreRepository.findByIsActiveTrue(pageable);
        return genrePage.map(genreMapper::toDTO);
    }
    
    /**
     * Obtener género por ID
     */
    @Transactional(readOnly = true)
    public GenreDTO getGenreById(Long id) {
        logger.info("Obteniendo género por ID: {}", id);
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));
        return genreMapper.toDTO(genre);
    }
    
    /**
     * Buscar géneros por nombre
     */
    @Transactional(readOnly = true)
    public List<GenreDTO> searchGenresByName(String name) {
        logger.info("Buscando géneros por nombre: {}", name);
        List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(name);
        return genreMapper.toDTOList(genres);
    }
    
    /**
     * Actualizar género
     */
    public GenreDTO updateGenre(Long id, GenreDTO genreDTO) {
        logger.info("Actualizando género con ID: {}", id);
        
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));
        
        // Verificar si el nuevo nombre ya existe en otro género
        if (!existingGenre.getName().equalsIgnoreCase(genreDTO.getName())) {
            Optional<Genre> duplicateGenre = genreRepository.findByNameIgnoreCase(genreDTO.getName());
            if (duplicateGenre.isPresent()) {
                throw new DuplicateResourceException("Ya existe un género con el nombre: " + genreDTO.getName());
            }
        }
        
        genreMapper.updateEntity(existingGenre, genreDTO);
        Genre updatedGenre = genreRepository.save(existingGenre);
        
        logger.info("Género actualizado exitosamente con ID: {}", updatedGenre.getId());
        return genreMapper.toDTO(updatedGenre);
    }
    
    /**
     * Eliminar género (soft delete)
     */
    public void deleteGenre(Long id) {
        logger.info("Eliminando género con ID: {}", id);
        
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));
        
        genre.setIsActive(false);
        genreRepository.save(genre);
        
        logger.info("Género eliminado exitosamente con ID: {}", id);
    }
    
    /**
     * Obtener géneros que tienen álbumes
     */
    @Transactional(readOnly = true)
    public List<GenreDTO> getGenresWithAlbums() {
        logger.info("Obteniendo géneros que tienen álbumes");
        List<Genre> genres = genreRepository.findGenresWithAlbums();
        return genreMapper.toDTOList(genres);
    }
}