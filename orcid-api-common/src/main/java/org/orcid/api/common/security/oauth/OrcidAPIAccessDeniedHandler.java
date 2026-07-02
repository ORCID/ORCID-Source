package org.orcid.api.common.security.oauth;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrcidAPIAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");

            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(HttpStatus.FORBIDDEN.value());
            
            // Determine the developer message based on the exception type
            String developerMessage;
            if (accessDeniedException != null && accessDeniedException.getMessage() != null) {
                developerMessage = accessDeniedException.getMessage();
            } else {
                developerMessage = "Access denied";
            }
            
            orcidError.setDeveloperMessage(developerMessage);

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
