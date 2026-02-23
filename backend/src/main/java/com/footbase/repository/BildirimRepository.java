package com.footbase.repository;

import com.footbase.entity.Bildirim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BildirimRepository extends JpaRepository<Bildirim, Long> {

    List<Bildirim> findByAliciKullaniciIdOrderByOlusturmaZamaniDesc(Long aliciKullaniciId);

    List<Bildirim> findByAliciKullaniciIdAndOkunduOrderByOlusturmaZamaniDesc(
            Long aliciKullaniciId, Boolean okundu);

    Long countByAliciKullaniciIdAndOkundu(Long aliciKullaniciId, Boolean okundu);

    List<Bildirim> findByMacId(Long macId);

    List<Bildirim> findByAliciKullaniciIdAndBildirimTipiOrderByOlusturmaZamaniDesc(
            Long aliciKullaniciId, String bildirimTipi);

    @Modifying
    @Transactional
    @Query("UPDATE Bildirim b SET b.okundu = true, b.okunmaZamani = CURRENT_TIMESTAMP WHERE b.id = :bildirimId")
    int okunduOlarakIsaretle(@Param("bildirimId") Long bildirimId);

    @Modifying
    @Transactional
    @Query("UPDATE Bildirim b SET b.okundu = true, b.okunmaZamani = CURRENT_TIMESTAMP WHERE b.aliciKullanici.id = :aliciKullaniciId AND b.okundu = false")
    int tumunuOkunduOlarakIsaretle(@Param("aliciKullaniciId") Long aliciKullaniciId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Bildirim b WHERE b.aliciKullanici.id = :aliciKullaniciId")
    void kullaniciBildirimleriSil(@Param("aliciKullaniciId") Long aliciKullaniciId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM bildirimler WHERE okundu = true AND olusturma_zamani < NOW() - INTERVAL ':gunSayisi days'", nativeQuery = true)
    int eskiBildirimleriSil(@Param("gunSayisi") int gunSayisi);

    @Query(value = "SELECT * FROM bildirimler WHERE alici_kullanici_id = :aliciKullaniciId ORDER BY olusturma_zamani DESC LIMIT :limit", nativeQuery = true)
    List<Bildirim> sonBildirimleriGetir(@Param("aliciKullaniciId") Long aliciKullaniciId,
            @Param("limit") int limit);
}
