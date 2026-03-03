# Day 3: Spring Configuration Options
### Java Config, Annotations, dan Externalized Configuration

> **Prerequisite:** Day 1 (IoC/DI) dan Day 2 (Lifecycle & Scope).
> **Tujuan hari ini:** Menguasai semua mode konfigurasi Spring, lalu merefaktor proyek Day 1 ke Java Config murni, dan belajar externalized configuration dengan properties files.

---

## Daftar Isi

**BAGIAN 1 — TEORI (30–40 menit)**
1. Recap: 3 Mode Konfigurasi
2. Java-Based Configuration — Mendalam
3. Annotation-Based Configuration — Mendalam
4. Externalized Configuration: Mengapa Penting?
5. Cara Spring Membaca Properties

**BAGIAN 2 — CODING (70–80 menit)**
6. Setup Proyek Day 3
7. Refactor Day 1 ke Java Config Murni
8. Multiple Configuration Classes
9. Externalized Configuration dengan .properties
10. Profile-Based Configuration (dev vs prod)
11. @Value vs @ConfigurationProperties

**BAGIAN 3 — LATIHAN**
12. Latihan: Property Injection dengan Properties File
13. Praktik: Multiple Config Classes
14. Ringkasan & Checklist Day 3

---

# BAGIAN 1 — TEORI

## 1. Recap: 3 Mode Konfigurasi

Sebelum Day 3, kamu sudah tahu 3 cara konfigurasi Spring:

```
XML Configuration
  -> <bean id="..." class="...">
  -> Cara lama, masih valid, banyak di legacy code

Annotation Configuration
  -> @Component, @Service, @Repository, @Controller
  -> @Autowired, @Value
  -> Cara modern, ringkas

Java-Based Configuration
  -> @Configuration + @Bean methods
  -> Full Java, type-safe, IDE support penuh
  -> Cocok untuk 3rd-party library
```

Hari ini kita akan:
1. Mendalami Java Config dan Annotation config
2. Mempelajari cara menggabungkan keduanya
3. Belajar externalized configuration (properties file, env variables)
4. Merefaktor proyek Day 1 ke Java Config penuh

---

## 2. Java-Based Configuration — Mendalam

### @Configuration Deep Dive

```java
@Configuration
public class AppConfig {

    // @Bean method = mendefinisikan Bean
    // Nama Bean = nama method (myService)
    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

    // Bean bisa punya nama eksplisit
    @Bean(name = "emailSvc")
    public EmailService emailService() {
        return new SmtpEmailService();
    }

    // Bean dengan dependency ke Bean lain
    @Bean
    public OrderService orderService() {
        // Memanggil myService() di sini TIDAK membuat instance baru
        // Spring intercept panggilan ini dan kembalikan Bean singleton yang sudah ada
        return new OrderService(myService(), emailService());
    }
}
```

> **Fakta Penting:** Spring men-subclass `@Configuration` class menggunakan CGLIB.
> Artinya setiap panggilan ke `myService()` di dalam `@Configuration` class
> diintersep oleh Spring — jika Bean sudah ada (singleton), dikembalikan yang lama,
> bukan dibuat baru. Inilah kenapa `@Configuration` berbeda dari class biasa!

### @Configuration vs @Component untuk @Bean

```java
// Kasus 1: @Configuration (CGLIB proxy)
@Configuration
public class Config1 {

    @Bean
    public A beanA() { return new A(); }

    @Bean
    public B beanB() {
        return new B(beanA()); // panggil beanA() -> Spring return singleton yang SAMA
    }
}

// Kasus 2: @Component (BUKAN CGLIB proxy — "lite mode")
@Component
public class Config2 {

    @Bean
    public A beanA() { return new A(); }

    @Bean
    public B beanB() {
        return new B(beanA()); // panggil beanA() -> new A() LAGI! Bukan singleton!
    }
}
```

> ATURAN: Kalau @Bean methods saling memanggil satu sama lain -> WAJIB pakai @Configuration.
> Kalau @Bean methods independen -> boleh pakai @Component (lite mode).

