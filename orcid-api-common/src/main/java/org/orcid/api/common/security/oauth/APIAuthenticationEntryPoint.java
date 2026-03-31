package org.orcid.api.common.security.oauth;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class APIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try {
            //TODO: This is NOT ready, exceptions should be handled and the proper error message returned
            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(response.getStatus());
            orcidError.setDeveloperMessage("GENERIC ERROR MESSAGE");

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(orcidError.toString());
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
