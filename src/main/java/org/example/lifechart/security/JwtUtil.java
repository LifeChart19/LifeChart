package org.example.lifechart.security;

import io.jsonwebtoken.*;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.token.exp}") long expirationMs
    ) {
        this.secretKey = secretKey;
        this.expirationMs = expirationMs;
    }

    public String createAccessToken(Long userId, String email) {
        return createToken(userId, email, expirationMs);
    }

    public String createRefreshToken(Long userId, String email) {
        return createToken(userId, email, expirationMs * 7); // refresh token은 access보다 7배
    }

    private String createToken(Long userId, String email, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_JWT);
        } catch (MalformedJwtException e) {
            throw new CustomException(ErrorCode.MALFORMED_JWT);
        } catch (SignatureException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.EMPTY_JWT);
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