### Dependency Injection dalam @Bean Methods

Ada dua cara inject dependency antar @Bean:

```java
@Configuration
public class AppConfig {

    // Cara 1: Panggil @Bean method lain (hanya bisa di @Configuration)
    @Bean
    public OrderService orderService() {
        return new OrderService(paymentService()); // memanggil method lain
    }

    // Cara 2: Method parameter — Spring inject otomatis (DIREKOMENDASIKAN)
    @Bean
    public NotificationService notificationService(EmailService emailService,
                                                    SmsService smsService) {
        // Spring otomatis inject emailService dan smsService dari container
        return new NotificationService(emailService, smsService);
    }

    @Bean
    public PaymentService paymentService() { return new PaymentService(); }

    @Bean
    public EmailService emailService() { return new SmtpEmailService(); }

    @Bean
    public SmsService smsService() { return new TwilioSmsService(); }
}
```

### @Import — Memisahkan Config ke File Berbeda

```java
@Configuration
@Import({DatabaseConfig.class, SecurityConfig.class, ServiceConfig.class})
public class AppConfig {
    // AppConfig adalah "master config"
    // Dia mengimport config dari class lain
}

@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource() { ... }

    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) { ... }
}

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
```

---

## 3. Annotation-Based Configuration — Mendalam

### @Component dan Stereotype Annotations

```java
// Semua ini adalah @Component dengan label berbeda:

@Component    // Generic — tidak spesifik
@Service      // Business logic layer
@Repository   // Data access layer (+ exception translation)
@Controller   // Web/presentation layer (Spring MVC)
@RestController // @Controller + @ResponseBody (Spring MVC REST)
```

Perbedaan teknis yang NYATA antara @Repository vs yang lain:
Spring otomatis translasikan database exceptions (SQLException, dll) menjadi
Spring's DataAccessException hierarchy — hanya untuk @Repository.

### @ComponentScan Detail

```java
@Configuration
@ComponentScan(
    basePackages = {"com.belajar.service", "com.belajar.repository"},

    // Sertakan hanya class dengan anotasi tertentu
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = MyCustomAnnotation.class
    ),

    // Exclude class tertentu dari scanning
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {TestConfig.class, MockService.class}
    )
)
public class AppConfig {}
```

### @Qualifier — Pilih Bean Spesifik

```java
public interface PaymentGateway {
    void charge(double amount);
}

@Component("stripeGateway")
public class StripePaymentGateway implements PaymentGateway {
    public void charge(double amount) { System.out.println("Stripe: " + amount); }
}

@Component("paypalGateway")
public class PaypalPaymentGateway implements PaymentGateway {
    public void charge(double amount) { System.out.println("PayPal: " + amount); }
}

// Jika ada 2+ implementasi, Spring bingung mau inject yang mana
// Gunakan @Qualifier untuk spesifikasi
@Service
public class OrderService {

    private final PaymentGateway paymentGateway;

    public OrderService(@Qualifier("stripeGateway") PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
}
```

### @Primary vs @Qualifier

```java
@Component
@Primary  // Gunakan ini sebagai default jika tidak ada @Qualifier
public class StripePaymentGateway implements PaymentGateway { ... }

@Component
public class PaypalPaymentGateway implements PaymentGateway { ... }

// Ini akan inject StripePaymentGateway (karena @Primary)
@Service
public class OrderService {
    public OrderService(PaymentGateway paymentGateway) { ... }
}

// Ini akan inject PaypalPaymentGateway (karena @Qualifier eksplisit)
@Service
public class RefundService {
    public RefundService(@Qualifier("paypalPaymentGateway") PaymentGateway gw) { ... }
}
```

---

## 4. Externalized Configuration: Mengapa Penting?

### Masalah Configuration Hardcoded

```java
// BURUK: Konfigurasi hardcoded di kode
@Service
public class EmailService {
    private String host = "smtp.gmail.com";      // Hardcoded!
    private int port = 587;                        // Hardcoded!
    private String username = "myapp@gmail.com";   // Hardcoded!
    private String password = "super-secret-pass"; // SANGAT BERBAHAYA!
}
```

