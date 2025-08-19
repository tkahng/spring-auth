package com.tkahng.spring_auth.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Accessors(chain = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
    /**
     * email. must be unique. required
     */
    @NonNull
    private String email;
    /**
     * raw plain password
     */
    private String password;

    private String hashedPassword;

    /**
     * name of the user. optional.
     */
    private String name;

    /**
     * provider type. e.g. credentials, google, github. required
     */
    @NonNull
    private AuthProvider provider;

    /**
     * provider specific account id. e.g. google account id, or if credentials, email. required
     */
    @NonNull
    private String accountId;

    private OffsetDateTime emailVerifiedAt;
}