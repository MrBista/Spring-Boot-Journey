# Day 2: Bean Management & Lifecycle
### Panduan Lengkap Bean Scopes, Lifecycle Methods, dan Post-Processors

> **Prerequisite:** Kamu sudah menyelesaikan Day 1 (IoC, DI, konfigurasi XML & Annotation).
> **Tujuan hari ini:** Memahami bagaimana Spring mengelola hidup dan matinya Bean — dari lahir sampai dihancurkan.

---

## Daftar Isi

**BAGIAN 1 — TEORI (30–40 menit)**
1. Recap: Apa itu Bean?
2. Bean Scopes — Semua Jenis
3. Bean Lifecycle — Fase demi Fase
4. BeanPostProcessor

**BAGIAN 2 — CODING (70–80 menit)**
5. Setup Proyek Day 2
6. Eksperimen Singleton vs Prototype
7. Custom Init & Destroy — 3 Cara
8. Lifecycle dalam XML
9. BeanPostProcessor Custom

**BAGIAN 3 — LATIHAN**
10. Latihan Lifecycle Callbacks
11. Observasi Perilaku di Berbagai Scope
12. Ringkasan & Checklist

---

# BAGIAN 1 — TEORI

## 1. Recap: Apa itu Bean?

```
Objek biasa:
  SomeClass obj = new SomeClass();   <- kamu yang buat
  // obj dipakai, lalu garbage collected

Spring Bean:
  @Component                         <- kamu deklarasikan
  public class SomeClass {}
  // Spring yang buat, kelola, dan hancurkan
  // Kamu bisa "hook" ke setiap fase
```

**Mengapa lifecycle penting?**

Bayangkan Bean yang:
- Perlu **membuka koneksi database** saat pertama kali dibuat
- Perlu **menutup koneksi** saat aplikasi shutdown
- Perlu **memvalidasi konfigurasi** setelah semua property di-inject

Spring menyediakan mekanisme untuk semua ini.

---

## 2. Bean Scopes — Semua Jenis

### Visualisasi

```
SINGLETON (default)
+-------------------------------+
|      ApplicationContext       |
|                               |
|  +------------------------+   |
|  | PaymentService         | <--- ServiceA inject ini
|  | (1 instance saja)      | <--- ServiceB inject ini (SAMA)
|  +------------------------+ <--- ServiceC inject ini (SAMA)
+-------------------------------+

PROTOTYPE
+-------------------------------+
|      ApplicationContext       |
|  getBean() #1 -> Cart A (baru)|
|  getBean() #2 -> Cart B (baru)|
|  getBean() #3 -> Cart C (baru)|
+-------------------------------+

REQUEST (web only)
+-------------------------------+
|  HTTP Request #1              |
|    -> RequestLogger #1        |
|  HTTP Request #2              |
|    -> RequestLogger #2 (baru) |
|  Request selesai -> destroy   |
+-------------------------------+

SESSION (web only)
+-------------------------------+
|  User A (session aktif)       |
|    -> UserCart #1             |
|  User B (session aktif)       |
|    -> UserCart #2 (berbeda)   |
|  Session expired -> destroy   |
+-------------------------------+
```

### Singleton — Deep Dive

```java
@Service
// @Scope("singleton") <- tidak perlu, ini default
public class ProductCatalog {

    private List<String> cache = new ArrayList<>();

    public void addToCache(String product) { cache.add(product); }
    public List<String> getCache() { return cache; }
}
```

Sifat Singleton:
- Dibuat SATU KALI saat ApplicationContext dibuat (eager by default)
- Semua yang inject ProductCatalog dapat referensi yang SAMA PERSIS
- State di dalam Bean BERBAGI — jika satu mengubah, semua terpengaruh
- Dihancurkan saat ApplicationContext ditutup

```java
ProductCatalog cat1 = ctx.getBean(ProductCatalog.class);
ProductCatalog cat2 = ctx.getBean(ProductCatalog.class);

System.out.println(cat1 == cat2);        // true — objek sama
cat1.addToCache("Laptop");
System.out.println(cat2.getCache());     // [Laptop] — berbagi state!
```