Masalah:
- Password di-commit ke Git = security disaster
- Ganti host untuk production = harus compile ulang
- Konfigurasi dev berbeda dengan production = tidak bisa pakai kode yang sama

### Solusi: Externalized Configuration

```
kode Java          <- tidak berubah antar environment
     +
properties file    <- berbeda per environment (dev/staging/prod)
     |
     v
aplikasi berjalan dengan konfigurasi yang tepat
```

### 12-Factor App Principle

Externalized configuration adalah salah satu prinsip "12-Factor App" yang populer:
> "Store config in the environment" — konfigurasi yang berbeda antar deployment
> harus disimpan di luar kode, bukan hardcoded.

---

## 5. Cara Spring Membaca Properties

### Sumber Konfigurasi (dari prioritas terendah ke tertinggi)

```
1. Default properties (@PropertySource)
2. application.properties di classpath
3. application.properties di luar JAR
4. System properties (-Dkey=value saat java -jar)
5. Environment variables (OS level)
6. Command line arguments (--key=value)
```

Artinya: **environment variable selalu override properties file**. Ini berguna untuk override konfigurasi di production tanpa ubah kode.

---

# BAGIAN 2 — CODING

## 6. Setup Proyek Day 3

```
spring-day3/
+-- pom.xml
+-- src/
    +-- main/
    |   +-- java/com/belajar/day3/
    |   |   +-- config/
    |   |   |   +-- AppConfig.java           <- master config
    |   |   |   +-- DatabaseConfig.java      <- db beans
    |   |   |   +-- ServiceConfig.java       <- service beans
    |   |   |   +-- InfrastructureConfig.java <- email, sms beans
    |   |   +-- service/
    |   |   |   +-- GreetingService.java
    |   |   |   +-- GreetingServiceImpl.java
    |   |   |   +-- NotificationService.java
    |   |   |   +-- EmailNotificationService.java
    |   |   +-- repository/
    |   |   |   +-- UserRepository.java
    |   |   +-- controller/
    |   |   |   +-- HelloController.java
    |   |   +-- properties/
    |   |   |   +-- AppProperties.java       <- @ConfigurationProperties
    |   |   +-- Main.java
    |   +-- resources/
    |       +-- application.properties       <- default
    |       +-- application-dev.properties   <- override untuk dev
    |       +-- application-prod.properties  <- override untuk prod
    +-- test/
        +-- java/com/belajar/day3/
            +-- config/
                +-- TestConfig.java          <- override untuk test
```

**pom.xml:**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.4</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
</dependencies>
```

---

## 7. Refactor Day 1 ke Java Config Murni

Ini adalah latihan inti Day 3. Kita ambil proyek Day 1 dan refactor ke Java Config.

### Sebelum (Annotation-based dari Day 1)

```java
// Day 1 style: anotasi di mana-mana
@Service
public class GreetingServiceImpl implements GreetingService { ... }

@Component
public class HelloController { ... }

@Configuration
@ComponentScan("com.belajar")
public class AppConfig { }
```

### Sesudah (Java Config murni)

**GreetingService.java (interface — tidak berubah)**
```java
package com.belajar.day3.service;

public interface GreetingService {
    String greet(String name);
}
```

**GreetingServiceImpl.java — TANPA anotasi Spring**
```java
package com.belajar.day3.service;

// Pure Java — tidak ada anotasi Spring sama sekali!
// Class ini bisa dipakai di luar Spring tanpa masalah
public class GreetingServiceImpl implements GreetingService {

    private final String greetingWord;

    public GreetingServiceImpl(String greetingWord) {
        this.greetingWord = greetingWord;
    }

    @Override
    public String greet(String name) {
        return greetingWord + ", " + name + "!";
    }
}
```

**HelloController.java — TANPA anotasi Spring**
```java
package com.belajar.day3.controller;

import com.belajar.day3.service.GreetingService;

