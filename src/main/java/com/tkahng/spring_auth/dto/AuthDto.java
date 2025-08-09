package com.tkahng.spring_auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
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

    private boolean emailVerified;
}