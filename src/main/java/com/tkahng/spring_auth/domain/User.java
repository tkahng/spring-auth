package com.tkahng.spring_auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private OffsetDateTime emailVerifiedAt;

    @Column(nullable = true)
    private String image;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime updatedAt;

//    @OneToMany(mappedBy = "user")
//    private List<Account> accounts = new ArrayList<>();
}
