package com.bisma.foundation.spring_core_ioc_di.controller;

import com.bisma.foundation.spring_core_ioc_di.service.GreetingService;

public class HelloControllerNonAnotation {
    private final GreetingService greetingService;

    public HelloControllerNonAnotation(GreetingService greetingService) {
        this.greetingService = greetingService;
    }


    public void handleRequest(String name) {
        String result = greetingService.greet(name);
        System.out.println("Controller HelloControllerNonAnotation menerima: " + result);
    }
}