> JEBAKAN: Jangan simpan state user-spesifik di Singleton! Ini thread-safety issue.

**Lazy Singleton** — dibuat hanya saat pertama kali diminta:

```java
@Service
@Lazy  // Tidak dibuat saat startup, tapi saat pertama kali dibutuhkan
public class HeavyReportGenerator {
    // Tidak memperlambat startup aplikasi
}
```

### Prototype — Deep Dive

```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataProcessor {

    private final String instanceId = "PT-" + (int)(Math.random() * 1000);
    private List<String> results = new ArrayList<>();

    public void process(String data) {
        results.add("Processed[" + instanceId + "]: " + data);
    }

    public List<String> getResults() { return results; }
    public String getInstanceId() { return instanceId; }
}
```

```java
DataProcessor p1 = ctx.getBean(DataProcessor.class);
DataProcessor p2 = ctx.getBean(DataProcessor.class);

System.out.println(p1 == p2);   // false — objek BERBEDA

p1.process("data-A");
System.out.println(p2.getResults()); // [] — tidak berbagi state
```

### Jebakan: Prototype dalam Singleton

```java
@Service  // Singleton
public class OrderProcessor {

    @Autowired
    private DataProcessor dataProcessor; // MASALAH!
    // DataProcessor adalah prototype, tapi di-inject SEKALI saat OrderProcessor dibuat
    // Selalu pakai instance yang SAMA — prototype tidak berfungsi seperti yang diharapkan!
}
```

**Solusi: @Lookup annotation**

```java
@Service
public abstract class OrderProcessor {

    public void process(String data) {
        DataProcessor processor = createDataProcessor(); // instance baru setiap kali
        processor.process(data);
    }

    @Lookup  // Spring override method ini untuk return prototype baru setiap panggilan
    protected abstract DataProcessor createDataProcessor();
}
```

---

## 3. Bean Lifecycle — Fase demi Fase

```
FASE INSTANTIATION
[1] Constructor dipanggil
    -> new OrderService(paymentService, emailService)

[2] Populate Properties
    -> Semua @Autowired field/setter diisi
    -> Semua @Value diisi

FASE INITIALIZATION
[3] Aware Interface Callbacks (jika diimplementasikan)
    -> setBeanName()          <- BeanNameAware
    -> setApplicationContext() <- ApplicationContextAware

[4] BeanPostProcessor.postProcessBeforeInitialization()
    -> Di sinilah @Autowired, @Value diproses
    -> Dipanggil SEBELUM init methods

[5] @PostConstruct method
    -> JSR-250 annotation, DIREKOMENDASIKAN
    -> Dipanggil SETELAH injection selesai

[6] InitializingBean.afterPropertiesSet()
    -> Jika implements InitializingBean (coupling ke Spring)

[7] custom initMethod dari @Bean(initMethod="...") atau XML init-method

[8] BeanPostProcessor.postProcessAfterInitialization()
    -> @Transactional proxy dibuat di SINI!

==========================================
         BEAN SIAP DIGUNAKAN
==========================================
    [Bean melayani request aplikasi]

FASE DESTRUCTION (saat ctx.close())
[9]  @PreDestroy method          <- DIREKOMENDASIKAN
[10] DisposableBean.destroy()    <- coupling ke Spring
[11] custom destroyMethod        <- dari @Bean atau XML
```

### Urutan Prioritas

| Urutan | Init | Destroy |
|--------|------|---------|
| 1 | `@PostConstruct` | `@PreDestroy` |
| 2 | `afterPropertiesSet()` | `destroy()` |
| 3 | custom `initMethod` | custom `destroyMethod` |

> **Rekomendasi:** Selalu gunakan `@PostConstruct` dan `@PreDestroy`. Mereka adalah JSR-250 standard — tidak coupling ke Spring API.

---

## 4. BeanPostProcessor

`BeanPostProcessor` adalah cara Spring mengimplementasikan fitur-fiturnya sendiri.

```java
public interface BeanPostProcessor {

    // Dipanggil SEBELUM init methods
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    // Dipanggil SETELAH init methods
    // BISA kembalikan PROXY dari bean asli!
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
```

