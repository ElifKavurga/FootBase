package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.MacOyuncuOlaylari;
import com.footbase.entity.Oyuncu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MacOyuncuOlaylariRepository extends JpaRepository<MacOyuncuOlaylari, Long> {

    List<MacOyuncuOlaylari> findByMac(Mac mac);

    List<MacOyuncuOlaylari> findByMacId(Long macId);

    List<MacOyuncuOlaylari> findByOyuncu(Oyuncu oyuncu);

    List<MacOyuncuOlaylari> findByOyuncuId(Long oyuncuId);

    @Query("SELECT DISTINCT mo FROM MacOyuncuOlaylari mo " +
            "LEFT JOIN FETCH mo.mac " +
            "LEFT JOIN FETCH mo.oyuncu " +
            "WHERE mo.mac.id = :macId " +
            "ORDER BY mo.id")
    List<MacOyuncuOlaylari> findByMacIdWithDetails(Long macId);
}
