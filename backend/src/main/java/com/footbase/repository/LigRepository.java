package com.footbase.repository;

import com.footbase.entity.Lig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LigRepository extends JpaRepository<Lig, Long> {

    Optional<Lig> findByLigAdi(String ligAdi);

    List<Lig> findByUlke(String ulke);

    boolean existsByLigAdi(String ligAdi);

    @Query("SELECT l FROM Lig l WHERE LOWER(l.ligAdi) LIKE LOWER(CONCAT('%', :ligAdi, '%'))")
    List<Lig> searchByLigAdi(String ligAdi);
}
