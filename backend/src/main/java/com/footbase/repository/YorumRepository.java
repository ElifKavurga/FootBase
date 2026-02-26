package com.footbase.repository;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.entity.Yorum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YorumRepository extends JpaRepository<Yorum, Long> {

       List<Yorum> findByMac(Mac mac);

       List<Yorum> findByMacId(Long macId);

       @Query("SELECT DISTINCT y FROM Yorum y " +
                     "LEFT JOIN FETCH y.kullanici " +
                     "WHERE y.mac = :mac " +
                     "ORDER BY y.yorumTarihi DESC")
       List<Yorum> findByMacOrderByYorumTarihiDesc(Mac mac);

       @Query("SELECT DISTINCT y FROM Yorum y " +
                     "LEFT JOIN FETCH y.kullanici " +
                     "LEFT JOIN FETCH y.mac " +
                     "ORDER BY y.yorumTarihi DESC")
       List<Yorum> findTopByOrderByYorumTarihiDesc(Pageable pageable);

       @Query("SELECT DISTINCT y FROM Yorum y " +
                     "LEFT JOIN FETCH y.mac m " +
                     "WHERE y.kullanici = :kullanici " +
                     "ORDER BY y.yorumTarihi DESC")
       List<Yorum> findByKullaniciOrderByYorumTarihiDesc(Kullanici kullanici);

       @Query("SELECT y FROM Yorum y LEFT JOIN FETCH y.begenenKullanicilar WHERE y.id = :yorumId")
       java.util.Optional<Yorum> findByIdWithLikes(Long yorumId);
}
