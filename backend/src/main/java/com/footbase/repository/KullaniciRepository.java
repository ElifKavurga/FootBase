package com.footbase.repository;

import com.footbase.entity.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {

    Optional<Kullanici> findByEmail(String email);

    Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi);

    Optional<Kullanici> findByEmailOrKullaniciAdi(String email, String kullaniciAdi);

    boolean existsByEmail(String email);

    boolean existsByKullaniciAdi(String kullaniciAdi);
}
