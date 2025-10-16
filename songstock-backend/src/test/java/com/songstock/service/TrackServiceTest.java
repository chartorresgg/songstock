package com.songstock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.songstock.dto.ProductDTO;
import com.songstock.entity.Album;
import com.songstock.entity.Product;
import com.songstock.entity.ProductType;
import com.songstock.entity.Track;
import com.songstock.mapper.ProductMapper;
import com.songstock.repository.ProductRepository;
import com.songstock.repository.TrackRepository;

@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private TrackService trackService;

    private Track sampleTrack;

    @BeforeEach
    public void setup() {
        Album album = new Album();
        album.setId(100L);

        sampleTrack = new Track();
        sampleTrack.setId(1L);
        sampleTrack.setTitle("Example Track 1 (MP3)");
        sampleTrack.setAlbum(album);
    }

    @Test
    public void testSearchTracksByTitle_returnsList() {
        when(trackRepository.searchByTitle("Example")).thenReturn(List.of(sampleTrack));

        List<Track> results = trackService.searchTracksByTitle("Example");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Example Track 1 (MP3)", results.get(0).getTitle());
    }

    @Test
    public void testGetVinylsForTrack_returnsProductDTOs() {
        Product vinyl = new Product();
        vinyl.setId(10L);
        vinyl.setAlbum(sampleTrack.getAlbum());
        vinyl.setProductType(ProductType.PHYSICAL);
        vinyl.setPrice(BigDecimal.valueOf(29.99));

        ProductDTO dto = new ProductDTO();
        dto.setId(10L);
        dto.setPrice(BigDecimal.valueOf(29.99));

        when(trackRepository.findById(1L)).thenReturn(Optional.of(sampleTrack));
        when(productRepository.findVinylVersionsByAlbumId(100L)).thenReturn(List.of(vinyl));
        when(productMapper.toDTO(vinyl)).thenReturn(dto);

        List<ProductDTO> vinyls = trackService.getVinylsForTrack(1L);
        assertNotNull(vinyls);
        assertEquals(1, vinyls.size());
        assertEquals(10L, vinyls.get(0).getId());
    }
}
