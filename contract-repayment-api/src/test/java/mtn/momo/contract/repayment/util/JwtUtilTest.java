package mtn.momo.contract.repayment.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
        when(userDetails.getUsername()).thenReturn("mtn.momo@mtn.co.za");
//        when(properties.getTokenExpiryTime()).thenReturn(3600000);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals("mtn.momo@mtn.co.za", username);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testExpiredToken() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Use reflection to access the private SECRET_KEY field
        java.lang.reflect.Field secretKeyField = JwtUtil.class.getDeclaredField("SECRET_KEY");
        secretKeyField.setAccessible(true);
        String secretKey = (String) secretKeyField.get(jwtUtil);

        // Generate token with short expiration time
        String token = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000)) // 1 second expiration
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Thread.sleep(2000);

        try {
            jwtUtil.validateToken(token, userDetails);
            fail("Expected ExpiredJwtException to be thrown");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Expected exception
        }
    }
}
