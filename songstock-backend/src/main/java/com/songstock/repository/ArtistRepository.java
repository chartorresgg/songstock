package com.songstock.repository;

import com.songstock.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByNameContainingIgnoreCase(String name);

    List<Artist> findByCountry(String country);

    List<Artist> findByFormedYear(Integer year);

    List<Artist> findByIsActive(Boolean isActive);

    @Query("SELECT a FROM Artist a WHERE a.name LIKE %:name% AND a.country = :country")
    List<Artist> findByNameAndCountry(@Param("name") String name, @Param("country") String country);

    @Query("SELECT a FROM Artist a WHERE a.formedYear BETWEEN :startYear AND :endYear")
    List<Artist> findByFormedYearBetween(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
}