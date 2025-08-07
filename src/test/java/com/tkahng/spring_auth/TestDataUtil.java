package com.tkahng.spring_auth;

import com.tkahng.spring_auth.domain.User;

public final class TestDataUtil {

    private TestDataUtil() {
        // no-op
    }


    public static User createTestAuthor() {
        return User.builder()
                .name("name")
                .email("email")
                .build();
    }


}
