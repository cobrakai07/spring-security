package com.example.springSecurity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MyTestController {
    @GetMapping("/test")
    public String fun(){
        return "Testing endpoint...!";
    }
}
