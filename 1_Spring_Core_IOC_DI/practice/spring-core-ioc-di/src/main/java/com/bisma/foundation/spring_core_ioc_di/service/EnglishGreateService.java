package com.bisma.foundation.spring_core_ioc_di.service;

public class EnglishGreateService implements GreetingService{
    //TODO

    @Override
    public String greet(String name) {
        return "Hello how is it going " + name + " ?";
    }

    @Override
    public String farewell(String name) {
        return "bye " + name + " see u latter";
    }
}

