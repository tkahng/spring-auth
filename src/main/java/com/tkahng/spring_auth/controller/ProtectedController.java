package com.tkahng.spring_auth.controller;

import com.tkahng.spring_auth.annotation.HasPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping("/admin")
    @HasPermission("admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/advanced")
    @HasPermission("advanced")
    public String advanced() {
        return "advanced";
    }

    @GetMapping("/pro")
    @HasPermission("pro")
    public String pro() {
        return "pro";
    }

    @GetMapping("/basic")
    @HasPermission("basic")
    public String basic() {
        return "basic";
    }
}
