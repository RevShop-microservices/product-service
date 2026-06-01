package com.example.product_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "mySuperSecretKeyForJwtSigningMustBeAtLeast256BitsLong!!";
    private Key key;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        jwtUtil.init();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private String generateTestToken(String email, String role, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    void testExtractClaimsAndValidateToken() {
        String email = "test@example.com";
        String role = "CUSTOMER";
        Long userId = 123L;

        String token = generateTestToken(email, role, userId);

        assertTrue(jwtUtil.validateToken(token));
        assertEquals(email, jwtUtil.extractUsername(token));
        assertEquals(role, jwtUtil.extractRole(token));
        assertEquals(userId, jwtUtil.extractUserId(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalidToken"));
        assertFalse(jwtUtil.validateToken(null));
    }
}
