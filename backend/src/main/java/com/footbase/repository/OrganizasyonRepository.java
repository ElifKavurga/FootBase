package com.footbase.repository;

import com.footbase.entity.Organizasyon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizasyonRepository extends JpaRepository<Organizasyon, Long> {

    Optional<Organizasyon> findByAd(String ad);

    boolean existsByAd(String ad);

    @Query("SELECT o FROM Organizasyon o WHERE LOWER(o.ad) LIKE LOWER(CONCAT('%', :ad, '%'))")
    List<Organizasyon> searchByAd(String ad);
}
