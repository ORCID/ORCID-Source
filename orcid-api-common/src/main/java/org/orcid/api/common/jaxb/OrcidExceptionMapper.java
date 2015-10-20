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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.MismatchedPutCodeException;
import org.orcid.core.exception.OrcidApiException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidClientNotFoundException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidForbiddenException;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.OrcidNotificationException;
import org.orcid.core.exception.OrcidNotificationNotFoundException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.OrcidWebhookNotFoundException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.security.aop.LockedException;
import org.orcid.core.version.ApiSection;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.error.OrcidError;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
    
    @Resource
    private OrcidSecurityManager securityManager;

    private static Map<Class<? extends Throwable>, Pair<Response.Status, Integer>> HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE = new HashMap<>();
    {
        // 301
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidDeprecatedException.class, new ImmutablePair<>(Response.Status.MOVED_PERMANENTLY, 9007));

        // 400
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(IllegalArgumentException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9006));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidBadRequestException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9012));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(MismatchedPutCodeException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9019));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(InvalidPutCodeException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9024));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9020));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ActivityTitleValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9022));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ActivityIdentifierValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9023));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(GroupIdRecordNotFoundException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9026));

        // 401
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(AuthenticationException.class, new ImmutablePair<>(Response.Status.UNAUTHORIZED, 9002));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OAuth2Exception.class, new ImmutablePair<>(Response.Status.UNAUTHORIZED, 9003));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidUnauthorizedException.class, new ImmutablePair<>(Response.Status.UNAUTHORIZED, 9017));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidInvalidScopeException.class, new ImmutablePair<>(Response.Status.UNAUTHORIZED, 9015));

        // 403
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(SecurityException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9004));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(IllegalStateException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9005));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotificationAlreadyReadException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9009));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(WrongSourceException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9010));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidForbiddenException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9014));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidVisibilityException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9013));

        // 404
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9011));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(NoResultException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9016));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidClientNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9027));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidWebhookNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9028));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotificationNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9029));

        // 409
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(LockedException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9018));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidDuplicatedActivityException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9021));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(DuplicatedGroupIdRecordException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9025));





        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotificationException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9032));
    }

    @Override
    public Response toResponse(Throwable t) {
        // Whatever exception has been caught, make sure we log it.
        String clientId = securityManager.getClientIdFromAPIRequest();
        if(PojoUtil.isEmpty(clientId)) {
            LOGGER.error("An exception has occured, no client id info provided", t);
        } else {
            LOGGER.error("An exception has occured processing request from client " + clientId, t);
        }
        
        switch (getApiSection()) {
        case NOTIFICATIONS:
            return newStyleErrorResponse(t);
        case V2:
            return newStyleErrorResponse(t);
        default:
            return legacyErrorResponse(t);
        }
    }

    private Response legacyErrorResponse(Throwable t) {
        if (OrcidApiException.class.isAssignableFrom(t.getClass())) {
            return ((OrcidApiException) t).getResponse();
        } else if (OrcidValidationException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Bad Request: ", t);
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        } else if (WebApplicationException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacy500OrcidEntity(t);
            WebApplicationException webException = (WebApplicationException) t;
            return Response.status(webException.getResponse().getStatus()).entity(entity).build();
        } else if (AuthenticationException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Authentication problem : ", t);
            return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
        } else if (OAuth2Exception.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("OAuth2 problem : ", t);
            return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
        } else if (OrcidInvalidScopeException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("OAuth2 problem : ", t);
            return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
        } else if (SecurityException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Security problem : ", t);
            return Response.status(Response.Status.FORBIDDEN).entity(entity).build();
        } else if (IllegalStateException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Illegal state : ", t);
            return Response.status(Response.Status.FORBIDDEN).entity(entity).build();
        } else if (IllegalArgumentException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Bad Request : ", t);
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        } else if (OrcidDeprecatedException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = (OrcidMessage) newStyleErrorResponse(t).getEntity();
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(entity).build();
        } else if (LockedException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Account locked : ", t);
            return Response.status(Response.Status.CONFLICT).entity(entity).build();
        } else if (NoResultException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Not found : ", t);
            return Response.status(Response.Status.NOT_FOUND).entity(entity).build();
        } else {
            OrcidMessage entity = getLegacy500OrcidEntity(t);
            return Response.status(getHttpStatusAndErrorCode(t).getKey()).entity(entity).build();
        }
    }

    private OrcidMessage getLegacy500OrcidEntity(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : messageSource.getMessage("apiError.unknown.exception", null,
                localeManager.getLocale())));
        return entity;
    }

    private OrcidMessage getLegacyOrcidEntity(String prefix, Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(prefix + e.getMessage()));
        return entity;
    }

    private Response newStyleErrorResponse(Throwable t) {
        if (WebApplicationException.class.isAssignableFrom(t.getClass())) {
            return getOrcidErrorResponse((WebApplicationException) t);
        } else {
            return getOrcidErrorResponse(t);
        }
    }

    private Response getOrcidErrorResponse(Throwable t) {
        Pair<Response.Status, Integer> pair = getHttpStatusAndErrorCode(t);
        return getOrcidErrorResponse(pair.getRight(), pair.getLeft(), t);
    }

    private Response getOrcidErrorResponse(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        return getOrcidErrorResponse(9001, status, e);
    }

    private Response getOrcidErrorResponse(int errorCode, Response.Status status, Throwable t) {
        return getOrcidErrorResponse(errorCode, status.getStatusCode(), t);
    }

    private Response getOrcidErrorResponse(int errorCode, int status, Throwable t) {
        OrcidError orcidError = getOrcidError(errorCode, status, t);
        return Response.status(status).entity(orcidError).build();
    }

    private OrcidError getOrcidError(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        OrcidError orcidError = new OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        // Returns an empty message if the key is not found
        String devMessage = messageSource.getMessage("apiError." + errorCode + ".developerMessage", null, "", locale);

        // Assign message from the exception
        if ("".equals(devMessage)) {
            devMessage = t.getClass().getCanonicalName();
            Throwable cause = t.getCause();
            String exceptionMessage = t.getLocalizedMessage();
            if (exceptionMessage != null) {
                devMessage += ": " + exceptionMessage;
            }

            if (cause != null) {
                String causeMessage = cause.getLocalizedMessage();
                if (causeMessage != null) {
                    devMessage += " (" + causeMessage + ")";
                }
            }
            orcidError.setDeveloperMessage(devMessage);
        } else {
            orcidError.setDeveloperMessage(resolveMessage(devMessage, params));
        }

        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }

    private String resolveMessage(String errorMessg, Map<String, String> params) {
        if (params == null) {
            return errorMessg;
        }
        StrSubstitutor sub = new StrSubstitutor(params);
        return sub.replace(errorMessg);
    }

    private ApiSection getApiSection() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ApiSection apiSection = (ApiSection) requestAttributes.getAttribute(ApiVersionFilter.API_SECTION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        return apiSection != null ? apiSection : ApiSection.V1;
    }

    private Pair<Status, Integer> getHttpStatusAndErrorCode(Throwable t) {
        Pair<Response.Status, Integer> pair = HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.get(t.getClass());
        if (pair != null) {
            return pair;
        }
        // Try super class
        pair = HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.get(t.getClass().getSuperclass());
        return pair != null ? pair : new ImmutablePair<>(Response.Status.INTERNAL_SERVER_ERROR, 9008);
    }

}