// Pure Java
public class HelloController {

    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public void handleRequest(String name) {
        String result = greetingService.greet(name);
        System.out.println("[HelloController] " + result);
    }
}
```

**ServiceConfig.java — mendefinisikan service beans**
```java
package com.belajar.day3.config;

import com.belajar.day3.service.GreetingService;
import com.belajar.day3.service.GreetingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public GreetingService greetingService() {
        // Kita bisa inject nilai dari properties di sini (lihat Bagian 9)
        return new GreetingServiceImpl("Halo");
    }
}
```

**ControllerConfig.java — mendefinisikan controller beans**
```java
package com.belajar.day3.config;

import com.belajar.day3.controller.HelloController;
import com.belajar.day3.service.GreetingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    // Spring inject greetingService otomatis via parameter
    @Bean
    public HelloController helloController(GreetingService greetingService) {
        return new HelloController(greetingService);
    }
}
```

**AppConfig.java — master config yang menggabungkan semuanya**
```java
package com.belajar.day3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({ServiceConfig.class, ControllerConfig.class, DatabaseConfig.class})
@PropertySource("classpath:application.properties")
public class AppConfig {
    // Kosong — hanya mengumpulkan semua config
    // Satu titik masuk untuk seluruh konfigurasi aplikasi
}
```

---

## 8. Multiple Configuration Classes

Strategi pemisahan config yang umum digunakan di proyek nyata:

### DatabaseConfig.java

```java
package com.belajar.day3.config;

import com.belajar.day3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    // @Value membaca dari properties file
    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Bean
    public UserRepository userRepository() {
        System.out.println("[DatabaseConfig] Membuat UserRepository");
        System.out.println("[DatabaseConfig] Connecting to: " + dbUrl);
        System.out.println("[DatabaseConfig] Username: " + dbUsername);
        // Dalam proyek nyata: setup DataSource, JdbcTemplate, dll
        return new UserRepository(dbUrl, dbUsername, dbPassword);
    }
}
```

### InfrastructureConfig.java

```java
package com.belajar.day3.config;

import com.belajar.day3.service.EmailNotificationService;
import com.belajar.day3.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private int emailPort;

    @Value("${email.username}")
    private String emailUsername;

    @Bean
    public NotificationService notificationService() {
        System.out.println("[InfrastructureConfig] Membuat EmailNotificationService");
        System.out.println("[InfrastructureConfig] SMTP: " + emailHost + ":" + emailPort);
        return new EmailNotificationService(emailHost, emailPort, emailUsername);
    }
}
```

---

## 9. Externalized Configuration dengan .properties

### application.properties (default, semua environment)

```properties
# src/main/resources/application.properties

# App info
app.name=Spring Hello World
app.version=1.0.0
app.description=Belajar Spring Core tanpa Boot

# Database
db.url=jdbc:h2:mem:testdb
db.username=sa
db.password=

# Email
email.host=localhost
email.port=25
email.username=noreply@local

# Greeting
greeting.word=Halo
greeting.farewell=Sampai jumpa
```

### application-dev.properties (override untuk dev)

```properties
# src/main/resources/application-dev.properties
# File ini OVERRIDE application.properties untuk profile 'dev'

# Database dev (in-memory)
db.url=jdbc:h2:mem:devdb
db.username=dev_user
db.password=dev_pass

# Email dev (mailhog lokal, tidak kirim email sungguhan)
email.host=localhost
email.port=1025
email.username=dev@localhost

# Greeting lebih informal untuk dev
greeting.word=Hei (DEV)
```

### application-prod.properties (override untuk prod)

```properties
# src/main/resources/application-prod.properties
# JANGAN commit file ini dengan password asli ke Git!
# Di production, gunakan environment variables atau secrets manager

db.url=jdbc:postgresql://prod-db.company.com:5432/appdb
db.username=app_user
db.password=${DB_PASSWORD}     <- ambil dari environment variable DB_PASSWORD

email.host=smtp.sendgrid.net
email.port=587
email.username=apikey

