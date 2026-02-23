package com.footbase.repository;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Oyuncu;
import com.footbase.entity.OyuncuYorumlari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OyuncuYorumlariRepository extends JpaRepository<OyuncuYorumlari, Long> {

       @Query("SELECT DISTINCT oy FROM OyuncuYorumlari oy " +
                     "LEFT JOIN FETCH oy.kullanici " +
                     "WHERE oy.oyuncu = :oyuncu " +
                     "ORDER BY oy.olusturmaTarihi DESC")
       List<OyuncuYorumlari> findByOyuncuOrderByOlusturmaTarihiDesc(Oyuncu oyuncu);

       @Query("SELECT DISTINCT oy FROM OyuncuYorumlari oy " +
                     "LEFT JOIN FETCH oy.kullanici " +
                     "WHERE oy.oyuncu.id = :oyuncuId " +
                     "ORDER BY oy.olusturmaTarihi DESC")
       List<OyuncuYorumlari> findByOyuncuIdOrderByOlusturmaTarihiDesc(Long oyuncuId);

       @Query("SELECT DISTINCT oy FROM OyuncuYorumlari oy " +
                     "LEFT JOIN FETCH oy.oyuncu " +
                     "WHERE oy.kullanici = :kullanici " +
                     "ORDER BY oy.olusturmaTarihi DESC")
       List<OyuncuYorumlari> findByKullaniciOrderByOlusturmaTarihiDesc(Kullanici kullanici);
}
