package org.orcid.api.t2.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;
import static org.orcid.core.api.OrcidApiConstants.WORKS_PATH;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.orcid.api.common.delegator.impl.OrcidApiServiceDelegatorImpl;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidClientNotFoundException;
import org.orcid.core.exception.OrcidForbiddenException;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidWebhookNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.WebhookManager;
import org.orcid.core.security.aop.NonLocked;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.utils.OrcidMessageUtil;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * <p/>
 * The delegator for the tier 2 API.
 * <p/>
 * The T2 delegator is responsible for the validation, retrieving results and
 * passing of objects to be from the core
 * 
 * @author Declan Newman (declan) Date: 07/03/2012
 */
public class T2OrcidApiServiceDelegatorImpl extends OrcidApiServiceDelegatorImpl implements T2OrcidApiServiceDelegator {

    @Resource(name = "orcidProfileManager")
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private WebhookManager webhookManager;
    
    @Resource
    private LocaleManager localeManager;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private OrcidMessageUtil orcidMessageUtil;
    
    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the profile's bio
     * details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    @NonLocked
    public Response findBioDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidBio(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the profile's
     * external identifier details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    @NonLocked
    public Response findExternalIdentifiers(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedExternalIdentifiers(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with all of the profile's
     * details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED)
    @NonLocked
    public Response findFullDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidProfile(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    /**
     * Takes the {@link org.orcid.jaxb.model.message.OrcidMessage} and attempts
     * to update the bio details only. If there is content other than the bio in
     * the message, it should return a 400 Bad Request. Privilege checks will be
     * performed to determine if the client or user has permissions to perform
     * the update.
     * 
     * @param orcidMessage
     *            the message containing the bio to be updated. Any elements
     *            outside of the bio will cause a 400 Bad Request to be returned
     * @return if the update was successful, a 200 response will be returned
     *         with the updated
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response updateBioDetails(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfile = orcidProfileManager.updateOrcidBio(orcidProfile);
            if (orcidProfile != null) {
                orcidProfile.setOrcidActivities(null);
            }
            return getOrcidMessageResponse(orcidProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_createorcid.exception"));
        }
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the work details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED)
    @NonLocked
    public Response findWorksDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidWorks(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the affiliation
     * details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED)
    @NonLocked
    public Response findAffiliationsDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedAffiliations(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the funding details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED)
    @NonLocked
    public Response findFundingDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedFundings(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    /**
     * Creates a new profile and returns the saved representation of it. The
     * response should include the 'location' to retrieve the newly created
     * profile from.
     * 
     * @param orcidMessage
     *            the message to be saved. If the message already contains an
     *            ORCID value a 400 Bad Request
     * @return if the creation was successful, returns a 201 along with the
     *         location of the newly created resource otherwise returns an error
     *         response describing the problem
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_PROFILE_CREATE)
    public Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            setSponsorFromAuthentication(orcidProfile);
            orcidProfile = orcidProfileManager.createOrcidProfileAndNotify(orcidProfile);
            return getCreatedResponse(uriInfo, PROFILE_GET_PATH, orcidProfile);
        } catch (DataAccessException e) {
            if (e.getCause() != null && ConstraintViolationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_email_exists.exception"));
            }
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_createorcid.exception"), e);
        }
    }

    /**
     * Add works to an existing ORCID profile. If the profile already contains
     * an identifiable work with the same id a 409 Conflict should be returned.
     * 
     * @param orcidMessage
     *            the message containing the works to be added
     * @return if the works were all added successfully, a 201 with a location
     *         should be returned
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_CREATE)
    public Response addWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfileManager.addOrcidWorks(orcidProfile);
            return getCreatedResponse(uriInfo, WORKS_PATH, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_updateorcid.exception"));
        } catch (PersistenceException pe) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_params.exception"));
        }
    }

    /**
     * Update the works for a given ORCID profile. This will cause all content
     * to be overwritten
     * 
     * @param orcidMessage
     *            the message containing all works to overwritten. If any other
     *            elements outside of the works are present, a 400 Bad Request
     *            is returned
     * @return
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_UPDATE)
    public Response updateWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfile = orcidProfileManager.updateOrcidWorks(orcidProfile);
            return getOrcidMessageResponse(orcidProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_updateorcid.exception"));
        }
    }

    /**
     * Add new external identifiers to the profile. As with all calls, if the
     * message contains any other elements, a 400 Bad Request will be returned.
     * 
     * @param orcidMessage
     *            the message congtaining the external ids
     * @return If successful, returns a 200 OK with the updated content.
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE)
    public Response addExternalIdentifiers(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {

            ExternalIdentifiers updatedExternalIdentifiers = orcidProfile.getOrcidBio().getExternalIdentifiers();

            // Get the client profile information
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String clientId = null;
            if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
                OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
                clientId = authorizationRequest.getClientId();
            }

            for (ExternalIdentifier ei : updatedExternalIdentifiers.getExternalIdentifier()) {
                // Set the client profile to each external identifier
                if (ei.getSource() == null) {
                    Source source = new Source();
                    source.setSourceClientId(new SourceClientId(clientId));
                    ei.setSource(source);
                } else {
                    // Check if the provided external orcid exists
                    Source source = ei.getSource();
                    String sourceOrcid = source.retrieveSourcePath();
                    if (sourceOrcid != null) {
                        if (StringUtils.isBlank(sourceOrcid) || (!profileEntityManager.orcidExists(sourceOrcid) && !clientDetailsManager.exists(sourceOrcid))) {
                        	Map<String, String> params = new HashMap<String, String>();
                        	params.put("orcid", sourceOrcid);
                            throw new OrcidNotFoundException(params);
                        }
                    }                    
                }
            }

            orcidProfile = orcidProfileManager.addExternalIdentifiers(orcidProfile);
            return getOrcidMessageResponse(orcidProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_createorcid.exception"));
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    public Response deleteProfile(UriInfo uriInfo, String orcid) {
        try {
            OrcidProfile deletedProfile = orcidProfileManager.deleteProfile(orcid);
            return getOrcidMessageResponse(deletedProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_deleteorcid.exception"));
        }
    }

    private Response getCreatedResponse(UriInfo uriInfo, String requested, OrcidProfile profile) {
        if (profile != null && profile.getOrcidIdentifier() != null) {
            return getCreatedResponse(uriInfo, requested, profile.getOrcidIdentifier().getPath());
        } else {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_findorcid.exception"));
        }
    }

    private Response getCreatedResponse(UriInfo uriInfo, String requested, String orcid) {
        if (StringUtils.isNotBlank(orcid)) {
            URI uri = uriInfo.getBaseUriBuilder().path("/").path(requested).build(orcid);
            return Response.created(uri).build();
        } else {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_findorcid.exception"));
        }
    }

    /**
     * Method to perform the mundane task of checking for null and returning the
     * response with an OrcidMessage entity
     * 
     * @param profile
     * @param requestedOrcid
     * @return
     */
    private Response getOrcidMessageResponse(OrcidProfile profile, String requestedOrcid) {
        if (profile != null) {            
            OrcidMessage orcidMessage = new OrcidMessage(profile);
            orcidMessageUtil.setSourceName(orcidMessage);
            return Response.ok(orcidMessage).build();
        } else {
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("orcid", requestedOrcid);
            throw new OrcidNotFoundException(params);
        }
    }

    public void setSponsorFromAuthentication(OrcidProfile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (profile.getOrcidHistory() == null) {
            OrcidHistory orcidHistory = new OrcidHistory();
            orcidHistory.setCreationMethod(CreationMethod.API);
            profile.setOrcidHistory(orcidHistory);
        }
        profile.getOrcidHistory().setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            Source sponsor = new Source();
            String sponsorId = authorizationRequest.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(sponsorId);
            if (clientDetails != null) {
                sponsor.setSourceName(new SourceName(clientDetails.getClientName()));
                if (OrcidStringUtils.isClientId(sponsorId)) {
                    sponsor.setSourceClientId(new SourceClientId(sponsorId));
                } else {
                    sponsor.setSourceOrcid(new SourceOrcid(sponsorId));
                }
            }
            profile.getOrcidHistory().setSource(sponsor);
        }
    }

    /**
     * Register a new webhook to the profile. As with all calls, if the message
     * contains any other elements, a 400 Bad Request will be returned.
     * 
     * @param orcid
     *            the identifier of the profile to add the webhook
     * @param uriInfo
     *            an uri object containing the webhook
     * @return If successful, returns a 2xx.
     * */
    @Override
    @AccessControl(requiredScope = ScopePathType.WEBHOOK)
    public Response registerWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        @SuppressWarnings("unused")
        URI validatedWebhookUri = null;
        try {
            validatedWebhookUri = new URI(webhookUri);
        } catch (URISyntaxException e) {
        	Object params[] = {webhookUri};
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_incorrect_webhook.exception", params));
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ClientDetailsEntity clientDetails = null;
        String clientId = null;
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            clientId = authorizationRequest.getClientId();
            clientDetails = clientDetailsManager.findByClientId(clientId);
        }
        if (profile != null && clientDetails != null) {
            WebhookEntityPk webhookPk = new WebhookEntityPk(profile, webhookUri);
            WebhookEntity webhook = webhookManager.find(webhookPk);
            boolean isNew = webhook == null;
            if (isNew) {
                webhook = new WebhookEntity();
                webhook.setProfile(profile);
                webhook.setDateCreated(new Date());
                webhook.setEnabled(true);
                webhook.setUri(webhookUri);
                webhook.setClientDetails(clientDetails);
            }
            webhookManager.update(webhook);
            return isNew ? Response.created(uriInfo.getAbsolutePath()).build() : Response.noContent().build();
        } else if (profile == null) {
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("orcid", orcid);
            throw new OrcidNotFoundException(params);
        } else {
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("client", clientId);
            throw new OrcidClientNotFoundException(params);
        }
    }

    /**
     * Unregister a webhook from a profile. As with all calls, if the message
     * contains any other elements, a 400 Bad Request will be returned.
     * 
     * @param orcid
     *            the identifier of the profile to unregister the webhook
     * @param uriInfo
     *            an uri object containing the webhook that will be unregistred
     * @return If successful, returns a 204 No content.
     * */
    @Override
    @AccessControl(requiredScope = ScopePathType.WEBHOOK)
    public Response unregisterWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile != null) {
            WebhookEntityPk webhookPk = new WebhookEntityPk(profile, webhookUri);
            WebhookEntity webhook = webhookManager.find(webhookPk);
            if (webhook == null) {
            	Map<String, String> params = new HashMap<String, String>();
            	params.put("orcid", orcid);
            	params.put("uri", webhookUri);
                throw new OrcidWebhookNotFoundException(params);
            } else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String clientId = null;
                if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
                    OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
                    clientId = authorizationRequest.getClientId();
                }
                // Check if user can unregister this webhook
                if (webhook.getClientDetails().getId().equals(clientId)) {
                    webhookManager.delete(webhookPk);
                    return Response.noContent().build();
                } else {
                    // Throw 403 exception: user is not allowed to unregister
                    // that webhook
                    throw new OrcidForbiddenException(localeManager.resolveMessage("apiError.forbidden_unregister_webhook.exception"));
                }
            }
        } else {
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("orcid", orcid);
            throw new OrcidNotFoundException(params);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_CREATE)
    public Response addAffiliations(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfileManager.addAffiliations(orcidProfile);
            return getCreatedResponse(uriInfo, AFFILIATIONS_PATH, orcidProfile);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_updateorcid.exception"));
        } catch (PersistenceException pe) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_params.exception"));
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_UPDATE)
    public Response updateAffiliations(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfile = orcidProfileManager.updateAffiliations(orcidProfile);
            return getOrcidMessageResponse(orcidProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_updateorcid.exception"));
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_CREATE)
    public Response addFunding(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfileManager.addFundings(orcidProfile);
            return getCreatedResponse(uriInfo, FUNDING_PATH, orcidProfile);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_updateorcid.exception"));
        } catch (PersistenceException pe) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_params.exception"));
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_UPDATE)
    public Response updateFunding(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            orcidProfile = orcidProfileManager.updateFundings(orcidProfile);
            return getOrcidMessageResponse(orcidProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_updateorcid.exception"));
        }
    }

}
