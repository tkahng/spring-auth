package com.tkahng.spring_auth.service;

public interface PasswordService {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
