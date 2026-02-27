# 1. IoC, DI And Lifecycle Bean

# Panduan Lengkap Spring Core: IoC dan DI

## Daftar Isi

1. [Pendahuluan](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#pendahuluan)
2. [Dasar Spring Framework](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#dasar-spring-framework)
3. [Inversion of Control (IoC)](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#inversion-of-control-ioc)
4. [Dependency Injection (DI)](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#dependency-injection-di)
5. [IoC Container di Spring](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#ioc-container-di-spring)
6. [Konfigurasi Spring](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#konfigurasi-spring)
7. [Jenis Dependency Injection](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#jenis-dependency-injection)
8. [Bean Scopes](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#bean-scopes)
9. [Siklus Hidup Bean](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#siklus-hidup-bean)
10. [Autowiring](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#autowiring)
11. [Best Practices](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#best-practices)
12. [Contoh Project Lengkap](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#contoh-project-lengkap)
13. [FAQ](https://claude.ai/chat/86594a8a-c51d-453b-b2aa-112d414e123d#faq)

## Pendahuluan

Spring Framework adalah salah satu framework Java paling populer yang digunakan untuk pengembangan aplikasi enterprise. Dalam panduan ini, kita akan fokus pada Spring Core, yang merupakan fondasi utama dari keseluruhan Spring Framework. Khususnya, kita akan menjelajahi dua konsep fundamental: *Inversion of Control (IoC)* dan *Dependency Injection (DI)*.

## Dasar Spring Framework

Sebelum kita mendalami IoC dan DI, mari pahami komponen utama Spring Framework:

- **Spring Core**: Menyediakan implementasi IoC dan DI
- **Spring AOP**: Pemrograman berorientasi aspek
- **Spring DAO**: Data Access Object untuk interaksi database
- **Spring Context**: Kerangka aplikasi berdasarkan Spring Core
- **Spring Web**: Framework web MVC
- **Spring ORM**: Integrasi dengan ORM seperti Hibernate

Spring Framework dibangun dengan prinsip-prinsip berikut:

- Lightweight (ringan)
- Inversion of Control
- Aspect-Oriented Programming
- Container
- Framework
- Integrasi dengan framework lain

## Inversion of Control (IoC)

### Pengertian IoC

Inversion of Control adalah prinsip desain yang "membalikkan" kontrol dari aplikasi ke framework. Dalam paradigma tradisional, kode yang Anda tulis mengendalikan alur program dan menciptakan semua objek yang diperlukan. Dengan IoC, framework (dalam hal ini Spring) yang mengendalikan alur dan menciptakan objek.

IoC dapat dianalogikan seperti restoran:

- Pendekatan tradisional: Anda datang ke dapur restoran dan memasak sendiri
- Pendekatan IoC: Anda duduk di meja dan pelayan yang mengurus kebutuhan Anda

### Keuntungan IoC

1. **Decouple antara komponen**: Mengurangi ketergantungan langsung antar komponen
2. **Kode lebih modular**: Komponen lebih mandiri dan bisa digunakan kembali
3. **Memudahkan testing**: Komponen dapat diuji secara individual
4. **Konfigurasi terpusat**: Konfigurasi aplikasi di satu tempat
5. **Manajemen resources lebih baik**: Pengelolaan resource seperti koneksi database lebih efisien

### Contoh Ilustrasi IoC

Tanpa IoC:

```java
public class MyApplication {
    private MyService service = new MyServiceImpl(); // Hard dependency

    public void doSomething() {
        service.performAction();
    }
}

```

Dengan IoC:

```java
public class MyApplication {
    private MyService service; // Tidak ada instantiasi langsung

    // Service akan diinjeksi oleh container
    public MyApplication(MyService service) {
        this.service = service;
    }

    public void doSomething() {
        service.performAction();
    }
}

```

## Dependency Injection (DI)

### Pengertian DI

Dependency Injection adalah implementasi spesifik dari IoC, di mana objek-objek tidak menciptakan dependensinya sendiri, tetapi menerima dependensi tersebut dari luar (diinjeksi). DI membuat kode lebih bersih, lebih mudah diuji, dan lebih fleksibel.

### Prinsip Dasar DI

1. **High-level modules** tidak boleh bergantung pada **low-level modules**. Keduanya harus bergantung pada **abstraksi**
2. **Abstraksi** tidak boleh bergantung pada detail. **Detail** harus bergantung pada **abstraksi**

Ini konsisten dengan prinsip SOLID, khususnya Dependency Inversion Principle (DIP).

### Tanpa DI vs Dengan DI

**Tanpa DI**:

```java
public class EmailService {
    public void sendEmail(String to, String subject) {
        // Logic untuk mengirim email
    }
}

public class UserService {
    private EmailService emailService = new EmailService(); // Hard dependency

    public void registerUser(String email) {
        // Logika registrasi
        emailService.sendEmail(email, "Welcome!");
    }
}

```

**Dengan DI**:

```java
public interface MessageService {
    void sendMessage(String to, String subject);
}

public class EmailService implements MessageService {
    @Override
    public void sendMessage(String to, String subject) {
        // Logic untuk mengirim email
    }
}

public class SMSService implements MessageService {
    @Override
    public void sendMessage(String to, String subject) {
        // Logic untuk mengirim SMS
    }
}

public class UserService {
    private final MessageService messageService; // Dependency pada abstraksi

    // Dependency diinjeksi melalui constructor
    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void registerUser(String contact) {
        // Logika registrasi
        messageService.sendMessage(contact, "Welcome!");
    }
}

```

Dengan DI, `UserService` tidak perlu tahu implementasi konkret dari `MessageService`. Implementasi dapat diganti tanpa mengubah `UserService`.

## IoC Container di Spring

IoC Container adalah inti dari Spring Framework. Container menciptakan objek, mewire mereka bersama, mengkonfigurasi mereka, dan mengelola siklus hidup mereka.

### Jenis IoC Container di Spring

Spring menyediakan dua jenis IoC Container:

1. **BeanFactory**: Interface dasar yang menyediakan mekanisme konfigurasi canggih untuk mengelola objek. BeanFactory menggunakan lazy-loading (bean dimuat hanya ketika diminta).

```java
BeanFactory factory = new XmlBeanFactory(new FileSystemResource("beans.xml"));
MyBean bean = (MyBean) factory.getBean("myBean");

```

1. **ApplicationContext**: Subinterface dari BeanFactory yang menambahkan fungsi lebih seperti integrasi AOP, event propagation, internasionalisasi, dan konteks khusus aplikasi web. ApplicationContext menggunakan eager-loading (semua singleton bean dimuat saat startup).

```java
ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
MyBean bean = context.getBean("myBean", MyBean.class);

```

### Perbedaan BeanFactory vs ApplicationContext

| BeanFactory | ApplicationContext |
| --- | --- |
| Lazy initialization | Eager initialization |
| Eksplisit menyediakan fungsi seperti resolusi pesan | Menyediakan fungsi tambahan secara implisit |
| Tidak mendukung anotasi | Mendukung anotasi |
| Tidak mendukung integrasi langsung dengan AOP | Mendukung integrasi dengan AOP |
| Cocok untuk aplikasi dengan resource terbatas | Direkomendasikan untuk sebagian besar aplikasi |

Dalam praktek, hampir selalu lebih baik menggunakan `ApplicationContext`, kecuali dalam kasus sangat spesifik dengan keterbatasan resource.

### Bean di Spring

Dalam konteks Spring, "Bean" adalah objek yang diinstansiasi, dikelola, dan dikonfigurasi oleh IoC Container. Bean dan dependensinya dideklarasikan dalam metadata konfigurasi yang digunakan container.

Karakteristik Bean:

- Harus memiliki default constructor (atau factory method)
- Properti dan dependensi didefinisikan dalam metadata
- Biasanya singleton (satu instance per container)
- Dapat dikonfigurasi dengan properti

## Konfigurasi Spring

Ada tiga cara utama untuk mengonfigurasi Spring Container:

### 1. XML-based Configuration

Ini adalah cara tradisional mengonfigurasi Spring sebelum Java 5. Semua konfigurasi diletakkan dalam file XML.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Definisi bean -->
    <bean id="messageService" class="com.example.EmailService" />

    <!-- Constructor injection -->
    <bean id="userService" class="com.example.UserService">
        <constructor-arg ref="messageService" />
    </bean>

    <!-- Setter injection -->
    <bean id="otherService" class="com.example.OtherService">
        <property name="messageService" ref="messageService" />
    </bean>
</beans>

```

### 2. Java-based Configuration

Sejak Spring 3.0, Anda dapat menggunakan konfigurasi berbasis Java dengan anotasi seperti `@Configuration` dan `@Bean`.

```java
@Configuration
public class AppConfig {

    @Bean
    public MessageService messageService() {
        return new EmailService();
    }

    @Bean
    public UserService userService() {
        // Constructor injection
        return new UserService(messageService());
    }

    @Bean
    public OtherService otherService() {
        OtherService service = new OtherService();
        // Setter injection
        service.setMessageService(messageService());
        return service;
    }
}

```

Untuk menggunakan konfigurasi ini:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
UserService userService = context.getBean("userService", UserService.class);

```

### 3. Annotation-based Configuration

Spring 2.5 memperkenalkan konfigurasi berbasis anotasi, yang memungkinkan Anda mendefinisikan bean dan dependensi menggunakan anotasi pada kelas itu sendiri.

```java
@Component
public class EmailService implements MessageService {
    @Override
    public void sendMessage(String to, String subject) {
        // Logic untuk mengirim email
    }
}

@Service
public class UserService {
    private final MessageService messageService;

    @Autowired
    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void registerUser(String email) {
        // Logika registrasi
        messageService.sendMessage(email, "Welcome!");
    }
}

```

Untuk mengaktifkan Component Scanning:

```java
@Configuration
@ComponentScan(basePackages = "com.example")
public class AppConfig {
    // Konfigurasi tambahan
}

```

Atau dengan XML:

```xml
<context:component-scan base-package="com.example" />

```

### Anotasi Stereotype

Spring menyediakan beberapa anotasi stereotype untuk mengklasifikasikan bean berdasarkan layer mereka:

- **@Component**: Anotasi umum untuk komponen Spring
- **@Service**: Untuk layer service (business logic)
- **@Repository**: Untuk layer data access
- **@Controller**: Untuk layer presentasi (web controllers)

Semua anotasi di atas pada dasarnya adalah alias dari `@Component` dengan semantik tambahan.

## Jenis Dependency Injection

Spring mendukung beberapa cara untuk menginjeksi dependensi:

### 1. Constructor Injection

Dependensi disediakan melalui konstruktor kelas. Ini adalah pendekatan yang paling direkomendasikan (terutama sejak Spring 4.3).

**Dengan XML**:

```xml
<bean id="userService" class="com.example.UserService">
    <constructor-arg ref="messageService" />
</bean>

```

**Dengan Java Config**:

```java
@Bean
public UserService userService() {
    return new UserService(messageService());
}

```

**Dengan Anotasi**:

```java
@Service
public class UserService {
    private final MessageService messageService;

    @Autowired
    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }
}

```

**Keuntungan Constructor Injection**:

- Objek dapat dibuat immutable
- Dependensi tidak dapat berubah sepanjang siklus hidup bean
- Dependensi dijamin tersedia saat objek dibuat
- Memudahkan unit testing

### 2. Setter Injection

Dependensi disediakan melalui setter methods.

**Dengan XML**:

```xml
<bean id="userService" class="com.example.UserService">
    <property name="messageService" ref="messageService" />
</bean>

```

**Dengan Java Config**:

```java
@Bean
public UserService userService() {
    UserService service = new UserService();
    service.setMessageService(messageService());
    return service;
}

```

**Dengan Anotasi**:

```java
@Service
public class UserService {
    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}

```

**Keuntungan Setter Injection**:

- Cocok untuk dependensi opsional
- Memungkinkan rekonfigurasi dependensi
- Memudahkan sub-classing

### 3. Field Injection

Dependensi diinjeksi langsung ke field menggunakan anotasi. Ini paling sederhana tetapi tidak direkomendasikan.

```java
@Service
public class UserService {
    @Autowired
    private MessageService messageService;
}

```

**Kelemahan Field Injection**:

- Sulit untuk unit testing
- Melanggar prinsip "programming to interfaces"
- Membuat objek tidak bisa immutable
- Dependensi tersembunyi (tidak dideklarasikan dalam konstruktor/setter)

### Perbandingan Ketiga Cara Injection

| Aspek | Constructor | Setter | Field |
| --- | --- | --- | --- |
| Imuabilitas | Ya | Tidak | Tidak |
| Dependensi Opsional | Sulit | Mudah | Mudah |
| Testability | Baik | Baik | Buruk |
| Circular Dependencies | Tidak didukung | Didukung | Didukung |
| Visibility | Eksplisit | Eksplisit | Implisit |
| Rekomendasi | Utama | Diutamakan untuk dependensi opsional | Dihindari |

## Bean Scopes

Spring menyediakan beberapa scope untuk bean:

### 1. Singleton (Default)

Hanya ada satu instance dari bean per IoC container. Ini adalah scope default.

```xml
<bean id="messageService" class="com.example.EmailService" scope="singleton" />

```

```java
@Bean
@Scope("singleton")
public MessageService messageService() {
    return new EmailService();
}

```

```java
@Component
@Scope("singleton")
public class EmailService implements MessageService {
    // ...
}

```

### 2. Prototype

Sebuah instance baru dibuat setiap kali bean diminta.

```xml
<bean id="messageService" class="com.example.EmailService" scope="prototype" />

```

```java
@Bean
@Scope("prototype")
public MessageService messageService() {
    return new EmailService();
}

```

```java
@Component
@Scope("prototype")
public class EmailService implements MessageService {
    // ...
}

```

### 3. Request, Session, Application, dan WebSocket (Khusus Web)

- **request**: Satu instance per HTTP request
- **session**: Satu instance per HTTP session
- **application**: Satu instance per ServletContext
- **websocket**: Satu instance per WebSocket

Untuk menggunakan scope ini, Anda perlu konfigurasi tambahan:

```java
@Bean
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public MessageService requestScopedBean() {
    return new EmailService();
}

```

### Perbedaan Utama Singleton dan Prototype

| Singleton | Prototype |
| --- | --- |
| Satu instance per container | Instance baru setiap kali diminta |
| Default scope | Harus dideklarasikan eksplisit |
| Dibuat saat container di-load (eager) | Dibuat saat diminta (lazy) |
| Dikelola oleh container sepanjang lifecycle-nya | Container hanya menciptakan instance |
| Cocok untuk stateless beans | Cocok untuk stateful beans |

## Siklus Hidup Bean

Spring Container mengelola siklus hidup bean dari inisialisasi sampai destruksi:

### 1. Inisialisasi

Ada beberapa cara untuk mengeksekusi kode saat bean diinisialisasi:

**Implementasi InitializingBean**:

```java
public class ExampleBean implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        // Kode inisialisasi
    }
}

```

**Metode kustom dengan XML**:

```xml
<bean id="exampleBean" class="com.example.ExampleBean" init-method="init" />

```

```java
public class ExampleBean {
    public void init() {
        // Kode inisialisasi
    }
}

```

**Metode kustom dengan Java Config**:

```java
@Bean(initMethod = "init")
public ExampleBean exampleBean() {
    return new ExampleBean();
}

```

**Menggunakan anotasi @PostConstruct**:

```java
public class ExampleBean {
    @PostConstruct
    public void init() {
        // Kode inisialisasi
    }
}

```

### 2. Destruksi

Sama seperti inisialisasi, ada beberapa cara untuk melakukan pembersihan sebelum bean dihancurkan:

**Implementasi DisposableBean**:

```java
public class ExampleBean implements DisposableBean {
    @Override
    public void destroy() throws Exception {
        // Kode pembersihan
    }
}

```

**Metode kustom dengan XML**:

```xml
<bean id="exampleBean" class="com.example.ExampleBean" destroy-method="cleanup" />

```

```java
public class ExampleBean {
    public void cleanup() {
        // Kode pembersihan
    }
}

```

**Metode kustom dengan Java Config**:

```java
@Bean(destroyMethod = "cleanup")
public ExampleBean exampleBean() {
    return new ExampleBean();
}

```

**Menggunakan anotasi @PreDestroy**:

```java
public class ExampleBean {
    @PreDestroy
    public void cleanup() {
        // Kode pembersihan
    }
}

```

### 3. Alur Lengkap Siklus Hidup Bean

Lifecycle bean Spring memiliki beberapa tahap penting:

1. **Instantiation**: Spring membuat instance dari bean
2. **Populate Properties**: Spring mengisi properti bean
3. **BeanNameAware**: Jika bean mengimplementasikan interface ini, Spring memanggil setBeanName()
4. **BeanFactoryAware**: Jika bean mengimplementasikan interface ini, Spring memanggil setBeanFactory()
5. **ApplicationContextAware**: Jika bean mengimplementasikan interface ini, Spring memanggil setApplicationContext()
6. **PreInitialization (BeanPostProcessor)**: Panggilan sebelum inisialisasi
7. **InitializingBean**: Jika bean mengimplementasikan interface ini, Spring memanggil afterPropertiesSet()
8. **Custom init-method**: Metode yang didefinisikan dalam konfigurasi
9. **PostInitialization (BeanPostProcessor)**: Panggilan setelah inisialisasi
10. **Bean siap digunakan**
11. **DisposableBean**: Saat container ditutup, jika bean mengimplementasikan interface ini, Spring memanggil destroy()
12. **Custom destroy-method**: Metode yang didefinisikan dalam konfigurasi

## Autowiring

Autowiring adalah fitur yang memungkinkan Spring secara otomatis mewire (menghubungkan) dependensi.

### Jenis-jenis Autowiring

1. **no** (default di XML): Tidak ada autowiring. Referensi bean harus didefinisikan eksplisit
2. **byName**: Autowiring berdasarkan nama properti
3. **byType**: Autowiring berdasarkan tipe properti
4. **constructor**: Mirip byType tetapi untuk parameter konstruktor
5. **autodetect**: Spring memilih constructor atau byType (sekarang tidak direkomendasikan)

### Contoh Autowiring dengan XML

```xml
<!-- Autowiring byName -->
<bean id="userService" class="com.example.UserService" autowire="byName" />

<!-- Autowiring byType -->
<bean id="userService" class="com.example.UserService" autowire="byType" />

<!-- Autowiring constructor -->
<bean id="userService" class="com.example.UserService" autowire="constructor" />

```

### Autowiring dengan Anotasi

**@Autowired**: Anotasi paling umum untuk autowiring

```java
@Service
public class UserService {
    private final MessageService messageService;

    @Autowired
    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }
}

```

**@Resource**: Autowiring berdasarkan nama (JSR-250)

```java
@Service
public class UserService {
    @Resource(name = "emailService")
    private MessageService messageService;
}

```

**@Inject**: Alternatif @Autowired dari JSR-330

```java
@Service
public class UserService {
    private MessageService messageService;

    @Inject
    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }
}

```

### Qualifier

Ketika ada beberapa bean dengan tipe yang sama, Spring tidak tahu mana yang harus diinjeksi. Untuk mengatasi ini, gunakan `@Qualifier`:

```java
@Service
public class UserService {
    private final MessageService messageService;

    @Autowired
    public UserService(@Qualifier("emailService") MessageService messageService) {
        this.messageService = messageService;
    }
}

```

Dengan XML:

```xml
<bean id="userService" class="com.example.UserService">
    <constructor-arg>
        <qualifier value="emailService" />
        <bean class="com.example.EmailService" />
    </constructor-arg>
</bean>

```

### @Primary

Alternatif dari `@Qualifier`, `@Primary` menandai bean sebagai kandidat utama ketika ada beberapa bean dengan tipe yang sama:

```java
@Component
@Primary
public class EmailService implements MessageService {
    // ...
}

@Component
public class SMSService implements MessageService {
    // ...
}

```

Dengan XML:

```xml
<bean id="emailService" class="com.example.EmailService" primary="true" />
<bean id="smsService" class="com.example.SMSService" />

```

## Best Practices

### 1. Penggunaan IoC dan DI

- **Gunakan interface**: Selalu program terhadap interface, bukan implementasi
- **Prioritaskan Constructor Injection**: Terutama untuk dependensi wajib
- **Hindari Field Injection**: Sulit untuk unit testing dan melanggar prinsip dependency inversion
- **Gunakan setter untuk dependensi opsional**: Jika dependensi bersifat opsional, gunakan setter injection

### 2. Konfigurasi

- **Konsisten dalam pemilihan konfigurasi**: Pilih satu pendekatan (XML, Java Config, atau anotasi) dan konsisten
- **Java Config untuk aplikasi modern**: Lebih type-safe dan mudah di-debug
- **XML untuk integrasi dengan sistem lama**: Ketika bekerja dengan sistem legacy

### 3. Pengelolaan Bean

- **Singleton vs Prototype**: Gunakan singleton (default) untuk stateless beans dan prototype untuk stateful beans
- **Bean berukuran kecil, kohesif**: Setiap bean harus memiliki tanggung jawab tunggal (Single Responsibility Principle)
- **Penggunaan autowiring bijaksana**: Autowiring mempermudah pengembangan tetapi bisa mengurangi keterbacaan

### 4. Struktur Aplikasi

- **Struktur berdasarkan layer**:
    - `com.example.domain` - entity classes
    - `com.example.repository` - data access objects
    - `com.example.service` - business logic
    - `com.example.controller` - user interface / API endpoints
    - `com.example.config` - konfigurasi aplikasi
- **Manfaatkan anotasi stereotype**:
    - `@Repository` untuk DAO/repositories
    - `@Service` untuk service classes
    - `@Controller` untuk controller classes
    - `@Component` untuk komponen umum

### 5. Testing

- **Gunakan Spring Test**: Framework testing yang terintegrasi dengan Spring
- **MockBean dan SpyBean**: Untuk membuat mock dan spy objects
- **ApplicationContext caching**: Spring caches application contexts selama testing

### 6. Performance

- **Lazy initialization ketika perlu**: Gunakan `@Lazy` untuk bean yang mahal untuk diinisialisasi
- **Bean prototype secara tepat**: Hindari penggunaan berlebihan bean prototype karena overhead pembuatan objek
- **CircularDependencyException**: Hindari circular dependencies, restruktur kode Anda

## Contoh Project Lengkap

Berikut adalah contoh project Spring Core lengkap, dengan domain sederhana untuk sistem notifikasi. Contoh ini menggabungkan berbagai jenis DI dan teknik autowiring.

### Struktur Project

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── app/
│   │           │   └── SpringCoreApp.java
│   │           ├── config/
│   │           │   └── AppConfig.java
│   │           ├── domain/
│   │           │   └── Notification.java
│   │           ├── repository/
│   │           │   ├── NotificationRepository.java
│   │           │   └── NotificationRepositoryImpl.java
│   │           └── service/
│   │               ├── notification/
│   │               │   ├── EmailNotificationService.java
│   │               │   ├── NotificationService.java
│   │               │   ├── PushNotificationService.java
│   │               │   └── SMSNotificationService.java
│   │               └── user/
│   │                   ├── UserService.java
│   │                   └── UserServiceImpl.java
│   └── resources/
│       └── applicationContext.xml
pom.xml

```

### pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>spring-core-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <spring.version>5.3.25</spring.version>
    </properties>

    <dependencies>
        <!-- Spring Core -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!-- Spring Test -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>

```

### Domain

**Notification.java**

```java
package com.example.domain;

public class Notification {
    private String to;
    private String subject;
    private String content;

    public Notification() {
    }

    public Notification(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    // Getters and setters
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

```

### Repository

**NotificationRepository.java**

```java
package com.example.repository;

import com.example.domain.Notification;

public interface NotificationRepository {
    void save(Notification notification);
    Notification findByRecipient(String recipient);
}

```

**NotificationRepositoryImpl.java**

```java
package com.example.repository;

import com.example.domain.Notification;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private Map<String, Notification> storage;

    @PostConstruct
    public void initialize() {
        System.out.println("Initializing Notification Repository");
        storage = new HashMap<>();
    }

    @Override
    public void save(Notification notification) {
        System.out.println("Saving notification: " + notification);
        storage.put(notification.getTo(), notification);
    }

    @Override
    public Notification findByRecipient(String recipient) {
        return storage.get(recipient);
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Cleaning up Notification Repository");
        storage.clear();
    }
}

```

### Service

**NotificationService.java**

```java
package com.example.service.notification;

import com.example.domain.Notification;

public interface NotificationService {
    void sendNotification(Notification notification);
    String getServiceName();
}

```

**EmailNotificationService.java**

```java
package com.example.service.notification;

import com.example.domain.Notification;
import com.example.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public EmailNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendNotification(Notification notification) {
        System.out.println("Sending EMAIL notification to: " + notification.getTo());
        System.out.println("Subject: " + notification.getSubject());
        System.out.println("Content: " + notification.getContent());

        // Simpan notifikasi ke repository
        notificationRepository.save(notification);
    }

    @Override
    public String getServiceName() {
        return "Email Notification Service";
    }
}

```

**SMSNotificationService.java**

```java
package com.example.service.notification;

import com.example.domain.Notification;
import com.example.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SMSNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public SMSNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendNotification(Notification notification) {
        System.out.println("Sending SMS notification to: " + notification.getTo());
        System.out.println("Message: " + notification.getContent());

        // Simpan notifikasi ke repository
        notificationRepository.save(notification);
    }

    @Override
    public String getServiceName() {
        return "SMS Notification Service";
    }
}

```

**PushNotificationService.java**

```java
package com.example.service.notification;

import com.example.domain.Notification;
import com.example.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class PushNotificationService implements NotificationService {

    private NotificationRepository notificationRepository;

    // Contoh setter injection
    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("Initializing Push Notification Service");
    }

    @Override
    public void sendNotification(Notification notification) {
        System.out.println("Sending PUSH notification to: " + notification.getTo());
        System.out.println("Alert: " + notification.getSubject());
        System.out.println("Details: " + notification.getContent());

        // Simpan notifikasi ke repository
        notificationRepository.save(notification);
    }

    @Override
    public String getServiceName() {
        return "Push Notification Service";
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Cleaning up Push Notification Service");
    }
}

```

**UserService.java**

```java
package com.example.service.user;

import com.example.domain.Notification;

public interface UserService {
    void registerUser(String username, String email, String phone);
    void notifyUser(String username, String subject, String content);
}

```

**UserServiceImpl.java**

```java
package com.example.service.user;

import com.example.domain.Notification;
import com.example.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final Map<String, String> userContacts = new HashMap<>();
    private final NotificationService defaultNotificationService;
    private final NotificationService emailNotificationService;
    private final NotificationService smsNotificationService;

    // Constructor injection dengan @Qualifier
    @Autowired
    public UserServiceImpl(
            NotificationService defaultNotificationService,  // Akan menggunakan @Primary (SMS)
            @Qualifier("emailNotificationService") NotificationService emailService,
            @Qualifier("smsNotificationService") NotificationService smsService) {
        this.defaultNotificationService = defaultNotificationService;
        this.emailNotificationService = emailService;
        this.smsNotificationService = smsService;

        System.out.println("Default service: " + defaultNotificationService.getServiceName());
        System.out.println("Email service: " + emailNotificationService.getServiceName());
        System.out.println("SMS service: " + smsNotificationService.getServiceName());
    }

    @Override
    public void registerUser(String username, String email, String phone) {
        System.out.println("Registering user: " + username);
        userContacts.put(username + ".email", email);
        userContacts.put(username + ".phone", phone);

        // Notifikasi melalui email
        Notification welcome = new Notification(
                email,
                "Welcome to our service",
                "Dear " + username + ", thank you for registering!"
        );
        emailNotificationService.sendNotification(welcome);
    }

    @Override
    public void notifyUser(String username, String subject, String content) {
        String email = userContacts.get(username + ".email");
        String phone = userContacts.get(username + ".phone");

        if (email != null) {
            Notification emailNotification = new Notification(email, subject, content);
            emailNotificationService.sendNotification(emailNotification);
        }

        if (phone != null) {
            Notification smsNotification = new Notification(phone, subject, content);
            smsNotificationService.sendNotification(smsNotification);
        }
    }
}

```

### Configuration

**AppConfig.java**

```java
package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example")
public class AppConfig {
    // Bean method definitions bisa ditambahkan di sini jika dibutuhkan
}

```

### Application

**SpringCoreApp.java**

```java
package com.example.app;

import com.example.config.AppConfig;
import com.example.service.notification.NotificationService;
import com.example.service.notification.PushNotificationService;
import com.example.service.user.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringCoreApp {

    public sttatic void main(String[] args) {
        // Membuat ApplicationContext dengan Java Config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            // Mendapatkan UserService dari context
            UserService userService = context.getBean(UserService.class);

            // Mendaftarkan pengguna baru
            userService.registerUser("john_doe", "john@example.com", "123456789");

            // Mengirim notifikasi ke pengguna
            userService.notifyUser("john_doe", "New Feature Available",
                    "We've just launched a new feature you might be interested in!");

            // Mendapatkan service spesifik
            PushNotificationService pushService = context.getBean(PushNotificationService.class);
            System.out.println("Got service: " + pushService.getServiceName());

        } finally {
            // Selalu tutup ApplicationContext
            context.close();
        }
    }
}

```

### XML Configuration (Alternatif)

**applicationContext.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- Aktifkan component scanning -->
    <context:component-scan base-package="com.example" />

    <!--
    Alternatif definisi bean manual (tidak diperlukan jika menggunakan component scanning):

    <bean id="notificationRepository" class="com.example.repository.NotificationRepositoryImpl" />

    <bean id="emailNotificationService" class="com.example.service.notification.EmailNotificationService">
        <constructor-arg ref="notificationRepository" />
    </bean>

    <bean id="smsNotificationService" class="com.example.service.notification.SMSNotificationService" primary="true">
        <constructor-arg ref="notificationRepository" />
    </bean>

    <bean id="pushNotificationService" class="com.example.service.notification.PushNotificationService">
        <property name="notificationRepository" ref="notificationRepository" />
    </bean>

    <bean id="userService" class="com.example.service.user.UserServiceImpl">
        <constructor-arg index="0" ref="smsNotificationService" />
        <constructor-arg index="1" ref="emailNotificationService" />
        <constructor-arg index="2" ref="smsNotificationService" />
    </bean>
    -->

</beans>

```

**Menggunakan XML Configuration**

```java
package com.example.app;

import com.example.service.user.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringXmlApp {

    public static void main(String[] args) {
        // Membuat ApplicationContext dengan XML Config
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        try {
            // Mendapatkan UserService dari context
            UserService userService = context.getBean(UserService.class);

            // Mendaftarkan pengguna baru
            userService.registerUser("jane_smith", "jane@example.com", "987654321");

            // Mengirim notifikasi ke pengguna
            userService.notifyUser("jane_smith", "Account Verification",
                    "Please verify your account by clicking the link in this message.");

        } finally {
            // Selalu tutup ApplicationContext
            context.close();
        }
    }
}

```

## Contoh Project dengan Spring Core Murni Lainnya

Berikut ini adalah contoh lain dari penggunaan Spring Core untuk aplikasi Event Management sederhana.

### Domain

**Event.java**

```java
package com.example.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Participant> participants = new ArrayList<>();

    // Constructors, getters, setters

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public List<Participant> getParticipants() {
        return new ArrayList<>(participants);
    }

    // toString, equals, hashCode
}

```

**Participant.java**

```java
package com.example.domain;

public class Participant {
    private String id;
    private String name;
    private String email;
    private ParticipantType type;

    public enum ParticipantType {
        SPEAKER, ATTENDEE, ORGANIZER, VOLUNTEER
    }

    // Constructors, getters, setters

    // toString, equals, hashCode
}

```

### Repository

**EventRepository.java**

```java
package com.example.repository;

import com.example.domain.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    void save(Event event);
    Optional<Event> findById(String id);
    List<Event> findAll();
    void delete(String id);
}

```

**InMemoryEventRepository.java**

```java
package com.example.repository;

import com.example.domain.Event;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryEventRepository implements EventRepository {

    private Map<String, Event> events;

    @PostConstruct
    public void init() {
        events = new HashMap<>();
    }

    @Override
    public void save(Event event) {
        events.put(event.getId(), event);
    }

    @Override
    public Optional<Event> findById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    @Override
    public void delete(String id) {
        events.remove(id);
    }
}

```

### Service

**EventService.java**

```java
package com.example.service;

import com.example.domain.Event;
import com.example.domain.Participant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {
    String createEvent(String name, String description, LocalDateTime startTime, LocalDateTime endTime);
    void updateEvent(Event event);
    Optional<Event> getEventById(String id);
    List<Event> getAllEvents();
    void deleteEvent(String id);
    void addParticipant(String eventId, Participant participant);
}

```

**EventServiceImpl.java**

```java
package com.example.service;

import com.example.domain.Event;
import com.example.domain.Participant;
import com.example.repository.EventRepository;
import com.example.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final IdGenerator idGenerator;

    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository,
            NotificationService notificationService,
            IdGenerator idGenerator) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.idGenerator = idGenerator;
    }

    @Override
    public String createEvent(String name, String description, LocalDateTime startTime, LocalDateTime endTime) {
        Event event = new Event();
        event.setId(idGenerator.generateId());
        event.setName(name);
        event.setDescription(description);
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        eventRepository.save(event);
        notificationService.sendNotification("New event created: " + name);

        return event.getId();
    }

    @Override
    public void updateEvent(Event event) {
        eventRepository.save(event);
        notificationService.sendNotification("Event updated: " + event.getName());
    }

    @Override
    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public void deleteEvent(String id) {
        Optional<Event> event = eventRepository.findById(id);
        eventRepository.delete(id);
        event.ifPresent(e -> notificationService.sendNotification("Event deleted: " + e.getName()));
    }

    @Override
    public void addParticipant(String eventId, Participant participant) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.addParticipant(participant);
            eventRepository.save(event);

            notificationService.sendNotification(
                    "New participant added to " + event.getName() + ": " + participant.getName());
        }
    }
}

```

**NotificationService.java**

```java
package com.example.service;

public interface NotificationService {
    void sendNotification(String message);
    void sendNotificationToAdmin(String message);
}

```

**ConsoleNotificationService.java**

```java
package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificationService implements NotificationService {

    @Value("${admin.email:admin@example.com}")
    private String adminEmail;

    @Override
    public void sendNotification(String message) {
        System.out.println("NOTIFICATION: " + message);
    }

    @Override
    public void sendNotificationToAdmin(String message) {
        System.out.println("ADMIN NOTIFICATION to " + adminEmail + ": " + message);
    }
}

```

### Utility

**IdGenerator.java**

```java
package com.example.util;

public interface IdGenerator {
    String generateId();
}

```

**UUIDGenerator.java**

```java
package com.example.util;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UUIDGenerator implements IdGenerator {

    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}

```

### Configuration

**AppConfig.java**

```java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = "com.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

```

**application.properties**

```
admin.email=admin@example.org

```

### Application

**EventManagementApp.java**

```java
package com.example.app;

import com.example.config.AppConfig;
import com.example.domain.Event;
import com.example.domain.Participant;
import com.example.service.EventService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;

public class EventManagementApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            EventService eventService = context.getBean(EventService.class);

            // Membuat event baru
            LocalDateTime now = LocalDateTime.now();
            String eventId = eventService.createEvent(
                    "Spring Core Workshop",
                    "A deep dive into Spring Core, IoC and DI",
                    now.plusDays(7),
                    now.plusDays(7).plusHours(4)
            );

            // Menambahkan peserta
            Participant speaker = new Participant();
            speaker.setId("P001");
            speaker.setName("John Smith");
            speaker.setEmail("john@example.com");
            speaker.setType(Participant.ParticipantType.SPEAKER);

            eventService.addParticipant(eventId, speaker);

            Participant attendee = new Participant();
            attendee.setId("P002");
            attendee.setName("Jane Doe");
            attendee.setEmail("jane@example.com");
            attendee.setType(Participant.ParticipantType.ATTENDEE);

            eventService.addParticipant(eventId, attendee);

            // Mendapatkan dan menampilkan detail event
            Event event = eventService.getEventById(eventId).orElseThrow();
            System.out.println("\nEvent Details:");
            System.out.println("ID: " + event.getId());
            System.out.println("Name: " + event.getName());
            System.out.println("Description: " + event.getDescription());
            System.out.println("Start: " + event.getStartTime());
            System.out.println("End: " + event.getEndTime());
            System.out.println("Participants:");

            for (Participant p : event.getParticipants()) {
                System.out.println(" - " + p.getName() + " (" + p.getType() + ")");
            }

        } finally {
            context.close();
        }
    }
}

```

## FAQ

### 1. Apa perbedaan IoC dan DI?

IoC (Inversion of Control) adalah prinsip desain yang lebih umum, di mana alur kontrol program "dibalik" dari aplikasi ke framework. DI (Dependency Injection) adalah implementasi spesifik dari IoC, di mana dependensi objek diinjeksi dari luar, bukan dibuat oleh objek itu sendiri.

### 2. Kapan menggunakan XML vs Java Config?

- **XML Config**: Cocok untuk aplikasi legacy atau ketika Anda ingin memisahkan konfigurasi dari kode secara total. Juga berguna untuk integrasi dengan sistem lain.
- **Java Config**: Memberikan type safety dan kemudahan debugging. Direkomendasikan untuk aplikasi baru.
- **Annotation Config**: Paling sederhana dan mengurangi boilerplate, tetapi dapat menambah coupling karena anotasi berada dalam kode sumber kelas.

### 3. Apa perbedaan @Component, @Service, @Repository, dan @Controller?

Semuanya adalah anotasi stereotype Spring yang menandai kelas sebagai bean yang akan dikelola oleh Spring:

- **@Component**: Anotasi umum untuk komponen Spring
- **@Service**: Untuk layer service yang berisi logika bisnis
- **@Repository**: Untuk DAO yang berinteraksi dengan database
- **@Controller**: Untuk controller dalam aplikasi web MVC

Secara teknis, ketiganya adalah spesialisasi dari @Component dengan semantik tambahan.

### 4. Apa perbedaan Singleton dan Prototype scope?

- **Singleton**: Hanya ada satu instance per IoC container (default)
- **Prototype**: Instance baru dibuat setiap kali diminta

### 5. Bagaimana menangani circular dependencies di Spring?

Circular dependencies terjadi ketika bean A bergantung pada bean B, dan bean B juga bergantung pada bean A. Ada beberapa cara untuk menanganinya:

- Gunakan setter injection daripada constructor injection
- Gunakan anotasi @Lazy
- Refactor kode untuk menghilangkan circular dependency (solusi terbaik)

### 6. Bagaimana cara mengakses properti dari file .properties?

Gunakan @Value dengan placeholder:

```java
@Value("${property.name}")
private String propertyValue;

```

Dan konfigurasikan PropertySourcesPlaceholderConfigurer:

```java
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

```

### 7. Apa itu BeanPostProcessor?

BeanPostProcessor memungkinkan kustomisasi bean sebelum dan sesudah inisialisasi. Ini adalah mekanisme utama Spring untuk menerapkan AOP dan menambahkan fungsionalitas ke bean secara transparan.

```java
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Kode yang dijalankan sebelum inisialisasi
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Kode yang dijalankan setelah inisialisasi
        return bean;
    }
}

```

### 8. Bagaimana cara membuat custom scope?

Anda dapat membuat custom scope dengan mengimplementasikan interface `Scope` dan mendaftarkannya dengan `CustomScopeConfigurer`:

```java
public class ThreadScope implements Scope {
    private final ThreadLocal<Map<String, Object>> threadScope = ThreadLocal.withInitial(HashMap::new);

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> scope = threadScope.get();
        return scope.computeIfAbsent(name, k -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
        Map<String, Object> scope = threadScope.get();
        return scope.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // Implementation
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
}

```

```java
@Configuration
public class AppConfig {
    @Bean
    public CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("thread", new ThreadScope());
        configurer.setScopes(scopes);
        return configurer;
    }
}

```

### 9. Bagaimana cara menggunakan SpEL (Spring Expression Language)?

SpEL adalah bahasa ekspresi yang kuat yang bisa digunakan dalam konfigurasi Spring:

```java
@Value("#{systemProperties['user.region']}")
private String region;

@Value("#{2 * 4}")
private int eight;

@Value("#{userService.findByUsername('admin')}")
private User adminUser;

```

[Inversion of Control And Depedency Injection](https://www.notion.so/Inversion-of-Control-And-Depedency-Injection-1db9539e52c48083bfaff55a794c74c5?pvs=21)