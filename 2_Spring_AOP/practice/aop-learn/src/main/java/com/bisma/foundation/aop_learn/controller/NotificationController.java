package com.bisma.foundation.aop_learn.controller;

import com.bisma.foundation.aop_learn.anotation.PerformanceMonitor;
import com.bisma.foundation.aop_learn.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;

public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(@Qualifier("notificationService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PerformanceMonitor
    public String send() {
        notificationService.send("bisma@mail.com", "Hai Aku Kirim ya");
        return "Success";
    }

    @PerformanceMonitor(warnThresholdMs = 1_00)
    public void sendTrhowTest() throws InterruptedException {
        Thread.sleep(5_00);
        throw new RuntimeException("Oopss! terjadi kesalahan");
    }
}
