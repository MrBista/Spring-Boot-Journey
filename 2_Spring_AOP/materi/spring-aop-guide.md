# 🌱 Panduan Lengkap AOP (Aspect-Oriented Programming) di Spring

---

## 📚 Daftar Isi

1. [Apa itu AOP?](#1-apa-itu-aop)
2. [Konsep Inti AOP](#2-konsep-inti-aop)
3. [Setup & Dependency](#3-setup--dependency)
4. [Jenis-jenis Advice](#4-jenis-jenis-advice)
5. [Pointcut Expression](#5-pointcut-expression)
6. [JoinPoint & ProceedingJoinPoint](#6-joinpoint--proceedingjoinpoint)
7. [Aspect Ordering](#7-aspect-ordering)
8. [Annotation-based AOP (Custom Annotation)](#8-annotation-based-aop-custom-annotation)
9. [Use Cases Nyata di Spring Boot](#9-use-cases-nyata-di-spring-boot)
10. [AOP Proxy: JDK vs CGLIB](#10-aop-proxy-jdk-vs-cglib)
11. [Limitations & Best Practices](#11-limitations--best-practices)
12. [Ringkasan Cheat Sheet](#12-ringkasan-cheat-sheet)

---

## 1. Apa itu AOP?

**AOP (Aspect-Oriented Programming)** adalah paradigma pemrograman yang memungkinkan kamu memisahkan *cross-cutting concerns* dari logika bisnis utama.

### ❓ Masalah Tanpa AOP

Bayangkan kamu perlu menambahkan **logging** di setiap method service:

```java
public class UserService {
    public void createUser(User user) {
        log.info("Start createUser");       // 😩 duplikasi
        // ... logika bisnis
        log.info("End createUser");         // 😩 duplikasi
    }

    public void deleteUser(Long id) {
        log.info("Start deleteUser");       // 😩 duplikasi
        // ... logika bisnis
        log.info("End deleteUser");         // 😩 duplikasi
    }
}
```

Ini mengarah ke **code duplication** yang masif. AOP hadir untuk mengatasi hal ini.

### ✅ Dengan AOP

```java
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.example.service.*.*(..))")
    public Object logMethod(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Start {}", pjp.getSignature().getName());
        Object result = pjp.proceed();
        log.info("End {}", pjp.getSignature().getName());
        return result;
    }
}
```

Satu aspect, berlaku untuk **semua** method di package service. 🎉

### Cross-Cutting Concerns yang Umum

| Concern | Deskripsi |
|---------|-----------|
| **Logging** | Mencatat eksekusi method |
| **Security** | Cek otorisasi sebelum eksekusi |
| **Transaction** | Manajemen database transaction |
| **Caching** | Cache hasil method |
| **Performance Monitoring** | Mengukur waktu eksekusi |
| **Exception Handling** | Penanganan error terpusat |
| **Auditing** | Mencatat siapa melakukan apa |

---

## 2. Konsep Inti AOP

Sebelum coding, wajib pahami terminologi AOP:

```
┌─────────────────────────────────────────────────────────────┐
│                        ASPECT                               │
│  (Class yang berisi semua concern, e.g. LoggingAspect)     │
│                                                             │
│  ┌───────────┐    ┌───────────┐    ┌──────────────────┐   │
│  │ POINTCUT  │───▶│ ADVICE   │    │    JOIN POINT     │   │
│  │(kapan/di  │    │(apa yg   │    │(titik eksekusi    │   │
│  │ mana)     │    │dilakukan)│    │di program)        │   │
│  └───────────┘    └───────────┘    └──────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Terminologi

| Istilah | Arti Sederhana | Contoh |
|---------|---------------|--------|
| **Aspect** | Modul yang berisi cross-cutting concern | `LoggingAspect`, `SecurityAspect` |
| **Join Point** | Titik di program yang bisa di-intercept | Eksekusi method, throw exception |
| **Pointcut** | Ekspresi untuk memilih Join Point mana | `execution(* com.example.*.*(..))` |
| **Advice** | Kode yang dijalankan di Join Point | Method dengan `@Before`, `@After`, dll |
| **Weaving** | Proses menghubungkan Aspect ke target object | Dilakukan Spring saat runtime |
| **Target Object** | Object yang di-intercept | `UserService` bean |
| **Proxy** | Wrapper object yang dibuat Spring | Objek yang sebenarnya dipanggil caller |

---

## 3. Setup & Dependency

### Maven (`pom.xml`)

```xml
<!-- Spring Boot Starter AOP - sudah termasuk AspectJ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### Gradle (`build.gradle`)

```groovy
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

### Enable AOP (Opsional di Spring Boot)

Spring Boot **auto-configure** AOP secara otomatis. Namun jika perlu eksplisit:

```java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
    // Spring Boot sudah otomatis, ini opsional
}
```

> 💡 **Tips:** `@EnableAspectJAutoProxy(proxyTargetClass = true)` memaksa penggunaan CGLIB proxy (bukan JDK proxy).

---

## 4. Jenis-jenis Advice

Spring AOP mendukung 5 jenis Advice:

### 4.1 `@Before` — Sebelum Method Dieksekusi

```java
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        log.info("[BEFORE] {}.{}() dipanggil dengan args: {}", 
            className, methodName, Arrays.toString(args));
    }
}
```

**Karakteristik:**
- Berjalan **sebelum** method target
- Tidak bisa mencegah eksekusi method (kecuali throw exception)
- Tidak bisa memodifikasi return value

---

### 4.2 `@After` — Setelah Method (Finally Block)

```java
@After("execution(* com.example.service.*.*(..))")
public void logAfter(JoinPoint joinPoint) {
    log.info("[AFTER] {} selesai dieksekusi (sukses maupun exception)", 
        joinPoint.getSignature().getName());
}
```

**Karakteristik:**
- Berjalan **selalu** setelah method — baik sukses maupun throw exception
- Seperti `finally` block
- Tidak bisa mengakses return value

---

### 4.3 `@AfterReturning` — Setelah Method Sukses

```java
@AfterReturning(
    pointcut = "execution(* com.example.service.UserService.findUser(..))",
    returning = "result"  // nama parameter harus sama
)
public void logAfterReturning(JoinPoint joinPoint, Object result) {
    log.info("[AFTER RETURNING] {} mengembalikan: {}", 
        joinPoint.getSignature().getName(), result);
}
```

**Karakteristik:**
- Hanya berjalan jika method **tidak throw exception**
- Bisa mengakses return value via parameter `returning`
- **Tidak bisa** mengubah return value

---

### 4.4 `@AfterThrowing` — Setelah Method Throw Exception

```java
@AfterThrowing(
    pointcut = "execution(* com.example.service.*.*(..))",
    throwing = "ex"  // nama parameter harus sama
)
public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
    log.error("[AFTER THROWING] {} melempar exception: {}", 
        joinPoint.getSignature().getName(), ex.getMessage());
    
    // Bisa filter exception spesifik
    if (ex instanceof DataAccessException) {
        // kirim alert, dll
    }
}
```

**Karakteristik:**
- Hanya berjalan jika method **throw exception**
- Bisa filter tipe exception tertentu
- **Tidak bisa** menelan (suppress) exception

---

### 4.5 `@Around` — Paling Powerful (Melingkupi Semua)

```java
@Around("execution(* com.example.service.*.*(..))")
public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
    String methodName = pjp.getSignature().getName();
    long start = System.currentTimeMillis();
    
    log.info("[AROUND] Sebelum eksekusi: {}", methodName);
    
    Object result;
    try {
        // ⭐ WAJIB dipanggil untuk melanjutkan eksekusi method asli
        result = pjp.proceed();
        
        log.info("[AROUND] Sukses: {}", methodName);
    } catch (Throwable ex) {
        log.error("[AROUND] Exception di {}: {}", methodName, ex.getMessage());
        throw ex; // re-throw atau handle
    } finally {
        long elapsed = System.currentTimeMillis() - start;
        log.info("[AROUND] Durasi {}: {} ms", methodName, elapsed);
    }
    
    return result; // bisa modifikasi return value di sini
}
```

**Karakteristik:**
- Paling fleksibel — bisa lakukan sebelum DAN sesudah
- **Bisa** mencegah eksekusi method asli (tidak panggil `pjp.proceed()`)
- **Bisa** memodifikasi args sebelum diteruskan: `pjp.proceed(newArgs)`
- **Bisa** memodifikasi return value
- **Wajib** return `Object` dan throw `Throwable`

### Urutan Eksekusi Semua Advice

```
┌────────────────────────────────────────────────────┐
│                                                    │
│  @Before ──▶ [@Around start]                      │
│                    │                               │
│                    ▼                               │
│           ┌─── METHOD EKSEKUSI ───┐                │
│           │                      │                │
│     [sukses]                [exception]            │
│           │                      │                │
│           ▼                      ▼                │
│  @AfterReturning          @AfterThrowing           │
│           │                      │                │
│           └──────────┬───────────┘                │
│                      ▼                             │
│                   @After                          │
│                      │                             │
│                      ▼                             │
│           [@Around end] ──▶ Caller                │
└────────────────────────────────────────────────────┘
```

---

## 5. Pointcut Expression

Pointcut Expression adalah "query" untuk memilih method mana yang akan di-intercept.

### 5.1 Sintaks `execution()`

```
execution([modifier] return-type [declaring-type].method-name(params) [throws])
```

| Simbol | Arti |
|--------|------|
| `*` | Wildcard satu segment |
| `..` | Wildcard banyak segment (package atau params) |
| `+` | Include subclass |

### Contoh-contoh `execution()`

```java
// Semua method di class UserService
execution(* com.example.service.UserService.*(..))

// Semua method di semua class dalam package service
execution(* com.example.service.*.*(..))

// Semua method di package service DAN sub-package nya
execution(* com.example.service..*.*(..))

// Hanya method public
execution(public * com.example.service.*.*(..))

// Return type spesifik
execution(String com.example.service.*.*(..))

// Nama method spesifik
execution(* com.example.service.*.find*(..))

// Dengan parameter spesifik
execution(* com.example.service.*.*(String))

// Dengan parameter spesifik (minimal 1 String)
execution(* com.example.service.*.*(String, ..))

// Tanpa parameter
execution(* com.example.service.*.*())
```

### 5.2 `within()` — Berdasarkan Class/Package

```java
// Semua method dalam package service
@Pointcut("within(com.example.service.*)")
public void serviceLayer() {}

// Semua method dalam class UserService
@Pointcut("within(com.example.service.UserService)")
public void userServiceMethods() {}

// Termasuk sub-package
@Pointcut("within(com.example.service..*)")
public void allServiceMethods() {}
```

### 5.3 `@annotation()` — Berdasarkan Annotation

```java
// Method yang memiliki annotation @Transactional
@Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
public void transactionalMethods() {}

// Method yang memiliki custom annotation @Loggable
@Pointcut("@annotation(com.example.annotation.Loggable)")
public void loggableMethods() {}
```

### 5.4 `@within()` — Class yang Memiliki Annotation

```java
// Semua method dalam class yang ber-annotation @Service
@Pointcut("@within(org.springframework.stereotype.Service)")
public void allServiceBeans() {}
```

### 5.5 `bean()` — Berdasarkan Spring Bean Name

```java
// Bean dengan nama tertentu
@Pointcut("bean(userService)")
public void userServiceBean() {}

// Semua bean yang namanya berakhiran "Service"
@Pointcut("bean(*Service)")
public void allServiceBeans() {}
```

### 5.6 `args()` — Berdasarkan Tipe Argument

```java
// Method dengan parameter pertama tipe Long
@Pointcut("args(Long, ..)")
public void methodsWithLongFirstArg() {}
```

### 5.7 Kombinasi Pointcut dengan Operator Logika

```java
@Aspect
@Component
public class MultiPointcutAspect {

    // Definisi pointcut yang bisa di-reuse
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.example.repository.*.*(..))")
    public void repositoryLayer() {}

    @Pointcut("@annotation(com.example.annotation.Auditable)")
    public void auditableMethod() {}

    // AND: kedua kondisi harus terpenuhi
    @Before("serviceLayer() && auditableMethod()")
    public void auditServiceMethod(JoinPoint jp) {
        log.info("Audit service method: {}", jp.getSignature().getName());
    }

    // OR: salah satu terpenuhi
    @Before("serviceLayer() || repositoryLayer()")
    public void logDataAccess(JoinPoint jp) {
        log.info("Data access: {}", jp.getSignature().getName());
    }

    // NOT: kondisi tidak terpenuhi
    @Before("serviceLayer() && !auditableMethod()")
    public void logNonAuditableService(JoinPoint jp) {
        log.info("Non-auditable service: {}", jp.getSignature().getName());
    }
}
```

---

## 6. JoinPoint & ProceedingJoinPoint

### 6.1 `JoinPoint` — Untuk `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`

```java
@Before("execution(* com.example.service.*.*(..))")
public void exampleJoinPoint(JoinPoint jp) {
    
    // Nama method
    String methodName = jp.getSignature().getName();
    
    // Signature lengkap
    String signature = jp.getSignature().toShortString();
    // e.g: "UserService.createUser(..)"
    
    // Arguments yang dikirim
    Object[] args = jp.getArgs();
    
    // Target object (object asli, bukan proxy)
    Object target = jp.getTarget();
    String targetClass = jp.getTarget().getClass().getSimpleName();
    
    // Proxy object
    Object proxy = jp.getThis();
    
    // Kind of join point (method-execution, etc.)
    String kind = jp.getKind();
    
    log.info("Method: {}, Class: {}, Args: {}", 
        methodName, targetClass, Arrays.toString(args));
}
```

### 6.2 `ProceedingJoinPoint` — Khusus `@Around`

```java
@Around("execution(* com.example.service.*.*(..))")
public Object aroundExample(ProceedingJoinPoint pjp) throws Throwable {
    
    // Semua method dari JoinPoint tersedia
    String methodName = pjp.getSignature().getName();
    Object[] args = pjp.getArgs();
    
    // Modifikasi arguments sebelum diteruskan
    Object[] modifiedArgs = Arrays.copyOf(args, args.length);
    if (modifiedArgs.length > 0 && modifiedArgs[0] instanceof String) {
        modifiedArgs[0] = ((String) modifiedArgs[0]).trim();
    }
    
    // Lanjutkan eksekusi dengan args asli
    Object result = pjp.proceed();
    
    // ATAU lanjutkan dengan args yang dimodifikasi
    // Object result = pjp.proceed(modifiedArgs);
    
    // Modifikasi return value
    if (result instanceof String) {
        return ((String) result).toUpperCase();
    }
    
    return result;
}
```

### 6.3 Mengakses Annotation via JoinPoint

```java
@Around("@annotation(com.example.annotation.RateLimit)")
public Object checkRateLimit(ProceedingJoinPoint pjp) throws Throwable {
    
    // Ambil annotation dari method
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    RateLimit rateLimit = method.getAnnotation(RateLimit.class);
    
    int limit = rateLimit.value(); // ambil attribute annotation
    
    // logika rate limiting...
    return pjp.proceed();
}
```

---

## 7. Aspect Ordering

Jika ada **banyak Aspect** yang berlaku pada method yang sama, urutan eksekusinya bisa dikontrol.

### Menggunakan `@Order`

```java
@Aspect
@Component
@Order(1)  // Angka lebih kecil = prioritas lebih tinggi (dieksekusi pertama)
public class SecurityAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void checkSecurity(JoinPoint jp) {
        log.info("1. Security check");
    }
}

@Aspect
@Component
@Order(2)
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint jp) {
        log.info("2. Logging");
    }
}

@Aspect
@Component
@Order(3)
public class PerformanceAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void measurePerformance(JoinPoint jp) {
        log.info("3. Performance");
    }
}
```

### Visualisasi Urutan

```
Caller
  │
  ▼
[@Order(1) SecurityAspect - Before]
  │
  ▼
[@Order(2) LoggingAspect - Before]
  │
  ▼
[@Order(3) PerformanceAspect - Before]
  │
  ▼
  METHOD EKSEKUSI
  │
  ▼
[@Order(3) PerformanceAspect - After]  ← Kebalikan untuk After
  │
  ▼
[@Order(2) LoggingAspect - After]
  │
  ▼
[@Order(1) SecurityAspect - After]
  │
  ▼
Caller
```

> 💡 Untuk `@Around`, `@Order(1)` akan menjadi wrapper paling luar (dieksekusi pertama dan terakhir).

### Menggunakan `Ordered` Interface

```java
@Aspect
@Component
public class SecurityAspect implements Ordered {
    
    @Override
    public int getOrder() {
        return 1;
    }
}
```

---

## 8. Annotation-based AOP (Custom Annotation)

Ini adalah pattern yang paling umum di Spring Boot production code.

### Step 1: Buat Custom Annotation

```java
// Annotation untuk logging
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {
    String value() default "";
    boolean logArgs() default true;
    boolean logResult() default true;
}

// Annotation untuk audit trail
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String action();
    String resource();
}

// Annotation untuk performance monitoring
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceMonitor {
    long warnThresholdMs() default 1000;
}
```

### Step 2: Buat Aspect

```java
@Aspect
@Component
@Slf4j
public class CustomAnnotationAspect {

    // ======== @Loggable Aspect ========
    @Around("@annotation(loggable)")
    public Object handleLoggable(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String label = loggable.value().isEmpty() ? methodName : loggable.value();

        if (loggable.logArgs()) {
            log.info("[LOG] {} dipanggil dengan: {}", label, Arrays.toString(pjp.getArgs()));
        } else {
            log.info("[LOG] {} dipanggil", label);
        }

        Object result = pjp.proceed();

        if (loggable.logResult()) {
            log.info("[LOG] {} mengembalikan: {}", label, result);
        }

        return result;
    }

    // ======== @Auditable Aspect ========
    @Around("@annotation(auditable)")
    public Object handleAuditable(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        // Ambil info user dari SecurityContext
        String user = getCurrentUser();
        String action = auditable.action();
        String resource = auditable.resource();

        Object result = pjp.proceed();

        // Simpan audit log ke database
        AuditLog auditLog = AuditLog.builder()
            .user(user)
            .action(action)
            .resource(resource)
            .timestamp(LocalDateTime.now())
            .build();
        auditLogRepository.save(auditLog);

        log.info("[AUDIT] User {} melakukan {} pada {}", user, action, resource);
        return result;
    }

    // ======== @PerformanceMonitor Aspect ========
    @Around("@annotation(monitor)")
    public Object handlePerformanceMonitor(ProceedingJoinPoint pjp, PerformanceMonitor monitor) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().toShortString();

        try {
            return pjp.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > monitor.warnThresholdMs()) {
                log.warn("[PERF] ⚠️  {} lambat: {} ms (threshold: {} ms)", 
                    methodName, elapsed, monitor.warnThresholdMs());
            } else {
                log.info("[PERF] {} selesai dalam {} ms", methodName, elapsed);
            }
        }
    }
}
```

### Step 3: Gunakan di Service

```java
@Service
public class UserService {

