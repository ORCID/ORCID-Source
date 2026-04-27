package org.orcid.core.web.filters;

import static org.orcid.core.api.OrcidApiConstants.APPLICATION_RDFXML;
import static org.orcid.core.api.OrcidApiConstants.JSON_LD;
import static org.orcid.core.api.OrcidApiConstants.N_TRIPLES;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.TEXT_N3;
import static org.orcid.core.api.OrcidApiConstants.TEXT_TURTLE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class AcceptFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptFilter.class);
    private static String[] accpetTypesArray = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON, TEXT_TURTLE,
            TEXT_N3, N_TRIPLES, JSON_LD, APPLICATION_RDFXML };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accept = request.getHeader("accept");
            String contentType = request.getHeader("Content-Type");

            if (accept == null || accept.equals("*/*")) {
                HttpServletRequestWrapper requestWrapper = null;
                if (isValidAcceptType(contentType))
                    requestWrapper = new AcceptHeaderRequestWrapper(request, contentType);
                else if (isStandardJsonRequest(request))
                    requestWrapper = new AcceptHeaderRequestWrapper(request, MediaType.APPLICATION_JSON);
                else
                    requestWrapper = new AcceptHeaderRequestWrapper(request, VND_ORCID_XML);
                filterChain.doFilter(requestWrapper, response);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (RequestRejectedException e) {
            if(request.getRequestURI() != null) {
                logger.warn(request.getRequestURI() + ":" + e.getMessage());
            } else {
                logger.warn(e.getMessage());
            }
            ResponseEntity<OrcidError> errorResponseEntity = buildErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
            new ObjectMapper().writeValue(response.getWriter(), errorResponseEntity);
            response.flushBuffer();
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            ResponseEntity<OrcidError> errorResponseEntity = buildErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
            new ObjectMapper().writeValue(response.getWriter(), errorResponseEntity);
            response.flushBuffer();
        }
    }

    private ResponseEntity<OrcidError> buildErrorResponseEntity(HttpStatus status, String message) {
        OrcidError orcidError = new OrcidError();
        orcidError.setResponseCode(status.value());
        orcidError.setDeveloperMessage(message);
        orcidError.setUserMessage(message);
        ResponseEntity<OrcidError> errorResponseEntity = new ResponseEntity<>(orcidError, status);
        return errorResponseEntity;
    }

    private boolean isStandardJsonRequest(HttpServletRequest request) {
        String path = OrcidUrlManager.getPathWithoutContextPath(request);
        return path.startsWith("/oauth/") || path.endsWith("/pubStatus") || path.endsWith("/apiStatus");
    }

    private boolean isValidAcceptType(String testAccept) {
        if (testAccept == null)
            return false;
        for (String accept : accpetTypesArray)
            if (accept.toLowerCase().indexOf(testAccept) != -1)
                return true;
        return false;
    }
}