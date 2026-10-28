package org.orcid.frontend.spring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.orcid.frontend.web.exception.PasswordResetRequiredException;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AjaxAuthenticationFailureHandlerTest {

    private final AjaxAuthenticationFailureHandler handler = new AjaxAuthenticationFailureHandler();

    @Test
    public void passwordResetRequiredIsReported() throws Exception {
        JsonNode body = fail(new PasswordResetRequiredException());

        assertFalse(body.get("success").asBoolean());
        assertTrue(body.get("passwordResetRequired").asBoolean());
        assertFalse(body.has("verificationCodeRequired"));
    }

    @Test
    public void badCredentialsDoNotReportPasswordResetRequired() throws Exception {
        JsonNode body = fail(new BadCredentialsException("Bad credentials"));

        assertFalse(body.get("success").asBoolean());
        assertFalse(body.has("passwordResetRequired"));
    }

    @Test
    public void aMissing2FACodeDoesNotReportPasswordResetRequired() throws Exception {
        JsonNode body = fail(new VerificationCodeFor2FARequiredException());

        assertTrue(body.get("verificationCodeRequired").asBoolean());
        assertFalse(body.has("passwordResetRequired"));
    }

    private JsonNode fail(AuthenticationException exception) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.onAuthenticationFailure(new MockHttpServletRequest(), response, exception);
        return new ObjectMapper().readTree(response.getContentAsString());
    }
}
