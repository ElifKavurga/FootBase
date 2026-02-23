package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.Puanlama;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuanlamaRepository extends JpaRepository<Puanlama, Long> {

    List<Puanlama> findByMac(Mac mac);

    List<Puanlama> findByMacId(Long macId);

    Optional<Puanlama> findByKullaniciIdAndMacId(Long kullaniciId, Long macId);

    @Query("SELECT AVG(p.puan) FROM Puanlama p WHERE p.mac.id = :macId")
    Double findOrtalamaPuanByMacId(@Param("macId") Long macId);

    long countByMacId(Long macId);
}