    @Loggable(value = "Create User", logArgs = true, logResult = false)
    @Auditable(action = "CREATE", resource = "USER")
    @PerformanceMonitor(warnThresholdMs = 500)
    public UserDto createUser(CreateUserRequest request) {
        // Hanya logika bisnis murni, tidak ada logging/audit/monitoring code
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Loggable
    @PerformanceMonitor
    public UserDto findById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

---

## 9. Use Cases Nyata di Spring Boot

### 9.1 Exception Handling Terpusat

```java
@Aspect
@Component
@Slf4j
public class ExceptionHandlingAspect {

    @AfterThrowing(
        pointcut = "within(@org.springframework.stereotype.Service *)",
        throwing = "ex"
    )
    public void handleServiceException(JoinPoint jp, Exception ex) {
        String method = jp.getSignature().toShortString();
        
        if (ex instanceof BusinessException) {
            log.warn("[EXCEPTION] Business error di {}: {}", method, ex.getMessage());
        } else {
            log.error("[EXCEPTION] Unexpected error di {}: {}", method, ex.getMessage(), ex);
            // Kirim notifikasi ke Slack/PagerDuty
            alertingService.sendAlert(method, ex);
        }
    }
}
```

### 9.2 Caching Manual

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    String key();
    long ttlSeconds() default 300;
}

@Aspect
@Component
public class CachingAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(cacheable)")
    public Object handleCacheable(ProceedingJoinPoint pjp, Cacheable cacheable) throws Throwable {
        // Buat cache key dari annotation key + args
        String cacheKey = cacheable.key() + ":" + Arrays.toString(pjp.getArgs());

        // Cek cache
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("[CACHE] Hit untuk key: {}", cacheKey);
            return cached;
        }

