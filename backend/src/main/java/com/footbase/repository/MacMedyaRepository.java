package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.MacMedya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MacMedyaRepository extends JpaRepository<MacMedya, Long> {

    List<MacMedya> findByMac(Mac mac);

    List<MacMedya> findByMacId(Long macId);

    @Query("SELECT DISTINCT mm FROM MacMedya mm " +
            "LEFT JOIN FETCH mm.mac " +
            "LEFT JOIN FETCH mm.editor " +
            "WHERE mm.mac.id = :macId " +
            "ORDER BY mm.id DESC")
    List<MacMedya> findByMacIdWithDetails(Long macId);
}
