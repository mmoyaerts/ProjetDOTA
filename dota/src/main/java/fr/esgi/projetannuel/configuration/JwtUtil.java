package fr.esgi.projetannuel.configuration;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 86400000; // 1 jour

    // Générer un token
    /*public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }*/
    public String generateToken(String username, String role) {
        Map<String,Object> claims = Map.of("role", role);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .addClaims(claims)
                .signWith(key)
                .compact();
    }

    // Lire et valider le token
    /*public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }*/
    public Claims validateTokenAndGetClaims(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return jws.getBody();
        } catch (JwtException e) {
            return null;
        }
    }

    /** Extrait simplement le username (sub) */
    public String getUsername(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    /** Extrait le rôle */
    public String getRole(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return (claims != null) ? claims.get("role", String.class) : null;
    }
}