        // Eksekusi method asli
        Object result = pjp.proceed();

        // Simpan ke cache
        redisTemplate.opsForValue().set(cacheKey, result, cacheable.ttlSeconds(), TimeUnit.SECONDS);
        log.debug("[CACHE] Miss, disimpan dengan key: {}", cacheKey);

        return result;
    }
}
```

### 9.3 Security / Authorization Check

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}

@Aspect
@Component
public class SecurityAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint jp, RequireRole requireRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Harus login terlebih dahulu");
        }

        boolean hasRole = Arrays.stream(requireRole.value())
            .anyMatch(role -> auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role)));

        if (!hasRole) {
            throw new ForbiddenException(
                "Membutuhkan role: " + Arrays.toString(requireRole.value())
            );
        }
    }
}

// Penggunaan:
@Service
public class AdminService {

    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public void deleteAllUsers() {
        // hanya bisa diakses ADMIN atau SUPER_ADMIN
    }

    @RequireRole("SUPER_ADMIN")
    public void resetSystem() {
        // hanya SUPER_ADMIN
    }
}
```

### 9.4 Retry Mechanism

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int maxAttempts() default 3;
    long delayMs() default 1000;
    Class<? extends Throwable>[] retryOn() default {Exception.class};
}

@Aspect
@Component
@Slf4j
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object handleRetry(ProceedingJoinPoint pjp, Retry retry) throws Throwable {
        String methodName = pjp.getSignature().getName();
        int maxAttempts = retry.maxAttempts();
        
        Throwable lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (attempt > 1) {
                    log.info("[RETRY] Percobaan ke-{} untuk {}", attempt, methodName);
                    Thread.sleep(retry.delayMs() * (attempt - 1)); // exponential backoff
                }
                return pjp.proceed();
                
            } catch (Throwable ex) {
                boolean shouldRetry = Arrays.stream(retry.retryOn())
                    .anyMatch(retryClass -> retryClass.isAssignableFrom(ex.getClass()));
                
                if (!shouldRetry || attempt == maxAttempts) {
                    throw ex;
                }
                
                lastException = ex;
                log.warn("[RETRY] {} gagal (percobaan {}/{}): {}", 
                    methodName, attempt, maxAttempts, ex.getMessage());
            }
        }
        
        throw lastException;
    }
}

