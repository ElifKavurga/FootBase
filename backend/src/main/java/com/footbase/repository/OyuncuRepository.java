package com.footbase.repository;

import com.footbase.entity.Oyuncu;
import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OyuncuRepository extends JpaRepository<Oyuncu, Long> {

    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim")
    List<Oyuncu> findAll();

    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.id = :id")
    java.util.Optional<Oyuncu> findById(Long id);

    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.takim = :takim")
    List<Oyuncu> findByTakim(Takim takim);

    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.takim.id = :takimId")
    List<Oyuncu> findByTakimId(Long takimId);

    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.pozisyon = :pozisyon")
    List<Oyuncu> findByPozisyon(String pozisyon);
}
