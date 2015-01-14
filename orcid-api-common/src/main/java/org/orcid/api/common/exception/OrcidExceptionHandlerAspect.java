/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.orcid.core.security.DeprecatedException;
import org.orcid.jaxb.model.error.OrcidError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 *
 */
@Aspect
// This aspect must have a lower order value than other aspects, so that it can
// catch exceptions from those aspects.
@Order(50)
@Component
public class OrcidExceptionHandlerAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidExceptionHandlerAspect.class);

    @Around("@within(org.orcid.api.common.exception.HandleException) && execution(public javax.ws.rs.core.Response *(..))")
    public Object handleException(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            return toResponse(t);
        }
    }

    public Response toResponse(Throwable t) {
        // Whatever exception has been caught, make sure we log it.
        LOGGER.error("An exception has occured", t);
        if (OrcidApiException.class.isAssignableFrom(t.getClass())) {
            return getOrcidErrorResponse((OrcidApiException) t);
        } else if (WebApplicationException.class.isAssignableFrom(t.getClass())) {
            OrcidError orcidError = getOrcidError("Web application error", Response.Status.INTERNAL_SERVER_ERROR, t);
            WebApplicationException webException = (WebApplicationException) t;
            return Response.status(webException.getResponse().getStatus()).entity(orcidError).build();
        } else {
            if (AuthenticationException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.UNAUTHORIZED;
                OrcidError orcidError = getOrcidError("Authentication problem", status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (OAuth2Exception.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.UNAUTHORIZED;
                OrcidError orcidError = getOrcidError("OAuth2 problem", status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (SecurityException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.FORBIDDEN;
                OrcidError orcidError = getOrcidError("Security problem", status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (IllegalStateException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.FORBIDDEN;
                OrcidError orcidError = getOrcidError("Illegal state", status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (IllegalArgumentException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.BAD_REQUEST;
                OrcidError orcidError = getOrcidError("Bad Request", status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (DeprecatedException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.MOVED_PERMANENTLY;
                OrcidError orcidError = getOrcidError("Account Deprecated", status, t);
                return Response.status(status).entity(orcidError).build();
            } else {
                OrcidError orcidError = getOrcidError("Unknown error", Response.Status.INTERNAL_SERVER_ERROR, t);
                return Response.serverError().entity(orcidError).build();
            }
        }
    }

    private Response getOrcidErrorResponse(OrcidApiException e) {
        OrcidError orcidError = new OrcidError();
        int status = e.getResponse().getStatus();
        orcidError.setResponseCode(status);
        orcidError.setDeveloperMessage(e.getLocalizedMessage());
        return Response.status(status).entity(orcidError).build();
    }

    private OrcidError getOrcidError(String string, Status status, Throwable t) {
        OrcidError orcidError = new OrcidError();
        orcidError.setResponseCode(status.getStatusCode());
        orcidError.setDeveloperMessage(t.getLocalizedMessage());
        return orcidError;
    }

}
