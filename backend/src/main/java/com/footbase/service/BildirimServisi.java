package com.footbase.service;

import com.footbase.entity.Bildirim;
import com.footbase.repository.BildirimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BildirimServisi {

    @Autowired
    private BildirimRepository bildirimRepository;

    public List<Bildirim> kullaniciBildirimleriGetir(Long kullaniciId) {
        return bildirimRepository.findByAliciKullaniciIdOrderByOlusturmaZamaniDesc(kullaniciId);
    }

    public List<Bildirim> okunmamisBildirimleriGetir(Long kullaniciId) {
        return bildirimRepository.findByAliciKullaniciIdAndOkunduOrderByOlusturmaZamaniDesc(
                kullaniciId, false);
    }

    public Long okunmamisBildirimSayisi(Long kullaniciId) {
        return bildirimRepository.countByAliciKullaniciIdAndOkundu(kullaniciId, false);
    }

    public List<Bildirim> sonBildirimleriGetir(Long kullaniciId, int limit) {
        return bildirimRepository.sonBildirimleriGetir(kullaniciId, limit);
    }

    @Transactional
    public void bildirimOkunduIsaretle(Long bildirimId) {
        bildirimRepository.okunduOlarakIsaretle(bildirimId);
    }

    @Transactional
    public int tumBildirimleriOkunduIsaretle(Long kullaniciId) {
        return bildirimRepository.tumunuOkunduOlarakIsaretle(kullaniciId);
    }

    @Transactional
    public void bildirimSil(Long bildirimId) {
        bildirimRepository.deleteById(bildirimId);
    }
}
