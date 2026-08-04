package org.orcid.api.common.security.oauth;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles authentication failures for API requests.
 * 
 * Returns 401 Unauthorized when:
 * - No authentication credentials are provided
 * - Authentication credentials are invalid or malformed
 * 
 * For authorization failures (authenticated but lacks permission), see OrcidAPIAccessDeniedHandler (403 Forbidden).
 */
public class APIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FORBIDDEN_MSG = "An Authentication object was not found in the SecurityContext";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try {
            String developerMessage = authException != null && authException.getMessage() != null
                ? authException.getMessage()
                : FORBIDDEN_MSG;
            HttpStatus status =FORBIDDEN_MSG.equals(developerMessage)
                ? HttpStatus.FORBIDDEN
                : HttpStatus.UNAUTHORIZED;

            response.setStatus(status.value());
            response.setContentType("application/json;charset=UTF-8");

            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(status.value());
            if(HttpStatus.FORBIDDEN.equals(status)) {
                orcidError.setDeveloperMessage("Forbidden: " + developerMessage);
            } 
            else {
                orcidError.setDeveloperMessage( developerMessage);
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("response-code", orcidError.getResponseCode());
            payload.put("developer-message", orcidError.getDeveloperMessage());
            response.getWriter().write(objectMapper.writeValueAsString(payload));
            response.flushBuffer();
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // Wrap other Exceptions. These are not expected to happen
            throw new RuntimeException(e);
        }
    }
}