greeting.word=Selamat datang
```

### Cara Membaca @Value

```java
@Configuration
public class ServiceConfig {

    // Baca string langsung
    @Value("${greeting.word}")
    private String greetingWord;

    // Dengan default value (jika key tidak ditemukan)
    @Value("${greeting.farewell:Bye}")
    private String farewellWord;

    // Baca integer
    @Value("${email.port}")
    private int emailPort;

    // Baca boolean
    @Value("${feature.darkMode:false}")
    private boolean darkModeEnabled;

    // Expression SpEL (Spring Expression Language)
    @Value("#{T(java.lang.Math).PI}")
    private double pi;

    // Ambil dari system properties
    @Value("#{systemProperties['user.home']}")
    private String userHome;

    @Bean
    public GreetingService greetingService() {
        System.out.println("[ServiceConfig] greeting.word = " + greetingWord);
        return new GreetingServiceImpl(greetingWord);
    }
}
```

---

## 10. Profile-Based Configuration

Spring Profiles memungkinkan kamu mengaktifkan Bean atau konfigurasi berbeda tergantung environment.

### Mendefinisikan Bean Spesifik Profile

```java
package com.belajar.day3.service;

public interface DataSourceProvider {
    String getConnectionString();
}

// Hanya aktif jika profile 'dev' atau 'test' aktif
@Component
@Profile({"dev", "test"})
public class InMemoryDataSourceProvider implements DataSourceProvider {

    @Override
    public String getConnectionString() {
        return "jdbc:h2:mem:testdb";
    }
}

// Hanya aktif jika profile 'prod' aktif
@Component
@Profile("prod")
public class ProductionDataSourceProvider implements DataSourceProvider {

    @Value("${db.url}")
    private String dbUrl;

    @Override
    public String getConnectionString() {
        return dbUrl;
    }
}
```

### Mengaktifkan Profile via Kode

```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.getEnvironment().setActiveProfiles("dev"); // Aktifkan profile 'dev'
ctx.register(AppConfig.class);
ctx.refresh();
```

### Mengaktifkan Profile via System Property

```bash
# Saat menjalankan JAR
java -Dspring.profiles.active=prod -jar myapp.jar

# Atau via environment variable
export SPRING_PROFILES_ACTIVE=prod
java -jar myapp.jar
```

### @Profile pada @Configuration class

```java
@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public DataSource dataSource() {
        // In-memory H2 database untuk development
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    @Bean
    public DataSource dataSource() {
        // Real database untuk production
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://...");
        return ds;
    }
}
```

---

## 11. @Value vs @ConfigurationProperties

### @Value — untuk satu atau dua properti

```java
@Component
public class EmailService {

    @Value("${email.host}")
    private String host;

    @Value("${email.port}")
    private int port;

    @Value("${email.username}")
    private String username;

    // Masalah: jika ada 10+ properti, @Value berulang sangat verbose
}
```

### @ConfigurationProperties — untuk grup properti (LEBIH BAIK)

Buat class yang merepresentasikan grup konfigurasi:

```java
package com.belajar.day3.properties;

// Tidak perlu @Component jika didaftarkan via @EnableConfigurationProperties
// Prefix "email" berarti membaca semua key yang dimulai dengan "email."
public class EmailProperties {

    private String host;
    private int port = 25; // default value
    private String username;
    private String password;
    private boolean ssl = false;

    // Getters dan Setters WAJIB ada (Spring set via setter)
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }

    @Override
    public String toString() {
        return "EmailProperties{host='" + host + "', port=" + port +
               ", username='" + username + "', ssl=" + ssl + "}";
    }
}
```

**Daftarkan via Config:**
```java
package com.belajar.day3.config;

