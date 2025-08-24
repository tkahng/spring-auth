package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.auth.TokenService;
import com.tkahng.spring_auth.dto.EmailDto;
import com.tkahng.spring_auth.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final TokenService tokenService;
    private final MailSender mailService;
    @Value("${spring.application.name:Spring Auth}")
    private String appName;
    @Value("${app.url:http://localhost:8080}")
    private String baseUrl;
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationMail(User user) {
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

    @Override
    public void sendResetPasswordMail(User user) {
        var token = tokenService.generatePasswordResetToken(user.getEmail());
        var link = buildResetPasswordUrl(token);
        var mailContent = buildResetPasswordMail(link, appName);
        var dto = EmailDto.builder()
                .subject("Confirm your email")
                .body(mailContent)
                .recipient(user.getEmail())
                .build();

        mailService.sendMail(dto);
    }

    private String buildVerificationUrl(String token) {
        return UriComponentsBuilder.fromUriString(frontendUrl) // recommended instead of fromHttpUrl
                .path("/auth/confirm-verification")
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

    private String buildResetPasswordUrl(String token) {
        return UriComponentsBuilder.fromUriString(frontendUrl) // recommended instead of fromHttpUrl
                .path("/auth/confirm-password-reset")
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private String buildResetPasswordMail(String resetPasswordLink, String name) {
        return """
                <p>Hello,</p>
                <p>Click on the button below to reset your password.</p>
                <p>
                <a class="btn" href="%s" target="_blank" rel="noopener">Reset password</a>
                </p>
                <p><i>If you didn't ask to reset your password, you can ignore this email.</i></p>
                <p>
                Thanks,<br/>
                 %s
                </p>
                """.formatted(resetPasswordLink, name);
    }
}