// Penggunaan:
@Service
public class ExternalApiService {

    @Retry(maxAttempts = 3, delayMs = 500, retryOn = {IOException.class})
    public ApiResponse callExternalApi(String endpoint) {
        // pemanggilan external API yang mungkin gagal
        return restTemplate.getForObject(endpoint, ApiResponse.class);
    }
}
```

### 9.5 Request/Response Validation & Sanitization

```java
@Aspect
@Component
public class InputSanitizationAspect {

    @Around("execution(* com.example.controller.*.*(..))")
    public Object sanitizeInput(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        
        // Sanitasi string input untuk mencegah XSS
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = sanitize((String) args[i]);
            }
        }
        
        return pjp.proceed(args);
    }
    
    private String sanitize(String input) {
        return input == null ? null : input
            .replaceAll("<script>", "")
            .replaceAll("</script>", "")
            .trim();
    }
}
```

---

## 10. AOP Proxy: JDK vs CGLIB

Spring AOP menggunakan **Proxy Pattern**. Ada dua jenis proxy:

### JDK Dynamic Proxy

```
Caller ──▶ JDK Proxy (implements interface) ──▶ Target Object
```

- Digunakan ketika target object **mengimplementasi interface**
- Proxy mengimplementasi **interface yang sama**
- Lebih ringan

### CGLIB Proxy

```
Caller ──▶ CGLIB Proxy (subclass dari target) ──▶ Target Object
```

- Digunakan ketika target **tidak mengimplementasi interface**
- Proxy adalah **subclass** dari target class
- Default di Spring Boot

### Konfigurasi

```java
// Paksa CGLIB untuk semua (tidak perlu interface)
@EnableAspectJAutoProxy(proxyTargetClass = true)

