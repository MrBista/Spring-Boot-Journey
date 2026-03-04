# Aspect-Oriented Programming (AOP) in Spring
### Complete Learning Module

---

## 📋 Learning Goals

By the end of this module, you will be able to:

- Explain the core concepts of AOP: join points, pointcuts, advices, and aspects
- Identify practical use cases for AOP in Spring applications
- Implement a **logging aspect** that measures method execution times
- Add a second aspect for **exception handling**
- Integrate aspects into an existing Spring Boot project

**Estimated Time:** 100–120 minutes total

---

## Part 1 — Theory: AOP Fundamentals (30–40 min)

---

### 1.1 Why AOP Exists

In enterprise applications, certain behaviors appear across many classes — logging, security checks, transaction management, performance monitoring. These are called **cross-cutting concerns**.

Without AOP, every class manually handles these concerns:

```java
// ❌ Without AOP — duplicated boilerplate everywhere
@Service
public class OrderService {
    public Order createOrder(OrderRequest req) {
        log.info("→ createOrder called");           // repeated in every method
        long start = System.currentTimeMillis();    // repeated in every method
        try {
            // ← only this is business logic
            Order order = processOrder(req);
            log.info("← createOrder done in {}ms", System.currentTimeMillis() - start);
            return order;
        } catch (Exception e) {
            log.error("✗ createOrder failed: {}", e.getMessage()); // repeated
            throw e;
        }
    }
}
```

AOP solves this by moving that boilerplate into a **single reusable module** called an **Aspect**, applied automatically to matching methods.

---

### 1.2 Core Concepts

#### Join Point

> A **join point** is any point during program execution where an aspect *could* be applied.

In Spring AOP, join points are always **method executions**. When `orderService.createOrder()` is called, that call is a join point.

```
Program execution timeline:
──────────────────────────────────────────────────────────►
         │              │                  │
     join point     join point         join point
  (createOrder)  (findById)          (deleteOrder)
```

#### Pointcut

> A **pointcut** is an *expression* that selects which join points to intercept.

```java
// This pointcut selects ALL methods in the service package
"execution(* com.example.service.*.*(..))"

// This pointcut selects only methods annotated with @Auditable
"@annotation(com.example.annotation.Auditable)"
```

Think of a pointcut as a **query** that answers: *"Which methods should this aspect affect?"*

#### Advice

> An **advice** is the *action* taken at a selected join point — the actual code that runs.

There are five types:

| Advice | When it Runs | Can Modify Result? | Can Stop Execution? |
|--------|--------------|--------------------|---------------------|
| `@Before` | Before the method | No | Only via exception |
| `@After` | Always after (like `finally`) | No | No |
| `@AfterReturning` | After successful return | No | No |
| `@AfterThrowing` | After exception is thrown | No | No |
| `@Around` | Wraps the method entirely | ✅ Yes | ✅ Yes |

#### Aspect

> An **aspect** is a class that combines pointcuts and advices into a reusable module.

```java
@Aspect           // ← marks this as an aspect
@Component        // ← makes it a Spring bean
public class LoggingAspect {

    @Pointcut("execution(* com.example.service.*.*(..))")  // ← pointcut
    public void serviceLayer() {}

    @Before("serviceLayer()")                              // ← advice
    public void logBefore(JoinPoint jp) {
        log.info("Calling: {}", jp.getSignature().getName());
    }
}
```

#### Weaving

> **Weaving** is the process of applying aspects to target objects. Spring does this at **runtime** using proxies.

```
Without AOP:                     With AOP (after weaving):

Caller ──► OrderService          Caller ──► [Proxy] ──► OrderService
                                               │
                                          LoggingAspect runs here
```

---

### 1.3 How Spring AOP Works (Proxy Mechanism)

Spring wraps your beans in a **proxy object**. When a method is called, the proxy intercepts it, runs the advice(s), then delegates to the real object.

```
┌─────────────────────────────────────────────────────────┐
│                    Spring Container                     │
│                                                         │
│   Caller ──► CGLIB Proxy ──► Real OrderService         │
│                  │                                      │
│                  ▼                                      │
│           Aspect logic runs                             │
│           (before/after/around)                         │
└─────────────────────────────────────────────────────────┘
```

