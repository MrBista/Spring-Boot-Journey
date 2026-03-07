package com.bisma.foundation.fundamental_spring_boot.controller;

import com.bisma.foundation.fundamental_spring_boot.services.HaiDunia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class HelloWorld {

    @Autowired
    ApplicationContext context;


    @GetMapping
    public ResponseEntity<String> getHelloWorld() {
        HaiDunia helloWorld = context.getBean("haiDunia", HaiDunia.class);
        return ResponseEntity.of(Optional.of(helloWorld.helloWorld("BisMen")));
    }
}
