package com.bisma.foundation.spring_core_ioc_di.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
@Component
public class DatabaseConfig {
    private final String poolName = "MainPool";
    private final int maxConnections = 10;
    private int activeConnections = 0;
    private boolean initialized = false;

    @PostConstruct
    public void init() {

        System.out.println("@PostConstruct being called");
        System.out.println("[DATABASE] initalize database");
        this.initialized = true;
        this.activeConnections = maxConnections;
        System.out.println("[DATABASE] Pool " + poolName + " Siap digunakan");

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


    @PreDestroy
    public void shutDown() {
        System.out.println("@PreDestroy called to destroy db");
        System.out.println("[DATABASE] connection closed");
        this.initialized = false;
        this.activeConnections = 0;
        System.out.println("[DATABASE] Pooling " + poolName + " ditutup");
    }
}
