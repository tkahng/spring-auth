package com.tkahng.spring_auth.dto;

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
    }
}
