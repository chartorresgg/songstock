package com.songstock.mapper;

import com.songstock.dto.GenreDTO;
import com.songstock.entity.Genre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper que convierte entre {@link Genre} y {@link GenreDTO}.
 */
@Component
public class GenreMapper {

    /**
     * Convierte un Genre a su DTO equivalente.
     */
    public GenreDTO toDTO(Genre genre) {
        if (genre == null)
            return null;

        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setDescription(genre.getDescription());
        dto.setIsActive(genre.getIsActive());
        dto.setCreatedAt(genre.getCreatedAt());

        if (genre.getAlbums() != null) {
            dto.setAlbumCount((long) genre.getAlbums().size());
        }

        return dto;
    }

    /**
     * Convierte un DTO a entidad Genre.
     */
    public Genre toEntity(GenreDTO dto) {
        if (dto == null)
            return null;

        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        genre.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        return genre;
    }

    /**
     * Actualiza una entidad Genre con los datos de un DTO.
     */
    public void updateEntity(Genre genre, GenreDTO dto) {
        if (genre == null || dto == null)
            return;

        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) {
            genre.setIsActive(dto.getIsActive());
        }
    }

    /**
     * Convierte una lista de entidades a lista de DTOs.
     */
    public List<GenreDTO> toDTOList(List<Genre> genres) {
        if (genres == null)
            return null;
        return genres.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