**Important limitation:** Self-invocation (calling `this.methodB()` from within the same class) bypasses the proxy and skips AOP. Always call through the proxy (inject self or restructure).

---

### 1.4 AOP Use Cases in Spring

#### Use Case 1 — Logging

The most common use of AOP. Log method entry, exit, arguments, and return values without touching business logic:

```java
@Around("execution(* com.example.service.*.*(..))")
public Object log(ProceedingJoinPoint pjp) throws Throwable {
    log.info("→ {}", pjp.getSignature().getName());
    Object result = pjp.proceed();
    log.info("← {} returned: {}", pjp.getSignature().getName(), result);
    return result;
}
```

#### Use Case 2 — Transaction Management

Spring's own `@Transactional` is implemented as an AOP aspect. It wraps methods in a transaction automatically — you never write `begin/commit/rollback` manually.

```java
// This annotation is processed by Spring's TransactionAspect
@Transactional
public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
    // Spring AOP begins a transaction before this line
    debit(fromId, amount);
    credit(toId, amount);
    // Spring AOP commits here (or rolls back if exception is thrown)
}
```

#### Use Case 3 — Performance Monitoring

Measure how long methods take and log warnings for slow calls:

```java
@Around("execution(* com.example.service.*.*(..))")
public Object measure(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = pjp.proceed();
    long ms = System.currentTimeMillis() - start;
    if (ms > 500) {
        log.warn("⚠ SLOW: {} took {}ms", pjp.getSignature().toShortString(), ms);
    }
    return result;
}
```

#### Other Common Use Cases

| Use Case | How AOP Helps |
|----------|--------------|
| Security / Authorization | `@Before` checks roles before method runs |
| Caching | `@Around` returns cached result, skipping real method |
| Input Validation | `@Before` validates arguments |
| Retry Logic | `@Around` retries on transient failures |
| Audit Trail | `@AfterReturning` records what changed |

---

### 1.5 Pointcut Expression Reference

```java
// All methods in a package
execution(* com.example.service.*.*(..))

// All methods in package + subpackages
execution(* com.example.service..*.*(..))

// Methods with a specific name pattern
execution(* com.example.service.*.find*(..))

// Methods annotated with a custom annotation
@annotation(com.example.annotation.Loggable)

// All classes annotated with @Service
@within(org.springframework.stereotype.Service)

// By bean name
bean(*Service)

// Combine with AND / OR / NOT
execution(* com.example.service.*.*(..)) && !@annotation(com.example.annotation.NoLog)
```

---

## Part 2 — Practice: Logging Method Execution Times (70–80 min)

---

### 2.1 Project Setup

**Add the AOP dependency to `pom.xml`:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

> Spring Boot auto-configures AOP when this dependency is present. No extra `@EnableAspectJAutoProxy` needed.

**Project structure used in examples:**

```
src/main/java/com/example/
├── annotation/
│   ├── TrackTime.java           ← custom annotation
│   └── LogException.java        ← custom annotation (challenge)
├── aspect/
│   ├── PerformanceAspect.java   ← logs execution time
│   └── ExceptionAspect.java     ← handles exceptions (challenge)
├── service/
│   └── ProductService.java      ← example service to integrate into
└── controller/
    └── ProductController.java
```

---

### 2.2 Step 1 — Create the Custom Annotation

Using a custom annotation makes AOP opt-in and explicit — you choose exactly which methods to monitor.

```java
// src/main/java/com/example/annotation/TrackTime.java

package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for execution-time tracking.
 * The PerformanceAspect will log how long the method takes.
 *
 * Usage:
 *   @TrackTime
 *   public Product findById(Long id) { ... }
 *
 *   @TrackTime(warnThresholdMs = 200)
 *   public List<Product> findAll() { ... }
 */
@Target(ElementType.METHOD)            // Can only be applied to methods
@Retention(RetentionPolicy.RUNTIME)    // Must be RUNTIME so AOP can read it
public @interface TrackTime {
    /**
     * Log a warning if execution exceeds this threshold (milliseconds).
     * Default: 1000ms (1 second)
     */
    long warnThresholdMs() default 1000;
}
```

**Why `@Retention(RetentionPolicy.RUNTIME)`?**
Without this, the annotation is discarded after compilation and AOP cannot detect it at runtime.

