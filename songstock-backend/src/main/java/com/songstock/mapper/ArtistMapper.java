package com.songstock.mapper;

import com.songstock.dto.ArtistDTO;
import com.songstock.entity.Artist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistMapper {
    
    public ArtistDTO toDTO(Artist artist) {
        if (artist == null) return null;
        
        ArtistDTO dto = new ArtistDTO();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setBio(artist.getBio());
        dto.setCountry(artist.getCountry());
        dto.setFormedYear(artist.getFormedYear());
        dto.setIsActive(artist.getIsActive());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());
        
        if (artist.getAlbums() != null) {
            dto.setAlbumCount((long) artist.getAlbums().size());
        }
        
        return dto;
    }
    
    public Artist toEntity(ArtistDTO dto) {
        if (dto == null) return null;
        
        Artist artist = new Artist();
        artist.setId(dto.getId());
        artist.setName(dto.getName());
        artist.setBio(dto.getBio());
        artist.setCountry(dto.getCountry());
        artist.setFormedYear(dto.getFormedYear());
        artist.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        return artist;
    }
    
    public void updateEntity(Artist artist, ArtistDTO dto) {
        if (artist == null || dto == null) return;
        
        artist.setName(dto.getName());
        artist.setBio(dto.getBio());
        artist.setCountry(dto.getCountry());
        artist.setFormedYear(dto.getFormedYear());
        if (dto.getIsActive() != null) {
            artist.setIsActive(dto.getIsActive());
        }
    }
    
    public List<ArtistDTO> toDTOList(List<Artist> artists) {
        if (artists == null) return null;
        return artists.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}