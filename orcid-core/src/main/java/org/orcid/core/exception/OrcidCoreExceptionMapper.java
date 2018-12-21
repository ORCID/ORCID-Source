package org.orcid.core.exception;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.common.adapters.IllegalEnumValueException;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.sun.jersey.api.NotFoundException;

public class OrcidCoreExceptionMapper {
    
    public static final String V2_RC1 = "2.0_rc1";
    public static final String V2_RC2 = "2.0_rc2";
    public static final String V2_RC3 = "2.0_rc3";
    public static final String V2_RC4 = "2.0_rc4";
    public static final String V2 = "2.0";
    public static final String V2_1 = "2.1";
    public static final String V3_RC1 = "3.0_rc1";
    public static final String V3_RC2 = "3.0_rc2";

    private static final String latest = "2.0";
    
    @Resource
    private MessageSource messageSource;
    
    @Resource
    private LocaleManager localeManager;
    
    private static Map<Class<? extends Throwable>, Pair<Response.Status, Integer>> HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE = new HashMap<>();
    {
        // 301
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidDeprecatedException.class, new ImmutablePair<>(Response.Status.MOVED_PERMANENTLY, 9007));

        // 400
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(JsonParseException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9001));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(IllegalArgumentException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9006));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidBadRequestException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9012));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(SearchStartParameterLimitExceededException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9043));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(MismatchedPutCodeException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9019));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(InvalidPutCodeException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9024));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9020));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ActivityTitleValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9022));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ActivityIdentifierValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9023));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(GroupIdRecordNotFoundException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9026));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OtherNameNotFoundException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9033));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(PutCodeFormatException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9034));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ActivityTypeValidationException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9037));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ExceedMaxNumberOfPutCodesException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9042));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(InvalidDisambiguatedOrgException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9045));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(InvalidOrgException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9046));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(InvalidJSONException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9047));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(InvalidFuzzyDateException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9049));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(MissingStartDateException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9050));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(IllegalEnumValueException.class, new ImmutablePair<>(Response.Status.BAD_REQUEST, 9051));
        
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
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(VisibilityMismatchException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9035));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidAccessControlException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9038));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNonPublicElementException.class, new ImmutablePair<>(Response.Status.FORBIDDEN, 9039));
        
        // 404
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9011));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(NoResultException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9016));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidClientNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9027));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidWebhookNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9028));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotificationNotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9029));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNoBioException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9041));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(NotFoundException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9016));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNoResultException.class, new ImmutablePair<>(Response.Status.NOT_FOUND, 9016));

        // 409
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(LockedException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9018));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidDuplicatedActivityException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9021));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(DuplicatedGroupIdRecordException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9025));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidDuplicatedElementException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9030));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(PutCodeRequiredException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9031));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotificationException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9032));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidNotClaimedException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9036)); 
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(OrcidElementCantBeDeletedException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9040));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(DeactivatedException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9044));
        HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.put(ExceedMaxNumberOfElementsException.class, new ImmutablePair<>(Response.Status.CONFLICT, 9048));
    }
    
    public static Pair<Status, Integer> getHttpStatusAndErrorCode(Throwable t) {
        Pair<Response.Status, Integer> pair = HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.get(t.getClass());
        if (pair != null) {
            return pair;
        }
        // Try super class
        pair = HTTP_STATUS_AND_ERROR_CODE_BY_THROWABLE_TYPE.get(t.getClass().getSuperclass());
        return pair != null ? pair : new ImmutablePair<>(Response.Status.INTERNAL_SERVER_ERROR, 9008);
    }
    
    public org.orcid.jaxb.model.error_v2.OrcidError getOrcidError(Throwable t) {
        return (org.orcid.jaxb.model.error_v2.OrcidError) getOrcidError(t, latest);
    }
    
    public org.orcid.jaxb.model.v3.rc2.error.OrcidError getV3OrcidError(Throwable t) {
        return (org.orcid.jaxb.model.v3.rc2.error.OrcidError) getOrcidError(t, V3_RC2);
    }
    
    public Object getOrcidError(Throwable t, String version) {
        Pair<Response.Status, Integer> pair = OrcidCoreExceptionMapper.getHttpStatusAndErrorCode(t);
        Integer errorCode = pair.getRight();
        int status = pair.getLeft().getStatusCode();
        
        return getOrcidError(errorCode, status, t, version);
    }
    
    public Object getOrcidError(Integer errorCode, Integer status, Throwable t, String version) {
        Object orcidError = null;
        if(V2.equals(version)) {
        	orcidError = (org.orcid.jaxb.model.error_v2.OrcidError) getOrcidErrorV2(errorCode, status, t);
        } else if(V2_RC4.equals(version)) {
            orcidError = (org.orcid.jaxb.model.error_rc4.OrcidError) getOrcidErrorV2Rc4(errorCode, status, t);
        } else if(V2_RC3.equals(version)) {
            orcidError = (org.orcid.jaxb.model.error_rc3.OrcidError) getOrcidErrorV2Rc3(errorCode, status, t);
        } else if (V2_RC2.equals(version)) {
            orcidError = (org.orcid.jaxb.model.error_rc2.OrcidError) getOrcidErrorV2Rc2(errorCode, status, t);
        } else if (V3_RC1.equals(version)) {
            orcidError = (org.orcid.jaxb.model.v3.rc1.error.OrcidError) getOrcidErrorV3Rc1(errorCode, status, t);
        } else if (V3_RC2.equals(version)) {
            orcidError = (org.orcid.jaxb.model.v3.rc2.error.OrcidError) getOrcidErrorV3Rc2(errorCode, status, t);
        } else {
            orcidError = (OrcidError) getOrcidErrorV2Rc1(errorCode, status, t);
        }
        
        return orcidError;
    }
    
    
    private OrcidError getOrcidErrorV2Rc1(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        OrcidError orcidError = new OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }

    private org.orcid.jaxb.model.error_rc2.OrcidError getOrcidErrorV2Rc2(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        org.orcid.jaxb.model.error_rc2.OrcidError orcidError = new org.orcid.jaxb.model.error_rc2.OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }
    
    public org.orcid.jaxb.model.error_rc3.OrcidError getOrcidErrorV2Rc3(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        org.orcid.jaxb.model.error_rc3.OrcidError orcidError = new org.orcid.jaxb.model.error_rc3.OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }

    public org.orcid.jaxb.model.error_rc4.OrcidError getOrcidErrorV2Rc4(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        org.orcid.jaxb.model.error_rc4.OrcidError orcidError = new org.orcid.jaxb.model.error_rc4.OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }
    
    public org.orcid.jaxb.model.error_v2.OrcidError getOrcidErrorV2(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        org.orcid.jaxb.model.error_v2.OrcidError orcidError = new org.orcid.jaxb.model.error_v2.OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }
    
    public org.orcid.jaxb.model.v3.rc1.error.OrcidError getOrcidErrorV3Rc1(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        org.orcid.jaxb.model.v3.rc1.error.OrcidError orcidError = new org.orcid.jaxb.model.v3.rc1.error.OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }
    
    public org.orcid.jaxb.model.v3.rc2.error.OrcidError getOrcidErrorV3Rc2(int errorCode, int status, Throwable t) {
        Locale locale = localeManager.getLocale();
        org.orcid.jaxb.model.v3.rc2.error.OrcidError orcidError = new org.orcid.jaxb.model.v3.rc2.error.OrcidError();
        orcidError.setResponseCode(status);
        orcidError.setErrorCode(errorCode);
        orcidError.setMoreInfo(messageSource.getMessage("apiError." + errorCode + ".moreInfo", null, locale));
        Map<String, String> params = null;
        if (t instanceof ApplicationException) {
            params = ((ApplicationException) t).getParams();
        } else if (t instanceof IllegalEnumValueException) {
            params = ((IllegalEnumValueException) t).getParams();
        }
        orcidError.setDeveloperMessage(getDeveloperMessage(errorCode, t, params));
        orcidError.setUserMessage(resolveMessage(messageSource.getMessage("apiError." + errorCode + ".userMessage", null, locale), params));
        return orcidError;
    }
    
    /** Gets a message for the error code from the properties. If no message, uses the full class name.
     * Appends a localised version of "Full validation error:" followed by the message embedded in the code (in English) 
     * 
     * @param errorCode
     * @param t
     * @param params
     * @return the error message
     */
    private String getDeveloperMessage(int errorCode, Throwable t, Map<String, String> params) {
        if (t== null)
            return "";
        Locale locale = localeManager.getLocale();
        
        // Returns an empty message if the key is not found
        String devMessage = messageSource.getMessage("apiError." + errorCode + ".developerMessage", null, "", locale);        
        if (devMessage == "")
            devMessage = t.getClass().getCanonicalName();
        
        String exceptionMessage = t.getLocalizedMessage();
        String validationMessage = messageSource.getMessage("apiError.validation.message", null, "", locale);
        if (exceptionMessage != null) {
            devMessage += " " + validationMessage + " " + exceptionMessage;
        }

        Throwable cause = t.getCause();
        if (cause != null) {
            String causeMessage = cause.getLocalizedMessage();
            if (causeMessage != null) {
                devMessage += " (" + causeMessage + ")";
            } else {
                Throwable secondCause = cause.getCause();
                String secondCauseMessage = secondCause.getLocalizedMessage();
                if (secondCauseMessage != null) {
                    devMessage += " (" + secondCauseMessage + ")";
                } else {
                    Throwable thirdCause = secondCause.getCause();
                    String thirdCauseMessage = thirdCause.getLocalizedMessage();
                    if (thirdCauseMessage != null) {
                        devMessage += " (" + thirdCauseMessage + ")";
                    }
                }
            }
        }
        
        return resolveMessage(devMessage, params);
    }

    private String resolveMessage(String errorMessg, Map<String, String> params) {
        if (params == null) {
            return errorMessg;
        }
        StrSubstitutor sub = new StrSubstitutor(params);
        return sub.replace(errorMessg);
    }
}
