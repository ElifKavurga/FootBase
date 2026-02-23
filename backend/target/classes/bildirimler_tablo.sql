-- Bildirimler Tablosu Oluşturma Script'i
-- Bu tablo Observer Pattern ile oluşturulan bildirimleri saklar

CREATE TABLE IF NOT EXISTS bildirimler (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,
    
    -- Alıcı kullanıcı (Foreign Key)
    alici_kullanici_id BIGINT NOT NULL,
    CONSTRAINT fk_bildirim_alici FOREIGN KEY (alici_kullanici_id) 
        REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    
    -- Gönderici kullanıcı (Opsiyonel - sistem bildirimleri için NULL olabilir)
    gonderici_kullanici_id BIGINT,
    CONSTRAINT fk_bildirim_gonderici FOREIGN KEY (gonderici_kullanici_id) 
        REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    
    -- Bildirim tipi
    bildirim_tipi VARCHAR(50) NOT NULL,
    
    -- Bildirim başlığı ve içeriği
    baslik VARCHAR(255) NOT NULL,
    icerik TEXT NOT NULL,
    
    -- İlgili maç (Opsiyonel)
    mac_id BIGINT,
    CONSTRAINT fk_bildirim_mac FOREIGN KEY (mac_id) 
        REFERENCES maclar(id) ON DELETE CASCADE,
    
    -- İlgili oyuncu (Opsiyonel)
    oyuncu_id BIGINT,
    CONSTRAINT fk_bildirim_oyuncu FOREIGN KEY (oyuncu_id) 
        REFERENCES oyuncular(oyuncu_id) ON DELETE CASCADE,
    
    -- Okundu durumu
    okundu BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Zaman bilgileri
    olusturma_zamani TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    okunma_zamani TIMESTAMP,
    
    -- Hedef URL (bildirime tıklandığında yönlendirilecek sayfa)
    hedef_url VARCHAR(500)
);

-- İndeksler (Performans optimizasyonu)
CREATE INDEX idx_bildirim_alici ON bildirimler(alici_kullanici_id);
CREATE INDEX idx_bildirim_okundu ON bildirimler(alici_kullanici_id, okundu);
CREATE INDEX idx_bildirim_tarih ON bildirimler(olusturma_zamani DESC);
CREATE INDEX idx_bildirim_tip ON bildirimler(bildirim_tipi);

-- Yorumlar
COMMENT ON TABLE bildirimler IS 'Observer Pattern ile oluşturulan kullanıcı bildirimleri';
COMMENT ON COLUMN bildirimler.bildirim_tipi IS 'MAC_EKLENDI, MAC_ONAYLANDI, MAC_REDDEDILDI, YENI_YORUM, GOL_ATILDI vb.';
COMMENT ON COLUMN bildirimler.okundu IS 'Kullanıcı bildirimi görüntüledi mi?';
COMMENT ON COLUMN bildirimler.hedef_url IS 'Bildirime tıklandığında yönlendirilecek sayfa (örn: /app/matches/123)';