import com.belajar.day3.properties.EmailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {

    // Spring otomatis bind semua property dengan prefix "email" ke EmailProperties
    @Bean
    @ConfigurationProperties(prefix = "email")
    public EmailProperties emailProperties() {
        return new EmailProperties();
    }

    // Sekarang bisa inject EmailProperties sebagai Bean
    @Bean
    public EmailNotificationService emailService(EmailProperties emailProperties) {
        System.out.println("[PropertiesConfig] Email config: " + emailProperties);
        return new EmailNotificationService(
            emailProperties.getHost(),
            emailProperties.getPort(),
            emailProperties.getUsername()
        );
    }
}
```

**application.properties yang sesuai:**
```properties
email.host=smtp.gmail.com
email.port=587
email.username=myapp@gmail.com
email.password=app-password-123
email.ssl=true
```

### Perbandingan @Value vs @ConfigurationProperties

| | @Value | @ConfigurationProperties |
|---|---|---|
| Cocok untuk | 1-3 properti sederhana | Grup properti yang related |
| Tipe support | Terbatas | Penuh (List, Map, nested objects) |
| Validasi | Manual | @Valid / @NotNull, @Min, dll |
| IDE support | Terbatas | Penuh (dengan spring-boot-configuration-processor) |
| Refactoring | Mudah typo | Type-safe |
| Penggunaan | @Value("${key}") | Inject sebagai Bean |

---

## 12. Kode Lengkap: UserRepository

```java
package com.belajar.day3.repository;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private final String dbUrl;
    private final String username;
    private final String password;

    private final Map<String, String> users = new HashMap<>();

    public UserRepository(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
        System.out.println("[UserRepository] Inisialisasi dengan: " + dbUrl);
        // Simulasi load data
        users.put("budi123", "Budi Santoso");
        users.put("siti456", "Siti Rahayu");
        users.put("joko789", "Joko Widodo");
    }

    public String findById(String id) {
        return users.getOrDefault(id, "User tidak ditemukan");
    }

    public Map<String, String> findAll() {
        return new HashMap<>(users);
    }

    public void save(String id, String name) {
        users.put(id, name);
        System.out.println("[UserRepository] Menyimpan: " + id + " -> " + name);
    }
}
```

## Kode Lengkap: NotificationService

```java
package com.belajar.day3.service;

public interface NotificationService {
    void send(String to, String message);
}

// EmailNotificationService.java
package com.belajar.day3.service;

public class EmailNotificationService implements NotificationService {

    private final String host;
    private final int port;
    private final String username;

    public EmailNotificationService(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    @Override
    public void send(String to, String message) {
        System.out.println("[EmailService] Mengirim dari " + username + "@" + host + ":" + port);
        System.out.println("[EmailService] To: " + to);
        System.out.println("[EmailService] Pesan: " + message);
    }
}
```

---

# BAGIAN 3 — LATIHAN

## 12. Latihan: Property Injection dengan Properties File

### Main.java — Jalankan dengan berbagai profile

```java
package com.belajar.day3;

import com.belajar.day3.config.AppConfig;
import com.belajar.day3.controller.HelloController;
import com.belajar.day3.repository.UserRepository;
import com.belajar.day3.service.NotificationService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        // Tentukan profile dari args atau gunakan default
        String profile = args.length > 0 ? args[0] : "dev";
        System.out.println("============================================");
        System.out.println("  SPRING DAY 3: CONFIGURATION OPTIONS");
        System.out.println("  Active Profile: " + profile.toUpperCase());
        System.out.println("============================================\n");

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profile);
        ctx.register(AppConfig.class);
        ctx.refresh();

        try {
            // Demo HelloController (dari refactoring Day 1)
            System.out.println("\n--- Hello Controller Demo ---");
            HelloController controller = ctx.getBean(HelloController.class);
            controller.handleRequest("Budi");
            controller.handleRequest("Siti");

            // Demo UserRepository (membaca dari properties)
            System.out.println("\n--- User Repository Demo ---");
            UserRepository userRepo = ctx.getBean(UserRepository.class);
            System.out.println("Cari budi123: " + userRepo.findById("budi123"));
            System.out.println("Cari unknown: " + userRepo.findById("unknown"));
            userRepo.save("ani999", "Ani Maharani");
            System.out.println("Cari ani999 : " + userRepo.findById("ani999"));

            // Demo NotificationService (membaca dari properties)
            System.out.println("\n--- Notification Service Demo ---");
            NotificationService notif = ctx.getBean(NotificationService.class);
            notif.send("budi@example.com", "Selamat datang di aplikasi kami!");

        } finally {
            ctx.close();
        }
    }
}
```

### AppConfig.java (Master Config)

```java
package com.belajar.day3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@Import({ServiceConfig.class, ControllerConfig.class,
         DatabaseConfig.class, InfrastructureConfig.class})
