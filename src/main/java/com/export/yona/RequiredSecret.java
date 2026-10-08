package com.export.yona;

final class RequiredSecret {
    private RequiredSecret() {}

    static String read(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value.trim();
    }
}
