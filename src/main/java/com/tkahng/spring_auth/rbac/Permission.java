package com.tkahng.spring_auth.rbac;

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
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String name;

    @Column(nullable = true, columnDefinition = "text")
    private String description;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime updatedAt;


}