// Atau di application.properties:
// spring.aop.proxy-target-class=true  (default: true di Spring Boot)
```

### ⚠️ Self-invocation Problem

```java
@Service
public class UserService {

    // ❌ SALAH: Memanggil method lain di dalam class yang sama
    // AOP TIDAK AKAN BERJALAN karena bypass proxy!
    public void methodA() {
        methodB(); // memanggil langsung, bukan melalui proxy
    }

    @Transactional
    public void methodB() {
        // @Transactional TIDAK akan berjalan jika dipanggil dari methodA() internal!
    }
}
```

**Solusi:**

```java
@Service
public class UserService {

    @Autowired
    private ApplicationContext context;

    public void methodA() {
        // Ambil proxy dari context
        UserService self = context.getBean(UserService.class);
        self.methodB(); // sekarang melalui proxy ✅
    }

    @Transactional
    public void methodB() {
        // @Transactional berjalan dengan benar
    }
}
```

---

## 11. Limitations & Best Practices

### ❌ Keterbatasan AOP

1. **Self-invocation**: Method yang dipanggil dari dalam class yang sama tidak ter-intercept
2. **Private methods**: Tidak bisa di-intercept (karena proxy)
3. **Static methods**: Tidak bisa di-intercept
4. **Final classes/methods**: Tidak bisa di-intercept dengan CGLIB
5. **Constructor**: Tidak bisa di-intercept (AspectJ full bisa, tapi Spring AOP tidak)

### ✅ Best Practices

```java
// ✅ DO: Gunakan @Pointcut untuk reuse
@Aspect
@Component
public class BestPracticeAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    @Before("serviceLayer()")
    public void serviceAdvice(JoinPoint jp) { /* ... */ }
}