**Fakta mengejutkan:** Semua fitur ini diimplementasikan via BeanPostProcessor:
- `@Autowired` -> `AutowiredAnnotationBeanPostProcessor`
- `@Value` -> `AutowiredAnnotationBeanPostProcessor`
- `@PostConstruct`/`@PreDestroy` -> `CommonAnnotationBeanPostProcessor`
- `@Transactional` -> `AnnotationAwareAspectJAutoProxyCreator`
- `@Async` -> `AsyncAnnotationBeanPostProcessor`

Semua "magic" anotasi Spring = BeanPostProcessor yang bekerja di balik layar!

---

# BAGIAN 2 — CODING

## 5. Setup Proyek Day 2

```
spring-day2/
+-- pom.xml
+-- src/main/java/com/belajar/day2/
    +-- scope/
    |   +-- SingletonCounter.java
    |   +-- PrototypeTask.java
    |   +-- ScopeDemo.java
    +-- lifecycle/
    |   +-- DatabasePool.java          <- @PostConstruct & @PreDestroy
    |   +-- CacheManager.java          <- InitializingBean & DisposableBean
    |   +-- ReportGenerator.java       <- custom initMethod & destroyMethod
    |   +-- LifecycleDemo.java
    +-- postprocessor/
    |   +-- LoggingBeanPostProcessor.java
    +-- config/
    |   +-- Day2Config.java
    +-- Main.java
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

## 6. Eksperimen Singleton vs Prototype

### SingletonCounter.java

```java
package com.belajar.day2.scope;

import org.springframework.stereotype.Component;

@Component
public class SingletonCounter {

    private int count = 0;
    private final String instanceId;

    public SingletonCounter() {
        this.instanceId = "SC-" + (int)(Math.random() * 1000);
        System.out.println("[SingletonCounter] Instance dibuat: " + instanceId);
    }

