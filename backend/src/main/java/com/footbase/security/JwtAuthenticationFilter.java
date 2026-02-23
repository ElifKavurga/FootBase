package com.footbase.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService kullaniciDetayServisi;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Public endpoint'ler için JWT kontrolü yapma (tüm API endpoint'leri geçici
        // olarak public)
        String path = request.getRequestURI();
        if (path.startsWith("/api/")) {
            // Token varsa doğrula, yoksa devam et
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Authorization header'ından token'ı al
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // Bearer token formatını kontrol et
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.getKullaniciEmailFromToken(jwt);
            } catch (Exception e) {
                // Token geçersizse devam et
            }
        }

        // Token geçerliyse ve kullanıcı henüz authenticate edilmemişse
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails kullaniciDetaylari = this.kullaniciDetayServisi.loadUserByUsername(email);

            // Token'ı doğrula
            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        kullaniciDetaylari, null, kullaniciDetaylari.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
