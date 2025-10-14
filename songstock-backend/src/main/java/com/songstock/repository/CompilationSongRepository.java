package com.songstock.repository;

import com.songstock.entity.CompilationSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompilationSongRepository extends JpaRepository<CompilationSong, Long> {

    long countByCompilationId(Long compilationId);

    boolean existsByCompilationIdAndSongId(Long compilationId, Long songId);

    Optional<CompilationSong> findByCompilationIdAndSongId(Long compilationId, Long songId);
}