    public int increment() { return ++count; }
    public int getCount() { return count; }
    public String getInstanceId() { return instanceId; }
}
```

### PrototypeTask.java

```java
package com.belajar.day2.scope;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeTask {

    private String taskName;
    private String status = "CREATED";
    private final String instanceId;

    public PrototypeTask() {
        this.instanceId = "PT-" + (int)(Math.random() * 1000);
        System.out.println("[PrototypeTask] Instance baru dibuat: " + instanceId);
    }

    public void start(String taskName) {
        this.taskName = taskName;
        this.status = "RUNNING";
        System.out.println("[" + instanceId + "] Task dimulai: " + taskName);
    }

    public void complete() {
        this.status = "DONE";
        System.out.println("[" + instanceId + "] Task selesai: " + taskName);
    }

    public String getStatus() {
        return taskName + " -> " + status + " (by " + instanceId + ")";
    }
}
```

### ScopeDemo.java

```java
package com.belajar.day2.scope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ScopeDemo {

    // Singleton: injected sekali — selalu objek yang sama
    @Autowired
    private SingletonCounter counter1;

    @Autowired
    private SingletonCounter counter2; // SAMA dengan counter1!

    @Autowired
    private ApplicationContext ctx;

    public void demonstrateSingleton() {
        System.out.println("\n=== SINGLETON DEMO ===");

        System.out.println("counter1 ID : " + counter1.getInstanceId());
        System.out.println("counter2 ID : " + counter2.getInstanceId());
        System.out.println("Sama? " + (counter1 == counter2));

        System.out.println("\nIncrement via counter1: " + counter1.increment()); // 1
        System.out.println("Increment via counter1: " + counter1.increment()); // 2
        System.out.println("Baca via counter2    : " + counter2.getCount());   // 2 — BERBAGI!
    }

    public void demonstratePrototype() {
        System.out.println("\n=== PROTOTYPE DEMO ===");

        // Setiap getBean() = instance baru
        PrototypeTask task1 = ctx.getBean(PrototypeTask.class);
        PrototypeTask task2 = ctx.getBean(PrototypeTask.class);
        PrototypeTask task3 = ctx.getBean(PrototypeTask.class);

        System.out.println("task1 == task2? " + (task1 == task2)); // false
        System.out.println("task2 == task3? " + (task2 == task3)); // false

        task1.start("Import CSV");
        task2.start("Generate Report");
        task3.start("Send Email");

        task1.complete();
        task2.complete();
        // task3 sengaja tidak di-complete

        System.out.println("\nStatus task1: " + task1.getStatus());
        System.out.println("Status task2: " + task2.getStatus());
        System.out.println("Status task3: " + task3.getStatus()); // Masih RUNNING
    }
}
```

---

## 7. Custom Init & Destroy Methods — 3 Cara

### Cara 1: @PostConstruct dan @PreDestroy (DIREKOMENDASIKAN)

```java
package com.belajar.day2.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class DatabasePool {

    private final String poolName = "MainPool";
    private final int maxConnections = 10;
    private int activeConnections = 0;
    private boolean initialized = false;

    // Dipanggil SETELAH constructor dan semua @Autowired selesai
    @PostConstruct
    public void initialize() {
        System.out.println("\n[DatabasePool] @PostConstruct dipanggil");
        System.out.println("[DatabasePool] Membuka " + maxConnections + " koneksi...");
        this.initialized = true;
        this.activeConnections = maxConnections;
        System.out.println("[DatabasePool] Pool '" + poolName + "' siap!");
    }

    public String getConnection() {
        if (!initialized) throw new IllegalStateException("Pool belum diinisialisasi!");
        if (activeConnections <= 0) throw new IllegalStateException("Tidak ada koneksi tersedia!");
        activeConnections--;
        String connId = "CONN-" + (maxConnections - activeConnections);
        System.out.println("[DatabasePool] Meminjam " + connId + ". Tersisa: " + activeConnections);
        return connId;
    }

    public void returnConnection(String connId) {
        activeConnections++;
        System.out.println("[DatabasePool] " + connId + " dikembalikan. Tersedia: " + activeConnections);
    }

    // Dipanggil saat ApplicationContext.close()
    @PreDestroy
    public void shutdown() {
        System.out.println("\n[DatabasePool] @PreDestroy dipanggil");
        System.out.println("[DatabasePool] Menutup semua koneksi...");
        activeConnections = 0;
        initialized = false;
        System.out.println("[DatabasePool] Pool '" + poolName + "' ditutup.");
    }
}
```

### Cara 2: InitializingBean dan DisposableBean (coupling ke Spring — hindari jika bisa)

```java
package com.belajar.day2.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class CacheManager implements InitializingBean, DisposableBean {

    private final Map<String, Object> cache = new HashMap<>();
    private boolean running = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Ekuivalen dengan @PostConstruct
        System.out.println("\n[CacheManager] afterPropertiesSet() dipanggil");
        System.out.println("[CacheManager] Memuat cache awal...");
        cache.put("config.timeout", 30);
        cache.put("config.maxRetry", 3);
        cache.put("config.version", "2.1.0");
        running = true;
        System.out.println("[CacheManager] Cache dimuat: " + cache.size() + " entri");
    }

    @Override
    public void destroy() throws Exception {
        // Ekuivalen dengan @PreDestroy
        System.out.println("\n[CacheManager] destroy() dipanggil");
        System.out.println("[CacheManager] Menyimpan cache ke disk...");
        cache.clear();
        running = false;
        System.out.println("[CacheManager] Cache disimpan dan dibersihkan.");
    }

    public Object get(String key) { return cache.get(key); }
    public void put(String key, Object value) { cache.put(key, value); }
    public boolean isRunning() { return running; }
}
```

### Cara 3: initMethod & destroyMethod via @Bean Config (untuk library pihak ketiga)

```java
package com.belajar.day2.lifecycle;

// Simulasi class dari library pihak ketiga — tidak ada anotasi Spring
// Anggap ini class dari Maven dependency yang tidak bisa kamu modifikasi
public class ReportGenerator {

    private String outputDirectory;
    private String templatePath;
    private boolean ready = false;

    public void setOutputDirectory(String dir) { this.outputDirectory = dir; }
    public void setTemplatePath(String path) { this.templatePath = path; }

