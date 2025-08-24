package com.tkahng.spring_auth.auth.dto;

public enum AuthProvider {
    CREDENTIALS {
        @Override
        public String toString() {
            return "credentials";
        }
    },
    GOOGLE {
        @Override
        public String toString() {
            return "google";
        }
    },
    GITHUB {
        @Override
        public String toString() {
            return "github";
        }
    };

    public static AuthProvider fromString(String value) {
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.toString()
                    .equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown auth provider: " + value);
    }
}
