package com.footbase.repository;

import com.footbase.entity.Hakem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HakemRepository extends JpaRepository<Hakem, Long> {

    Optional<Hakem> findByAdSoyad(String adSoyad);

    List<Hakem> findByUyruk(String uyruk);

    boolean existsByAdSoyad(String adSoyad);

    @Query("SELECT h FROM Hakem h WHERE LOWER(h.adSoyad) LIKE LOWER(CONCAT('%', :adSoyad, '%'))")
    List<Hakem> searchByAdSoyad(String adSoyad);
}
