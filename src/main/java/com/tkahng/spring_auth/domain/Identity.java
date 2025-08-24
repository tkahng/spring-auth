package com.tkahng.spring_auth.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "identities")
public class Identity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String providerId;
    @Column(nullable = false)
    private String accountId;
    @Column(nullable = true, name = "password_hash")
    private String passwordHash;
    @Column(nullable = true)
    private String refreshToken;
    @Column(nullable = true)
    private String accessToken;
    @Column(nullable = true)
    private Long expiresAt;
    @Column(nullable = true)
    private String idToken;
    @Column(nullable = true)
    private String scope;
    @Column(nullable = true)
    private String sessionState;
    @Column(nullable = true)
    private String tokenType;


    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
