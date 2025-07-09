# Spring Boot Backend Project

Bu proje Clean Architecture ve SOLID prensipleri kullanılarak geliştirilmiş bir Spring Boot backend uygulamasıdır.

## Özellikler

- **Clean Architecture**: Katmanlı mimari yapısı
- **SOLID Prensipler**: Kod kalitesi ve sürdürülebilirlik
- **MVC Pattern**: Model-View-Controller yapısı
- **JWT Authentication**: Token tabanlı kimlik doğrulama
- **Spring Security**: Güvenlik yapılandırması
- **JPA/Hibernate**: Veritabanı işlemleri
- **H2 Database**: Geliştirme için in-memory veritabanı

## Paket Yapısı

```
src/main/kotlin/org/oguzhanozgokce/springbootproject/
├── controller/          # REST API endpoints
├── service/            # Business logic
├── repository/         # Data access layer
├── model/             # Entity sınıfları
├── dto/               # Data Transfer Objects
├── config/            # Configuration sınıfları
├── security/          # Security utilities
└── exception/         # Exception handling
```

## API Endpoints

### Authentication

- **POST** `/api/auth/register` - Kullanıcı kaydı
- **POST** `/api/auth/login` - Kullanıcı girişi

### Register Request

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

### Login Request

```json
{
  "username": "testuser",
  "password": "password123"
}
```

### Response Format

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "token": "jwt-token-here",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "firstName": "Test",
      "lastName": "User",
      "role": "USER"
    }
  }
}
```

## Çalıştırma

1. **Projeyi klonlayın**
2. **Bağımlılıkları yükleyin:**
   ```bash
   ./gradlew build
   ```

3. **Uygulamayı çalıştırın:**
   ```bash
   ./gradlew bootRun
   ```

4. **API testleri:**
   ```bash
   # Register
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"testuser","email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'
   
   # Login
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"testuser","password":"password123"}'
   ```

## Veritabanı

- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## Teknolojiler

- **Kotlin**
- **Spring Boot 3.5.3**
- **Spring Security**
- **Spring Data JPA**
- **JWT (JSON Web Token)**
- **H2 Database**
- **Gradle**

## Önemli Notlar

- Proje Clean Architecture prensipleri ile organize edilmiştir
- SOLID prensipler uygulanmıştır
- JWT token süresi 24 saat (86400000 ms) olarak ayarlanmıştır
- H2 veritabanı development için kullanılmıştır, production'da PostgreSQL kullanılabilir