    // Akan dijadikan initMethod via konfigurasi
    public void setupEngine() {
        System.out.println("\n[ReportGenerator] setupEngine() dipanggil (custom initMethod)");
        System.out.println("[ReportGenerator] Output dir : " + outputDirectory);
        System.out.println("[ReportGenerator] Template   : " + templatePath);
        ready = true;
        System.out.println("[ReportGenerator] Engine siap!");
    }

    public String generate(String reportName) {
        if (!ready) throw new IllegalStateException("Engine belum siap!");
        return "Report[" + reportName + "] -> " + outputDirectory + "/" + reportName + ".pdf";
    }

    // Akan dijadikan destroyMethod via konfigurasi
    public void teardownEngine() {
        System.out.println("\n[ReportGenerator] teardownEngine() dipanggil (custom destroyMethod)");
        System.out.println("[ReportGenerator] Membersihkan temporary files...");
        ready = false;
        System.out.println("[ReportGenerator] Engine di-teardown.");
    }
}
```

---

## 8. Lifecycle dalam Konfigurasi XML

```xml
<!-- src/main/resources/applicationContext-day2.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- init-method dan destroy-method dari XML -->
    <bean id="reportGenerator"
          class="com.belajar.day2.lifecycle.ReportGenerator"
          init-method="setupEngine"
          destroy-method="teardownEngine">
        <property name="outputDirectory" value="/tmp/reports"/>
        <property name="templatePath" value="/resources/templates/default.jrxml"/>
    </bean>

    <!-- Singleton (default) -->
    <bean id="singletonCounter"
          class="com.belajar.day2.scope.SingletonCounter"
          scope="singleton"/>

    <!-- Prototype -->
    <bean id="prototypeTask"
          class="com.belajar.day2.scope.PrototypeTask"
          scope="prototype"/>

</beans>
```

---

## 9. BeanPostProcessor Custom

```java
package com.belajar.day2.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {

        // Hanya log Bean dari package kita
        if (bean.getClass().getName().startsWith("com.belajar")) {
            System.out.println("  [BPP BEFORE] '" + beanName + "' ("
                    + bean.getClass().getSimpleName() + ") - sebelum init");
        }
        return bean; // WAJIB kembalikan bean!
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {

        if (bean.getClass().getName().startsWith("com.belajar")) {
            System.out.println("  [BPP AFTER]  '" + beanName + "' ("
                    + bean.getClass().getSimpleName() + ") - siap digunakan");
        }
        return bean; // Bisa return PROXY di sini — itulah cara @Transactional bekerja!
    }
}
```

---

## Day2Config.java

```java
package com.belajar.day2.config;

import com.belajar.day2.lifecycle.ReportGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.belajar.day2")
public class Day2Config {

    // ReportGenerator adalah "3rd party class" — tidak bisa tambah anotasi
    // Jadi kita daftarkan via @Bean dengan lifecycle methods eksplisit
    @Bean(initMethod = "setupEngine", destroyMethod = "teardownEngine")
    public ReportGenerator reportGenerator() {
        ReportGenerator generator = new ReportGenerator();
        generator.setOutputDirectory("/tmp/reports");
        generator.setTemplatePath("/resources/templates/default.jrxml");
        return generator;
    }
}
```

---

# BAGIAN 3 — LATIHAN

## 10. Latihan: Komponen dengan Lifecycle Callbacks

```java
package com.belajar.day2.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifecycleDemo {

    @Autowired
    private DatabasePool dbPool;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ReportGenerator reportGenerator;

    public void runDemo() {
        System.out.println("\n========================================");
        System.out.println("     LIFECYCLE DEMO BERJALAN");
        System.out.println("========================================");

        // Demo DatabasePool
        System.out.println("\n--- Database Pool Demo ---");
        String conn1 = dbPool.getConnection();
        String conn2 = dbPool.getConnection();
        System.out.println("Menggunakan: " + conn1 + " dan " + conn2);
        dbPool.returnConnection(conn1);

        // Demo CacheManager
        System.out.println("\n--- Cache Manager Demo ---");
        System.out.println("Cache running? " + cacheManager.isRunning());
        System.out.println("config.timeout: " + cacheManager.get("config.timeout"));
        System.out.println("config.version: " + cacheManager.get("config.version"));
        cacheManager.put("user.123", "Budi Santoso");
        System.out.println("user.123: " + cacheManager.get("user.123"));

        // Demo ReportGenerator
        System.out.println("\n--- Report Generator Demo ---");
        System.out.println(reportGenerator.generate("sales-report-jan"));
        System.out.println(reportGenerator.generate("inventory-summary"));
    }
}
```

### Main.java

```java
package com.belajar.day2;

