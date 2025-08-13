package com.tkahng.spring_auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public String admin() {
        return "admin";
    }

    @GetMapping("/advanced")
    @PreAuthorize("hasRole('advanced')")
    public String advanced() {
        return "advanced";
    }

    @GetMapping("/pro")
    @PreAuthorize("hasRole('pro')")
    public String pro() {
        return "pro";
    }

    @GetMapping("/basic")
    @PreAuthorize("hasRole('basic')")
    public String basic() {
        return "basic";
    }
}