// ✅ DO: Gunakan @Around untuk use case yang butuh kontrol penuh
// ✅ DO: Gunakan annotation-based pointcut untuk lebih eksplisit
// ✅ DO: Berikan @Order yang jelas jika ada multiple aspects
// ✅ DO: Handle exception dengan benar di @Around (re-throw jika perlu)

// ❌ DON'T: Terlalu banyak logic bisnis di Aspect
// ❌ DON'T: Lupa memanggil pjp.proceed() di @Around
// ❌ DON'T: Gunakan AOP untuk hal yang seharusnya di service
```

### Testing Aspect

```java
@SpringBootTest
class LoggingAspectTest {

    @Autowired
    private UserService userService; // ini adalah proxy, bukan object asli

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldLogMethodExecution() {
        // Capture log output
        // ...
        userService.createUser(new CreateUserRequest("test", "test@example.com"));
        // Verify log was called
    }

    @Test
    void aspectIsApplied() {
        // Pastikan UserService adalah proxy
        assertThat(AopUtils.isAopProxy(userService)).isTrue();
        assertThat(AopUtils.isCglibProxy(userService)).isTrue();
    }
}
```

---

## 12. Ringkasan Cheat Sheet

### Quick Reference: Pilih Advice yang Tepat

| Kebutuhan | Advice yang Digunakan |
|-----------|----------------------|
| Log sebelum method | `@Before` |
| Log setelah method (selalu) | `@After` |
| Akses return value | `@AfterReturning` |
| Handle/log exception | `@AfterThrowing` |
| Ukur waktu eksekusi | `@Around` |
| Modifikasi arguments | `@Around` |
| Modifikasi return value | `@Around` |
| Cek security/role | `@Before` |
| Retry on failure | `@Around` |
| Caching | `@Around` |
| Audit trail | `@Around` atau `@AfterReturning` |

### Quick Reference: Pointcut Expressions

```java
// Package tertentu
"execution(* com.example.service.*.*(..))"

