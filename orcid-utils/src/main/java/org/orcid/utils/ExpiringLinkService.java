package org.orcid.utils;

import org.apache.commons.lang.StringUtils;
import java.nio.charset.StandardCharsets;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;


public class ExpiringLinkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiringLinkService.class);

    private final JWSSigner signer;
    private final JWSVerifier verifier;
    private final JWSAlgorithm algorithm = JWSAlgorithm.HS256;

    public enum ExpiringLinkType {
        ACCOUNT_DEACTIVATION
    }

    public enum VerificationStatus {
        VALID,
        EXPIRED,
        INVALID
    }

    public static class VerificationResult {
        private final VerificationStatus status;
        private final JWTClaimsSet claims;

        private VerificationResult(VerificationStatus status, JWTClaimsSet claims) {
            this.status = status;
            this.claims = claims;
        }

        public static VerificationResult valid(JWTClaimsSet claims) {
            return new VerificationResult(VerificationStatus.VALID, claims);
        }

        public static VerificationResult expired() {
            return new VerificationResult(VerificationStatus.EXPIRED, null);
        }

        public static VerificationResult invalid() {
            return new VerificationResult(VerificationStatus.INVALID, null);
        }

        public VerificationStatus getStatus() {
            return status;
        }

        public JWTClaimsSet getClaims() {
            return this.claims;
        }
    }


    public static class ExpiringLinkServiceConfig {
        private String key;

        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
    }

    public ExpiringLinkService(ExpiringLinkServiceConfig config) throws KeyLengthException, JOSEException {
        String secretString = config.getKey();

        if (StringUtils.isEmpty(secretString)) {
            throw new IllegalArgumentException("No 'org.orcid.utils.jwtKey' provided. Application cannot start.");
        }

        byte[] secretKey = secretString.getBytes(StandardCharsets.UTF_8);

        this.signer = new MACSigner(secretKey);
        this.verifier = new MACVerifier(secretKey);
    }

    /**
     * Generates a complete, expiring link containing a JWT.
     *
     * @param subject The primary identifier, such as a user ID.
     * @param durationInMinutes The number of minutes until the link expires.
     * @param type The purpose of the link (e.g., "ACCOUNT_DEACTIVATION").
     * @return A JWT string.
     * @throws JOSEException if there is an error signing the token.
     */
    public String generateExpiringToken(String subject, long durationInMinutes, ExpiringLinkType type) throws JOSEException {
        long nowMillis = System.currentTimeMillis();
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(subject)
                .issueTime(new Date(nowMillis))
                .expirationTime(new Date(nowMillis + durationInMinutes * 60 * 1000));

        // Add all custom claims to the token payload
        if (type != null) {
            claimsBuilder.claim("type", type.name());
        }

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(this.algorithm), claimsBuilder.build());
        signedJWT.sign(this.signer);

        return signedJWT.serialize();
    }

    /**
     * Verifies a JWT string by checking its signature and expiration time.
     *
     * @param token A JWT string
     * @return An VerificationResult object containing the claims set and indicating whether the token is valid or not.
     */
    public VerificationResult verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(this.verifier)) {
                return VerificationResult.invalid();
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (new Date().after(expirationTime)) {
                return VerificationResult.expired();
            }

            return VerificationResult.valid(signedJWT.getJWTClaimsSet());

        } catch (ParseException | JOSEException e) {
            LOGGER.error("Error parsing or verifying token", e);
            return VerificationResult.invalid();
        }
    }
}