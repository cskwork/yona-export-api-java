package com.export.yona;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequiredSecretTests {
    @Test
    void missingOrBlankSecretFailsWithoutEchoingInput() {
        for (String value : new String[] { null, "", "   " }) {
            IllegalStateException error = assertThrows(IllegalStateException.class,
                    () -> RequiredSecret.read(value, "YONA_ADMIN_API_TOKEN"));
            assertEquals("Missing required environment variable: YONA_ADMIN_API_TOKEN", error.getMessage());
        }
    }

    @Test
    void explicitEnvironmentSecretIsAccepted() {
        assertEquals("offline-fixture", RequiredSecret.read(" offline-fixture ", "YONA_ADMIN_API_TOKEN"));
    }
}
