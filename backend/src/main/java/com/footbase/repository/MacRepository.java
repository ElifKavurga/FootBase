package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MacRepository extends JpaRepository<Mac, Long> {

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig")
       List<Mac> findAll();

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "WHERE m.id = :id")
       java.util.Optional<Mac> findById(Long id);

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "WHERE EXISTS (SELECT 1 FROM MacTakimlari mt2 WHERE mt2.mac = m AND mt2.takim = :takim AND mt2.evSahibi = true)")
       List<Mac> findByEvSahibiTakim(Takim takim);

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "WHERE EXISTS (SELECT 1 FROM MacTakimlari mt2 WHERE mt2.mac = m AND mt2.takim = :takim AND mt2.evSahibi = false)")
       List<Mac> findByDeplasmanTakim(Takim takim);

       // Durum kolonu veritabannda yok, bu metod kullanlamaz
       // List<Mac> findByDurum(String durum);

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "WHERE m.tarih BETWEEN :baslangicTarihi AND :bitisTarihi")
       List<Mac> findByTarihBetween(LocalDate baslangicTarihi, LocalDate bitisTarihi);

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "WHERE (m.tarih > :simdiTarih) OR (m.tarih = :simdiTarih AND m.saat > :simdiSaat) " +
                     "ORDER BY m.tarih ASC, m.saat ASC")
       List<Mac> findGelecekMaclar(@Param("simdiTarih") java.time.LocalDate simdiTarih,
                     @Param("simdiSaat") java.time.LocalTime simdiSaat);

       @Query("SELECT DISTINCT m FROM Mac m " +
                     "LEFT JOIN FETCH m.hakem " +
                     "LEFT JOIN FETCH m.macTakimlari mt " +
                     "LEFT JOIN FETCH mt.takim t " +
                     "LEFT JOIN FETCH t.stadyum " +
                     "LEFT JOIN FETCH t.lig " +
                     "WHERE (m.tarih < :simdiTarih) OR (m.tarih = :simdiTarih AND m.saat < :simdiSaat) " +
                     "ORDER BY m.tarih DESC, m.saat DESC")
       List<Mac> findGecmisMaclar(@Param("simdiTarih") java.time.LocalDate simdiTarih,
                     @Param("simdiSaat") java.time.LocalTime simdiSaat);
}