---

### 2.3 Step 2 — Implement the Performance Aspect

```java
// src/main/java/com/example/aspect/PerformanceAspect.java

package com.example.aspect;

import com.example.annotation.TrackTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(1)   // Run this aspect first (lower = higher priority)
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    /**
     * Intercepts all methods annotated with @TrackTime.
     *
     * The parameter name 'trackTime' must match a method parameter of type TrackTime.
     * Spring automatically binds the annotation instance to this parameter,
     * giving us access to annotation attributes like warnThresholdMs.
     */
    @Around("@annotation(trackTime)")
    public Object measureExecutionTime(ProceedingJoinPoint pjp, TrackTime trackTime)
            throws Throwable {

        // 1. Capture method metadata before execution
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String fullName = className + "." + methodName + "()";

        log.debug("→ Starting: {}", fullName);

        // 2. Record start time
        long startTime = System.currentTimeMillis();

        Object result;
        try {
            // 3. Proceed with actual method execution
            //    Without this call, the method would never run!
            result = pjp.proceed();

        } catch (Throwable ex) {
            // 4a. Still measure time even if an exception occurred
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("✗ {} threw {} after {}ms: {}",
                    fullName,
                    ex.getClass().getSimpleName(),
                    elapsed,
                    ex.getMessage());
            throw ex; // Re-throw — don't suppress the exception
        }

        // 4b. Calculate elapsed time on success
        long elapsed = System.currentTimeMillis() - startTime;

        // 5. Log with appropriate level based on threshold
        long threshold = trackTime.warnThresholdMs();
        if (elapsed > threshold) {
            log.warn("⚠ SLOW METHOD: {} took {}ms (threshold: {}ms)",
                    fullName, elapsed, threshold);
        } else {
            log.info("✓ {} completed in {}ms", fullName, elapsed);
        }

        return result;
    }
}
```

**Key points explained:**

- `@Around("@annotation(trackTime)")` — the parameter name `trackTime` binds the annotation instance automatically. Spring uses it to match the pointcut AND inject the annotation so you can read its attributes.
- `pjp.proceed()` is mandatory — without it, the real method never executes.
- Always re-throw caught exceptions unless you intentionally want to suppress them.

---

### 2.4 Step 3 — Apply to Your Service

```java
// src/main/java/com/example/service/ProductService.java

package com.example.service;

import com.example.annotation.TrackTime;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    /**
     * Default threshold (1000ms) — normal case
     */
    @TrackTime
    public Product findById(Long id) {
        // Simulate DB query
        simulateWork(50);
        return new Product(id, "Product " + id, 99.99);
    }

    /**
     * Custom threshold of 200ms — this method should be fast
     * A warning will appear if it takes more than 200ms
     */
    @TrackTime(warnThresholdMs = 200)
    public List<Product> findAll() {
        simulateWork(300); // Intentionally slow to trigger warning
        return List.of(
            new Product(1L, "Laptop", 1299.99),
            new Product(2L, "Mouse", 29.99)
        );
    }

    /**
     * This method has NO @TrackTime — AOP will NOT run on it.
     * The annotation makes AOP opt-in.
     */
    public Product save(Product product) {
        simulateWork(100);
        return product;
    }

    private void simulateWork(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
```

---

### 2.5 Step 4 — Verify It Works

Run the application and call your service. You should see log output like:

```
INFO  c.e.aspect.PerformanceAspect - ✓ ProductService.findById() completed in 52ms
WARN  c.e.aspect.PerformanceAspect - ⚠ SLOW METHOD: ProductService.findAll() took 303ms (threshold: 200ms)
```

**To verify AOP is active in a test:**

```java
// src/test/java/com/example/aspect/PerformanceAspectTest.java

@SpringBootTest
class PerformanceAspectTest {

    @Autowired
    private ProductService productService; // This is the PROXY, not the real class

    @Test
    void serviceIsWrappedInAopProxy() {
        // Confirm Spring wrapped our service in a proxy
        assertThat(AopUtils.isAopProxy(productService)).isTrue();
    }

    @Test
    void trackTimeAnnotatedMethodIsIntercepted() {
        // This call should trigger the aspect — verify via logs or a spy
        Product product = productService.findById(1L);
        assertThat(product).isNotNull();
        // If aspect intercepted correctly, no exception should be thrown
        // and product is returned normally
    }
}
```

