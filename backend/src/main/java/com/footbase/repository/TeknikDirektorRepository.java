package com.footbase.repository;

import com.footbase.entity.TeknikDirektor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeknikDirektorRepository extends JpaRepository<TeknikDirektor, Long> {

    Optional<TeknikDirektor> findByAdSoyad(String adSoyad);

    List<TeknikDirektor> findByUyruk(String uyruk);

    boolean existsByAdSoyad(String adSoyad);

    @Query("SELECT td FROM TeknikDirektor td WHERE LOWER(td.adSoyad) LIKE LOWER(CONCAT('%', :adSoyad, '%'))")
    List<TeknikDirektor> searchByAdSoyad(String adSoyad);
}
