package com.songstock.mapper;

import com.songstock.dto.AlbumDTO;
import com.songstock.entity.Album;
import com.songstock.entity.Artist;
import com.songstock.entity.Genre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {
    
    public AlbumDTO toDTO(Album album) {
        if (album == null) return null;
        
        AlbumDTO dto = new AlbumDTO();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseYear(album.getReleaseYear());
        dto.setLabel(album.getLabel());
        dto.setCatalogNumber(album.getCatalogNumber());
        dto.setDescription(album.getDescription());
        dto.setDurationMinutes(album.getDurationMinutes());
        dto.setIsActive(album.getIsActive());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());
        
        if (album.getArtist() != null) {
            dto.setArtistId(album.getArtist().getId());
            dto.setArtistName(album.getArtist().getName());
        }
        
        if (album.getGenre() != null) {
            dto.setGenreId(album.getGenre().getId());
            dto.setGenreName(album.getGenre().getName());
        }
        
        if (album.getProducts() != null) {
            dto.setProductCount((long) album.getProducts().size());
            dto.setHasVinylVersion(album.hasVinylVersion());
            dto.setHasDigitalVersion(album.hasDigitalVersion());
        }
        
        return dto;
    }
    
    public Album toEntity(AlbumDTO dto) {
        if (dto == null) return null;
        
        Album album = new Album();
        album.setId(dto.getId());
        album.setTitle(dto.getTitle());
        album.setReleaseYear(dto.getReleaseYear());
        album.setLabel(dto.getLabel());
        album.setCatalogNumber(dto.getCatalogNumber());
        album.setDescription(dto.getDescription());
        album.setDurationMinutes(dto.getDurationMinutes());
        album.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        return album;
    }
    
    public Album toEntity(AlbumDTO dto, Artist artist, Genre genre) {
        Album album = toEntity(dto);
        if (album != null) {
            album.setArtist(artist);
            album.setGenre(genre);
        }
        return album;
    }
    
    public void updateEntity(Album album, AlbumDTO dto) {
        if (album == null || dto == null) return;
        
        album.setTitle(dto.getTitle());
        album.setReleaseYear(dto.getReleaseYear());
        album.setLabel(dto.getLabel());
        album.setCatalogNumber(dto.getCatalogNumber());
        album.setDescription(dto.getDescription());
        album.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getIsActive() != null) {
            album.setIsActive(dto.getIsActive());
        }
    }
    
    public List<AlbumDTO> toDTOList(List<Album> albums) {
        if (albums == null) return null;
        return albums.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}