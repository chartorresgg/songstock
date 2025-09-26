package com.songstock.service;

import com.songstock.dto.AlbumDTO;
import com.songstock.dto.AlbumFormatsDTO;
import com.songstock.dto.ProductDTO;
import com.songstock.entity.Album;
import com.songstock.entity.Artist;
import com.songstock.entity.Genre;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.mapper.AlbumMapper;
import com.songstock.mapper.ProductMapper;
import com.songstock.repository.AlbumRepository;
import com.songstock.repository.ArtistRepository;
import com.songstock.repository.GenreRepository;
import com.songstock.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los álbumes.
 * Incluye creación, consulta, actualización, eliminación (soft delete)
 * y consultas avanzadas como búsqueda de formatos (vinilo/digital).
 */
@Service
@Transactional
public class AlbumService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Crear un nuevo álbum.
     * 
     * @param albumDTO DTO con los datos del álbum a crear.
     * @return DTO del álbum creado.
     */
    public AlbumDTO createAlbum(AlbumDTO albumDTO) {
        logger.info("Creando nuevo álbum: {}", albumDTO.getTitle());

        // Obtener artista asociado
        Artist artist = artistRepository.findById(albumDTO.getArtistId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Artista no encontrado con ID: " + albumDTO.getArtistId()));

        // Obtener género (opcional)
        Genre genre = null;
        if (albumDTO.getGenreId() != null) {
            genre = genreRepository.findById(albumDTO.getGenreId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Género no encontrado con ID: " + albumDTO.getGenreId()));
        }

        // Convertir DTO a entidad y persistir
        Album album = albumMapper.toEntity(albumDTO, artist, genre);
        Album savedAlbum = albumRepository.save(album);

        logger.info("Álbum creado exitosamente con ID: {}", savedAlbum.getId());
        return albumMapper.toDTO(savedAlbum);
    }

    /**
     * Obtener todos los álbumes activos (isActive = true).
     */
    @Transactional(readOnly = true)
    public List<AlbumDTO> getAllActiveAlbums() {
        logger.info("Obteniendo todos los álbumes activos");
        List<Album> albums = albumRepository.findByIsActiveTrue();
        return albumMapper.toDTOList(albums);
    }

    /**
     * Obtener álbumes paginados.
     * 
     * @param pageable objeto con información de la paginación.
     */
    @Transactional(readOnly = true)
    public Page<AlbumDTO> getAlbums(Pageable pageable) {
        logger.info("Obteniendo álbumes con paginación");
        Page<Album> albumPage = albumRepository.findByIsActiveTrue(pageable);
        return albumPage.map(albumMapper::toDTO);
    }

    /**
     * Obtener un álbum por su ID.
     */
    @Transactional(readOnly = true)
    public AlbumDTO getAlbumById(Long id) {
        logger.info("Obteniendo álbum por ID: {}", id);
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));
        return albumMapper.toDTO(album);
    }

    /**
     * Buscar álbumes por coincidencia parcial en el título (case insensitive).
     */
    @Transactional(readOnly = true)
    public List<AlbumDTO> searchAlbumsByTitle(String title) {
        logger.info("Buscando álbumes por título: {}", title);
        List<Album> albums = albumRepository.findByTitleContainingIgnoreCase(title);
        return albumMapper.toDTOList(albums);
    }

    /**
     * Obtener álbumes de un artista específico.
     */
    @Transactional(readOnly = true)
    public List<AlbumDTO> getAlbumsByArtist(Long artistId) {
        logger.info("Obteniendo álbumes por artista ID: {}", artistId);
        List<Album> albums = albumRepository.findByArtistIdAndIsActiveTrue(artistId);
        return albumMapper.toDTOList(albums);
    }

    /**
     * Obtener álbumes de un género específico.
     */
    @Transactional(readOnly = true)
    public List<AlbumDTO> getAlbumsByGenre(Long genreId) {
        logger.info("Obteniendo álbumes por género ID: {}", genreId);
        List<Album> albums = albumRepository.findByGenreIdAndIsActiveTrue(genreId);
        return albumMapper.toDTOList(albums);
    }

    /**
     * Obtener álbumes por año de lanzamiento.
     */
    @Transactional(readOnly = true)
    public List<AlbumDTO> getAlbumsByReleaseYear(Integer year) {
        logger.info("Obteniendo álbumes por año: {}", year);
        List<Album> albums = albumRepository.findByReleaseYearAndIsActiveTrue(year);
        return albumMapper.toDTOList(albums);
    }

    /**
     * Actualizar información de un álbum existente.
     */
    public AlbumDTO updateAlbum(Long id, AlbumDTO albumDTO) {
        logger.info("Actualizando álbum con ID: {}", id);

        Album existingAlbum = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));

        // Si cambió el artista, actualizar
        if (!existingAlbum.getArtist().getId().equals(albumDTO.getArtistId())) {
            Artist newArtist = artistRepository.findById(albumDTO.getArtistId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Artista no encontrado con ID: " + albumDTO.getArtistId()));
            existingAlbum.setArtist(newArtist);
        }

        // Si cambió el género, actualizar
        if (albumDTO.getGenreId() != null) {
            if (existingAlbum.getGenre() == null || !existingAlbum.getGenre().getId().equals(albumDTO.getGenreId())) {
                Genre newGenre = genreRepository.findById(albumDTO.getGenreId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Género no encontrado con ID: " + albumDTO.getGenreId()));
                existingAlbum.setGenre(newGenre);
            }
        } else {
            existingAlbum.setGenre(null);
        }

        // Mapear datos actualizados desde el DTO
        albumMapper.updateEntity(existingAlbum, albumDTO);
        Album updatedAlbum = albumRepository.save(existingAlbum);

        logger.info("Álbum actualizado exitosamente con ID: {}", updatedAlbum.getId());
        return albumMapper.toDTO(updatedAlbum);
    }

    /**
     * Eliminar un álbum (soft delete: se marca como inactivo).
     */
    public void deleteAlbum(Long id) {
        logger.info("Eliminando álbum con ID: {}", id);

        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));

        album.setIsActive(false); // Soft delete
        albumRepository.save(album);

        logger.info("Álbum eliminado exitosamente con ID: {}", id);
    }

    /**
     * Obtener todos los formatos disponibles de un álbum (digital y vinilo).
     * Incluye recomendaciones según el precio más bajo y disponibilidad.
     */
    @Transactional(readOnly = true)
    public AlbumFormatsDTO getAlbumFormats(Long albumId) {
        logger.info("Obteniendo formatos disponibles para álbum ID: {}", albumId);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + albumId));

        // Construir DTO con datos base del álbum
        AlbumFormatsDTO formatsDTO = new AlbumFormatsDTO();
        formatsDTO.setAlbumId(albumId);
        formatsDTO.setAlbumTitle(album.getTitle());
        formatsDTO.setArtistName(album.getArtist().getName());
        formatsDTO.setReleaseYear(album.getReleaseYear());

        if (album.getGenre() != null) {
            formatsDTO.setGenreName(album.getGenre().getName());
        }

        // Obtener productos digitales asociados
        List<ProductDTO> digitalVersions = productRepository.findDigitalVersionsByAlbumId(albumId)
                .stream()
                .map(productMapper::toBasicDTO)
                .toList();

        // Obtener productos en vinilo asociados
        List<ProductDTO> vinylVersions = productRepository.findVinylVersionsByAlbumId(albumId)
                .stream()
                .map(productMapper::toBasicDTO)
                .toList();

        formatsDTO.setDigitalVersions(digitalVersions);
        formatsDTO.setVinylVersions(vinylVersions);

        // Recomendación: la versión más económica de cada tipo (con stock > 0)
        if (!digitalVersions.isEmpty()) {
            ProductDTO recommendedDigital = digitalVersions.stream()
                    .filter(p -> p.getStockQuantity() > 0)
                    .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                    .orElse(digitalVersions.get(0));
            formatsDTO.setRecommendedDigital(recommendedDigital);
        }

        if (!vinylVersions.isEmpty()) {
            ProductDTO recommendedVinyl = vinylVersions.stream()
                    .filter(p -> p.getStockQuantity() > 0)
                    .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                    .orElse(vinylVersions.get(0));
            formatsDTO.setRecommendedVinyl(recommendedVinyl);
        }

        logger.info("Formatos obtenidos: {} digitales, {} vinilos", digitalVersions.size(), vinylVersions.size());
        return formatsDTO;
    }

    /**
     * Verificar si un álbum tiene versión en vinilo.
     */
    @Transactional(readOnly = true)
    public boolean hasVinylVersion(Long albumId) {
        logger.info("Verificando si álbum {} tiene versión en vinilo", albumId);
        return productRepository.hasVinylVersion(albumId);
    }

    /**
     * Verificar si un álbum tiene versión digital.
     */
    @Transactional(readOnly = true)
    public boolean hasDigitalVersion(Long albumId) {
        logger.info("Verificando si álbum {} tiene versión digital", albumId);
        return productRepository.hasDigitalVersion(albumId);
    }

    /**
     * Obtener álbumes que cuentan con ambos formatos (digital y vinilo).
     */
    @Transactional(readOnly = true)
    public List<AlbumDTO> getAlbumsWithBothFormats() {
        logger.info("Obteniendo álbumes que tienen ambos formatos");
        List<Album> albums = albumRepository.findAlbumsWithBothFormats();
        return albumMapper.toDTOList(albums);
    }
}
