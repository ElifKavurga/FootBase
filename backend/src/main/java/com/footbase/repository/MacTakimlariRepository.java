package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.MacTakimlari;
import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MacTakimlariRepository extends JpaRepository<MacTakimlari, Long> {

    List<MacTakimlari> findByMac(Mac mac);

    List<MacTakimlari> findByMacId(Long macId);

    List<MacTakimlari> findByTakim(Takim takim);

    List<MacTakimlari> findByTakimId(Long takimId);

    Optional<MacTakimlari> findByMacIdAndEvSahibiTrue(Long macId);

    Optional<MacTakimlari> findByMacIdAndEvSahibiFalse(Long macId);

    @Query("SELECT DISTINCT mt FROM MacTakimlari mt " +
            "LEFT JOIN FETCH mt.mac " +
            "LEFT JOIN FETCH mt.takim " +
            "WHERE mt.mac.id = :macId")
    List<MacTakimlari> findByMacIdWithDetails(Long macId);
}
