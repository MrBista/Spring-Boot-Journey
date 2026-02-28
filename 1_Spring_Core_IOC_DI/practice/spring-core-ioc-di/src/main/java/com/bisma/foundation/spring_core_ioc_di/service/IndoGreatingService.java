package com.bisma.foundation.spring_core_ioc_di.service;

import org.springframework.stereotype.Service;

@Service
public class IndoGreatingService implements GreetingService{
    @Override
    public String greet(String name) {
        return "Hallo " + name + " apa kabar ?";
    }

    @Override
    public String farewell(String name) {
        return "Sampai jumpa lagi " + name;
    }
}
