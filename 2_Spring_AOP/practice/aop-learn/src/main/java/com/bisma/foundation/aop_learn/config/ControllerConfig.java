package com.bisma.foundation.aop_learn.config;

import com.bisma.foundation.aop_learn.controller.NotificationController;
import com.bisma.foundation.aop_learn.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    @Bean("notificationController")
    public NotificationController notificationController(NotificationService notificationService) {
        return new NotificationController(notificationService);
    }

}
