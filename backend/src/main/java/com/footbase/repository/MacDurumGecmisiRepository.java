package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.MacDurumGecmisi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MacDurumGecmisiRepository extends JpaRepository<MacDurumGecmisi, Long> {

       List<MacDurumGecmisi> findByMac(Mac mac);

       List<MacDurumGecmisi> findByMacId(Long macId);

       @Query("SELECT DISTINCT mdg FROM MacDurumGecmisi mdg " +
                     "LEFT JOIN FETCH mdg.mac " +
                     "LEFT JOIN FETCH mdg.islemYapanKullanici " +
                     "WHERE mdg.mac.id = :macId " +
                     "ORDER BY mdg.islemTarihi DESC")
       List<MacDurumGecmisi> findByMacIdWithDetails(Long macId);

       @Query("SELECT mdg FROM MacDurumGecmisi mdg " +
                     "WHERE mdg.mac.id = :macId " +
                     "AND mdg.islemTarihi = (SELECT MAX(mdg2.islemTarihi) FROM MacDurumGecmisi mdg2 WHERE mdg2.mac.id = :macId)")
       java.util.Optional<MacDurumGecmisi> findLatestByMacId(@Param("macId") Long macId);

       @Query(value = "SELECT DISTINCT mdg.mac_id FROM mac_durum_gecmisi mdg " +
                     "WHERE CAST(mdg.durum AS TEXT) = ?1 " +
                     "AND mdg.islem_tarihi = (SELECT MAX(mdg2.islem_tarihi) FROM mac_durum_gecmisi mdg2 WHERE mdg2.mac_id = mdg.mac_id)", nativeQuery = true)
       List<Long> findMacIdsByLatestDurum(String durum);

       @Query(value = "SELECT DISTINCT mdg.mac_id FROM mac_durum_gecmisi mdg " +
                     "WHERE CAST(mdg.durum AS TEXT) = 'ONAY_BEKLIYOR' " +
                     "AND mdg.islem_tarihi = (SELECT MAX(mdg2.islem_tarihi) FROM mac_durum_gecmisi mdg2 WHERE mdg2.mac_id = mdg.mac_id)", nativeQuery = true)
       List<Long> findAllPendingMacIds();

       @Query(value = "SELECT mdg.islem_yapan_kullanici_id FROM mac_durum_gecmisi mdg " +
                     "WHERE mdg.mac_id = ?1 " +
                     "AND mdg.islem_tarihi = (SELECT MIN(mdg2.islem_tarihi) FROM mac_durum_gecmisi mdg2 WHERE mdg2.mac_id = ?1) "
                     +
                     "LIMIT 1", nativeQuery = true)
       java.util.Optional<Long> findFirstRecordEditorIdByMacId(Long macId);

       @Modifying
       @Transactional
       @Query(value = "INSERT INTO mac_durum_gecmisi (mac_id, durum, islem_tarihi, islem_yapan_kullanici_id) " +
                     "VALUES (?1, CAST(?2 AS yayim_durumu_enum), ?3, ?4)", nativeQuery = true)
       void saveMacDurumGecmisiNative(Long macId, String durum, java.time.LocalDateTime islemTarihi,
                     Long islemYapanKullaniciId);
}
