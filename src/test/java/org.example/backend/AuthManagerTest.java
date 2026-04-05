package org.example.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AuthManagerTest {

    @BeforeEach
    void setup() {
        // reset users file so test is predictable
        File file = new File("src/test/resources/users.csv");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testAuthenticateSuccess() {
        AuthManager auth = new AuthManager();

        // username: admin
        // password: admin

        boolean result = auth.authenticate("admin", "admin");

        assertTrue(result);
    }

    @Test
    void testAuthenticateFail() {
        AuthManager auth = new AuthManager();

        boolean result = auth.authenticate("admin", "wrongpassword");

        assertFalse(result);
    }
}