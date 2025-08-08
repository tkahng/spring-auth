package com.tkahng.spring_auth.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UUID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String accountId;
    @Column(nullable = false)
    private String providerId;
    @Column(nullable = true)
    private String accessToken;
    @Column(nullable = true)
    private String refreshToken;
    @Column(nullable = true)
    private String accessTokenExpiresAt;
    @Column(nullable = true)
    private String refreshTokenExpiresAt;
    @Column(nullable = true)
    private String scope;
    @Column(nullable = true)
    private String idToken;
    @Column(nullable = true)
    private String password;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime updatedAt;
}
