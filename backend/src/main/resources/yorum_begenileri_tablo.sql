-- Mac yorum begenileri tablosu
CREATE TABLE IF NOT EXISTS yorum_begenileri (
    yorum_id BIGINT NOT NULL,
    kullanici_id BIGINT NOT NULL,
    PRIMARY KEY (yorum_id, kullanici_id),
    CONSTRAINT fk_yorum_begenileri_yorum FOREIGN KEY (yorum_id)
        REFERENCES yorumlar(yorum_id) ON DELETE CASCADE,
    CONSTRAINT fk_yorum_begenileri_kullanici FOREIGN KEY (kullanici_id)
        REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_yorum_begenileri_kullanici
    ON yorum_begenileri(kullanici_id);
