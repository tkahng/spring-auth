package com.tkahng.spring_auth.filter;


import com.tkahng.spring_auth.repository.UserRepository;
import com.tkahng.spring_auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String email = jwtService.extractUsername(token);

        if (email != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            var user = userRepository.findByEmail(email)
                    .orElse(null);
            if (user != null) {
                var auth = new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(user), null, new CustomUserDetails(user).getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext()
                        .setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}
