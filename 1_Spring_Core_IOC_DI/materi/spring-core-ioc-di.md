# Belajar Spring Core: IoC dan Dependency Injection

> Panduan ini dirancang agar kamu benar-benar **paham** apa yang terjadi di balik layar Spring Boot — tidak ada lagi yang terasa "magic".

---

## Daftar Isi

1. [Masalah yang Dipecahkan Spring](#1-masalah-yang-dipecahkan-spring)
2. [Inversion of Control (IoC)](#2-inversion-of-control-ioc)
3. [Dependency Injection (DI)](#3-dependency-injection-di)
4. [Spring IoC Container](#4-spring-ioc-container)
5. [Bean: Objek yang Dikelola Spring](#5-bean-objek-yang-dikelola-spring)
6. [Cara Mendefinisikan Bean](#6-cara-mendefinisikan-bean)
7. [Cara Menyuntikkan Dependency](#7-cara-menyuntikkan-dependency)
8. [Bean Scope](#8-bean-scope)
9. [ApplicationContext vs BeanFactory](#9-applicationcontext-vs-beanfactory)
10. [Contoh Proyek Lengkap](#10-contoh-proyek-lengkap)
11. [Apa yang Terjadi di Spring Boot?](#11-apa-yang-terjadi-di-spring-boot)

---

## 1. Masalah yang Dipecahkan Spring

Bayangkan kamu membangun aplikasi tanpa framework apapun:

```java
public class OrderService {
    // Kamu harus buat sendiri semua dependency
    private PaymentService paymentService = new PaymentService();
    private EmailService emailService = new EmailService(new SmtpConfig("smtp.gmail.com", 587));
    private OrderRepository orderRepository = new OrderRepository(new DatabaseConnection("jdbc:mysql://..."));

    public void placeOrder(Order order) {
        orderRepository.save(order);
        paymentService.charge(order);
        emailService.sendConfirmation(order);
    }
}
```

**Masalah yang muncul:**

- `OrderService` harus **tahu cara membuat** semua dependency-nya → Tight coupling
- Kalau `PaymentService` butuh konfigurasi berbeda di test vs production → Susah diganti
- Kalau ingin mengganti `EmailService` dengan `SmsService` → Harus ubah kode `OrderService`
- Unit test menjadi sulit karena tidak bisa mock dependency

Spring hadir untuk memecahkan masalah ini.

---

## 2. Inversion of Control (IoC)

### Konsep

**IoC** adalah prinsip desain di mana **kontrol pembuatan dan pengelolaan objek dipindahkan dari kode kamu ke sebuah framework/container**.

Tanpa IoC (kamu yang kontrol):
```
Kamu → new PaymentService() → new OrderService(paymentService)
```

Dengan IoC (container yang kontrol):
```
Spring Container → membuat PaymentService → membuat OrderService → menyuntikkan PaymentService ke OrderService
```

Analogi sederhana: bayangkan kamu pesan makanan di restoran. Tanpa IoC, kamu harus masak sendiri. Dengan IoC, kamu tinggal duduk dan restoran (container) yang mengurus semuanya — bahan, masak, dan antarkan ke mejamu.

### Sebelum IoC
```java
// Kamu yang bertanggung jawab membuat dan menghubungkan semua objek
PaymentService payment = new PaymentService();
EmailService email = new EmailService();
OrderService order = new OrderService(payment, email); // manual wiring
```

### Sesudah IoC
```java
// Spring yang bertanggung jawab — kamu tinggal deklarasikan apa yang kamu butuhkan
@Component
public class OrderService {
    @Autowired
    private PaymentService paymentService; // Spring yang suntikkan ini
    
    @Autowired
    private EmailService emailService;
}
```

---

## 3. Dependency Injection (DI)

DI adalah **mekanisme konkret** untuk mengimplementasikan IoC. Alih-alih objek membuat dependency-nya sendiri, dependency **disuntikkan (injected) dari luar**.

### Tanpa DI (Anti-pattern)
```java
public class Car {
    private Engine engine;
    
    public Car() {
        this.engine = new PetrolEngine(); // Car tahu persis jenis mesin
        // Kalau mau ganti ke ElectricEngine → harus ubah kode Car
    }
}
```

### Dengan DI
```java
public class Car {
    private Engine engine; // Bergantung pada interface, bukan implementasi konkret
    
    public Car(Engine engine) { // Engine "disuntikkan" dari luar
        this.engine = engine;
    }
}

// Di tempat lain (atau oleh Spring):
Engine engine = new ElectricEngine();
Car car = new Car(engine); // Mudah diganti tanpa ubah Car
```

---

## 4. Spring IoC Container

Spring IoC Container adalah inti dari Spring Framework. Tugasnya:

1. **Membaca konfigurasi** (XML, anotasi, atau Java class)
2. **Membuat objek** (Bean) berdasarkan konfigurasi tersebut
3. **Menyuntikkan dependency** antar Bean
4. **Mengelola lifecycle** Bean (kapan dibuat, kapan dihancurkan)

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring IoC Container                      │
│                                                             │
│  Konfigurasi ──► Membuat Bean ──► Menyuntikkan Dependency   │
│  (@Component,      (new ...)       (@Autowired)             │
│   @Bean, XML)                                               │
│                                                             │
│  Hasil: ApplicationContext — kumpulan Bean yang siap pakai  │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. Bean: Objek yang Dikelola Spring

**Bean** adalah objek Java yang dibuat dan dikelola oleh Spring IoC Container. Tidak semua objek adalah Bean — hanya yang kamu daftarkan ke Spring.

```java
// Ini BUKAN Bean (kamu buat sendiri dengan new)
PaymentService ps = new PaymentService();

// Ini adalah Bean (Spring yang buat dan kelola)
@Component
public class PaymentService { ... }
```

---

## 6. Cara Mendefinisikan Bean

Ada 3 cara mendefinisikan Bean di Spring:

### Cara 1: Anotasi `@Component` (dan turunannya)

Ini cara paling umum. Anotasi ditempatkan langsung di class.

```java
@Component          // Bean biasa
public class EmailService { ... }

@Service            // Bean untuk business logic (secara teknis sama dengan @Component)
public class OrderService { ... }

@Repository         // Bean untuk akses data (+ exception translation)
public class OrderRepository { ... }

@Controller         // Bean untuk web layer (Spring MVC)
public class OrderController { ... }
```

> `@Service`, `@Repository`, `@Controller` semuanya adalah `@Component` dengan label berbeda. Mereka tidak memiliki perbedaan teknis besar, tapi memberikan kejelasan semantik.

Agar Spring "scan" dan menemukan anotasi ini, kamu perlu menentukan package mana yang akan di-scan:

```java
@Configuration
@ComponentScan("com.example.myapp") // Scan semua class di package ini
public class AppConfig { }
```

### Cara 2: `@Bean` di dalam `@Configuration` class

Digunakan ketika kamu perlu kontrol lebih atas pembuatan Bean, atau ketika kamu tidak bisa menambahkan anotasi ke class (misalnya class dari library pihak ketiga).

```java
@Configuration
public class AppConfig {
    
    @Bean
    public DataSource dataSource() {
        // Kamu yang tentukan bagaimana Bean ini dibuat
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        ds.setUsername("root");
        ds.setPassword("secret");
        return ds;
    }
    
    @Bean
    public EmailService emailService() {
        // Misalnya EmailService dari library luar yang tidak bisa kita modifikasi
        return new ThirdPartyEmailService("api-key-123");
    }
    
    @Bean
    public OrderService orderService() {
        // Spring akan inject dataSource() dan emailService() secara otomatis
        return new OrderService(dataSource(), emailService());
    }
}
```

### Cara 3: Konfigurasi XML (cara lama)

Masih valid tapi jarang digunakan di proyek baru.

```xml
<!-- applicationContext.xml -->
<beans>
    <bean id="paymentService" class="com.example.PaymentService"/>
    
    <bean id="orderService" class="com.example.OrderService">
        <constructor-arg ref="paymentService"/>
    </bean>
</beans>
```

---

## 7. Cara Menyuntikkan Dependency

Ada 3 jenis DI di Spring:

### Jenis 1: Constructor Injection ✅ (Direkomendasikan)

```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final EmailService emailService;
    
    // Spring otomatis inject jika hanya ada 1 constructor
    // (Sejak Spring 4.3, @Autowired tidak wajib di constructor)
    public OrderService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }
    
    public void placeOrder(Order order) {
        paymentService.charge(order);
        emailService.sendConfirmation(order);
    }
}
```

**Kenapa ini yang terbaik?**
- Dependency bersifat `final` → immutable, lebih aman
- Jelas dependency apa yang dibutuhkan class ini
- Mudah di-unit test tanpa Spring sama sekali

```java
// Unit test tanpa Spring Container:
PaymentService mockPayment = mock(PaymentService.class);
EmailService mockEmail = mock(EmailService.class);
OrderService service = new OrderService(mockPayment, mockEmail); // Langsung bisa!
```

### Jenis 2: Setter Injection

```java
@Service
public class OrderService {
    private PaymentService paymentService;
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Gunakan ini hanya untuk dependency **opsional** (bisa null).

### Jenis 3: Field Injection ❌ (Tidak Direkomendasikan)

```java
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService; // Spring inject langsung ke field
    
    @Autowired
    private EmailService emailService;
}
```

**Kenapa dihindari?**
- Field bersifat `private` tapi Spring inject via reflection → tidak natural
- Tidak bisa unit test tanpa Spring Container
- Dependency tersembunyi, tidak jelas dari constructor

---

## 8. Bean Scope

Scope menentukan **berapa banyak instance** dari sebuah Bean yang dibuat Spring.

### Singleton (Default)
```java
@Component
// @Scope("singleton") // tidak perlu ditulis, ini default
public class PaymentService {
    // Hanya ADA SATU instance di seluruh aplikasi
    // Semua yang inject PaymentService akan dapat objek yang SAMA
}
```

```
OrderService ──────►  PaymentService (instance yang sama)
NotificationService ──►  PaymentService (instance yang sama)
```

### Prototype
```java
@Component
@Scope("prototype")
public class ShoppingCart {
    // Setiap kali di-inject atau di-request, dibuat instance BARU
    private List<Item> items = new ArrayList<>();
}
```

```
UserA.request → ShoppingCart (instance baru #1)
UserB.request → ShoppingCart (instance baru #2)
```

### Request & Session (Khusus Web)
```java
@Component
@Scope("request")  // Satu instance per HTTP request
public class RequestContext { ... }

@Component
@Scope("session")  // Satu instance per HTTP session
public class UserSession { ... }
```

---

## 9. ApplicationContext vs BeanFactory

| | BeanFactory | ApplicationContext |
|---|---|---|
| **Kelas** | `BeanFactory` | `ApplicationContext` (extends BeanFactory) |
| **Lazy loading** | Bean dibuat saat pertama kali diminta | Bean dibuat saat container startup (eager) |
| **Fitur tambahan** | Dasar saja | Event, i18n, AOP, dll |
| **Kapan digunakan** | Jarang, resource sangat terbatas | **Selalu gunakan ini** |

**Implementasi ApplicationContext yang umum:**

```java
// Konfigurasi Java (modern)
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

// Konfigurasi XML (lama)
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

// Di Spring Boot (otomatis dibuat)
// → SpringApplication.run() membuat ApplicationContext di balik layar
```

---

## 10. Contoh Proyek Lengkap

Mari kita buat proyek Spring Core **tanpa Spring Boot** dari nol supaya kamu benar-benar paham.

### Struktur Proyek
```
src/
├── main/java/com/example/
│   ├── config/
│   │   └── AppConfig.java
│   ├── service/
│   │   ├── NotificationService.java   (interface)
│   │   ├── EmailNotification.java     (implementasi)
│   │   └── OrderService.java
│   ├── repository/
│   │   └── OrderRepository.java
│   └── Main.java
└── main/resources/
    └── (kosong untuk sekarang)
```

### pom.xml (Maven)
```xml
<dependencies>
    <!-- Spring Context sudah include IoC Container -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.4</version>
    </dependency>
</dependencies>
```

### Interface NotificationService
```java
package com.example.service;

public interface NotificationService {
    void send(String message);
}
```

### Implementasi EmailNotification
```java
package com.example.service;

import org.springframework.stereotype.Component;

@Component
public class EmailNotification implements NotificationService {
    
    @Override
    public void send(String message) {
        System.out.println("📧 Mengirim email: " + message);
    }
}
```

### OrderRepository
```java
package com.example.repository;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    
    public void save(String orderId) {
        System.out.println("💾 Menyimpan order: " + orderId);
    }
}
```

### OrderService
```java
package com.example.service;

import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    
    // Constructor injection — Spring otomatis inject
    public OrderService(NotificationService notificationService, 
                        OrderRepository orderRepository) {
        this.notificationService = notificationService;
        this.orderRepository = orderRepository;
    }
    
    public void processOrder(String orderId) {
        System.out.println("🛒 Memproses order: " + orderId);
        orderRepository.save(orderId);
        notificationService.send("Order " + orderId + " berhasil diproses!");
    }
}
```

### AppConfig.java (Konfigurasi)
```java
package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example") // Scan semua @Component, @Service, @Repository di package ini
public class AppConfig {
    // Kosong — Spring yang handle semuanya via @ComponentScan
}
```

### Main.java
```java
package com.example;

import com.example.config.AppConfig;
import com.example.service.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        
        // 1. Buat Spring IoC Container
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 2. Ambil Bean dari container
        OrderService orderService = context.getBean(OrderService.class);
        
        // 3. Gunakan Bean
        orderService.processOrder("ORD-001");
        orderService.processOrder("ORD-002");
        
        // Cek singleton: kedua getBean() mengembalikan objek yang sama
        OrderService os1 = context.getBean(OrderService.class);
        OrderService os2 = context.getBean(OrderService.class);
        System.out.println("Singleton? " + (os1 == os2)); // true
    }
}
```

### Output
```
🛒 Memproses order: ORD-001
💾 Menyimpan order: ORD-001
📧 Mengirim email: Order ORD-001 berhasil diproses!
🛒 Memproses order: ORD-002
💾 Menyimpan order: ORD-002
📧 Mengirim email: Order ORD-002 berhasil diproses!
Singleton? true
```

### Mengganti Implementasi dengan Mudah

Karena `OrderService` bergantung pada **interface** `NotificationService`, mengganti ke SMS sangat mudah:

```java
@Component
@Primary // Spring pilih ini saat ada lebih dari 1 implementasi
public class SmsNotification implements NotificationService {
    
    @Override
    public void send(String message) {
        System.out.println("📱 Mengirim SMS: " + message);
    }
}
```

`OrderService` **tidak perlu diubah sama sekali**. Inilah kekuatan IoC + DI!

---

## 11. Apa yang Terjadi di Spring Boot?

Sekarang kamu bisa "membaca" apa yang Spring Boot lakukan secara otomatis:

```java
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

`@SpringBootApplication` adalah gabungan dari:

```java
@Configuration          // Ini adalah @Configuration class
@EnableAutoConfiguration // Aktifkan auto-configuration (tambah Bean otomatis berdasarkan dependency di classpath)
@ComponentScan          // Scan semua @Component di package ini dan sub-package-nya
```

Dan `SpringApplication.run()` melakukan:
1. Membuat `ApplicationContext`
2. Menjalankan `@ComponentScan`
3. Menjalankan semua auto-configuration (DataSource, JPA, dll)
4. Menyuntikkan semua dependency
5. Menjalankan web server (jika ada `spring-boot-starter-web`)

**Tidak ada magic — hanya otomasi dari apa yang sudah kamu pelajari di atas!**

---

## Ringkasan

| Konsep | Penjelasan Singkat |
|---|---|
| **IoC** | Kontrol pembuatan objek dipindah ke Spring Container |
| **DI** | Mekanisme IoC: dependency "disuntikkan" dari luar |
| **Bean** | Objek yang dibuat dan dikelola Spring |
| **ApplicationContext** | Spring IoC Container |
| **@Component** | Tandai class agar di-scan dan dijadikan Bean |
| **@Autowired** | Tandai constructor/field/setter untuk di-inject |
| **@Configuration + @Bean** | Definisikan Bean secara manual dengan kode Java |
| **Singleton** | Default scope: satu instance per container |

---

*Setelah memahami ini, kamu siap masuk Spring Boot dengan kepala tegak — tidak ada lagi yang terasa ajaib!* 🚀
