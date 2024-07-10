package mtn.momo.contract.repayment.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtUtil {
//    @Value("${jwt.token.expiry.time}")
    private Integer jwtTokenExpiryTimeMs = 1800000;

    private final String SECRET_KEY = getNewSecreteKey();

    /**
     * Generates a new secret key for JWT encoding.
     *
     * @return A Base64 encoded secret key.
     */
    private String getNewSecreteKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate the key
        byte[] encodedKey = key.getEncoded();
        System.out.println("Key length in bits: " + encodedKey.length * 8); // Log the key length in bits
        return Encoders.BASE64.encode(encodedKey);
    }

    /**
     * Extracts the username from the JWT.
     *
     * @param token JWT from which the username is extracted.
     * @return The username from the JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT.
     *
     * @param token JWT from which the expiration date is extracted.
     * @return The expiration date of the JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * General method to extract claims from the JWT.
     *
     * @param token JWT from which the claims are extracted.
     * @param claimsResolver Function that extracts the required information from the claims.
     * @return Extracted claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT.
     *
     * @param token JWT from which claims are to be extracted.
     * @return Claims object containing all the claims from JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    /**
     * Checks if the token has expired.
     *
     * @param token JWT to check for expiration.
     * @return true if the token has expired, otherwise false.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT using the username of the user.
     *
     * @param userDetails UserDetails object containing user information.
     * @return A new JWT.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Helper method to create a JWT.
     *
     * @param claims Claims to include in the JWT.
     * @param subject The subject for whom the token is created.
     * @return A newly created JWT.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenExpiryTimeMs))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Validates the JWT token.
     *
     * @param token JWT to validate.
     * @param userDetails UserDetails against which to validate the JWT.
     * @return true if the token is valid, otherwise false.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
