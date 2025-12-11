package dev.byowa.hdp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() throws Exception {
        jwtService = new JwtService();
        setField(jwtService, "secret", "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        // 1 hour default for non-expiry assertions
        setField(jwtService, "expirationMs", 3600_000L);
    }

    @Test
    void generateToken_containsSubjectAndRole_andIsNotExpiredInitially() {
        String token = jwtService.generateToken("alice", "PATIENT", "");
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        assertEquals("alice", username);
        assertEquals("PATIENT", role);
        assertFalse(jwtService.isExpired(token));
    }

    @Test
    void tokenExpiresWhenExpirationIsShort() throws Exception {
        // 0 ms expiration -> immediately expired; current implementation throws when parsing expired tokens
        setField(jwtService, "expirationMs", 0L);
        String token = jwtService.generateToken("bob", "ADMIN", "");
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtService.isExpired(token));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
