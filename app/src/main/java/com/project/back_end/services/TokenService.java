package com.project.back_end.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /*
     * Creates the signing key from the secret
     * configured in application.properties.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /*
     * Generate a JWT token.
     *
     * identifier:
     * - Admin   -> username
     * - Doctor  -> email
     * - Patient -> email
     */
    public String generateToken(String identifier) {

        Date now = new Date();

        // Token expires after 7 days
        Date expiration = new Date(
                now.getTime() + 7L * 24 * 60 * 60 * 1000
        );

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * Extract the subject from the JWT.
     *
     * The subject represents:
     * - username for Admin
     * - email for Doctor/Patient
     */
    public String extractIdentifier(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /*
     * Validate token according to user type.
     */
    public boolean validateToken(
            String token,
            String user
    ) {

        try {

            String identifier =
                    extractIdentifier(token);

            if (identifier == null
                    || identifier.isBlank()) {
                return false;
            }

            if (user == null) {
                return false;
            }

            switch (user.toLowerCase()) {

                case "admin":
                    return adminRepository
                            .findByUsername(identifier) != null;

                case "doctor":
                    return doctorRepository
                            .findByEmail(identifier) != null;

                case "patient":
                    return patientRepository
                            .findByEmail(identifier) != null;

                default:
                    return false;
            }

        } catch (Exception e) {

            /*
             * This also handles:
             * - expired tokens
             * - invalid signatures
             * - malformed tokens
             */
            return false;
        }
    }
}
