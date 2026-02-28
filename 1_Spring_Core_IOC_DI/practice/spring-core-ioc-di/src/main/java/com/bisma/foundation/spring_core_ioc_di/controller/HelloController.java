package com.bisma.foundation.spring_core_ioc_di.controller;

import com.bisma.foundation.spring_core_ioc_di.service.GreetingService;
import org.springframework.stereotype.Component;

@Component
public class HelloController {
    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }


    public void handleRequest(String name) {
        String result = greetingService.greet(name);
        System.out.println("Controller menerima: " + result);
    }
}
