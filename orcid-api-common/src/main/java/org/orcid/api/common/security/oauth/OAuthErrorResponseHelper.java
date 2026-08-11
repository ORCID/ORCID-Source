package org.orcid.api.common.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

final class OAuthErrorResponseHelper {

    private OAuthErrorResponseHelper() {
    }

    static Map<String, Object> buildPayload(int responseCode, String error, String errorDescription) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("response-code", responseCode);
        payload.put("error", error);
        payload.put("error_description", errorDescription);
        return payload;
    }

    static String appendTokenIfPresent(String description, String token) {
        if (token != null && !token.isBlank() && description != null && !description.contains(token)) {
            return description + ": " + token;
        }
        return description;
    }

    static String extractToken(HttpServletRequest request) {
        String token = extractHeaderToken(request);
        if (token == null || token.isBlank()) {
            token = request.getParameter("access_token");
        }
        return token;
    }

    private static String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value != null && value.toLowerCase().startsWith("bearer")) {
                String authHeaderValue = value.substring("bearer".length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }
        return null;
    }
}