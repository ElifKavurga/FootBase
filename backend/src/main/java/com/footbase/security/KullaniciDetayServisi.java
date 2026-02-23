package com.footbase.security;

import com.footbase.entity.Kullanici;
import com.footbase.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class KullaniciDetayServisi implements UserDetailsService {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        return new User(
                kullanici.getEmail(),
                kullanici.getSifre(),
                new ArrayList<>());
    }
}
