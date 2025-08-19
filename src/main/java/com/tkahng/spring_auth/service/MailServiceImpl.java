package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final TokenService tokenService;
    private final MailSender mailService;
    @Value("${app.url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendVerificationMail(User user) throws Exception {
        var token = tokenService.generateEmailVerificationToken(user.getEmail());
        var link = buildVerificationUrl(token);
        var mailContent = buildVerificationMail(link);
        var dto = EmailDto.builder()
                .subject("Confirm your email")
                .body(mailContent)
                .recipient(user.getEmail())
                .build();

        mailService.sendMail(dto);
    }

    private String buildVerificationUrl(String token) {
        return UriComponentsBuilder.fromUriString(baseUrl) // recommended instead of fromHttpUrl
                .path("/api/auth/confirm-verification")
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private String buildVerificationMail(String verificationLink) {
        return """
                <h2>Confirm your email</h2>
                <p>Follow this link to confirm your email:</p>
                <p><a href="%s">Confirm your email address</a></p>
                """.formatted(verificationLink);
    }
}