@PropertySources({
    @PropertySource("classpath:application.properties"),
    // Ignore jika file tidak ada (opsional)
    @PropertySource(value = "classpath:application-${spring.profiles.active:dev}.properties",
                    ignoreResourceNotFound = true)
})
public class AppConfig {
    // Satu titik masuk untuk seluruh konfigurasi
}
```

---

## 13. Praktik: Multiple Config Classes

Ini menunjukkan bagaimana proyek nyata mengorganisir konfigurasi:

```java
// ServiceConfig.java
package com.belajar.day3.config;

import com.belajar.day3.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Value("${greeting.word:Halo}")
    private String greetingWord;

    @Value("${greeting.farewell:Sampai jumpa}")
    private String farewellWord;

    @Bean
    public GreetingService greetingService() {
        System.out.println("[ServiceConfig] greeting.word = '" + greetingWord + "'");
        return new GreetingServiceImpl(greetingWord);
    }
}

// ControllerConfig.java
package com.belajar.day3.config;

import com.belajar.day3.controller.HelloController;
import com.belajar.day3.service.GreetingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    @Bean
    public HelloController helloController(GreetingService greetingService) {
        return new HelloController(greetingService);
    }
}
```

---

### Output Lengkap yang Diharapkan

**Jalankan dengan profile 'dev':**

```
============================================
  SPRING DAY 3: CONFIGURATION OPTIONS
  Active Profile: DEV
============================================

[ServiceConfig] greeting.word = 'Hei (DEV)'
[DatabaseConfig] Connecting to: jdbc:h2:mem:devdb
[DatabaseConfig] Username: dev_user
[InfrastructureConfig] SMTP: localhost:1025
[UserRepository] Inisialisasi dengan: jdbc:h2:mem:devdb

--- Hello Controller Demo ---
[HelloController] Hei (DEV), Budi!
[HelloController] Hei (DEV), Siti!

--- User Repository Demo ---
Cari budi123: Budi Santoso
Cari unknown: User tidak ditemukan
[UserRepository] Menyimpan: ani999 -> Ani Maharani
Cari ani999 : Ani Maharani

--- Notification Service Demo ---
[EmailService] Mengirim dari apikey@localhost:1025
[EmailService] To: budi@example.com
[EmailService] Pesan: Selamat datang di aplikasi kami!
```

---

## 14. Ringkasan & Checklist Day 3

### Peta Konsep

```
KONFIGURASI SPRING
+-- XML Config             <- legacy, masih valid
+-- Annotation Config      <- @Component, @Autowired (modern)
+-- Java Config            <- @Configuration + @Bean (paling fleksibel)
    +-- @Import            <- gabungkan multiple config class
    +-- @ComponentScan     <- auto-detect @Component
    +-- @PropertySource    <- baca file .properties

EXTERNALIZED CONFIGURATION
+-- application.properties        <- default semua env
+-- application-{profile}.props   <- override per profile
+-- Environment Variables         <- override saat deploy
+-- System Properties             <- override via -D flags
+-- Prioritas: env var > file > default

PROPERTY INJECTION
+-- @Value("${key}")              <- untuk 1-3 properti
+-- @Value("${key:default}")      <- dengan default value
+-- @ConfigurationProperties      <- untuk grup properti (lebih baik)

