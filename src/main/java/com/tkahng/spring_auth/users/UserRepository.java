package com.tkahng.spring_auth.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.emailVerifiedAt = :emailVerifiedAt WHERE u.id = :id")
    int updateEmailVerifiedAt(@Param("id") UUID id, @Param("emailVerifiedAt") OffsetDateTime emailVerifiedAt);
}
