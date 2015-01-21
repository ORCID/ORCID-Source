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
package org.orcid.api.common.jaxb;

import java.util.Locale;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.orcid.api.common.OrcidApiConstants;
import org.orcid.api.common.exception.OrcidApiException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.security.DeprecatedException;
import org.orcid.core.version.ApiSection;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.error.OrcidError;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * orcid-api - Nov 8, 2011 - OrcidExceptionMapper
 * 
 * @author Declan Newman (declan)
 */
@Provider
@Consumes(value = { OrcidApiConstants.VND_ORCID_JSON, OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_JSON, OrcidApiConstants.ORCID_XML,
        MediaType.APPLICATION_XML, MediaType.WILDCARD, MediaType.APPLICATION_JSON })
@Produces(value = { OrcidApiConstants.VND_ORCID_JSON, OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_JSON, OrcidApiConstants.ORCID_XML,
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Component
public class OrcidExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidExceptionMapper.class);

    @Resource
    private MessageSource messageSource;

    @Resource
    private LocaleManager localeManager;

    @Override
    public Response toResponse(Throwable t) {
        // Whatever exception has been caught, make sure we log it.
        LOGGER.error("An exception has occured", t);
        switch (getApiSection()) {
        case NOTIFICATIONS:
            return newStyleErrorREsponse(t);
        default:
            return legacyErrorResponse(t);
        }
    }

    private Response legacyErrorResponse(Throwable t) {
        if (OrcidApiException.class.isAssignableFrom(t.getClass())) {
            return ((OrcidApiException) t).getResponse();
        } else if (WebApplicationException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacy500OrcidEntity(t);
            WebApplicationException webException = (WebApplicationException) t;
            return Response.status(webException.getResponse().getStatus()).entity(entity).build();
        } else if (AuthenticationException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Authentication problem", t);
            return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
        } else if (OAuth2Exception.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("OAuth2 problem", t);
            return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
        } else if (SecurityException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Security problem", t);
            return Response.status(Response.Status.FORBIDDEN).entity(entity).build();
        } else if (IllegalStateException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Illegal state", t);
            return Response.status(Response.Status.FORBIDDEN).entity(entity).build();
        } else if (IllegalArgumentException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Bad Request", t);
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        } else if (DeprecatedException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Account Deprecated", t);
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(entity).build();
        } else {
            OrcidMessage entity = getLegacy500OrcidEntity(t);
            return Response.serverError().entity(entity).build();
        }
    }

    private OrcidMessage getLegacy500OrcidEntity(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage()
                : "It is possible that this is a bug. If you could raise an issue in Github it would be much " + "appreciated. Thanks, the ORCID team"));
        return entity;
    }

    private OrcidMessage getLegacyOrcidEntity(String prefix, Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(prefix + " : " + e.getMessage()));
        return entity;
    }

    private Response newStyleErrorREsponse(Throwable t) {
        if (OrcidApiException.class.isAssignableFrom(t.getClass())) {
            return getOrcidErrorResponse((OrcidApiException) t);
        } else if (WebApplicationException.class.isAssignableFrom(t.getClass())) {
            return getOrcidErrorResponse((WebApplicationException) t);
        } else {
            if (AuthenticationException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.UNAUTHORIZED;
                OrcidError orcidError = getOrcidError(9002, status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (OAuth2Exception.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.UNAUTHORIZED;
                OrcidError orcidError = getOrcidError(9003, status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (SecurityException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.FORBIDDEN;
                OrcidError orcidError = getOrcidError(9004, status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (IllegalStateException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.FORBIDDEN;
                OrcidError orcidError = getOrcidError(9005, status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (IllegalArgumentException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.BAD_REQUEST;
                OrcidError orcidError = getOrcidError(9006, status, t);
                return Response.status(status).entity(orcidError).build();
            } else if (DeprecatedException.class.isAssignableFrom(t.getClass())) {
                Status status = Response.Status.MOVED_PERMANENTLY;
                OrcidError orcidError = getOrcidError(9007, status, t);
                return Response.status(status).entity(orcidError).build();
            } else {
                OrcidError orcidError = getOrcidError(9008, Response.Status.INTERNAL_SERVER_ERROR, t);
                return Response.serverError().entity(orcidError).build();
            }
        }
    }

    private Response getOrcidErrorResponse(OrcidApiException e) {
        int status = e.getResponse().getStatus();
        return getOrcidErrorResponse(9000, status, e);
    }

    private Response getOrcidErrorResponse(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        return getOrcidErrorResponse(9001, status, e);
    }

    private Response getOrcidErrorResponse(int errorCode, int status, Throwable t) {
        OrcidError orcidError = getOrcidError(errorCode, status, t);
        return Response.status(status).entity(orcidError).build();
    }

    private OrcidError getOrcidError(int errorCode, Status status, Throwable t) {
        return getOrcidError(errorCode, status.getStatusCode(), t);
    }

    private OrcidError getOrcidError(int errorCode, int status, Throwable t) {
        OrcidError orcidError = new OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setDeveloperMessage(t.getLocalizedMessage());
        Locale locale = localeManager.getLocale();
        orcidError.setUserMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale));
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        return orcidError;
    }

    private ApiSection getApiSection() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ApiSection apiSection = (ApiSection) requestAttributes.getAttribute(ApiVersionFilter.API_SECTION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        return apiSection;
    }

}
