package com.songstock.repository;

import com.songstock.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);

    List<Genre> findByIsActive(Boolean isActive);

    List<Genre> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}