import com.belajar.day2.config.Day2Config;
import com.belajar.day2.lifecycle.LifecycleDemo;
import com.belajar.day2.scope.ScopeDemo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("  SPRING DAY 2: BEAN LIFECYCLE & SCOPE");
        System.out.println("============================================");
        System.out.println("\n>> Membuat ApplicationContext...");
        System.out.println(">> Perhatikan @PostConstruct di bawah ini:\n");

        // try-with-resources: otomatis panggil ctx.close() -> trigger @PreDestroy
        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(Day2Config.class)) {

            System.out.println("\n>> ApplicationContext siap!\n");

            // Scope Demo
            ScopeDemo scopeDemo = ctx.getBean(ScopeDemo.class);
            scopeDemo.demonstrateSingleton();
            scopeDemo.demonstratePrototype();

            // Lifecycle Demo
            LifecycleDemo lifecycleDemo = ctx.getBean(LifecycleDemo.class);
            lifecycleDemo.runDemo();

            System.out.println("\n\n>> Menutup ApplicationContext...");
            System.out.println(">> Perhatikan @PreDestroy di bawah ini:\n");

        } // ctx.close() otomatis dipanggil

        System.out.println("\n>> Selesai! Semua Bean telah dihancurkan.");
    }
}
```

---

## 11. Observasi Perilaku di Berbagai Scope

### Output yang Diharapkan

```
============================================
  SPRING DAY 2: BEAN LIFECYCLE & SCOPE
============================================

>> Membuat ApplicationContext...
>> Perhatikan @PostConstruct di bawah ini:

  [BPP BEFORE] 'databasePool' (DatabasePool) - sebelum init
[DatabasePool] @PostConstruct dipanggil
[DatabasePool] Membuka 10 koneksi...
[DatabasePool] Pool 'MainPool' siap!
  [BPP AFTER]  'databasePool' (DatabasePool) - siap digunakan

  [BPP BEFORE] 'cacheManager' (CacheManager) - sebelum init
[CacheManager] afterPropertiesSet() dipanggil
[CacheManager] Cache dimuat: 3 entri
  [BPP AFTER]  'cacheManager' (CacheManager) - siap digunakan

[ReportGenerator] setupEngine() dipanggil (custom initMethod)
[ReportGenerator] Engine siap!

[SingletonCounter] Instance dibuat: SC-472

>> ApplicationContext siap!

=== SINGLETON DEMO ===
counter1 ID : SC-472
counter2 ID : SC-472
Sama? true
Increment via counter1: 1
Increment via counter1: 2
Baca via counter2    : 2

=== PROTOTYPE DEMO ===
[PrototypeTask] Instance baru dibuat: PT-831
[PrototypeTask] Instance baru dibuat: PT-195
[PrototypeTask] Instance baru dibuat: PT-047
task1 == task2? false
...

>> Menutup ApplicationContext...
>> Perhatikan @PreDestroy di bawah ini:

[CacheManager] destroy() dipanggil
[CacheManager] Cache disimpan dan dibersihkan.

[DatabasePool] @PreDestroy dipanggil
[DatabasePool] Pool 'MainPool' ditutup.

[ReportGenerator] teardownEngine() dipanggil
[ReportGenerator] Engine di-teardown.

