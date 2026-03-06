package com.bisma.foundation.fundamental_spring_boot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class HelloWorl {


    @GetMapping
    public ResponseEntity<String> getHelloWorld() {
        return ResponseEntity.of(Optional.of("Hai Dunia"));
    }
}