---

### 2.6 Expanding: Package-Level Pointcut Alternative

If you want to track all methods in a package (without requiring `@TrackTime` on each method), use an `execution()` pointcut:

```java
@Aspect
@Component
public class PackageLevelPerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PackageLevelPerformanceAspect.class);

    // Reusable pointcut definition
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void allServiceMethods() {}

    // Exclude methods annotated with @NoTrack
    @Pointcut("allServiceMethods() && !@annotation(com.example.annotation.NoTrack)")
    public void trackableMethods() {}

    @Around("trackableMethods()")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            log.info("{} → {}ms",
                pjp.getSignature().toShortString(),
                System.currentTimeMillis() - start);
        }
    }
}
```

---

## Part 3 — Practical Exercise & Challenge

---

### Exercise: Enhance Your Project with the Logging Aspect

**Goal:** Integrate the `PerformanceAspect` into your existing project.

**Step-by-step tasks:**

1. Add `spring-boot-starter-aop` to your `pom.xml`
2. Create the `@TrackTime` annotation in your `annotation` package
3. Create the `PerformanceAspect` in your `aspect` package
4. Add `@TrackTime` to **at least 3 methods** across your service classes:
   - One method that is expected to be fast (`warnThresholdMs = 100`)
   - One method that is expected to be slower
   - One method that involves external I/O (DB call, HTTP, etc.)
5. Run the application, trigger those methods, and verify the logs appear correctly
6. Write one test confirming the service bean is an AOP proxy

---

### 🏆 Challenge: Add a Second Aspect for Exception Handling

This second aspect centralizes exception handling and notification across all services.

#### Step 1 — Create the `@LogException` Annotation

```java
// src/main/java/com/example/annotation/LogException.java

package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for centralized exception logging.
 * All uncaught exceptions will be intercepted and logged with full context.
 *
 * Usage:
 *   @LogException
 *   public Product findById(Long id) { ... }
 *
 *   @LogException(rethrow = false)  // Swallow exception and return null
 *   public Optional<Product> safeFindById(Long id) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogException {
    /**
     * Whether to re-throw the exception after logging.
     * Default: true (always re-throw)
     */
    boolean rethrow() default true;

    /**
     * Whether to include the full stack trace in the log.
     * Default: true
     */
    boolean includeStackTrace() default true;
}
```

#### Step 2 — Implement the Exception Aspect

```java
// src/main/java/com/example/aspect/ExceptionAspect.java

package com.example.aspect;

import com.example.annotation.LogException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Order(2)   // Run after PerformanceAspect (which has @Order(1))
public class ExceptionAspect {

    private static final Logger log = LoggerFactory.getLogger(ExceptionAspect.class);

    @Around("@annotation(logException)")
    public Object handleException(ProceedingJoinPoint pjp, LogException logException)
            throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        String fullName = className + "." + methodName + "()";
        Object[] args = pjp.getArgs();

        try {
            return pjp.proceed();

        } catch (Exception ex) {
            // Build a rich error message with full context
            String errorMessage = String.format(
                "Exception in %s | Args: %s | Exception: [%s] %s",
                fullName,
                Arrays.toString(args),
                ex.getClass().getSimpleName(),
                ex.getMessage()
            );

            if (logException.includeStackTrace()) {
                log.error(errorMessage, ex);           // Logs full stack trace
            } else {
                log.error(errorMessage);               // Logs message only
            }

            // Optionally notify external systems here:
            // slackNotifier.sendAlert(fullName, ex);
            // metricsService.incrementErrorCounter(className, methodName);

            if (logException.rethrow()) {
                throw ex;   // Re-throw so the caller still gets the exception
            }

            // If rethrow=false, return null (only suitable for Optional-returning methods)
            return null;
        }
    }
}
```

#### Step 3 — Apply Both Aspects Together

