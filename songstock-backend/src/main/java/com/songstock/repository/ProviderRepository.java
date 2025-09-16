package com.songstock.repository;

import com.songstock.entity.Provider;
import com.songstock.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUserId(Long userId);

    List<Provider> findByVerificationStatus(VerificationStatus verificationStatus);

    List<Provider> findByBusinessNameContainingIgnoreCase(String businessName);

    List<Provider> findByCity(String city);

    List<Provider> findByCountry(String country);

    @Query("SELECT p FROM Provider p WHERE p.businessName LIKE %:name% AND p.verificationStatus = :status")
    List<Provider> findByBusinessNameAndStatus(@Param("name") String name, @Param("status") VerificationStatus status);

    @Query("SELECT p FROM Provider p JOIN p.user u WHERE u.isActive = :isActive")
    List<Provider> findByUserIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT COUNT(p) FROM Provider p WHERE p.verificationStatus = 'VERIFIED'")
    Long countVerifiedProviders();
}