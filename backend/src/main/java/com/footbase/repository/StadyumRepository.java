package com.footbase.repository;

import com.footbase.entity.Stadyum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StadyumRepository extends JpaRepository<Stadyum, Long> {

    Optional<Stadyum> findByStadyumAdi(String stadyumAdi);

    List<Stadyum> findBySehir(String sehir);

    List<Stadyum> findByUlke(String ulke);

    boolean existsByStadyumAdi(String stadyumAdi);

    @Query("SELECT s FROM Stadyum s WHERE LOWER(s.stadyumAdi) LIKE LOWER(CONCAT('%', :stadyumAdi, '%'))")
    List<Stadyum> searchByStadyumAdi(String stadyumAdi);
}
