package com.tkahng.spring_auth.command;

import com.tkahng.spring_auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("superuser")
@AllArgsConstructor
public class SuperUserCommand implements CommandLineRunner {
    private final AuthService authService;

    @Override
    public void run(String... args) {
        if (args.length < 2) {
            System.err.println("Usage: --args='<email> <password>'");
        }
        String email = args[0];
        String password = args[1];
        var existingSuperUser = authService.findUserByEmail(email);
        if (existingSuperUser.isPresent()) {
            System.out.println("Super user already exists with email: " + email);
            return;
        }
        

        System.out.println("Super user created: " + email);
    }
}