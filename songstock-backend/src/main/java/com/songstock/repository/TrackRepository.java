package com.songstock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.songstock.entity.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    @Query("SELECT t FROM Track t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) AND t.isActive = true")
    List<Track> searchByTitle(@Param("query") String query);

    List<Track> findByAlbumIdAndIsActiveTrue(Long albumId);
}
