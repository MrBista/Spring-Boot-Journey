package com.bisma.foundation.spring_core_ioc_di.config;

import com.bisma.foundation.spring_core_ioc_di.service.EmailNotificationService;
import com.bisma.foundation.spring_core_ioc_di.service.GreetingService;
import com.bisma.foundation.spring_core_ioc_di.service.GreetingServiceImpl;
import com.bisma.foundation.spring_core_ioc_di.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean("gService")
    public GreetingService greetingService() {
        return new GreetingServiceImpl("Hallo");
    }



    @Bean
    public NotificationService notificationService(EmailProperties emailProperties) {
        System.out.println("[PropertiesConfig] Email config: " + emailProperties.toString());
        return new EmailNotificationService(
                emailProperties.getHost(),
                emailProperties.getPort(),
                emailProperties.getUsername()
        );
    }
}
