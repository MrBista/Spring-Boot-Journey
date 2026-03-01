package com.bisma.foundation.spring_core_ioc_di.config;

import com.bisma.foundation.spring_core_ioc_di.service.GreetingService;
import com.bisma.foundation.spring_core_ioc_di.service.GreetingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public GreetingService greetingService() {
        return new GreetingServiceImpl("Hallo");
    }
}
