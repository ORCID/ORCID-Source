package org.orcid.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Before;
import org.junit.Test;
import org.orcid.utils.ExpiringLinkService.ExpiringLinkServiceConfig;
import org.orcid.utils.ExpiringLinkService.ExpiringLinkType;
import org.orcid.utils.ExpiringLinkService.VerificationResult;
import org.orcid.utils.ExpiringLinkService.VerificationStatus;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExpiringLinkServiceTest {
    private static final String TEST_KEY = "a-very-secret-and-sufficiently-long-key-for-testing-hs256";
    private static final String OTHER_TEST_KEY = "another-different-but-valid-long-key-for-testing-hs256";

    private ExpiringLinkService expiringLinkService;

    @Before
    public void setUp() throws JOSEException {
        ExpiringLinkServiceConfig config = new ExpiringLinkServiceConfig();
        config.setKey(TEST_KEY);
        expiringLinkService = new ExpiringLinkService(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_shouldThrowIllegalArgumentException_whenKeyIsNull() throws JOSEException {
        ExpiringLinkServiceConfig config = new ExpiringLinkServiceConfig();
        config.setKey(null);
        new ExpiringLinkService(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_shouldThrowIllegalArgumentException_whenKeyIsEmpty() throws JOSEException {
        ExpiringLinkServiceConfig config = new ExpiringLinkServiceConfig();
        config.setKey("");
        new ExpiringLinkService(config);
    }

    @Test(expected = KeyLengthException.class)
    public void constructor_shouldThrowKeyLengthException_whenKeyIsTooShort() throws JOSEException {
        ExpiringLinkServiceConfig config = new ExpiringLinkServiceConfig();
        config.setKey("short-key");
        new ExpiringLinkService(config);
    }

    @Test
    public void generateAndVerifyToken_shouldBeValid() throws JOSEException, ParseException {
        String subject = "test-user-123";
        long durationInMinutes = 15;
        ExpiringLinkType type = ExpiringLinkType.ACCOUNT_DEACTIVATION;

        String token = expiringLinkService.generateExpiringToken(subject, durationInMinutes, type);
        assertNotNull("Token should not be null", token);

        VerificationResult result = expiringLinkService.verifyToken(token);

        // Check status
        assertNotNull("Verification result should not be null", result);
        assertEquals("Token should be valid", VerificationStatus.VALID, result.getStatus());

        // Check claims
        JWTClaimsSet claims = result.getClaims();
        assertNotNull("Claims should not be null for a valid token", claims);
        assertEquals("Subject should match", subject, claims.getSubject());
        assertEquals("Type claim should match", type.name(), claims.getClaim("type"));
        assertTrue("Expiration time should be in the future", claims.getExpirationTime().after(new Date()));
        assertNotNull("Issue time should be set", claims.getIssueTime());
    }

    @Test
    public void verifyToken_shouldReturnExpired() throws JOSEException {
        String subject = "test-user-expired";
        long durationInMinutes = -5; // 5 minutes in the past
        ExpiringLinkType type = ExpiringLinkType.ACCOUNT_DEACTIVATION;

        String token = expiringLinkService.generateExpiringToken(subject, durationInMinutes, type);
        VerificationResult result = expiringLinkService.verifyToken(token);

        assertEquals("Token should be expired", VerificationStatus.EXPIRED, result.getStatus());
        assertNull("Claims should be null for an expired token", result.getClaims());
    }

    @Test
    public void verifyToken_shouldReturnInvalid_forBadSignature() throws JOSEException {
        // Create a token with a different key
        ExpiringLinkServiceConfig badConfig = new ExpiringLinkServiceConfig();
        badConfig.setKey(OTHER_TEST_KEY);
        ExpiringLinkService badSignerService = new ExpiringLinkService(badConfig);
        String token = badSignerService.generateExpiringToken("user", 10, ExpiringLinkType.ACCOUNT_DEACTIVATION);

        // Verify with the original service
        VerificationResult result = expiringLinkService.verifyToken(token);

        assertEquals("Token signature should be invalid", VerificationStatus.INVALID, result.getStatus());
        assertNull("Claims should be null for an invalid token", result.getClaims());
    }

    @Test
    public void verifyToken_shouldReturnInvalid_forMalformedToken() {
        String malformedToken = "not.a.real.token";
        VerificationResult result = expiringLinkService.verifyToken(malformedToken);

        assertEquals("Malformed token should be invalid", VerificationStatus.INVALID, result.getStatus());
        assertNull("Claims should be null for an invalid token", result.getClaims());
    }

    @Test
    public void verifyToken_shouldReturnInvalid_forEmptyToken() {
        VerificationResult result = expiringLinkService.verifyToken("");
        assertEquals("Empty token should be invalid", VerificationStatus.INVALID, result.getStatus());
        assertNull("Claims should be null for an invalid token", result.getClaims());
    }

    @Test
    public void generateAndVerifyToken_shouldBeValid_whenTypeIsNull() throws JOSEException, ParseException {
        String subject = "user-no-type";
        String token = expiringLinkService.generateExpiringToken(subject, 10, null);
        assertNotNull(token);

        VerificationResult result = expiringLinkService.verifyToken(token);

        assertEquals(VerificationStatus.VALID, result.getStatus());
        assertNotNull(result.getClaims());
        assertEquals(subject, result.getClaims().getSubject());
        assertNull("Type claim should be null", result.getClaims().getClaim("type"));
    }

    @Test
    public void generateAndVerifyToken_shouldBeValid_whenSubjectIsNull() throws JOSEException, ParseException {
        String token = expiringLinkService.generateExpiringToken(null, 10, ExpiringLinkType.ACCOUNT_DEACTIVATION);
        assertNotNull(token);

        VerificationResult result = expiringLinkService.verifyToken(token);

        assertEquals(VerificationStatus.VALID, result.getStatus());
        assertNotNull(result.getClaims());
        assertNull("Subject should be null", result.getClaims().getSubject());
        assertEquals(ExpiringLinkType.ACCOUNT_DEACTIVATION.name(), result.getClaims().getClaim("type"));
    }
}