// Annotation tertentu
"@annotation(com.example.annotation.Loggable)"

// Class ber-annotation @Service
"@within(org.springframework.stereotype.Service)"

// Kombinasi
"within(com.example.service.*) && !@annotation(com.example.annotation.NoLog)"
```

### Skeleton Aspect Template

```java
@Aspect
@Component
@Slf4j
@Order(10) // Tentukan urutan
public class MyAspect {

    // Definisi pointcut
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void myPointcut() {}

    // Before advice
    @Before("myPointcut()")
    public void before(JoinPoint jp) {
        // code here
    }

    // Around advice (paling umum dipakai)
    @Around("myPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // before
        try {
            Object result = pjp.proceed();
            // after returning
            return result;
        } catch (Throwable ex) {
            // after throwing
            throw ex;
        } finally {
            // always after
        }
    }
}
```

---

## 📖 Referensi

- [Spring Framework Docs - AOP](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)
- [AspectJ Documentation](https://www.eclipse.org/aspectj/doc/released/progguide/index.html)
- [Spring Boot AOP Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/auto-configuration-classes.html)

---

*Dibuat untuk keperluan belajar Spring AOP - Covers: Advice Types, Pointcut Expressions, JoinPoint, Ordering, Custom Annotations, Real-world Use Cases, Proxy Mechanism, Best Practices*
