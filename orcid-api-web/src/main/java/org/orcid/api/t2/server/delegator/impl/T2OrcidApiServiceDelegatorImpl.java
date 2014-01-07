/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import static org.orcid.api.common.OrcidApiConstants.AFFILIATIONS_PATH;
import static org.orcid.api.common.OrcidApiConstants.FUNDING_PATH;
import static org.orcid.api.common.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.api.common.OrcidApiConstants.STATUS_OK_MESSAGE;
import static org.orcid.api.common.OrcidApiConstants.WORKS_PATH;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.api.common.delegator.impl.OrcidApiServiceDelegatorImpl;
import org.orcid.api.common.exception.OrcidBadRequestException;
import org.orcid.api.common.exception.OrcidForbiddenException;
import org.orcid.api.common.exception.OrcidNotFoundException;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.aop.VisibilityControl;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

/**
 * 2011-2012 ORCID
 * <p/>
 * The delegator for the tier 2 API.
 * <p/>
 * The T2 delegator is responsible for the validation, retrieving results and 
 * passing of objects to be from the core
 * 
 * @author Declan Newman (declan) Date: 07/03/2012
 */
@Component("orcidT2ServiceDelegator")
public class T2OrcidApiServiceDelegatorImpl extends OrcidApiServiceDelegatorImpl implements T2OrcidApiServiceDelegator {

    @Resource(name = "orcidProfileManager")
    private OrcidProfileManager orcidProfileManager;

    @Resource(name = "orcidSearchManager")
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private WebhookDao webhookDao;

