# FootBase Backend

FootBase - Futbol Maç Yorumlama ve Oyuncu Puanlama Platformu Backend Uygulaması

## Teknolojiler

- **Spring Boot 3.2.0** - Java framework
- **Spring Security** - Güvenlik ve kimlik doğrulama
- **JWT (JSON Web Token)** - Token tabanlı kimlik doğrulama
- **Spring Data JPA** - Veritabanı işlemleri
- **MySQL** - Veritabanı
- **Maven** - Bağımlılık yönetimi

## Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- MySQL 8.0 veya üzeri

## Kurulum

1. MySQL veritabanında `footbase` adında bir veritabanı oluşturun:
```sql
CREATE DATABASE footbase;
```

2. `application.properties` dosyasındaki veritabanı bağlantı bilgilerini düzenleyin:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Projeyi derleyin:
```bash
# Maven Wrapper kullanarak (önerilen - Maven yüklemenize gerek yok)
.\mvnw.cmd clean install

# Veya Maven yüklüyse
mvn clean install
```

4. Uygulamayı çalıştırın:
```bash
# Maven Wrapper kullanarak (önerilen)
.\mvnw.cmd spring-boot:run

# Veya Maven yüklüyse
mvn spring-boot:run
```

**Not:** İlk çalıştırmada Maven Wrapper otomatik olarak Maven'ı indirecektir.

Backend uygulaması `http://localhost:8080` adresinde çalışacaktır.

## API Endpoint'leri

### Kimlik Doğrulama
- `POST /api/auth/login` - Kullanıcı girişi
- `POST /api/auth/register` - Kullanıcı kaydı
- `POST /api/auth/reset-password` - Şifre sıfırlama

### Maçlar
- `GET /api/matches` - Tüm maçları getir
- `GET /api/matches/{id}` - Maç detayını getir
- `GET /api/matches/{id}/comments` - Maça ait yorumları getir
- `POST /api/matches/{id}/comments` - Maça yorum ekle
- `POST /api/matches/{id}/predictions` - Maç için tahmin yap

### Yorumlar
- `PUT /api/matches/comments/{id}` - Yorumu güncelle
- `DELETE /api/matches/comments/{id}` - Yorumu sil
- `POST /api/matches/comments/{id}/like` - Yorumu beğen

### Oyuncular
- `GET /api/players` - Tüm oyuncuları getir
- `GET /api/players/{id}` - Oyuncu detayını getir
- `GET /api/players/{id}/ratings` - Oyuncuya ait puanlamaları getir
- `POST /api/players/{id}/ratings` - Oyuncuya puanlama yap

### Takımlar
- `GET /api/teams` - Tüm takımları getir
- `GET /api/teams/{id}` - Takım detayını getir

### Kullanıcılar
- `GET /api/users/me` - Mevcut kullanıcı bilgilerini getir
- `GET /api/users/{id}` - Kullanıcı profilini getir
- `POST /api/users/{id}/follow` - Kullanıcıyı takip et
- `DELETE /api/users/{id}/follow` - Takibi bırak

### Feed
- `GET /api/feed` - Kullanıcı feed'ini getir

### Admin
- `POST /api/admin/matches` - Yeni maç oluştur
- `PUT /api/admin/matches/{id}` - Maç bilgilerini güncelle
- `POST /api/admin/teams` - Yeni takım oluştur
- `PUT /api/admin/teams/{id}` - Takım bilgilerini güncelle
- `POST /api/admin/players` - Yeni oyuncu oluştur
- `PUT /api/admin/players/{id}` - Oyuncu bilgilerini güncelle

## Veritabanı Yapısı

Uygulama aşağıdaki tabloları otomatik olarak oluşturur:

- `kullanicilar` - Kullanıcı bilgileri
- `takimlar` - Takım bilgileri
- `oyuncular` - Oyuncu bilgileri
- `maclar` - Maç bilgileri
- `yorumlar` - Maç yorumları
- `puanlamalar` - Oyuncu puanlamaları
- `tahminler` - Maç tahminleri
- `takip_edilenler` - Kullanıcı takip ilişkileri
- `yorum_begenileri` - Yorum beğeni ilişkileri

## Güvenlik

- Tüm endpoint'ler (auth ve home hariç) JWT token gerektirir
- Şifreler BCrypt ile hash'lenir
- CORS yapılandırması frontend için yapılmıştır (http://localhost:3000)

## Notlar

- Veritabanı tabloları `spring.jpa.hibernate.ddl-auto=update` ayarı ile otomatik oluşturulur
- JWT token geçerlilik süresi varsayılan olarak 24 saattir
- Tüm sınıf, değişken ve açıklamalar Türkçe olarak yazılmıştır

