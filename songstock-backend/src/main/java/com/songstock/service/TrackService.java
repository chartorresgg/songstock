package com.songstock.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.songstock.dto.ProductDTO;
import com.songstock.entity.Product;
import com.songstock.entity.Track;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.mapper.ProductMapper;
import com.songstock.repository.ProductRepository;
import com.songstock.repository.TrackRepository;

@Service
@Transactional
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<Track> searchTracksByTitle(String query) {
        return trackRepository.searchByTitle(query);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getVinylsForTrack(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        Long albumId = track.getAlbum().getId();

        List<Product> vinyls = productRepository.findVinylVersionsByAlbumId(albumId);

        return vinyls.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}