```java
@Service
public class ProductService {

    // Both aspects intercept this method:
    // 1. PerformanceAspect measures time (@Order(1) — outermost)
    // 2. ExceptionAspect logs exceptions (@Order(2) — inner)
    @TrackTime(warnThresholdMs = 300)
    @LogException
    public Product findById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive, got: " + id);
        }
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }

    // Only exception handling, no performance tracking
    @LogException(rethrow = false, includeStackTrace = false)
    public Optional<Product> safeFindById(Long id) {
        return productRepository.findById(id);
        // If this throws, ExceptionAspect logs it and returns null
        // The null triggers Optional.ofNullable in the caller
    }
}
```

#### Execution Order Visualization (both aspects applied)

```
Caller
  │
  ▼
PerformanceAspect @Around — start timer          (@Order(1) — outermost)
  │
  ▼
ExceptionAspect @Around — set up try/catch       (@Order(2) — inner)
  │
  ▼
  ┌──── ProductService.findById() ────┐
  │   [actual business logic here]    │
  └───────────────────────────────────┘
  │
  ▼ (exception path)
ExceptionAspect catches exception, logs it, re-throws
  │
  ▼
PerformanceAspect catches re-thrown exception, logs time, re-throws
  │
  ▼
Caller receives exception

  ▼ (success path)
ExceptionAspect — no exception, passes result up
  │
  ▼
PerformanceAspect — logs execution time, passes result up
  │
  ▼
Caller receives result
```

---

### Self-Review Checklist

After implementing both aspects, verify the following:

**PerformanceAspect:**
- [ ] `@TrackTime` annotation created with `warnThresholdMs` attribute
- [ ] Aspect uses `@Around` and calls `pjp.proceed()`
- [ ] Logs method name and execution time on success
- [ ] Logs a `WARN` when threshold is exceeded
- [ ] Still logs time (in catch block) when an exception is thrown
- [ ] Exception is re-thrown after logging

**ExceptionAspect:**
- [ ] `@LogException` annotation created with `rethrow` and `includeStackTrace` attributes
- [ ] Aspect uses `@Around` and catches `Exception`
- [ ] Logs class name, method name, and arguments in the error message
- [ ] Respects `rethrow = false` option
- [ ] `@Order(2)` is set so PerformanceAspect wraps it

**Integration:**
- [ ] Both annotations applied to at least one method together
- [ ] Application logs show correct output from both aspects
- [ ] One test confirms the bean is an AOP proxy
- [ ] AOP does NOT run on methods without the annotations (confirm by checking logs)

---

## Quick Reference — Complete Summary

### Concept Map

```
AOP in Spring
│
├── Aspect          → The class annotated with @Aspect + @Component
├── Join Point      → A method execution (Spring only supports this)
├── Pointcut        → Expression selecting which join points to intercept
├── Advice          → The code that runs at a selected join point
│   ├── @Before         Runs before. Cannot stop execution (unless throws).
│   ├── @After          Runs always after (finally). No result access.
│   ├── @AfterReturning Runs after success. Can read result.
│   ├── @AfterThrowing  Runs after exception. Can read exception.
│   └── @Around         Wraps method. Full control. Must call pjp.proceed().
└── Weaving         → Spring creating proxies that route calls through aspects
```

### Advice Decision Guide

```
Need to run code before?           → @Before
Need to run code regardless?       → @After
Need to access the return value?   → @AfterReturning
Need to react to exceptions?       → @AfterThrowing
Need timing / full control / 
  modify args or result?           → @Around  ← default choice for most use cases
```

### Common Pitfalls

| Pitfall | Problem | Fix |
|---------|---------|-----|
| Forgot `pjp.proceed()` in `@Around` | Method never executes | Always call `pjp.proceed()` |
| Self-invocation | `this.method()` bypasses proxy | Inject self via `ApplicationContext` |
| `@Retention` missing on annotation | AOP can't see annotation at runtime | Add `@Retention(RetentionPolicy.RUNTIME)` |
| Private method | Can't be proxied | Make method package-private or public |
| `@Order` not set | Unpredictable aspect ordering | Add `@Order(n)` to all aspects |
| Swallowing exception silently | Errors disappear with no trace | Always log before suppressing |

---

*Module covers: AOP Fundamentals, Join Points, Pointcuts, Advices, Aspects, Use Cases (Logging / Transaction / Performance), Execution Time Aspect, Exception Handling Aspect, Aspect Ordering, Custom Annotations, Project Integration*
