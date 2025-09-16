package com.songstock.service;

import com.songstock.entity.Genre;
import com.songstock.exception.ResourceAlreadyExistsException;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Genre> getActiveGenres() {
        return genreRepository.findByIsActive(true);
    }

    @Transactional(readOnly = true)
    public Genre getGenreByName(String name) {
        return genreRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado: " + name));
    }

    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByName(genre.getName())) {
            throw new ResourceAlreadyExistsException("El género ya existe: " + genre.getName());
        }
        return genreRepository.save(genre);
    }

    public Genre updateGenre(Long id, Genre genre) {
        Genre existingGenre = getGenreById(id);

        if (!existingGenre.getName().equals(genre.getName()) &&
                genreRepository.existsByName(genre.getName())) {
            throw new ResourceAlreadyExistsException("El género ya existe: " + genre.getName());
        }

        existingGenre.setName(genre.getName());
        existingGenre.setDescription(genre.getDescription());
        existingGenre.setIsActive(genre.getIsActive());

        return genreRepository.save(existingGenre);
    }

    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Género no encontrado con ID: " + id);
        }
        genreRepository.deleteById(id);
    }
}