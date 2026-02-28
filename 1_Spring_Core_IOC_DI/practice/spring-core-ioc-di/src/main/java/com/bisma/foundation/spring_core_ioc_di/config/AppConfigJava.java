package com.bisma.foundation.spring_core_ioc_di.config;

import com.bisma.foundation.spring_core_ioc_di.controller.HelloController;
import com.bisma.foundation.spring_core_ioc_di.service.GreetingService;
import com.bisma.foundation.spring_core_ioc_di.service.IndoGreatingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigJava {

    @Bean
    public GreetingService greetingServiceIndo() {
        GreetingService greetingService = new IndoGreatingService();
        return greetingService;
    }

    @Bean
    public HelloController helloController() {
        return new HelloController(greetingServiceIndo());
    }

}