    @Resource
    private ProfileDao profileDao;

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
    @AccessControl(requiredScope = ScopePathType.ORCID_PROFILE_READ_LIMITED)
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
            throw new OrcidBadRequestException("Cannot create ORCID");
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
    public Response findWorksDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidWorks(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }
    
    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the affiliation details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED)
    public Response findAffiliationsDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedAffiliations(orcid);
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
        checkHasAtLeastOneEmail(orcidProfile);
        try {
            setSponsorFromAuthentication(orcidProfile);
            orcidProfile = orcidProfileManager.createOrcidProfileAndNotify(orcidProfile);
            return getCreatedResponse(uriInfo, PROFILE_GET_PATH, orcidProfile);
        } catch (DataAccessException e) {
            if (e.getCause() != null && ConstraintViolationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw new OrcidBadRequestException("User with this email already exist.");
            }
            throw new OrcidBadRequestException("Cannot create ORCID", e);
        }
    }

    private void checkHasAtLeastOneEmail(OrcidProfile orcidProfile) {
        if (NullUtils.anyNull(orcidProfile.getOrcidBio(), orcidProfile.getOrcidBio().getContactDetails())
                || orcidProfile.getOrcidBio().getContactDetails().getEmail().isEmpty()) {
            throw new OrcidBadRequestException("There must be a least one email in the new profile");
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
            throw new OrcidBadRequestException("Cannot update ORCID");
        } catch (PersistenceException pe) {
            throw new OrcidBadRequestException("One of the parameters passed in the request is either too big or invalid.");
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
            throw new OrcidBadRequestException("Cannot update ORCID");
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
                AuthorizationRequest authorizationRequest = ((OAuth2Authentication) authentication).getAuthorizationRequest();
                clientId = authorizationRequest.getClientId();
            }

            for (ExternalIdentifier ei : updatedExternalIdentifiers.getExternalIdentifier()) {
                // Set the client profile to each external identifier
                if (ei.getExternalIdOrcid() == null) {
                    ExternalIdOrcid eio = new ExternalIdOrcid(clientId);
                    ei.setExternalIdOrcid(eio);
                } else {
                    // Check if the provided external orcid exists
                    ExternalIdOrcid eio = ei.getExternalIdOrcid();

                    if (StringUtils.isBlank(eio.getPath()) || !profileEntityManager.orcidExists(eio.getValueAsString())) {
                        throw new OrcidNotFoundException("Cannot find external ORCID");
                    }
                }
            }

            orcidProfile = orcidProfileManager.addExternalIdentifiers(orcidProfile);
            return getOrcidMessageResponse(orcidProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException("Cannot create ORCID");
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    public Response deleteProfile(UriInfo uriInfo, String orcid) {
        try {
            OrcidProfile deletedProfile = orcidProfileManager.deleteProfile(orcid);
            return getOrcidMessageResponse(deletedProfile, orcid);
        } catch (DataAccessException e) {
            throw new OrcidBadRequestException("Cannot delete ORCID");
        }
    }

    private Response getCreatedResponse(UriInfo uriInfo, String requested, OrcidProfile profile) {
        if (profile != null && profile.getOrcid() != null) {
            return getCreatedResponse(uriInfo, requested, profile.getOrcid().getValue());
        } else {
            throw new OrcidNotFoundException("Cannot find ORCID");
        }
    }

    private Response getCreatedResponse(UriInfo uriInfo, String requested, String orcid) {
        if (StringUtils.isNotBlank(orcid)) {
            URI uri = uriInfo.getBaseUriBuilder().path("/").path(requested).build(orcid);
            return Response.created(uri).build();
        } else {
            throw new OrcidNotFoundException("Cannot find ORCID");
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
            return Response.ok(orcidMessage).build();
        } else {
            throw new OrcidNotFoundException("ORCID " + requestedOrcid + " not found");
        }
    }

    /**
     * See {@link OrcidApiServiceDelegator}{@link #searchByQuery(Map)}
     */
    @Override
    @VisibilityControl
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        OrcidMessage orcidMessage = orcidSearchManager.findOrcidsByQuery(queryMap);
        List<OrcidSearchResult> searchResults = orcidMessage.getOrcidSearchResults() != null ? orcidMessage.getOrcidSearchResults().getOrcidSearchResult() : null;
        List<OrcidSearchResult> filteredResults = new ArrayList<OrcidSearchResult>();
        if (searchResults != null && searchResults.size() > 0) {
            for (OrcidSearchResult searchResult : searchResults) {
                OrcidSearchResult filteredSearchResult = new OrcidSearchResult();
                filteredSearchResult.setRelevancyScore(searchResult.getRelevancyScore());
                OrcidProfile filteredProfile = new OrcidProfile();
                String retrievedOrcid = searchResult.getOrcidProfile().getOrcid().getValue();
                filteredProfile.setOrcid(retrievedOrcid);
                filteredProfile.setOrcidBio(searchResult.getOrcidProfile().getOrcidBio());
                filteredSearchResult.setOrcidProfile(filteredProfile);
                filteredResults.add(filteredSearchResult);
            }

        }

        OrcidSearchResults orcidSearchResults = new OrcidSearchResults();
        orcidSearchResults.getOrcidSearchResult().addAll(filteredResults);
        return getOrcidSearchResultsResponse(orcidSearchResults, queryMap.toString());

    }

    private Response getOrcidSearchResultsResponse(OrcidSearchResults orcidSearchResults, String query) {
        if (orcidSearchResults != null) {
            OrcidMessage orcidMessage = new OrcidMessage();
            orcidMessage.setOrcidSearchResults(orcidSearchResults);
            return Response.ok(orcidMessage).build();
        } else {
            throw new OrcidNotFoundException("No search results found using query " + query);
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
            AuthorizationRequest authorizationRequest = ((OAuth2Authentication) authentication).getAuthorizationRequest();
            Source sponsor = new Source();
            String sponsorOrcid = authorizationRequest.getClientId();
            OrcidProfile sponsorProfile = orcidProfileManager.retrieveOrcidProfile(sponsorOrcid);
            sponsor.setSourceName(new SourceName(sponsorProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent()));
            sponsor.setSourceOrcid(new SourceOrcid(sponsorOrcid));
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
            throw new OrcidBadRequestException(String.format("Webhook uri:%s is syntactically incorrect", webhookUri));
        }

        ProfileEntity profile = profileDao.find(orcid);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProfileEntity clientProfile = null;
        String clientId = null;
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            AuthorizationRequest authorizationRequest = ((OAuth2Authentication) authentication).getAuthorizationRequest();
            clientId = authorizationRequest.getClientId();
            clientProfile = profileDao.find(clientId);
        }
        if (profile != null && clientProfile != null) {
            WebhookEntityPk webhookPk = new WebhookEntityPk(profile, webhookUri);
            WebhookEntity webhook = webhookDao.find(webhookPk);
            boolean isNew = webhook == null;
            if (isNew) {
                webhook = new WebhookEntity();
                webhook.setProfile(profile);
                webhook.setDateCreated(new Date());
                webhook.setEnabled(true);
                webhook.setUri(webhookUri);
                webhook.setClientDetails(clientProfile.getClientDetails());
            }
            webhookDao.merge(webhook);
            webhookDao.flush();

            return isNew ? Response.created(uriInfo.getAbsolutePath()).build() : Response.noContent().build();
        } else if (profile == null) {
            throw new OrcidNotFoundException("Unable to find profile associated with orcid:" + orcid);
        } else {
            throw new OrcidNotFoundException("Unable to find client profile associated with client:" + clientId);
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
        ProfileEntity profile = profileDao.find(orcid);
        if (profile != null) {
            WebhookEntityPk webhookPk = new WebhookEntityPk(profile, webhookUri);
            WebhookEntity webhook = webhookDao.find(webhookPk);
            if (webhook == null) {
                throw new OrcidNotFoundException(String.format("No webhook found for orcid=%s, and uri=%s", orcid, webhookUri));
            } else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String clientId = null;
                if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
                    AuthorizationRequest authorizationRequest = ((OAuth2Authentication) authentication).getAuthorizationRequest();
                    clientId = authorizationRequest.getClientId();
                }
                // Check if user can unregister this webhook
                if (webhook.getClientDetails().getId().equals(clientId)) {
                    webhookDao.remove(webhookPk);
                    webhookDao.flush();
                    return Response.noContent().build();
                } else {
                    // Throw 403 exception: user is not allowed to unregister
                    // that webhook
                    throw new OrcidForbiddenException("Unable to unregister webhook: Only the client that register the webhook can unregister it.");
                }
            }
        } else {
            throw new OrcidNotFoundException("Unable to find profile associated with orcid:" + orcid);
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
            throw new OrcidBadRequestException("Cannot update ORCID");
        } catch (PersistenceException pe) {
            throw new OrcidBadRequestException("One of the parameters passed in the request is either too big or invalid.");
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
            throw new OrcidBadRequestException("Cannot update ORCID");
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
            throw new OrcidBadRequestException("Cannot update ORCID");
        } catch (PersistenceException pe) {
            throw new OrcidBadRequestException("One of the parameters passed in the request is either too big or invalid.");
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
            throw new OrcidBadRequestException("Cannot update ORCID");
        }
    }

}
