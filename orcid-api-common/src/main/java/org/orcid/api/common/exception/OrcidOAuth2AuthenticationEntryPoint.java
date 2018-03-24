package org.orcid.api.common.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint implements AccessDeniedHandler, AuthenticationEntryPoint {

    private WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    private OAuth2ExceptionRenderer exceptionRenderer = new DefaultOAuth2ExceptionRenderer();

    // This is from Spring MVC.
    private HandlerExceptionResolver handlerExceptionResolver = new DefaultHandlerExceptionResolver();

    public void setExceptionTranslator(WebResponseExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    public void setExceptionRenderer(OAuth2ExceptionRenderer exceptionRenderer) {
        this.exceptionRenderer = exceptionRenderer;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        handleAsOrcidError(request, response, accessDeniedException);

    }

    public void handleAsOrcidError(HttpServletRequest request, HttpServletResponse response, Exception authException) throws IOException, ServletException {
        try {
            ResponseEntity<OAuth2Exception> result = exceptionTranslator.translate(authException);
            result = enhanceResponse(result, authException);
            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(result.getStatusCode().value());
            orcidError.setDeveloperMessage(result.getBody().getLocalizedMessage());
            ResponseEntity<OrcidError> errorResponseEntity = new ResponseEntity<>(orcidError, result.getHeaders(), result.getStatusCode());
            exceptionRenderer.handleHttpEntityResponse(errorResponseEntity, new ServletWebRequest(request, response));
            response.flushBuffer();
        } catch (ServletException e) {
            // Re-use some of the default Spring dispatcher behaviour - the
            // exception came from the filter chain and
            // not from an MVC handler so it won't be caught by the dispatcher
            // (even if there is one)
            if (handlerExceptionResolver.resolveException(request, response, this, e) == null) {
                throw e;
            }
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // Wrap other Exceptions. These are not expected to happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        handleAsOrcidError(request, response, authException);
    }

}