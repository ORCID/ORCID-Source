package org.orcid.api.common.security.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class OrcidAPIAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            
            String developerMessage;
            if (accessDeniedException != null && accessDeniedException.getMessage() != null) {
                developerMessage = accessDeniedException.getMessage();
            } else {
                developerMessage = "Forbidden: The server understood the request but refuses to authorize it.";
            }

            String token = OAuthErrorResponseHelper.extractToken(request);
            String description = OAuthErrorResponseHelper.appendTokenIfPresent(developerMessage, token);

            Map<String, Object> payload = OAuthErrorResponseHelper.buildPayload(HttpStatus.FORBIDDEN.value(), "forbidden", description);
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
