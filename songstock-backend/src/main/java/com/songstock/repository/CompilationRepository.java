package com.songstock.repository;

import com.songstock.entity.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findByUserIdOrderByCreatedAtDesc(Long userId);
}