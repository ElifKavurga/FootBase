package com.footbase.repository;

import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TakimRepository extends JpaRepository<Takim, Long> {

       @Query("SELECT DISTINCT t FROM Takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "LEFT JOIN FETCH t.teknikDirektor")
       List<Takim> findAll();

       @Query("SELECT DISTINCT t FROM Takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "LEFT JOIN FETCH t.teknikDirektor " +
                     "WHERE t.id = :id")
       java.util.Optional<Takim> findById(Long id);

       @Query("SELECT DISTINCT t FROM Takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "LEFT JOIN FETCH t.teknikDirektor " +
                     "WHERE t.ad = :ad")
       Optional<Takim> findByAd(String ad);

       boolean existsByAd(String ad);
}
