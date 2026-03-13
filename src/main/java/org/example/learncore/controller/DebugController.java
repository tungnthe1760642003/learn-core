package org.example.learncore.controller;

import org.example.learncore.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/crash")
    public ApiResponse<String> triggerCrash() {
        throw new RuntimeException("🔥 BOOM! Đây là lỗi thử nghiệm để kiểm tra Sentry.");
    }
}
