# 2. AOP - Aspect Oriented Programming

## Daftar Isi

1. [Pendahuluan](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
2. [Konsep Dasar AOP](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
3. [Terminologi AOP](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
4. [Jenis Advice](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
5. [Pointcut Expression](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
6. [Implementasi AOP di Spring](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
7. [Spring AOP vs AspectJ](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
8. [Konfigurasi AOP di Spring](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
9. [Studi Kasus Lengkap](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
10. [Penanganan Error dan Exception](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
11. [Best Practices](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
12. [Debugging AOP](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
13. [Pertanyaan Umum (FAQ)](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)
14. [Referensi](https://www.notion.so/2-AOP-Aspect-Oriented-Programming-1de9539e52c4808ab8cccee531b3a6d1?pvs=21)

## 1. Pendahuluan

### Apa itu AOP?

**Aspect-Oriented Programming (AOP)** adalah paradigma pemrograman yang melengkapi Object-Oriented Programming (OOP)  dengan bertujuan untuk memisahkan **cross-cutting concerns**—fungsi yang tersebar di berbagai bagian aplikasi, seperti logging, keamanan, atau pengelolaan transaksi—dari logika bisnis utama. Dengan AOP, kamu bisa menulis kode yang lebih bersih, modular, dan mudah dipelihara.

Di **Spring Framework**, AOP menjadi salah satu fitur inti yang memungkinkan kamu menerapkan fungsi tambahan tanpa mengubah kode utama aplikasi. Misalnya, bayangkan kamu ingin mencatat setiap kali sebuah metode dipanggil. Tanpa AOP, kamu harus menambahkan kode logging di setiap metode. Dengan AOP, kamu cukup mendefinisikan satu "aspek" logging yang otomatis bekerja di tempat yang kamu tentukan.

### Permasalahan yang Disebabkan Cross-Cutting Concerns

Dalam pengembangan software tradisional, cross-cutting concerns sering menyebabkan:

- **Code Tangling** - Kode bisnis utama tercampur dengan kode untuk menangani cross-cutting concerns, membuat kode sulit dibaca dan dipahami.
    
    ```java
    
    java
    // Contoh code tangling
    public void saveUser(User user) {
    // Security check
        if (!securityService.hasPermission(currentUser, "CREATE_USER")) {
            throw new SecurityException("No permission");
        }
    
    // Logging
        logger.info("Saving user: " + user.getUsername());
    
    // Transaction management
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
    
    // Business logic
            userDao.save(user);
    
            tx.commit();
            logger.info("User saved successfully");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to save user", e);
            throw e;
        }
    }
    
    ```
    
- **Code Scattering** - Kode untuk satu concern tersebar di banyak tempat, sehingga sulit untuk diubah atau dikelola.
    
    AOP mengatasi masalah ini dengan memisahkan logika cross-cutting dari kode bisnis utama.
    

### Bagaimana AOP Menyelesaikan Masalah Ini?

AOP menyelesaikan masalah ini dengan memisahkan cross-cutting concerns ke dalam "aspects", yang kemudian dapat "dianyam" (weaved) ke dalam kode aplikasi pada waktu kompilasi atau runtime.

Dengan AOP, kode bisnis utama menjadi lebih bersih:

```java

java
// Dengan AOP
@Secured("CREATE_USER")
@Transactional
public void saveUser(User user) {
// Hanya business logic
    userDao.save(user);
}

```

Kemudian, fungsionalitas logging, security check, dan transaction management diimplementasikan dalam aspects terpisah dan diterapkan secara otomatis.

### Keuntungan Menggunakan AOP

1. **Modularitas yang lebih baik**: Kode lebih terstruktur dan lebih mudah dipahami.
2. **Mengurangi duplikasi kode**: Logika cross-cutting didefinisikan di satu tempat.
3. **Pemeliharaan yang lebih mudah**: Perubahan pada concerns seperti logging hanya perlu dilakukan di satu tempat.
4. **Separation of concerns**: Pengembang dapat fokus pada logika bisnis tanpa memikirkan concerns teknis.
5. **Kode yang lebih bersih**: Kode bisnis bebas dari detail implementasi teknis.

## Konsep Dasar AOP

### Pemisahan Cross-Cutting Concerns

Dalam aplikasi berbasis OOP, cross-cutting concerns biasanya didistribusikan ke berbagai objek. AOP memungkinkan untuk mengenkapsulasi concerns ini ke dalam entitas terpisah yang disebut "aspects".

## Terminologi AOP

Untuk memahami AOP di Spring, Anda perlu memahami istilah-istilah berikut:

1. **Aspect** - Modul yang mengenkapsulasi advice dan pointcuts untuk cross-cutting concerns
2. **Join Point** - Titik eksekusi dalam aplikasi seperti pemanggilan metode atau penanganan exception
3. **Advice** - Tindakan yang dilakukan oleh aspect pada join point tertentu
4. **Pointcut** - Ekspresi yang mencocokkan join points untuk menentukan dimana advice harus dieksekusi
5. **Target Object** - Objek yang sedang dimodifikasi oleh AOP proxy
6. **AOP Proxy** - Objek yang dibuat oleh framework AOP untuk mengimplementasikan aspek
7. **Weaving** - Proses menghubungkan aspek dengan objek target untuk membuat objek yang ditingkatkan

### 1. Aspect

**Aspect** adalah unit modularitas yang mengenkapsulasi cross-cutting concerns. Dalam Spring, aspect biasanya diimplementasikan sebagai kelas biasa yang ditandai dengan anotasi `@Aspect`.

```java

@Aspect
@Component
public class LoggingAspect {
// Implementation
}

```

### 2. Join Point

**Join point** adalah titik spesifik dalam eksekusi program, seperti pemanggilan method, exception handling, atau field access. Spring AOP hanya mendukung **method execution join points**, berbeda dengan AspectJ yang mendukung lebih banyak jenis join point.

Join point bisa dianggap sebagai "tempat potensial" di mana advice bisa diterapkan.

### 3. Pointcut

**Pointcut** adalah predicate atau ekspresi yang menentukan join point mana yang akan diberi advice. Pointcut membantu menyaring join points yang relevan untuk sebuah advice.

```java

java
@Pointcut("execution(* com.example.service.*.*(..))")
public void serviceMethods() {}

```

### Pointcut Expressions

Pointcut expression adalah mekanisme untuk menentukan join point mana yang akan diintervensi oleh advice. Spring AOP menggunakan AspectJ pointcut expression language untuk menentukan pointcut.

### Sintaks Dasar

Format dasar untuk pointcut expression adalah:

```

execution(modifiers-pattern? return-type-pattern declaring-type-pattern? method-name-pattern(param-pattern) throws-pattern?)

```

Di mana:

- `?` menunjukkan bagian opsional
- `modifiers-pattern`: public, protected, dll.
- `return-type-pattern`: tipe return metode
- `declaring-type-pattern`: kelas atau interface yang mendeklarasikan metode
- `method-name-pattern`: nama metode
- `param-pattern`: parameter metode
- `throws-pattern`: pengecualian yang dideklarasikan

### Contoh Pointcut Expression

1. **Semua method public di package service**
    
    ```
    
    execution(public * com.example.service.*.*(..))
    
    ```
    
2. **Semua method dengan nama dimulai dengan "get"**
    
    ```
    
    execution(* com.example.*.get*(..))
    
    ```
    
3. **Method dengan parameter String**
    
    ```
    
    execution(* *.*(java.lang.String))
    
    ```
    
4. **Method dengan dua parameter, yang pertama String**
    
    ```
    
    execution(* *.*(java.lang.String, ..))
    
    ```
    
5. **Semua method di class dengan annotation @Service**
    
    ```
    
    @within(org.springframework.stereotype.Service)
    
    ```
    
6. **Method dengan annotation @Transactional**
    
    ```
    
    @annotation(org.springframework.transaction.annotation.Transactional)
    
    ```
    
    ### Kombinasi Pointcut
    
    Pointcut expressions bisa dikombinasikan dengan operator `&&` (and), `||` (or), dan `!` (not):
    
    ```java
    
    java
    @Pointcut("execution(* com.example.service.*.*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalServiceMethods() {}
    
    ```
    
    ### Reuse Pointcut
    
    Untuk menggunakan kembali pointcut, buat method dengan anotasi `@Pointcut`:
    
    ```java
    
    java
    @Aspect
    @Component
    public class CommonPointcuts {
    
        @Pointcut("execution(* com.example.service.*.*(..))")
        public void serviceLayer() {}
    
        @Pointcut("execution(* com.example.repository.*.*(..))")
        public void dataAccessLayer() {}
    
        @Pointcut("serviceLayer() || dataAccessLayer()")
        public void businessLogic() {}
    
        @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
        public void transactionalMethod() {}
    }
    
    ```
    
    Kemudian, gunakan di advice:
    
    ```java
    
    java
    @Before("com.example.aspect.CommonPointcuts.serviceLayer()")
    public void beforeServiceMethod() {
    // ...
    }
    
    ```
    

### 4. Advice

**Advice** adalah tindakan yang dilakukan oleh aspect pada join point tertentu. Spring AOP mendukung beberapa jenis advice:

- **Before advice**: Dijalankan sebelum join point
- **After returning advice**: Dijalankan setelah join point menyelesaikan eksekusi normal
- **After throwing advice**: Dijalankan jika method throws exception
- **After (finally) advice**: Dijalankan setelah join point (baik normal atau exception)

### Before Advice

Before advice dijalankan sebelum join point dieksekusi. Ini tidak dapat mencegah eksekusi join point kecuali jika melemparkan pengecualian.

```java

java
@Before("execution(* com.example.service.*.*(..))")
public void doBeforeMethod(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getName();

    System.out.println("Before executing: " + className + "." + methodName);
}

```

Gunakan before advice untuk:

- Logging sebelum eksekusi metode
- Validasi parameter masukan
- Pengukuran waktu mulai eksekusi
- Persiapan konteks atau sumber daya

### After Returning Advice

After returning advice dijalankan setelah join point selesai dieksekusi secara normal (tanpa melemparkan pengecualian).

```java

java
@AfterReturning(
    pointcut = "execution(* com.example.service.*.*(..))",
    returning = "result"
)
public void doAfterReturning(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().getName();
    System.out.println("Method " + methodName + " returned: " + result);
}

```

Gunakan after returning advice untuk:

- Logging hasil metode
- Validasi nilai kembalian
- Pemrosesan tambahan pada hasil
- Caching hasil

### After Throwing Advice

After throwing advice dijalankan ketika join point selesai dengan melemparkan pengecualian.

```java

java
@AfterThrowing(
    pointcut = "execution(* com.example.service.*.*(..))",
    throwing = "ex"
)
public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
    String methodName = joinPoint.getSignature().getName();
    System.out.println("Exception in " + methodName + ": " + ex.getMessage());
}

```

Gunakan after throwing advice untuk:

- Logging pengecualian
- Penanganan khusus untuk pengecualian tertentu
- Pembersihan sumber daya setelah pengecualian
- Konversi pengecualian ke jenis lain

### After (Finally) Advice

After advice dijalankan setelah join point dieksekusi, terlepas dari bagaimana eksekusi berakhir (normal atau dengan pengecualian).

```java

java
@After("execution(* com.example.service.*.*(..))")
public void doAfterMethod(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    System.out.println("After executing method: " + methodName);
}

```

Gunakan after advice untuk:

- Pembersihan sumber daya
- Logging akhir eksekusi
- Melepaskan lock atau semaphore
- Penanganan akhir yang harus dilakukan terlepas dari hasil

### Around Advice

Around advice mengelilingi join point dan memiliki kontrol penuh atas eksekusi join point. Ini adalah jenis advice yang paling kuat tetapi juga paling rumit.

```java

java
@Around("execution(* com.example.service.*.*(..))")
public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    long startTime = System.currentTimeMillis();

    try {
// Proceed to the join point (method execution)
        Object result = joinPoint.proceed();

// Post-processing
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println(methodName + " executed in " + executionTime + "ms");

        return result;
    } catch (Exception e) {
// Exception handling
        System.out.println("Exception in " + methodName + ": " + e.getMessage());
        throw e;
    }
}

```

Gunakan around advice untuk:

- Pengukuran waktu eksekusi
- Pengubahan parameter metode
- Pengubahan nilai kembalian
- Caching dengan kontrol penuh
- Retrying pada kegagalan
- Transaksi kustom

Around advice memberikan kontrol penuh atas eksekusi metode target, termasuk:

- Apakah akan melanjutkan ke join point (dengan `joinPoint.proceed()`)
- Mengubah argumen yang diteruskan ke metode target
- Mengubah nilai kembalian atau melemparkan pengecualian
- Melakukan tindakan sebelum dan sesudah eksekusi

### 5. Weaving

**Weaving** adalah proses menghubungkan aspects dengan objek target untuk menciptakan advised object. Weaving bisa dilakukan pada:

- **Compile time**: Saat source code dikompilasi
- **Load time**: Saat kelas di-load oleh classloader
- **Runtime**: Saat aplikasi berjalan

Spring AOP menggunakan **runtime weaving** melalui proxy pattern.

## Best Practices

1. **Buat Pointcut yang Reusable**
    
    ```java
    
    java
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceMethods() {}
    
    @Before("serviceMethods()")
    public void logBefore() {/* ... */ }
    
    @After("serviceMethods()")
    public void logAfter() {/* ... */ }
    
    ```
    
2. **Pisahkan Definisi Pointcut dari Advice**
    
    ```java
    
    java
    // File PointcutDefinitions.java
    @Component
    @Aspect
    public class PointcutDefinitions {
        @Pointcut("execution(* com.example.service.*.*(..))")
        public void serviceMethods() {}
    }
    
    // File LoggingAspect.java
    @Component
    @Aspect
    public class LoggingAspect {
        @Before("com.example.aspect.PointcutDefinitions.serviceMethods()")
        public void logBefore() {/* ... */ }
    }
    
    ```
    
3. **Gunakan Anotasi Kustom untuk Pointcut**
    
    ```java
    
    java
    // Definisi anotasi
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LogExecutionTime {}
    
    // Pointcut berdasarkan anotasi
    @Around("@annotation(com.example.aspect.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    // implementasi
    }
    
    ```
    
4. **Berikan Prioritas pada Aspects**
    
    ```java
    
    java
    @Aspect
    @Component
    @Order(1)// Prioritas lebih tinggi
    public class SecurityAspect {
    // ...
    }
    
    @Aspect
    @Component
    @Order(2)// Prioritas lebih rendah
    public class LoggingAspect {
    // ...
    }
    
    ```
    
5. **Gunakan Log Framework daripada System.out.println**
    
    ```java
    
    java
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    @Aspect
    @Component
    public class LoggingAspect {
        private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
        @Before("execution(* com.example.service.*.*(..))")
        public void logBefore(JoinPoint joinPoint) {
            logger.info("Sebelum menjalankan: {}", joinPoint.getSignature().getName());
        }
    }
    
    ```
    
6. **Tangkap Parameter dan Return Value**
    
    ```java
    
    java
    @AfterReturning(
        pointcut = "execution(* com.example.service.*.*(..))",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Metode {} mengembalikan: {}",
            joinPoint.getSignature().getName(), result);
    }
    
    ```
    
7. **Menggunakan Join Point untuk Akses Informasi Metode**
    
    ```java
    
    java
    @Before("execution(* com.example.service.*.*(..))")
    public void logMethodSignature(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
    
        logger.info("Menjalankan {}.{}() dengan parameter: {}",
            className, methodName, Arrays.toString(args));
    }
    
    ```
    
8. **Gunakan AspectJ untuk Ekspresi Pointcut yang Kompleks**
    
    ```java
    
    java
    // Metode dalam paket service, kecuali getter dan setter
    @Pointcut("execution(* com.example.service.*.*(..)) && !execution(* get*()) && !execution(* set*())")
    public void nonAccessorMethods() {}
    
    ```
    

### Order Eksekusi Aspek

Ketika beberapa aspect berlaku pada join point yang sama, urutan eksekusi perlu dikontrol. Gunakan anotasi `@Order` untuk menentukan urutan:

```java

java
@Aspect
@Component
@Order(1)// Prioritas tertinggi
public class SecurityAspect {
// implementasi
}

@Aspect
@Component
@Order(2)
public class LoggingAspect {
// implementasi
}

@Aspect
@Component
@Order(3)// Prioritas terendah
public class PerformanceAspect {
// implementasi
}

```

Nilai `@Order` yang lebih rendah memiliki prioritas lebih tinggi. Untuk advice pada aspect yang sama:

- Highest priority: @Around
- High priority: @Before
- Low priority: @After
- Lowest priority: @AfterReturning or @AfterThrowing

## Hal yang Harus Dihindari

- **Menerapkan AOP untuk Setiap Fungsi**
    - Terlalu banyak aspek bisa membingungkan dan menurunkan performa
    - Gunakan hanya untuk cross-cutting concerns yang nyata
- **Logika Bisnis di dalam Aspects**
    - Aspek seharusnya menangani cross-cutting concerns, bukan logika bisnis
    - Jangan tergoda untuk menambahkan validasi bisnis di aspect
- **Aspek yang Saling Bergantung**
    - Hindari ketergantungan antar aspect
    - Gunakan @Order untuk menentukan urutan eksekusi jika diperlukan
- **Pointcut yang Terlalu Luas**
    - `execution(* *.*(..))` akan menangkap semua metode di aplikasi
    - Gunakan pointcut yang spesifik untuk mencegah perilaku yang tidak diinginkan
- **Mengabaikan Order Eksekusi**
    - Jika memiliki beberapa aspek, order eksekusi bisa penting
    - Gunakan @Order untuk menentukan prioritas
- **Ignore Exception di Around Advice**
    
    ```java
    
    java
    // JANGAN LAKUKAN INI
    @Around("execution(* com.example.service.*.*(..))")
    public Object badAdvice(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("Error: ", e);
            return null;// Menelan exception!
        }
    }
    
    ```
    
- **Mengandalkan AOP untuk Logging Performa Produksi**
    - AOP bagus untuk logging sederhana, tapi untuk pemantauan produksi yang serius, gunakan alat khusus
    - Pertimbangkan solusi seperti Spring Boot Actuator atau Micrometer
- **Menghindari Pembuatan Test untuk AOP**
    - Ujilah aspek-aspek Anda seperti kode lainnya
    - Verifikasi bahwa advice dijalankan pada waktu yang tepat
- **Menggunakan AOP untuk Masalah yang Lebih Baik Diselesaikan dengan OOP**
    - AOP bukan pengganti desain OOP yang baik
    - Jika fungsi bisa dibuat sebagai interface atau abstract class, itu mungkin bukan kandidat AOP
- **Memodifikasi Parameter Masukan di Before Advice**
    - Mengubah parameter dapat mengakibatkan perilaku yang sulit diprediksi
    - Jika perlu mengubah input, gunakan Around Advice dan dokumentasikan dengan jelas

## Batasan AOP di Spring

1. **Hanya method execution join points**
    - Spring AOP hanya mendukung method execution join points
    - Tidak seperti AspectJ yang mendukung lebih banyak jenis join points
2. **Hanya public methods**
    - Advice hanya dipicu untuk public methods
    - Private/protected methods tidak akan dipicu
3. **Self-invocation issue**
    - AOP tidak bekerja untuk pemanggilan method dalam class yang sama
    
    ```java
    
    java
    @Service
    public class UserService {
        public void methodA() {
    // AOP advice tidak dipicu ketika methodB dipanggil dari sini
            this.methodB();
        }
    
        public void methodB() {
    // ...
        }
    }
    
    ```
    
4. **Proksi berbasis**
    - Spring AOP adalah proxy-based, tidak seperti AspectJ yang compile-time/load-time weaving
    - Ada beberapa batasan dengan proxy (seperti final methods/classes)

##