PROFILES
+-- @Profile("dev")               <- Bean aktif di profile tertentu
+-- ctx.setActiveProfiles("dev")  <- aktifkan via kode
+-- -Dspring.profiles.active=prod <- aktifkan via JVM args
+-- SPRING_PROFILES_ACTIVE=prod   <- aktifkan via env var
```

### Alur Refactoring Day 1 -> Day 3

```
Day 1 (Annotation)          Day 3 (Java Config)
+------------------+         +------------------+
| @Service         |  ---->  | Pure Java class  |
| @Component       |  ---->  | + @Bean di Config|
| @ComponentScan   |  ---->  | @Import multiple |
| hardcoded value  |  ---->  | @Value / props   |
| single config    |  ---->  | multiple configs |
+------------------+         +------------------+
```

### Checklist Day 3

**Java Config:**
- [ ] Aku mengerti perbedaan @Configuration vs @Component untuk @Bean
- [ ] Aku tahu kenapa @Configuration pakai CGLIB proxy
- [ ] Aku bisa inject dependency antar @Bean via method parameter
- [ ] Aku bisa memisahkan konfigurasi ke multiple @Configuration class
- [ ] Aku bisa menggabungkan config dengan @Import

**Annotation Config:**
- [ ] Aku tahu perbedaan @Service, @Repository, @Controller vs @Component
- [ ] Aku bisa pakai @Qualifier untuk pilih Bean spesifik
- [ ] Aku mengerti perbedaan @Primary vs @Qualifier
- [ ] Aku bisa konfigurasi @ComponentScan dengan filter

**Externalized Configuration:**
- [ ] Aku bisa membuat application.properties
- [ ] Aku bisa inject nilai dengan @Value
- [ ] Aku bisa set default value di @Value
- [ ] Aku bisa membuat profile-specific properties file
- [ ] Aku mengerti urutan prioritas konfigurasi
- [ ] Aku bisa menggunakan @ConfigurationProperties untuk grup props
- [ ] Aku bisa mengaktifkan profile via kode, JVM args, env var

**Refactoring:**
- [ ] Aku berhasil merefaktor proyek Day 1 ke Java Config murni
- [ ] Aku bisa memisahkan konfigurasi ke DatabaseConfig, ServiceConfig, dll
- [ ] Aku bisa mengganti hardcoded values dengan @Value dari properties file
- [ ] Aku bisa membuat konfigurasi yang berbeda untuk dev vs prod

### Pertanyaan Kuis Akhir

1. Apa yang terjadi jika kamu menggunakan @Component alih-alih @Configuration untuk class yang punya @Bean methods yang saling memanggil?

2. Kamu punya properties berikut:
   - application.properties: `greeting.word=Halo`
   - application-dev.properties: `greeting.word=Hei`
   - Environment variable: `GREETING_WORD=Yo`
   Manakah yang dipakai jika profile 'dev' aktif dan env var ada?

3. Kenapa kita TIDAK boleh commit application-prod.properties dengan password asli ke Git? Apa alternatifnya?

4. Kapan lebih baik pakai @Value dibanding @ConfigurationProperties?

---

## Recap 3 Hari — Gambaran Besar

```
DAY 1: IoC & DI
  - Prinsip IoC: kontrol diserahkan ke container
  - DI: 3 jenis (constructor, setter, field)
  - ApplicationContext dan Bean
  - XML config vs Annotation config (pengenalan)

DAY 2: Bean Lifecycle
  - Scope: singleton, prototype, request, session
  - Lifecycle: @PostConstruct, @PreDestroy
  - BeanPostProcessor: cara Spring implementasikan fiturnya
  - Jebakan: Prototype dalam Singleton

DAY 3: Configuration Options
  - Java Config: @Configuration + @Bean
  - Multiple config classes + @Import
  - Externalized config: properties files
  - Profiles: dev vs prod
  - @Value vs @ConfigurationProperties

KAMU SIAP untuk Spring Boot — semua yang Spring Boot lakukan
adalah OTOMASI dari konsep-konsep di atas!
```

*Setelah menguasai Day 1-3, langkah selanjutnya adalah Spring Boot — di mana semua konfigurasi ini dilakukan secara otomatis dengan "convention over configuration". Tapi sekarang kamu tahu persis apa yang terjadi di balik layar!*
