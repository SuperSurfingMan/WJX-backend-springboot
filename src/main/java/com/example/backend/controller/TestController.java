package com.example.backend.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@RestController
public class TestController {

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/api/message")
    public String getMessage() {
        return "Hello from Spring Boot!";
    }
}
