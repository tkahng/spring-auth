//package com.tkahng.spring_auth.service;
//
//import com.tkahng.spring_auth.dto.EmailDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.mail.javamail.MimeMessagePreparator;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.util.UriComponentsBuilder;
//
//
//@Slf4j
//@Service
//public class MailServiceImpl implements MailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${app.url:http://localhost:8080}")
//    private String baseUrl;
//
//    @Value("${spring.mail.username:default@example.com}")
//    private String from;
//
//    public MailServiceImpl(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public String buildVerificationUrl(String token) {
//        return UriComponentsBuilder.fromUriString(baseUrl) // recommended instead of fromHttpUrl
//                .path("/api/auth/confirm-verification")
//                .queryParam("token", token)
//                .build()
//                .toUriString();
//    }
//
//    public String sendVerificationMail(String token) {
//        return """
//                <h2>Confirm your email</h2>
//                <p>Follow this link to confirm your email:</p>
//                <p><a href="%s">Confirm your email address</a></p>
//                """.formatted(token);
//    }
//
//    @Async
//    public void sendMail(EmailDto notificationEmail) throws Exception {
//        MimeMessagePreparator messagePreparator = mimeMessage -> {
//            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
//            messageHelper.setFrom(from);
//            messageHelper.setTo(notificationEmail.getRecipient());
//            messageHelper.setSubject(notificationEmail.getSubject());
//            messageHelper.setText(notificationEmail.getBody(), true);
//        };
//        try {
//            mailSender.send(messagePreparator);
//            log.info("Activation email sent!!");
//        } catch (MailException e) {
//            log.error("Exception occurred when sending mail", e);
//            throw new Exception(
//                    "Exception occurred when sending mail to " + notificationEmail.getRecipient(), e);
//        }
//    }
//}