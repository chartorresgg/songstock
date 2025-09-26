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

/**
 * Servicio encargado de la lógica de negocio relacionada con los géneros
 * musicales.
 * Proporciona operaciones CRUD, búsquedas y filtros relacionados con la entidad
 * {@link Genre}.
 *
 * Anotaciones:
 * - {@link Service}: define esta clase como un servicio dentro del contexto de
 * Spring.
 * - {@link Transactional}: asegura que las operaciones de base de datos se
 * ejecuten dentro de una transacción.
 */
@Service
@Transactional
public class GenreService {

    private static final Logger logger = LoggerFactory.getLogger(GenreService.class);

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreMapper genreMapper;

    /**
     * Crea un nuevo género en la base de datos.
     *
     * @param genreDTO Objeto de transferencia con los datos del género a crear.
     * @return El género creado en formato {@link GenreDTO}.
     * @throws DuplicateResourceException Si ya existe un género con el mismo
     *                                    nombre.
     */
    public GenreDTO createGenre(GenreDTO genreDTO) {
        logger.info("Creando nuevo género: {}", genreDTO.getName());

        // Verificar si ya existe un género con el mismo nombre (ignorando
        // mayúsculas/minúsculas)
        Optional<Genre> existingGenre = genreRepository.findByNameIgnoreCase(genreDTO.getName());
        if (existingGenre.isPresent()) {
            throw new DuplicateResourceException("Ya existe un género con el nombre: " + genreDTO.getName());
        }

        // Mapear DTO a entidad y guardar en BD
        Genre genre = genreMapper.toEntity(genreDTO);
        Genre savedGenre = genreRepository.save(genre);

        logger.info("Género creado exitosamente con ID: {}", savedGenre.getId());
        return genreMapper.toDTO(savedGenre);
    }

    /**
     * Obtiene todos los géneros activos.
     *
     * @return Lista de géneros activos en formato {@link GenreDTO}.
     */
    @Transactional(readOnly = true)
    public List<GenreDTO> getAllActiveGenres() {
        logger.info("Obteniendo todos los géneros activos");
        List<Genre> genres = genreRepository.findByIsActiveTrue();
        return genreMapper.toDTOList(genres);
    }

    /**
     * Obtiene géneros con paginación.
     *
     * @param pageable Objeto de configuración de paginación.
     * @return Página de géneros activos en formato {@link GenreDTO}.
     */
    @Transactional(readOnly = true)
    public Page<GenreDTO> getGenres(Pageable pageable) {
        logger.info("Obteniendo géneros con paginación");
        Page<Genre> genrePage = genreRepository.findByIsActiveTrue(pageable);
        return genrePage.map(genreMapper::toDTO);
    }

    /**
     * Obtiene un género específico por su ID.
     *
     * @param id Identificador único del género.
     * @return El género encontrado en formato {@link GenreDTO}.
     * @throws ResourceNotFoundException Si no existe el género con el ID
     *                                   especificado.
     */
    @Transactional(readOnly = true)
    public GenreDTO getGenreById(Long id) {
        logger.info("Obteniendo género por ID: {}", id);
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));
        return genreMapper.toDTO(genre);
    }

    /**
     * Busca géneros por nombre (coincidencia parcial e insensible a
     * mayúsculas/minúsculas).
     *
     * @param name Nombre o fragmento de nombre del género.
     * @return Lista de géneros que coinciden con el criterio de búsqueda.
     */
    @Transactional(readOnly = true)
    public List<GenreDTO> searchGenresByName(String name) {
        logger.info("Buscando géneros por nombre: {}", name);
        List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(name);
        return genreMapper.toDTOList(genres);
    }

    /**
     * Actualiza un género existente.
     *
     * @param id       Identificador del género a actualizar.
     * @param genreDTO Datos nuevos a aplicar al género.
     * @return El género actualizado en formato {@link GenreDTO}.
     * @throws ResourceNotFoundException  Si el género no existe.
     * @throws DuplicateResourceException Si el nuevo nombre ya está en uso por otro
     *                                    género.
     */
    public GenreDTO updateGenre(Long id, GenreDTO genreDTO) {
        logger.info("Actualizando género con ID: {}", id);

        // Verificar existencia del género
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));

        // Validar si el nombre fue cambiado y ya existe en otro registro
        if (!existingGenre.getName().equalsIgnoreCase(genreDTO.getName())) {
            Optional<Genre> duplicateGenre = genreRepository.findByNameIgnoreCase(genreDTO.getName());
            if (duplicateGenre.isPresent()) {
                throw new DuplicateResourceException("Ya existe un género con el nombre: " + genreDTO.getName());
            }
        }

        // Actualizar entidad y guardar cambios
        genreMapper.updateEntity(existingGenre, genreDTO);
        Genre updatedGenre = genreRepository.save(existingGenre);

        logger.info("Género actualizado exitosamente con ID: {}", updatedGenre.getId());
        return genreMapper.toDTO(updatedGenre);
    }

    /**
     * Realiza un "soft delete" de un género (marcar como inactivo en lugar de
     * eliminar físicamente).
     *
     * @param id Identificador del género a eliminar.
     * @throws ResourceNotFoundException Si no existe el género con el ID
     *                                   especificado.
     */
    public void deleteGenre(Long id) {
        logger.info("Eliminando género con ID: {}", id);

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));

        // Soft delete: se marca como inactivo en vez de eliminarlo físicamente
        genre.setIsActive(false);
        genreRepository.save(genre);

        logger.info("Género eliminado exitosamente con ID: {}", id);
    }

    /**
     * Obtiene todos los géneros que tienen al menos un álbum asociado.
     *
     * @return Lista de géneros con álbumes en formato {@link GenreDTO}.
     */
    @Transactional(readOnly = true)
    public List<GenreDTO> getGenresWithAlbums() {
        logger.info("Obteniendo géneros que tienen álbumes");
        List<Genre> genres = genreRepository.findGenresWithAlbums();
        return genreMapper.toDTOList(genres);
    }
}
