package com.songstock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.songstock.dto.ProductDTO;
import com.songstock.entity.Track;
import com.songstock.service.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TrackControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrackService trackService;

    @InjectMocks
    private TrackController trackController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Track sampleTrack;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(trackController).build();

        sampleTrack = new Track();
        sampleTrack.setId(1L);
        sampleTrack.setTitle("Example Track 1 (MP3)");
    }

    @Test
    public void testSearchTracks_endpoint() throws Exception {
        when(trackService.searchTracksByTitle("Example")).thenReturn(List.of(sampleTrack));

        mockMvc.perform(get("/api/tracks?q=Example").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(sampleTrack))));
    }

    @Test
    public void testGetVinylsForTrack_endpoint() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setId(10L);
        dto.setPrice(BigDecimal.valueOf(29.99));

        when(trackService.getVinylsForTrack(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tracks/1/vinyls").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }
}
