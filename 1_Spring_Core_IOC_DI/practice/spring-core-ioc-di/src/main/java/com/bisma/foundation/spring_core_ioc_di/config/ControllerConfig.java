package com.bisma.foundation.spring_core_ioc_di.config;

import com.bisma.foundation.spring_core_ioc_di.controller.HelloControllerNonAnotation;
import com.bisma.foundation.spring_core_ioc_di.service.GreetingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    @Bean
    public HelloControllerNonAnotation helloControllerNonAnotation(@Qualifier("greetingService") GreetingService greetingService) {
        return new HelloControllerNonAnotation(greetingService);
    }
}
