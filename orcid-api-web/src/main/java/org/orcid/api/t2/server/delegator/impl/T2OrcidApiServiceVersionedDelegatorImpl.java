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
package org.orcid.api.t2.server.delegator.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ValidationBehaviour;
import org.orcid.core.manager.ValidationManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.impl.ValidationManagerImpl;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class T2OrcidApiServiceVersionedDelegatorImpl implements T2OrcidApiServiceDelegator {

    @Resource(name = "orcidT2ServiceDelegator")
    private T2OrcidApiServiceDelegator t2OrcidApiServiceDelegator;

    @Resource
    private OrcidMessageVersionConverterChain orcidMessageVersionConverterChain;

    private ValidationManager incomingValidationManager;

    private ValidationManager outgoingValidationManager;

    private String externalVersion = OrcidMessage.DEFAULT_VERSION;

    private boolean supportsAffiliations = true;

    @Resource
    private EmailDao emailDao;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;        
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    public void setIncomingValidationManager(ValidationManager incomingValidationManager) {
        this.incomingValidationManager = incomingValidationManager;
    }

    public void setOutgoingValidationManager(ValidationManager outgoingValidationManager) {
        this.outgoingValidationManager = outgoingValidationManager;
    }

    public void setSupportsAffiliations(boolean supportsAffiliations) {
        this.supportsAffiliations = supportsAffiliations;
    }

    @Override
    public Response viewStatusText() {
        return t2OrcidApiServiceDelegator.viewStatusText();
    }

    public void autoConfigureValidators() {
        if (incomingValidationManager == null) {
            ValidationManagerImpl incomingValidationManagerImpl = new ValidationManagerImpl();
            incomingValidationManagerImpl.setValidationBehaviour(ValidationBehaviour.THROW_VALIDATION_EXCEPTION);
            incomingValidationManagerImpl.setVersion(externalVersion);
            incomingValidationManagerImpl.setRequireOrcidProfile(true);
            incomingValidationManagerImpl.setValidateTitle(true);
            setIncomingValidationManager(incomingValidationManagerImpl);
        }

        if (outgoingValidationManager == null) {
            ValidationManagerImpl outgoingValidationManagerImpl = new ValidationManagerImpl();
            outgoingValidationManagerImpl.setVersion(externalVersion);
            setOutgoingValidationManager(outgoingValidationManagerImpl);
        }
    }

    @Override
    public Response findBioDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findBioDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findBioDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findExternalIdentifiers(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findExternalIdentifiers(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findExternalIdentifiersFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFullDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findFullDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFullDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findAffiliationsDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findAffiliationsDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findAffiliationsDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findAffiliationsDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFundingDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findFundingDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFundingDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findFundingDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findWorksDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findWorksDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findWorksDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response redirectClientToGroup(String clientId) {
        return t2OrcidApiServiceDelegator.redirectClientToGroup(clientId);
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        Response response = t2OrcidApiServiceDelegator.searchByQuery(queryMap);
        return downgradeAndValidateResponse(response);
    }
    
    @Override
    public Response publicSearchByQuery(Map<String, List<String>> queryMap) {
        return searchByQuery(queryMap);
    }

    @Override
    public Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage) {
        Response response = null;
        validateRegistrationMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        response = t2OrcidApiServiceDelegator.createProfile(uriInfo, upgradedMessage);
        return response;
    }

    @Override
    public Response updateBioDetails(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        Response response = null;
        validateBioUpdateMessage(orcidMessage);
        validateEmailAvailability(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        response = t2OrcidApiServiceDelegator.updateBioDetails(uriInfo, orcid, upgradedMessage);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response addWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        Response response = null;
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        response = t2OrcidApiServiceDelegator.addWorks(uriInfo, orcid, upgradedMessage);
        return response;
    }

    @Override
    public Response updateWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        Response response = null;
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        response = t2OrcidApiServiceDelegator.updateWorks(uriInfo, orcid, upgradedMessage);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response addExternalIdentifiers(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        Response response = null;
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        response = t2OrcidApiServiceDelegator.addExternalIdentifiers(uriInfo, orcid, upgradedMessage);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response deleteProfile(UriInfo uriInfo, String orcid) {
        return t2OrcidApiServiceDelegator.deleteProfile(uriInfo, orcid);
    }

    @Override
    public Response registerWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        return t2OrcidApiServiceDelegator.registerWebhook(uriInfo, orcid, webhookUri);
    }

    @Override
    public Response unregisterWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        return t2OrcidApiServiceDelegator.unregisterWebhook(uriInfo, orcid, webhookUri);
    }

    private void validateRegistrationMessage(OrcidMessage orcidMessage) {
        try {
            if(orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidHistory() != null) {
                OrcidHistory history = orcidMessage.getOrcidProfile().getOrcidHistory();
                if(history.getClaimed() != null) {
                    throw new OrcidValidationException(new Throwable(localeManager.resolveMessage("apiError.validation_claimedstatus.exception")));
                } else if(history.getCreationMethod() != null) {
                    throw new OrcidValidationException(new Throwable(localeManager.resolveMessage("apiError.validation_creationmethod.exception")));
                } else if(history.getCompletionDate() != null) {
                    throw new OrcidValidationException(new Throwable(localeManager.resolveMessage("apiError.validation_completiondate.exception")));
                } else if(history.getSubmissionDate() != null) {
                    throw new OrcidValidationException(new Throwable(localeManager.resolveMessage("apiError.validation_submissiondate.exception")));
                } else if(history.getLastModifiedDate() != null) {
                    throw new OrcidValidationException(new Throwable(localeManager.resolveMessage("apiError.validation_lastmodifieddate.exception")));
                }
            }
            validateIncomingMessage(orcidMessage, null);
        } catch (OrcidValidationException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            } else {
                Throwable underlyingCause = cause.getCause();
                if (underlyingCause != null) {
                    cause = underlyingCause;
                }
            }
            Object params[] = {cause.toString()};
            throw new OrcidValidationException(localeManager.resolveMessage("apiError.validation_invalidmessage.exception", params));
        }
    }
    
    private void validateIncomingMessage(OrcidMessage orcidMessage, String orcid) {
        try {
            incomingValidationManager.validateMessage(orcidMessage);
            if(!PojoUtil.isEmpty(orcid)) {
            	checkDeprecation(orcid);
            }            
        } catch (OrcidValidationException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            } else {
                Throwable underlyingCause = cause.getCause();
                if (underlyingCause != null) {
                    cause = underlyingCause;
                }
            }
            Object params[] = {cause.toString()};
            throw new OrcidValidationException(localeManager.resolveMessage("apiError.validation_invalidmessage.exception", params));
        }
    }

    private void validateBioUpdateMessage(OrcidMessage orcidMessage) {
        try {
            incomingValidationManager.validateBioMessage(orcidMessage);
        } catch (OrcidValidationException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            } else {
                Throwable underlyingCause = cause.getCause();
                if (underlyingCause != null) {
                    cause = underlyingCause;
                }
            }
            Object params[] = {cause.toString()};
            throw new OrcidValidationException(localeManager.resolveMessage("apiError.validation_invalidmessage.exception", params));
        } 
    }
    
    /**
     * Checks if an account is deprecated
     * 
     * @param orcidMessage
     *            OrcidMessage, for it we can get the orcid to check for
     *            deprecation
     * @throws DeprecatedException
     *             if the account is deprecated
     * */
    private void checkDeprecation(String orcid) {
        ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);            
        if (entity != null) {
            if(entity.getDeprecatedDate() != null) {
                Map<String, String> params = new HashMap<String, String>(); 
                StringBuffer primary = new StringBuffer(orcidUrlManager.getBaseUrl()).append("/").append(entity.getPrimaryRecord().getId());
                params.put(OrcidDeprecatedException.ORCID, primary.toString());                    
                if(entity.getDeprecatedDate() != null) {
                    XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(entity.getDeprecatedDate());
                    params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
                }                    
                throw new OrcidDeprecatedException(params);                    
            }                
        }
    }    

    /**
     * Verify if the orcid message is deprecated, if so, builds a reponse object
     * with 301 status
     * 
     * @param response
     *            The current response object.
     * @return a response object.
     * */
    protected Response checkProfileStatus(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidDeprecated() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, orcidMessage.getOrcidProfile().getOrcidDeprecated().getPrimaryRecord().getOrcidIdentifier().getUri());
            params.put(OrcidDeprecatedException.DEPRECATED_DATE, orcidMessage.getOrcidProfile().getOrcidDeprecated().getDate().getValue().toXMLFormat());
            throw new OrcidDeprecatedException(params);
        }
        return response;
    }

    private void validateOutgoingResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        outgoingValidationManager.validateMessage(orcidMessage);
    }

    private OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        return orcidMessageVersionConverterChain.upgradeMessage(orcidMessage, OrcidMessage.DEFAULT_VERSION);
    }

    private Response downgradeResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null) {
            orcidMessageVersionConverterChain.downgradeMessage(orcidMessage, externalVersion);
        }
        return Response.fromResponse(response).entity(orcidMessage).build();
    }

    private Response downgradeAndValidateResponse(Response response) {
        checkProfileStatus(response);
        Response downgradedResponse = downgradeResponse(response);
        validateOutgoingResponse(downgradedResponse);
        return downgradedResponse;
    }

    @Override
    public Response addAffiliations(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        checkAffiliationsSupport();
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.addAffiliations(uriInfo, orcid, upgradedMessage);
    }

    @Override
    public Response updateAffiliations(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        checkAffiliationsSupport();
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.updateAffiliations(uriInfo, orcid, upgradedMessage);
    }

    private void checkAffiliationsSupport() {
        if (!supportsAffiliations) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_affiliations_notsupported.exception"));
        }
    }

    @Override
    public Response addFunding(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.addFunding(uriInfo, orcid, upgradedMessage);
    }

    @Override
    public Response updateFunding(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage, orcid);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.updateFunding(uriInfo, orcid, upgradedMessage);
    }
    
    private void validateEmailAvailability(OrcidMessage orcidMessage, String targetOrcid) {
        if(orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidBio() != null && orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails() != null) {
            List<Email> emailList = orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
            for(Email email : emailList) {
                if(!PojoUtil.isEmpty(email.getValue())) {
                    EmailEntity emailEntity = emailDao.findCaseInsensitive(email.getValue());
                    if(emailEntity != null) {
                        String emailOrcid = emailEntity.getProfile().getId();
                        if(!targetOrcid.equals(emailOrcid)) {
                        	Object params[] = {email.getValue()};
                        	throw new OrcidValidationException(localeManager.resolveMessage("apiError.validation_invalidmessage_email.exception", params));
                        }
                            
                    }                                        
                }                
            }
        }
    }

}
