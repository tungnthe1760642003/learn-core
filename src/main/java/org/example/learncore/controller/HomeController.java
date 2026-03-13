package org.example.learncore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "status", "UP",
            "message", "Chào mừng bạn đến với LearnCore API!",
            "debug_crash_link", "/api/debug/crash",
            "banking_demo_link", "/api/demo/transaction/init"
        );
    }
}
