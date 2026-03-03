package com.bisma.foundation.aop_learn.config;

import com.bisma.foundation.aop_learn.properties.EmailProperties;
import com.bisma.foundation.aop_learn.service.EmailNotification;
import com.bisma.foundation.aop_learn.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ServiceConfig {

    @Bean("notificationService")
    public NotificationService notificationService(EmailProperties emailProperties) {
        return new EmailNotification(emailProperties.getPort(), emailProperties.getHost(), emailProperties.getUsername(), emailProperties.getPassword());
    }
}
