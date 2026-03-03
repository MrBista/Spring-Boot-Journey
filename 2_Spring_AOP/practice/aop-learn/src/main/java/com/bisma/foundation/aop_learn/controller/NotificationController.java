package com.bisma.foundation.aop_learn.controller;

import com.bisma.foundation.aop_learn.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;

public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(@Qualifier("notificationService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public String send() {
        notificationService.send("bisma@mail.com", "Hai Aku Kirim ya");
        return "Success";
    }
}
