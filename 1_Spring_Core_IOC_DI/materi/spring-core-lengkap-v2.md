# Belajar Spring Core: IoC & Dependency Injection
### Panduan Lengkap dari Nol sampai Paham Betulan

> **Tujuan panduan ini:** Setelah membaca dan mempraktikkan ini, kamu akan mampu menjelaskan IoC & DI, mengkonfigurasi Spring Container dengan XML maupun Java, dan tidak lagi merasa Spring Boot "ajaib".

---

## 🗺️ Daftar Isi

**BAGIAN 1 — TEORI (≈ 30–40 menit membaca)**
1. [Masalah yang Dipecahkan Spring](#bagian-1--teori)
2. [Inversion of Control (IoC)](#2-inversion-of-control-ioc)
3. [Dependency Injection (DI)](#3-dependency-injection-di)
4. [Spring IoC Container — Cara Kerjanya](#4-spring-ioc-container--cara-kerjanya)
5. [Bean dan Lifecycle-nya](#5-bean-dan-lifecycle-nya)
6. [Bean Scope](#6-bean-scope)
7. [ApplicationContext vs BeanFactory](#7-applicationcontext-vs-beanfactory)

**BAGIAN 2 — KONFIGURASI (≈ 70–80 menit praktek)**

8. [Setup Proyek Maven](#8-setup-proyek-maven)
9. [Konfigurasi XML](#9-konfigurasi-xml--cara-klasik)
10. [Konfigurasi Annotation](#10-konfigurasi-annotation--cara-modern)
11. [Konfigurasi Java-Based](#11-konfigurasi-java-based-configuration)
12. [Perbandingan XML vs Annotation vs Java Config](#12-perbandingan-xml-vs-annotation-vs-java-config)

**BAGIAN 3 — PRAKTIK (Hello World Exercise)**

13. [Latihan: Hello World Spring Application](#bagian-3--latihan-hello-world)
14. [Cara Inject Service ke Controller (tanpa Spring MVC)](#14-inject-service-ke-controller)
15. [Apa yang Terjadi di Spring Boot?](#15-apa-yang-terjadi-di-spring-boot)
16. [Ringkasan & Checklist Belajar](#16-ringkasan--checklist-belajar)

---

# BAGIAN 1 — TEORI

## 1. Masalah yang Dipecahkan Spring

Sebelum bicara IoC, mari pahami dulu **mengapa** kita butuh IoC.

### Kode Tanpa Framework (The Old Way)

```java
public class OnlineShop {
    public static void main(String[] args) {
        // Kamu harus buat SEMUA objek secara manual dan dalam urutan yang benar
        DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost/shop", "root", "pass");
        ProductRepository productRepo = new ProductRepository(db);
        
        SmtpConfig smtpConfig = new SmtpConfig("smtp.gmail.com", 587, "user@gmail.com", "secret");
        EmailService emailService = new EmailService(smtpConfig);
        
        InventoryService inventoryService = new InventoryService(productRepo);
        PaymentService paymentService = new PaymentService("stripe-api-key-xyz");
        
        OrderRepository orderRepo = new OrderRepository(db);
        
        // Dan baru bisa buat OrderService setelah semua dependency siap
        OrderService orderService = new OrderService(
            orderRepo, 
            inventoryService, 
            paymentService, 
            emailService
        );
        
        orderService.placeOrder(new Order(...));
    }
}
```

**5 Masalah Nyata dari Kode di Atas:**

| Masalah | Dampak |
|---|---|
| **Tight coupling** | `OrderService` tahu cara membuat semua dependency-nya — susah diganti |
| **Urutan pembuatan** | Harus buat `db` sebelum `productRepo`, dsb. — rawan bug |
| **Konfigurasi tersebar** | Credentials database dan SMTP hardcoded di sana-sini |
| **Testing sulit** | Tidak bisa ganti `EmailService` dengan mock tanpa ubah kode |
| **Kode berulang** | Kalau 5 tempat butuh `EmailService`, kamu buat 5 kali `new EmailService(...)` |

Spring memecahkan semua masalah ini sekaligus.

---

## 2. Inversion of Control (IoC)

### Definisi

**IoC (Inversion of Control)** adalah prinsip desain software di mana **alur kontrol sebuah program dibalik**: alih-alih kode kamu yang memanggil framework, framework yang memanggil kode kamu.

Dalam konteks manajemen objek: alih-alih kamu yang membuat dan menghubungkan objek, **Spring Container yang melakukannya**.

### Visualisasi

```
❌ TANPA IoC (kamu yang kontrol segalanya):

main() ──► new DatabaseConnection()
       ──► new EmailService()
       ──► new OrderRepository()
       ──► new OrderService(emailService, orderRepo)
       ──► orderService.placeOrder()

Kamu = Chef yang harus belanja bahan, masak, dan sajikan


✅ DENGAN IoC (Spring yang kontrol):

main() ──► ApplicationContext.run()
              │
              ├──► Spring baca konfigurasi
              ├──► Spring buat semua Bean yang dibutuhkan
              ├──► Spring hubungkan Bean satu sama lain
              └──► Kamu tinggal getBean(OrderService.class)

Kamu = Tamu restoran, Spring = Restoran (chef + pelayan)
```

### Analogi Lebih Dalam: Hollywood Principle

IoC sering disebut **"Hollywood Principle"**: *"Don't call us, we'll call you."*

- **Tanpa IoC:** Kamu menelepon semua orang (membuat semua objek sendiri)
- **Dengan IoC:** Kamu pasang diri sebagai kandidat (`@Component`), Spring yang "menelepon" kamu saat dibutuhkan

### Perbedaan IoC dan DI

Ini sering membingungkan:

- **IoC** = Prinsip/konsep (kontrol dibalik)
- **DI** = Salah satu cara mengimplementasikan IoC (dengan menyuntikkan dependency)

IoC bisa diimplementasikan dengan cara lain juga (misalnya Service Locator pattern), tapi DI adalah cara yang paling populer dan yang digunakan Spring.

---

## 3. Dependency Injection (DI)

### Apa itu Dependency?

**Dependency** adalah objek lain yang dibutuhkan sebuah class agar bisa berfungsi.

```java
public class OrderService {
    // PaymentService adalah DEPENDENCY dari OrderService
    private PaymentService paymentService;
    
    // EmailService adalah DEPENDENCY dari OrderService
    private EmailService emailService;
}
```

### Apa itu Injection?

**Injection** = memberikan/menyuntikkan dependency dari luar, bukan dibuat sendiri di dalam class.

```java
// ❌ BUKAN injection — OrderService buat sendiri (tight coupling)
public class OrderService {
    private PaymentService paymentService = new PaymentService(); // Hardcoded!
}

// ✅ INJECTION — dependency diberikan dari luar
public class OrderService {
    private PaymentService paymentService;
    
    public OrderService(PaymentService paymentService) { // Diterima dari luar
        this.paymentService = paymentService;
    }
}
```

### 3 Jenis DI di Spring

#### Jenis 1: Constructor Injection ✅ DIREKOMENDASIKAN

```java
@Service
public class OrderService {
    
    private final PaymentService paymentService;  // final = immutable!
    private final EmailService emailService;
    
    // Spring otomatis inject karena hanya ada 1 constructor
    // @Autowired tidak wajib sejak Spring 4.3
    public OrderService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }
}
```

**Keunggulan Constructor Injection:**
- Dependency bersifat `final` → dijamin tidak null, tidak bisa diganti setelah dibuat
- Class terbukti testable — bisa di-test tanpa Spring sama sekali
- Jelas terlihat apa yang dibutuhkan class ini (dari constructor signature)
- Spring sendiri merekomendasikan ini

```java
// Unit test TANPA Spring — semudah ini!
@Test
void testPlaceOrder() {
    PaymentService mockPayment = Mockito.mock(PaymentService.class);
    EmailService mockEmail = Mockito.mock(EmailService.class);
    
    OrderService service = new OrderService(mockPayment, mockEmail); // Langsung!
    service.placeOrder(new Order("ORD-001"));
    
    Mockito.verify(mockPayment).charge(any());
}
```

#### Jenis 2: Setter Injection

```java
@Service
public class ReportService {
    
    private EmailService emailService;
    
    @Autowired  // @Autowired WAJIB di setter
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

Gunakan setter injection hanya untuk **dependency opsional** yang bisa null atau diganti setelah pembuatan object.

#### Jenis 3: Field Injection ❌ HINDARI

```java
@Service
public class OrderService {
    
    @Autowired
    private PaymentService paymentService;  // Spring inject via reflection
    
    @Autowired
    private EmailService emailService;
}
```

**Mengapa dihindari?**
- Menggunakan Java Reflection untuk inject ke field `private` — tidak natural
- Tidak bisa unit test tanpa Spring Container (field private tidak bisa di-set dari luar)
- Menyembunyikan dependency — tidak terlihat dari luar class apa yang dibutuhkan
- Tidak bisa `final` — risiko nilai berubah

> **Aturan praktis:** Selalu gunakan Constructor Injection. Jika dependency opsional, gunakan Setter Injection. Jangan pakai Field Injection.

---

## 4. Spring IoC Container — Cara Kerjanya

### Dua Komponen Utama

Spring Container terdiri dari dua bagian:

```
┌──────────────────────────────────────────────────────────────────┐
│                      Spring IoC Container                        │
│                                                                  │
│  ┌─────────────────────┐    ┌──────────────────────────────────┐ │
│  │   Configuration     │    │         Bean Registry            │ │
│  │   Metadata          │───►│                                  │ │
│  │                     │    │  paymentService → PaymentService │ │
│  │  • XML files        │    │  emailService   → EmailService   │ │
│  │  • @Component scan  │    │  orderService   → OrderService   │ │
│  │  • @Configuration   │    │                                  │ │
│  │    class + @Bean    │    │  (semua dikelola di sini)        │ │
│  └─────────────────────┘    └──────────────────────────────────┘ │
│                                         │                        │
│                                         ▼                        │
│                              Kamu: getBean(OrderService.class)   │
└──────────────────────────────────────────────────────────────────┘
```

### Proses Step-by-Step

Ketika kamu menjalankan:
```java
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
```

Spring melakukan ini secara berurutan:

**Step 1 — Baca Konfigurasi**
```
Spring baca AppConfig.class
→ Temukan @ComponentScan("com.example")
→ Scan semua class di package com.example
→ Temukan: @Component, @Service, @Repository, @Controller
→ Buat daftar: "saya harus buat ini semua"
```

**Step 2 — Buat Bean (Instantiation)**
```
Spring panggil constructor setiap class:
→ new PaymentService()       // tidak butuh dependency lain
→ new EmailService()         // tidak butuh dependency lain  
→ new OrderRepository()      // tidak butuh dependency lain
→ new OrderService(???)      // BUTUH PaymentService & EmailService dulu!
```

**Step 3 — Selesaikan Dependency (Dependency Resolution)**
```
Spring periksa constructor OrderService:
→ Butuh PaymentService? ✓ Sudah ada di registry
→ Butuh EmailService?   ✓ Sudah ada di registry
→ Inject keduanya ke OrderService constructor
→ new OrderService(paymentService, emailService) ✓
```

**Step 4 — Simpan ke Registry**
```
Semua Bean disimpan:
→ "orderService" → instance OrderService
→ "paymentService" → instance PaymentService
→ dst.
```

**Step 5 — Siap Dipakai**
```
ctx.getBean(OrderService.class)
→ Spring ambil dari registry
→ Kembalikan instance yang sudah jadi
```

---

## 5. Bean dan Lifecycle-nya

### Apa itu Bean?

Bean = objek Java yang **hidupnya dikelola** oleh Spring IoC Container. Bedanya dengan objek biasa:

```
Objek biasa:
  new PaymentService()  → kamu buat → kamu pakai → garbage collected (kalau tidak ada reference)

Spring Bean:
  @Component            → Spring buat → Spring kelola → Spring hancurkan saat container tutup
  PaymentService        
```

### Lifecycle Bean Secara Detail

```
1. INSTANTIATION
   Spring panggil constructor:
   → new OrderService(paymentService, emailService)

2. POPULATE PROPERTIES
   Spring inject semua @Autowired setter/field (kalau ada)

3. AWARE INTERFACES (opsional)
   Jika Bean implement BeanNameAware, ApplicationContextAware, dll
   → Spring panggil setter yang sesuai

4. POST-PROCESSING (BeanPostProcessor)
   Spring jalankan pre-init processors
   → Ini yang dipakai AOP, @Transactional, dll

5. INITIALIZATION
   Jalankan @PostConstruct method (kalau ada):
   
   @PostConstruct
   public void init() {
       System.out.println("Bean siap digunakan!");
       // Cocok untuk: load config, buka koneksi, validasi
   }

6. BEAN SIAP DIGUNAKAN ✓
   Bean ada di container, siap di-inject ke mana saja

7. DESTRUCTION (saat container ditutup)
   Jalankan @PreDestroy method (kalau ada):
   
   @PreDestroy
   public void cleanup() {
       System.out.println("Bean akan dihancurkan!");
       // Cocok untuk: tutup koneksi, simpan state, cleanup resource
   }
```

### Contoh Lifecycle Hook

```java
@Component
public class DatabaseConnectionPool {
    
    private Connection connection;
    
    // Dipanggil SETELAH constructor dan injection selesai
    @PostConstruct
    public void openConnection() {
        System.out.println("Membuka koneksi database...");
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost/mydb");
    }
    
    // Dipanggil SEBELUM Bean dihancurkan
    @PreDestroy
    public void closeConnection() {
        System.out.println("Menutup koneksi database...");
        if (connection != null) connection.close();
    }
}
```

### Nama Bean

Secara default, nama Bean = nama class dengan huruf pertama kecil:

```java
@Service
public class OrderService {}
// Nama Bean: "orderService"

@Component
public class EmailNotificationService {}
// Nama Bean: "emailNotificationService"

// Kamu bisa override nama:
@Component("emailSvc")
public class EmailNotificationService {}
// Nama Bean: "emailSvc"
```

---

## 6. Bean Scope

Scope menentukan **berapa banyak instance** yang dibuat untuk sebuah Bean.

### Singleton (Default)

```java
@Component
// @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // tidak perlu, ini default
public class PaymentGateway {
    private int transactionCount = 0;
    
    public void process() {
        transactionCount++;
        System.out.println("Transaksi ke-" + transactionCount);
    }
}
```

**Karakteristik Singleton:**
- Hanya **satu instance** per ApplicationContext
- Semua Bean yang inject `PaymentGateway` dapat **objek yang sama**
- Dibuat saat container startup (eager loading)
- State di dalam Bean **berbagi** ke semua yang pakai

```java
// Bukti singleton:
PaymentGateway pg1 = ctx.getBean(PaymentGateway.class);
PaymentGateway pg2 = ctx.getBean(PaymentGateway.class);
System.out.println(pg1 == pg2); // true — objek yang SAMA

pg1.process(); // "Transaksi ke-1"
pg2.process(); // "Transaksi ke-2" — shared state!
```

> ⚠️ **Perhatian:** Karena state-nya berbagi, jangan simpan data user-spesifik (seperti data session) di Singleton Bean!

### Prototype

```java
@Component
@Scope("prototype")
public class ShoppingCart {
    private List<String> items = new ArrayList<>();
    
    public void addItem(String item) {
        items.add(item);
    }
    
    public List<String> getItems() {
        return items;
    }
}
```

**Karakteristik Prototype:**
- **Instance baru** setiap kali di-inject atau di-request
- State **tidak berbagi** antar pemakai
- Spring **tidak** manage lifecycle setelah delivery (tidak ada @PreDestroy)

```java
ShoppingCart cart1 = ctx.getBean(ShoppingCart.class);
ShoppingCart cart2 = ctx.getBean(ShoppingCart.class);
System.out.println(cart1 == cart2); // false — objek BERBEDA

cart1.addItem("Laptop");
System.out.println(cart2.getItems()); // [] — kosong, tidak berbagi!
```

### Scope Web (Hanya untuk Aplikasi Web)

```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestLogger {
    // Satu instance per HTTP request
    // Otomatis dihancurkan saat request selesai
}

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserPreferences {
    // Satu instance per HTTP session (per user)
    // Dihancurkan saat session expired/logout
}
```

### Kapan Pakai Scope Apa?

| Scope | Gunakan untuk |
|---|---|
| `singleton` | Service, Repository, Controller — stateless |
| `prototype` | Objek yang butuh state berbeda per pemakai (form, cart) |
| `request` | Data yang hidup hanya 1 HTTP request |
| `session` | Data user yang perlu persist antar request (login info) |

---

## 7. ApplicationContext vs BeanFactory

### BeanFactory

Antarmuka paling dasar dari Spring Container. Hanya mendukung fitur inti: membuat Bean dan menyuntikkan dependency.

```java
// Jarang dipakai langsung
BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
MyService service = factory.getBean("myService", MyService.class);
```

### ApplicationContext

Extends `BeanFactory` dengan banyak fitur tambahan. **Ini yang selalu kamu pakai.**

```java
// Implementasi utama ApplicationContext:

// 1. Untuk konfigurasi berbasis Java (modern)
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

// 2. Untuk konfigurasi XML
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

// 3. Untuk web application
// (Biasanya dibuat otomatis oleh Spring MVC / Spring Boot)
```

### Perbandingan Fitur

| Fitur | BeanFactory | ApplicationContext |
|---|---|---|
| Bean instantiation & wiring | ✅ | ✅ |
| Bean lifecycle management | ✅ | ✅ |
| Lazy loading (default) | ✅ Lazy | ❌ Eager (bean dibuat saat startup) |
| Internationalization (i18n) | ❌ | ✅ |
| Event propagation | ❌ | ✅ |
| AOP integration | ❌ | ✅ |
| Environment abstraction | ❌ | ✅ |

> **Kesimpulan:** Selalu gunakan `ApplicationContext`. `BeanFactory` hanya untuk environment dengan resource sangat terbatas (embedded systems).

---

# BAGIAN 2 — KONFIGURASI

## 8. Setup Proyek Maven

Buat proyek Maven baru dengan struktur ini:

```
spring-hello-world/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── belajar/
        │           ├── config/
        │           │   ├── AppConfigXml.java       (untuk load XML)
        │           │   └── AppConfigJava.java      (untuk Java config)
        │           ├── controller/
        │           │   └── HelloController.java
        │           ├── service/
        │           │   ├── GreetingService.java    (interface)
        │           │   └── GreetingServiceImpl.java
        │           ├── MainXml.java                (jalankan versi XML)
        │           └── MainAnnotation.java         (jalankan versi Annotation)
        └── resources/
            └── applicationContext.xml
```

### pom.xml Lengkap

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.belajar</groupId>
    <artifactId>spring-hello-world</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>17</java.version>
        <spring.version>6.1.4</spring.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    
    <dependencies>
        <!-- Spring Context: inti Spring, sudah include spring-core, spring-beans, spring-aop -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Logging (agar tidak banyak warning di console) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.9</version>
        </dependency>
    </dependencies>
    
</project>
```

> **Kenapa hanya `spring-context`?** Dependency graph-nya:
> `spring-context` → `spring-aop` → `spring-beans` → `spring-core`
> Satu dependency ini sudah menarik semua yang kita butuhkan.

---

## 9. Konfigurasi XML — Cara Klasik

Cara ini digunakan sebelum era anotasi (sebelum Spring 2.5). Penting untuk dipahami karena banyak proyek lama masih menggunakannya.

### Interface GreetingService

```java
// src/main/java/com/belajar/service/GreetingService.java
package com.belajar.service;

public interface GreetingService {
    String greet(String name);
}
```

### Implementasi GreetingServiceImpl

```java
// src/main/java/com/belajar/service/GreetingServiceImpl.java
package com.belajar.service;

// PERHATIKAN: Tidak ada anotasi Spring sama sekali!
// Class ini pure Java, Spring manage-nya dari XML
public class GreetingServiceImpl implements GreetingService {
    
    private String greeting; // Akan di-inject dari XML
    
    // Spring butuh setter untuk property injection dari XML
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
    
    @Override
    public String greet(String name) {
        return greeting + ", " + name + "!";
    }
}
```

### HelloController (Pure Java, tanpa anotasi)

```java
// src/main/java/com/belajar/controller/HelloController.java
package com.belajar.controller;

import com.belajar.service.GreetingService;

// Pure Java — tidak ada anotasi Spring
public class HelloController {
    
    private GreetingService greetingService; // Akan di-inject dari XML
    
    // Spring butuh setter ini untuk property injection
    public void setGreetingService(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
    
    public void handleRequest(String name) {
        String result = greetingService.greet(name);
        System.out.println("Controller menerima: " + result);
    }
}
```

### applicationContext.xml

Buat file ini di `src/main/resources/applicationContext.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--
        Mendefinisikan Bean greetingService
        id     = nama Bean (dipakai untuk referensi)
        class  = fully qualified class name
    -->
    <bean id="greetingService" class="com.belajar.service.GreetingServiceImpl">
        <!-- Property injection: panggil setGreeting("Halo") -->
        <property name="greeting" value="Halo"/>
    </bean>

    <!--
        Mendefinisikan Bean helloController
        Dengan dependency ke greetingService di atas
    -->
    <bean id="helloController" class="com.belajar.controller.HelloController">
        <!-- Property injection dengan referensi ke Bean lain -->
        <property name="greetingService" ref="greetingService"/>
    </bean>

</beans>
```

**Penjelasan tag XML:**

```xml
<!-- Property injection dengan nilai literal -->
<property name="greeting" value="Halo"/>
<!-- Ini setara dengan: greetingServiceImpl.setGreeting("Halo") -->

<!-- Property injection dengan referensi ke Bean lain -->
<property name="greetingService" ref="greetingService"/>
<!-- Ini setara dengan: helloController.setGreetingService(ctx.getBean("greetingService")) -->

<!-- Constructor injection di XML -->
<bean id="orderService" class="com.belajar.service.OrderService">
    <constructor-arg ref="paymentService"/>
    <constructor-arg ref="emailService"/>
</bean>
```

### Main untuk XML Config

```java
// src/main/java/com/belajar/MainXml.java
package com.belajar;

import com.belajar.controller.HelloController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainXml {
    public static void main(String[] args) {
        
        System.out.println("=== Versi XML Configuration ===");
        
        // Load XML config dari classpath (src/main/resources/)
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        
        // Ambil Bean berdasarkan ID yang didefinisikan di XML
        HelloController controller = (HelloController) context.getBean("helloController");
        
        // Atau lebih aman dengan type-safe:
        // HelloController controller = context.getBean("helloController", HelloController.class);
        
        controller.handleRequest("Budi");
        controller.handleRequest("Siti");
        
        // Tutup context untuk trigger @PreDestroy (best practice)
        ((ClassPathXmlApplicationContext) context).close();
    }
}
```

**Output:**
```
=== Versi XML Configuration ===
Controller menerima: Halo, Budi!
Controller menerima: Halo, Siti!
```

---

## 10. Konfigurasi Annotation — Cara Modern

Cara ini diperkenalkan di Spring 2.5 (2007) dan menjadi standar de facto.

### GreetingServiceImpl dengan Anotasi

```java
// src/main/java/com/belajar/service/GreetingServiceImpl.java
package com.belajar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service  // Tandai sebagai Bean — Spring akan buat instance ini
public class GreetingServiceImpl implements GreetingService {
    
    // @Value inject nilai literal atau dari properties file
    @Value("Halo")
    private String greeting;
    
    @Override
    public String greet(String name) {
        return greeting + ", " + name + "!";
    }
}
```

### HelloController dengan Anotasi

```java
// src/main/java/com/belajar/controller/HelloController.java
package com.belajar.controller;

import com.belajar.service.GreetingService;
import org.springframework.stereotype.Component;

@Component  // Atau @Controller jika pakai Spring MVC
public class HelloController {
    
    private final GreetingService greetingService;
    
    // Constructor injection — Spring inject otomatis
    // @Autowired tidak perlu ditulis jika hanya ada 1 constructor (Spring 4.3+)
    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
    
    public void handleRequest(String name) {
        String result = greetingService.greet(name);
        System.out.println("Controller menerima: " + result);
    }
}
```

### AppConfig untuk Annotation

```java
// src/main/java/com/belajar/config/AppConfigAnnotation.java
package com.belajar.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.belajar")  // Scan seluruh package com.belajar
public class AppConfigAnnotation {
    // Tidak perlu isi apa-apa — @ComponentScan yang bekerja
}
```

### Main untuk Annotation Config

```java
// src/main/java/com/belajar/MainAnnotation.java
package com.belajar;

import com.belajar.config.AppConfigAnnotation;
import com.belajar.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainAnnotation {
    public static void main(String[] args) {
        
        System.out.println("=== Versi Annotation Configuration ===");
        
        // Buat context dari Java config class
        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfigAnnotation.class);
        
        // Ambil Bean berdasarkan type (type-safe, tidak perlu cast)
        HelloController controller = context.getBean(HelloController.class);
        
        controller.handleRequest("Budi");
        controller.handleRequest("Siti");
        
        context.close();
    }
}
```

**Output:**
```
=== Versi Annotation Configuration ===
Controller menerima: Halo, Budi!
Controller menerima: Halo, Siti!
```

---

## 11. Konfigurasi Java-Based Configuration

Cara ini menggunakan `@Configuration` class dengan `@Bean` method. Paling fleksibel karena full Java — ada type checking, IDE support, dan bisa define Bean dari library pihak ketiga.

```java
// src/main/java/com/belajar/config/AppConfigJava.java
package com.belajar.config;

import com.belajar.controller.HelloController;
import com.belajar.service.GreetingService;
import com.belajar.service.GreetingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigJava {
    
    // @Bean method — Spring panggil ini untuk membuat Bean
    // Nama Bean = nama method (greetingService)
    @Bean
    public GreetingService greetingService() {
        GreetingServiceImpl service = new GreetingServiceImpl();
        service.setGreeting("Halo");
        return service;
    }
    
    @Bean
    public HelloController helloController() {
        // Spring inject greetingService() secara otomatis
        // Kalau greetingService sudah dibuat (singleton), tidak dibuat ulang
        return new HelloController(greetingService());
    }
    
    // Contoh: Bean dari library pihak ketiga yang tidak bisa kita tambah anotasi
    // @Bean
    // public ObjectMapper objectMapper() {
    //     return new ObjectMapper(); // Jackson library
    // }
}
```

### Main untuk Java Config

```java
package com.belajar;

import com.belajar.config.AppConfigJava;
import com.belajar.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainJavaConfig {
    public static void main(String[] args) {
        
        System.out.println("=== Versi Java Configuration ===");
        
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(AppConfigJava.class);
        
        HelloController controller = context.getBean(HelloController.class);
        controller.handleRequest("Budi");
        
        context.close();
    }
}
```

---

## 12. Perbandingan XML vs Annotation vs Java Config

### Tabel Perbandingan

| Aspek | XML Config | Annotation Config | Java Config (@Bean) |
|---|---|---|---|
| **Cara define Bean** | `<bean>` tag di XML | `@Component` di class | `@Bean` method di `@Configuration` |
| **Cara inject** | `<property ref="...">` | `@Autowired` | Via constructor/setter di @Bean method |
| **Modifikasi class?** | ❌ Tidak perlu | ✅ Perlu tambah anotasi | ❌ Tidak perlu |
| **Type safety** | ❌ String-based (typo = error runtime) | ✅ Compile-time | ✅ Compile-time |
| **IDE Support** | 🟡 Terbatas | ✅ Penuh | ✅ Penuh |
| **Refactoring** | ❌ Perlu update XML manual | ✅ IDE bisa auto-refactor | ✅ IDE bisa auto-refactor |
| **Konfigurasi external** | ✅ Mudah (tidak compile ulang) | 🟡 Perlu @Value | 🟡 Perlu @Value / Environment |
| **Library pihak ketiga** | ✅ Bisa (tanpa modifikasi) | ❌ Tidak bisa (butuh akses source) | ✅ Bisa (tanpa modifikasi) |
| **Readability** | 🟡 Verbose, tapi eksplisit | ✅ Ringkas | ✅ Ringkas & fleksibel |
| **Dipakai di proyek baru** | ❌ Jarang | ✅ Umum (gabung dengan Java config) | ✅ Umum |

### Pros dan Cons Detail

#### XML Configuration
```
✅ PROS:
  - Semua konfigurasi terpusat di satu tempat (mudah lihat gambaran besar)
  - Bisa ganti konfigurasi tanpa recompile (tinggal edit XML)
  - Cocok untuk konfigurasi yang sering berubah (environment-specific)
  - Bisa define Bean dari class yang tidak bisa kamu modifikasi

❌ CONS:
  - Verbose — kode yang panjang dan berulang
  - Tidak type-safe — salah nulis nama class baru ketahuan saat runtime
  - Refactoring kelas → harus ingat update XML secara manual
  - Kurva belajar lebih tinggi (harus hafal tag XML)
```

#### Annotation Configuration
```
✅ PROS:
  - Ringkas — cukup @Service, @Autowired, selesai
  - Dekat dengan kode — konfigurasi ada di class yang relevan
  - Type-safe
  - IDE support penuh (completion, navigation, refactoring)

❌ CONS:
  - Konfigurasi tersebar di banyak file
  - Coupling antara business code dan Spring (class jadi "tahu" tentang Spring)
  - Tidak bisa pakai untuk class dari library luar
  - Harder to see the "big picture" konfigurasi aplikasi
```

#### Java-Based Configuration
```
✅ PROS:
  - Fleksibel — full Java, bisa pakai kondisi, loop, dll
  - Type-safe dan IDE support penuh
  - Bisa define Bean dari library luar
  - Bisa gabung dengan annotation config

❌ CONS:
  - Lebih verbose dibanding annotation
  - Masih harus tahu tentang dependency graph manual
```

### Rekomendasi di Dunia Nyata

```
Proyek Baru (Spring Boot):
  → Pakai Annotation (@Service, @Repository, @Component) untuk class milik sendiri
  → Pakai Java Config (@Bean) untuk library pihak ketiga atau konfigurasi kompleks
  → XML? Hampir tidak pernah

Proyek Lama (Legacy):
  → Mungkin masih full XML
  → Migrasi bertahap ke annotation/Java config adalah umum
  → Keduanya bisa dipakai bersamaan!
```

---

# BAGIAN 3 — LATIHAN HELLO WORLD

## 13. Latihan: Hello World Spring Application

Sekarang kita praktikkan semua yang sudah dipelajari dengan membuat **Hello World** yang mengimplementasikan kedua pendekatan konfigurasi.

### Skenario Latihan

Kita buat aplikasi greeting sederhana dengan arsitektur berlapis:
```
HelloController → GreetingService → (output ke console)
```

**Tujuan latihan:**
1. Jalankan dengan XML config
2. Jalankan dengan Annotation config
3. Bandingkan kode keduanya
4. Eksperimen: ganti implementasi GreetingService tanpa ubah Controller

### Langkah 1: Buat Interface

```java
// src/main/java/com/belajar/service/GreetingService.java
package com.belajar.service;

public interface GreetingService {
    String greet(String name);
    String farewell(String name);
}
```

### Langkah 2: Buat 2 Implementasi (untuk eksperimen penggantian)

```java
// IndonesianGreetingService.java
package com.belajar.service;

public class IndonesianGreetingService implements GreetingService {
    
    @Override
    public String greet(String name) {
        return "Selamat pagi, " + name + "!";
    }
    
    @Override
    public String farewell(String name) {
        return "Sampai jumpa, " + name + "!";
    }
}
```

```java
// EnglishGreetingService.java
package com.belajar.service;

public class EnglishGreetingService implements GreetingService {
    
    @Override
    public String greet(String name) {
        return "Good morning, " + name + "!";
    }
    
    @Override
    public String farewell(String name) {
        return "Goodbye, " + name + "!";
    }
}
```

### Langkah 3: Buat Controller

```java
// HelloController.java
package com.belajar.controller;

import com.belajar.service.GreetingService;

public class HelloController {
    
    private final GreetingService greetingService;
    
    // Constructor injection
    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
        System.out.println("[HelloController] Dibuat dengan: " + 
                           greetingService.getClass().getSimpleName());
    }
    
    public void greetUser(String name) {
        System.out.println(greetingService.greet(name));
    }
    
    public void farewellUser(String name) {
        System.out.println(greetingService.farewell(name));
    }
}
```

### Langkah 4a: Jalankan dengan XML Config

**applicationContext.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Coba ganti class ini ke EnglishGreetingService — Controller tidak perlu diubah! -->
    <bean id="greetingService" class="com.belajar.service.IndonesianGreetingService"/>

    <bean id="helloController" class="com.belajar.controller.HelloController">
        <constructor-arg ref="greetingService"/>
    </bean>

</beans>
```

**MainXml.java:**
```java
package com.belajar;

import com.belajar.controller.HelloController;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainXml {
    public static void main(String[] args) {
        System.out.println("▶ Menjalankan Hello World dengan XML Config");
        System.out.println("─".repeat(45));
        
        try (ClassPathXmlApplicationContext ctx = 
                new ClassPathXmlApplicationContext("applicationContext.xml")) {
            
            HelloController controller = ctx.getBean("helloController", HelloController.class);
            
            controller.greetUser("Budi");
            controller.greetUser("Siti");
            controller.farewellUser("Budi");
        }
        // try-with-resources otomatis panggil ctx.close() → trigger @PreDestroy
    }
}
```

**Output:**
```
▶ Menjalankan Hello World dengan XML Config
─────────────────────────────────────────────
[HelloController] Dibuat dengan: IndonesianGreetingService
Selamat pagi, Budi!
Selamat pagi, Siti!
Sampai jumpa, Budi!
```

### Langkah 4b: Jalankan dengan Annotation Config

Tambahkan anotasi ke class:

```java
// IndonesianGreetingService.java
package com.belajar.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary  // Jika ada 2+ implementasi, pakai yang ini sebagai default
public class IndonesianGreetingService implements GreetingService {
    // ... isi sama seperti sebelumnya
}
```

```java
// HelloController.java
package com.belajar.controller;

import com.belajar.service.GreetingService;
import org.springframework.stereotype.Component;

@Component
public class HelloController {
    
    private final GreetingService greetingService;
    
    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
        System.out.println("[HelloController] Dibuat dengan: " + 
                           greetingService.getClass().getSimpleName());
    }
    
    public void greetUser(String name) {
        System.out.println(greetingService.greet(name));
    }
    
    public void farewellUser(String name) {
        System.out.println(greetingService.farewell(name));
    }
}
```

```java
// AppConfig.java
package com.belajar.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.belajar")
public class AppConfig {}
```

```java
// MainAnnotation.java
package com.belajar;

import com.belajar.config.AppConfig;
import com.belajar.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainAnnotation {
    public static void main(String[] args) {
        System.out.println("▶ Menjalankan Hello World dengan Annotation Config");
        System.out.println("─".repeat(50));
        
        try (AnnotationConfigApplicationContext ctx = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            HelloController controller = ctx.getBean(HelloController.class);
            
            controller.greetUser("Budi");
            controller.greetUser("Siti");
            controller.farewellUser("Budi");
        }
    }
}
```

---

## 14. Inject Service ke Controller

### Skenario: Beberapa Bean yang Saling Terhubung

```java
// UserRepository.java
package com.belajar.repository;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    
    // Simulasi database sederhana
    private final Map<String, String> users = new HashMap<>();
    
    public UserRepository() {
        users.put("budi123", "Budi Santoso");
        users.put("siti456", "Siti Rahayu");
    }
    
    public String findNameById(String id) {
        return users.getOrDefault(id, "Unknown User");
    }
}
```

```java
// UserService.java
package com.belajar.service;

import com.belajar.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final GreetingService greetingService;
    
    // Constructor injection dari 2 dependency sekaligus
    public UserService(UserRepository userRepository, GreetingService greetingService) {
        this.userRepository = userRepository;
        this.greetingService = greetingService;
    }
    
    public String getPersonalizedGreeting(String userId) {
        String name = userRepository.findNameById(userId);
        return greetingService.greet(name);
    }
}
```

```java
// UserController.java
package com.belajar.controller;

import com.belajar.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    public void handleLoginRequest(String userId) {
        String greeting = userService.getPersonalizedGreeting(userId);
        System.out.println(">>> " + greeting);
    }
}
```

```java
// MainLayered.java
package com.belajar;

import com.belajar.config.AppConfig;
import com.belajar.controller.UserController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainLayered {
    public static void main(String[] args) {
        System.out.println("▶ Aplikasi Berlapis: Controller → Service → Repository");
        System.out.println("─".repeat(55));
        
        try (AnnotationConfigApplicationContext ctx = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            UserController controller = ctx.getBean(UserController.class);
            
            controller.handleLoginRequest("budi123");
            controller.handleLoginRequest("siti456");
            controller.handleLoginRequest("unknown");
        }
    }
}
```

**Output:**
```
▶ Aplikasi Berlapis: Controller → Service → Repository
───────────────────────────────────────────────────────
>>> Selamat pagi, Budi Santoso!
>>> Selamat pagi, Siti Rahayu!
>>> Selamat pagi, Unknown User!
```

### Visualisasi Dependency Graph

```
UserController
    └─── UserService
              ├─── UserRepository  (tidak punya dependency lain)
              └─── GreetingService (IndonesianGreetingService)
                                   (tidak punya dependency lain)
```

Spring membangun graph ini secara otomatis, mendeteksi urutan yang benar, dan membuat semua Bean dalam urutan yang tepat (leaves first, root last).

---

## 15. Apa yang Terjadi di Spring Boot?

Sekarang kamu punya fondasi untuk memahami Spring Boot dengan benar.

### Spring Boot = Spring + Auto-Configuration + Starter POMs

```java
// Ini yang kamu tulis:
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

### Breakdown `@SpringBootApplication`

```java
// @SpringBootApplication adalah shortcut dari:
@Configuration
//  → Ini adalah @Configuration class, bisa punya @Bean methods

@EnableAutoConfiguration
//  → Spring baca semua META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
//  → Temukan 100+ auto-configuration class
//  → Cek kondisi: @ConditionalOnClass, @ConditionalOnProperty, dll
//  → Aktifkan yang sesuai (misal: ada HikariCP di classpath → auto-config DataSource)

@ComponentScan
//  → Scan package class ini dan semua sub-package
//  → Temukan @Component, @Service, @Repository, @Controller
//  → Daftarkan ke ApplicationContext
```

### Breakdown `SpringApplication.run()`

```java
SpringApplication.run(MyApp.class, args);

// Di balik layar:
// 1. Buat SpringApplication instance
// 2. Tentukan tipe ApplicationContext (web/non-web)
// 3. Buat ApplicationContext (AnnotationConfigServletWebServerApplicationContext untuk web)
// 4. Prepare context: register MyApp.class sebagai configuration
// 5. Refresh context:
//    a. Scan @Component
//    b. Buat semua Bean
//    c. Jalankan auto-configuration
//    d. Inject semua dependency
// 6. Untuk web app: start embedded Tomcat/Netty
// 7. Panggil CommandLineRunner / ApplicationRunner beans (jika ada)
// 8. Aplikasi siap!
```

### Perbandingan: Spring Core vs Spring Boot

| | Spring Core (murni) | Spring Boot |
|---|---|---|
| Setup | Manual (buat AppConfig, pom.xml manual) | `spring-boot-starter-*` |
| Container | Buat sendiri `new AnnotationConfigApplicationContext(...)` | `SpringApplication.run()` |
| Auto-config | ❌ | ✅ (HikariCP, JPA, MVC, dll otomatis) |
| Web server | Setup manual (Tomcat terpisah) | Embedded (Tomcat/Jetty/Netty) |
| Properties | Manual | `application.properties` / `application.yml` |
| Magic? | Tidak ada magic | Auto-config = kondisi + Bean yang kamu sudah paham |

---

## 16. Ringkasan & Checklist Belajar

### Konsep Kunci

| Konsep | Definisi Singkat |
|---|---|
| **IoC** | Kontrol pembuatan objek dipindah ke Spring Container |
| **DI** | Cara implementasi IoC: dependency "disuntikkan" dari luar |
| **Bean** | Objek Java yang dibuat dan dikelola Spring |
| **ApplicationContext** | Spring IoC Container yang kamu pakai |
| **@Component** | Tandai class agar di-scan dan dijadikan Bean |
| **@Service** | @Component dengan label "business logic" |
| **@Repository** | @Component dengan label "data access" |
| **@Controller** | @Component dengan label "web layer" |
| **@Autowired** | Minta Spring untuk inject dependency |
| **@Configuration** | Tandai class sebagai sumber konfigurasi Spring |
| **@Bean** | Method yang menghasilkan Bean untuk dikelola Spring |
| **@ComponentScan** | Perintahkan Spring untuk scan package tertentu |
| **@Primary** | Jika ada 2+ implementasi interface, gunakan yang ini |
| **@Qualifier** | Pilih Bean spesifik saat ada 2+ kandidat |
| **Singleton** | Default scope: 1 instance per container |
| **Prototype** | Scope: instance baru setiap kali di-request |
| **@PostConstruct** | Method yang dijalankan setelah Bean dibuat |
| **@PreDestroy** | Method yang dijalankan sebelum Bean dihancurkan |

### ✅ Checklist Pemahaman

Tandai jika sudah bisa menjawab atau lakukan:

**Teori:**
- [ ] Aku bisa menjelaskan IoC dengan analogi sederhana kepada orang lain
- [ ] Aku mengerti perbedaan IoC (prinsip) vs DI (implementasi)
- [ ] Aku bisa menjelaskan perbedaan Constructor Injection vs Field Injection
- [ ] Aku tahu kenapa Constructor Injection lebih baik
- [ ] Aku mengerti apa itu Bean Lifecycle (@PostConstruct, @PreDestroy)
- [ ] Aku bisa menjelaskan perbedaan Singleton vs Prototype scope

**Konfigurasi:**
- [ ] Aku bisa membuat Bean dengan XML configuration
- [ ] Aku bisa membuat Bean dengan @Component annotation
- [ ] Aku bisa membuat Bean dengan @Configuration + @Bean
- [ ] Aku mengerti kapan pakai XML vs Annotation vs Java config
- [ ] Aku bisa membuat ApplicationContext dari XML dan dari Java config

**Praktik:**
- [ ] Aku berhasil menjalankan Hello World dengan XML config
- [ ] Aku berhasil menjalankan Hello World dengan Annotation config
- [ ] Aku bisa mengganti implementasi service tanpa ubah controller
- [ ] Aku bisa membuat arsitektur berlapis (Controller → Service → Repository)

**Spring Boot:**
- [ ] Aku mengerti bahwa @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
- [ ] Aku mengerti bahwa Spring Boot bukan magic, hanya otomasi dari konsep Spring Core

### Pertanyaan untuk Refleksi Diri

1. Jika kamu punya `UserService` yang butuh `EmailService` dan `UserRepository`, bagaimana Spring tahu harus inject yang mana?

2. Mengapa kita membuat interface (`GreetingService`) daripada langsung inject `IndonesianGreetingService`?

3. Jika kamu punya Bean Singleton yang menyimpan `List<String> history`, apa yang terjadi ketika 2 user mengakses bersamaan?

4. Apa yang terjadi jika kamu punya 2 Bean yang implement interface yang sama dan tidak ada `@Primary` atau `@Qualifier`?

---

*Selamat! Kamu sekarang memiliki pemahaman yang solid tentang Spring Core. Tidak ada lagi yang terasa "magic" di Spring Boot — semua hanyalah otomasi dari konsep yang sudah kamu kuasai.* 🚀
