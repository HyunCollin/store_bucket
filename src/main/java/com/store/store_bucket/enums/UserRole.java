package com.store.store_bucket.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public static UserRole getRoleByUserId(String userId) {
        if ("superuser".equals(userId)) {
            return ADMIN;
        } else {
            return USER;
        }
    }
}
