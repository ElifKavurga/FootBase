package com.footbase.repository;

import com.footbase.entity.Oyuncu;
import com.footbase.entity.OyuncuPuanlari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OyuncuPuanlariRepository extends JpaRepository<OyuncuPuanlari, Long> {

    Optional<OyuncuPuanlari> findByOyuncu(Oyuncu oyuncu);

    Optional<OyuncuPuanlari> findByOyuncuId(Long oyuncuId);

    @Query("SELECT DISTINCT op FROM OyuncuPuanlari op " +
            "LEFT JOIN FETCH op.oyuncu " +
            "WHERE op.oyuncuId = :oyuncuId")
    Optional<OyuncuPuanlari> findByOyuncuIdWithDetails(Long oyuncuId);
}
