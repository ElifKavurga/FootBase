package com.footbase.repository;

import com.footbase.entity.Oyuncu;
import com.footbase.entity.OyuncuMedya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OyuncuMedyaRepository extends JpaRepository<OyuncuMedya, Long> {

    List<OyuncuMedya> findByOyuncu(Oyuncu oyuncu);

    List<OyuncuMedya> findByOyuncuId(Long oyuncuId);

    @Query("SELECT DISTINCT om FROM OyuncuMedya om " +
            "LEFT JOIN FETCH om.oyuncu " +
            "WHERE om.oyuncu.id = :oyuncuId " +
            "ORDER BY om.id DESC")
    List<OyuncuMedya> findByOyuncuIdWithDetails(Long oyuncuId);
}
