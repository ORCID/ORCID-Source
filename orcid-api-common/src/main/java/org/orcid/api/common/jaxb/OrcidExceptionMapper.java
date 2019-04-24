package org.orcid.api.common.jaxb;

import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.ExceedMaxNumberOfElementsException;
import org.orcid.core.exception.OrcidApiException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.oauth.OAuthErrorUtils;
import org.orcid.core.security.aop.LockedException;
import org.orcid.core.version.ApiSection;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.message.DeprecatedDate;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.PrimaryRecord;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sun.jersey.api.NotFoundException;

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

    private static final String LOCATION_HEADER = "location";

    private static final String OAUTH_TOKEN_REQUEST = "/oauth/token";

    @Context
    private HttpServletRequest httpRequest;

    @Resource
    private MessageSource messageSource;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Resource
    private OrcidSecurityManager securityManager;

    @Override
    public Response toResponse(Throwable t) {
        // Whatever exception has been caught, make sure we log it.
        String clientId = securityManager.getClientIdFromAPIRequest();
        if (t instanceof NotFoundException) {
            logShortError(t, clientId);
        } else if (t instanceof NoResultException) {
            logShortError(t, clientId); 
        } else {
                LOGGER.error("An exception has occured processing request from client " + clientId, t);
        }

        if (isOAuthTokenRequest()) {
            return oAuthErrorResponse(t);
        }

        String apiVersion = getApiVersion();

        if (!PojoUtil.isEmpty(apiVersion)) {
            switch (apiVersion) {
            case OrcidCoreExceptionMapper.V2:
            	return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2);
            case OrcidCoreExceptionMapper.V2_RC1:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2_RC1);
            case OrcidCoreExceptionMapper.V2_RC2:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2_RC2);
            case OrcidCoreExceptionMapper.V2_RC3:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2_RC3);
            case OrcidCoreExceptionMapper.V2_RC4:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2_RC4);
            case OrcidCoreExceptionMapper.V2_1:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2_1);
            case OrcidCoreExceptionMapper.V3_RC1:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V3_RC1);
            case OrcidCoreExceptionMapper.V3_RC2:
                return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V3_RC2);
            }
        }

        // If there was no api version, check if it is notifications or a 1.2
        // error type
        switch (getApiSection()) {
        case NOTIFICATIONS:
            return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2);
        case WEBHOOKS:
            return newStyleErrorResponse(t, OrcidCoreExceptionMapper.V2);
        default:
            return legacyErrorResponse(t);
        }
    }

    private void logShortError(Throwable t, String clientId) {
        StringBuffer temp = new StringBuffer(t.getClass().getSimpleName() + " exception from client: ").append(clientId).append(". ").append(t.getMessage());
        LOGGER.error(temp.toString());
    }

    private Response oAuthErrorResponse(Throwable t) {
        OAuthError error = OAuthErrorUtils.getOAuthError(t);
        return Response.status(error.getResponseStatus()).entity(error).build();
    }

    private Response legacyErrorResponse(Throwable t) {
        if (OrcidApiException.class.isAssignableFrom(t.getClass())) {
            return ((OrcidApiException) t).getResponse();
        } else if (OrcidValidationException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Bad Request: ", t);
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        } else if (NotFoundException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Resource not found: ", t);
            return Response.status(OrcidCoreExceptionMapper.getHttpStatusAndErrorCode(t).getKey()).entity(entity).build();
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
            OrcidDeprecatedException exception = (OrcidDeprecatedException) t;
            OrcidDeprecated depreciatedError = new OrcidDeprecated();
            Map<String, String> params = exception.getParams();
            String location = null;
            if (params != null) {
                if (params.containsKey(OrcidDeprecatedException.ORCID)) {
                    PrimaryRecord pr = new PrimaryRecord();
                    pr.setOrcid(new Orcid(params.get(OrcidDeprecatedException.ORCID)));
                    depreciatedError.setPrimaryRecord(pr);
                    location = getPrimaryRecordLocation(params);
                }
                if (params.containsKey(OrcidDeprecatedException.DEPRECATED_DATE)) {
                    DeprecatedDate dd = new DeprecatedDate();
                    String dateString = params.get(OrcidDeprecatedException.DEPRECATED_DATE);
                    dd.setValue(DateUtils.convertToXMLGregorianCalendar(dateString, false));
                    depreciatedError.setDate(dd);
                }
            }

            Response response = null;
            if (location != null) {
                response = Response.status(Response.Status.MOVED_PERMANENTLY).header(LOCATION_HEADER, location).entity(depreciatedError).build();
            } else {
                response = Response.status(Response.Status.MOVED_PERMANENTLY).entity(depreciatedError).build();
            }
            return response;
        } else if (LockedException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Account locked : ", t);
            return Response.status(Response.Status.CONFLICT).entity(entity).build();
        } else if (NoResultException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Not found : ", t);
            return Response.status(Response.Status.NOT_FOUND).entity(entity).build();
        } else if (ExceedMaxNumberOfElementsException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity(
                    "The maximum number of works that can be connected to an ORCID record is 10,000 and you have now exceeded this limit. Please remove some works and try again. For more information, see https://support.orcid.org/hc/articles/360006973133", null);
            return Response.status(Response.Status.CONFLICT).entity(entity).build();
        } else if(DeactivatedException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Account deactivated : ", t);
            return Response.status(Response.Status.CONFLICT).entity(entity).build();
        } else if(OrcidNotClaimedException.class.isAssignableFrom(t.getClass())) {
            OrcidMessage entity = getLegacyOrcidEntity("Account non claimed : ", t);
            return Response.status(Response.Status.CONFLICT).entity(entity).build();
        } else {
            OrcidMessage entity = getLegacy500OrcidEntity(t);
            return Response.status(OrcidCoreExceptionMapper.getHttpStatusAndErrorCode(t).getKey()).entity(entity).build();
        }
    }

    private OrcidMessage getLegacy500OrcidEntity(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(
                StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : messageSource.getMessage("apiError.unknown.exception", null, localeManager.getLocale())));
        return entity;
    }

    private OrcidMessage getLegacyOrcidEntity(String prefix, Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        if (e != null && !PojoUtil.isEmpty(e.getMessage()))
            entity.setErrorDesc(new ErrorDesc(prefix + e.getMessage()));
        else
            entity.setErrorDesc(new ErrorDesc(prefix));
        return entity;
    }

    private Response newStyleErrorResponse(Throwable t, String version) {
        if(NotFoundException.class.isAssignableFrom(t.getClass())) {
            return getOrcidErrorResponse(t, version);
        } else if (WebApplicationException.class.isAssignableFrom(t.getClass())) {
            return getOrcidErrorResponse((WebApplicationException) t, version);
        } else {
            return getOrcidErrorResponse(t, version);
        }
    }

    private Response getOrcidErrorResponse(WebApplicationException e, String version) {
        int status = e.getResponse().getStatus();
        return getOrcidErrorResponse(9001, status, e, version);
    }

    private Response getOrcidErrorResponse(Integer errorCode, Integer status, Throwable t, String version) {
        Object orcidError = orcidCoreExceptionMapper.getOrcidError(errorCode, status, t, version);
        return getOrcidErrorResponse(orcidError, t);
    }

    private Response getOrcidErrorResponse(Throwable t, String version) {
        Object orcidError = orcidCoreExceptionMapper.getOrcidError(t, version);
        return getOrcidErrorResponse(orcidError, t);
    }

    private Response getOrcidErrorResponse(Object orcidError, Throwable t) {
        int statusCode = 0;
        if (org.orcid.jaxb.model.error_rc1.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
            statusCode = ((org.orcid.jaxb.model.error_rc1.OrcidError) orcidError).getResponseCode();
        } else if (org.orcid.jaxb.model.error_rc2.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
            statusCode = ((org.orcid.jaxb.model.error_rc2.OrcidError) orcidError).getResponseCode();
        } else if (org.orcid.jaxb.model.error_rc3.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
            statusCode = ((org.orcid.jaxb.model.error_rc3.OrcidError) orcidError).getResponseCode();
        } else if (org.orcid.jaxb.model.error_rc4.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
            statusCode = ((org.orcid.jaxb.model.error_rc4.OrcidError) orcidError).getResponseCode();
        } else if (org.orcid.jaxb.model.error_v2.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
        	statusCode = ((org.orcid.jaxb.model.error_v2.OrcidError) orcidError).getResponseCode();
        } else if (org.orcid.jaxb.model.v3.rc1.error.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
            statusCode = ((org.orcid.jaxb.model.v3.rc1.error.OrcidError) orcidError).getResponseCode();
        } else if (org.orcid.jaxb.model.v3.rc2.error.OrcidError.class.isAssignableFrom(orcidError.getClass())) {
            statusCode = ((org.orcid.jaxb.model.v3.rc2.error.OrcidError) orcidError).getResponseCode();
        }

        if (OrcidDeprecatedException.class.isAssignableFrom(t.getClass())) {
            OrcidDeprecatedException exception = (OrcidDeprecatedException) t;
            Map<String, String> params = exception.getParams();
            String location = null;
            if (params != null) {
                if (params.containsKey(OrcidDeprecatedException.ORCID)) {
                    location = getPrimaryRecordLocation(params);
                }
            }

            Response response = null;
            if (location != null) {
                response = Response.status(statusCode).header(LOCATION_HEADER, location).entity(orcidError).build();
            } else {
                response = Response.status(statusCode).entity(orcidError).build();
            }
            return response;
        }
        return Response.status(statusCode).entity(orcidError).build();
    }

    private ApiSection getApiSection() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ApiSection apiSection = (ApiSection) requestAttributes.getAttribute(ApiVersionFilter.API_SECTION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        return apiSection != null ? apiSection : ApiSection.V1;
    }

    private String getApiVersion() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String apiVersion = (String) requestAttributes.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        return apiVersion;
    }

    private boolean isOAuthTokenRequest() {
        String url = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURL().toString();
        return url.endsWith(OAUTH_TOKEN_REQUEST);
    }

    /**
     * Returns the location of the primary record for a deprecated record
     */
    private String getPrimaryRecordLocation(Map<String, String> params) {
        String deprecatedOrcid = OrcidStringUtils.getOrcidNumber(httpRequest.getRequestURI());
        String primaryOrcid = OrcidStringUtils.getOrcidNumber(params.get(OrcidDeprecatedException.ORCID));
        String originalRequest = httpRequest.getRequestURL().toString();

        if (OrcidUrlManager.isSecure(httpRequest)) {
            if (originalRequest.startsWith("http:")) {
                originalRequest = originalRequest.replaceFirst("http:", "https:");
            }
        }

        return originalRequest.replace(deprecatedOrcid, primaryOrcid);
    }
}
