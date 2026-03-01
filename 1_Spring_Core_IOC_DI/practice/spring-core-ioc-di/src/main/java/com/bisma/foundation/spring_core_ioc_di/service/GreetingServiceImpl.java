package com.bisma.foundation.spring_core_ioc_di.service;

public class GreetingServiceImpl implements GreetingService{

    private final String greetingWorld;

    public GreetingServiceImpl(String greetingWorld) {
        this.greetingWorld = greetingWorld;
    }


    @Override
    public String greet(String name) {
        return greetingWorld + " " + name;
    }

    @Override
    public String farewell(String name) {
        return "";
    }
}