>> Selesai! Semua Bean telah dihancurkan.
```

> PERHATIKAN: Prototype Bean (PrototypeTask) TIDAK muncul di fase @PreDestroy!
> Spring tidak track Prototype setelah delivery — kamu bertanggung jawab untuk cleanup.

### Eksperimen — Coba Sendiri!

**Eksperimen 1:** Hapus try-with-resources, tidak ada ctx.close(). Apakah @PreDestroy masih dipanggil?
> Jawaban: TIDAK. Selalu tutup ctx dengan close() atau try-with-resources.

**Eksperimen 2:** Ubah DatabasePool scope menjadi Prototype. Apa yang terjadi dengan @PreDestroy?
> Jawaban: @PreDestroy tidak akan dipanggil.

**Eksperimen 3:** Tambah @Lazy ke SingletonCounter. Kapan "Instance dibuat" muncul?
> Jawaban: Muncul SETELAH ApplicationContext dibuat, saat pertama kali Bean di-request.

**Eksperimen 4:** Buat 2 class yang saling inject satu sama lain (circular dependency). Apa yang terjadi?
> Jawaban: BeanCurrentlyInCreationException — Spring detect circular dependency dan throw error.

---

## 12. Ringkasan & Checklist Day 2

### Peta Konsep

```
BEAN SCOPE
+-- singleton   -> 1 instance, shared state, eager creation, @PreDestroy dipanggil
+-- prototype   -> instance baru tiap request, TIDAK ada @PreDestroy
+-- request     -> 1 per HTTP request (web only)
+-- session     -> 1 per HTTP session (web only)

BEAN LIFECYCLE
+-- Constructor       -> Bean dibuat
+-- @Autowired        -> Dependency di-inject
+-- BPP Before        -> BeanPostProcessor sebelum init
+-- @PostConstruct    -> Init code kamu (DIREKOMENDASIKAN)
+-- afterPropertiesSet -> InitializingBean (coupling, hindari)
+-- initMethod        -> Custom init dari @Bean/XML
+-- BPP After         -> AOP proxy dibuat di sini!
+-- [BEAN AKTIF]
+-- @PreDestroy       -> Cleanup code kamu (DIREKOMENDASIKAN)
+-- destroy()         -> DisposableBean (coupling, hindari)
+-- destroyMethod     -> Custom destroy dari @Bean/XML

CARA DEFINE LIFECYCLE HOOKS
+-- @PostConstruct/@PreDestroy    <- TERBAIK (JSR-250)
+-- InitializingBean/DisposableBean <- coupling ke Spring
+-- initMethod/destroyMethod      <- terbaik untuk 3rd-party
```

### Checklist Day 2

**Scope:**
- [ ] Aku bisa menjelaskan perbedaan Singleton dan Prototype dengan contoh
- [ ] Aku mengerti kenapa Singleton berbahaya untuk state user-spesifik
- [ ] Aku tahu masalah "Prototype dalam Singleton" dan solusi @Lookup
- [ ] Aku bisa menggunakan @Lazy untuk menunda pembuatan Bean
- [ ] Aku mengerti @PreDestroy TIDAK dipanggil untuk Prototype

**Lifecycle:**
- [ ] Aku bisa menggambar urutan fase Lifecycle secara kasar
- [ ] Aku bisa pakai @PostConstruct untuk inisialisasi resource
- [ ] Aku bisa pakai @PreDestroy untuk cleanup resource
- [ ] Aku tahu kapan pakai initMethod vs @PostConstruct
- [ ] Aku bisa mendefinisikan custom initMethod/destroyMethod via @Bean

**BeanPostProcessor:**
- [ ] Aku mengerti apa BeanPostProcessor dan kapan ia dipanggil
- [ ] Aku tahu @Autowired dan @Transactional bekerja via BPP
- [ ] Aku bisa membuat BeanPostProcessor sederhana

### Pertanyaan Kuis

1. Kamu punya Singleton Bean dengan field `List<String> logs`. Dua thread menambahkan ke list bersamaan. Apa masalahnya dan bagaimana solusinya?

2. Kenapa @PreDestroy tidak dipanggil pada Prototype Bean meskipun ctx.close() dipanggil?

3. Jelaskan mengapa @Transactional bisa bekerja "ajaib". Mekanisme apa di baliknya?

4. Apa perbedaan @PostConstruct dan konstruktor biasa? Kapan pakai yang mana?

---

*Lanjutkan ke Day 3: Spring Configuration Options — refactor proyek, Java Config penuh, dan externalized configuration!*
