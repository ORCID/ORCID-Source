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
package org.orcid.api.common.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidSearchException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.OrcidProfileManagerReadOnly;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.security.aop.NonLocked;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.aop.VisibilityControl;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * This class will retrieve {@link OrcidProfile}s and return them for use in the
 * Tier 1 API. Its is worth noting that this will not return
 * {@link OrcidProfile}s that have not been confirmed, but it does this by
 * checking the status on the object rather than at database level.
 * <p/>
 * 
 * @author Declan Newman (declan) Date: 02/03/2012
 */
public class OrcidApiServiceDelegatorImpl implements OrcidApiServiceDelegator {

    @Resource(name = "orcidProfileManagerReadOnly")
    private OrcidProfileManagerReadOnly orcidProfileManager;

    private OrcidSearchManager orcidSearchManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    LocaleManager localeManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidApiServiceDelegatorImpl.class);

    private static final int MAX_SEARCH_ROWS = 1000;

    @Required
    public void setOrcidSearchManager(OrcidSearchManager orcidSearchManager) {
        this.orcidSearchManager = orcidSearchManager;
    }

    /**
     * @return Plain text message indicating health of service
     */
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
    @VisibilityControl
    @NonLocked
    public Response findBioDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidBio(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    @Override
    @VisibilityControl()
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @NonLocked
    public Response findBioDetailsFromPublicCache(String orcid) {
        try {
            OrcidMessage orcidMessage = orcidSearchManager.findPublicProfileById(orcid);
            if (orcidMessage != null) {
                OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
                if (orcidProfile != null) {
                    orcidProfile.downgradeToBioOnly();
                }
            }
            return getOrcidMessageResponse(orcidMessage, orcid);
        } catch (OrcidSearchException e) {
            LOGGER.warn("Error searching, so falling back to DB", e);
            return findBioDetails(orcid);
        }
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
    @VisibilityControl
    @NonLocked
    public Response findExternalIdentifiers(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedExternalIdentifiers(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    @Override
    @VisibilityControl()
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @NonLocked
    public Response findExternalIdentifiersFromPublicCache(String orcid) {
        try {
            OrcidMessage orcidMessage = orcidSearchManager.findPublicProfileById(orcid);
            if (orcidMessage != null) {
                OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
                if (orcidProfile != null) {
                    orcidProfile.downgradeToExternalIdentifiersOnly();
                }
            }
            return getOrcidMessageResponse(orcidMessage, orcid);
        } catch (OrcidSearchException e) {
            LOGGER.warn("Error searching, so falling back to DB", e);
            return findExternalIdentifiers(orcid);
        }
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
    @VisibilityControl
    @NonLocked
    public Response findFullDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidProfile(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    @Override
    @VisibilityControl()
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @NonLocked
    public Response findFullDetailsFromPublicCache(String orcid) {
        try {
            OrcidMessage orcidMessage = orcidSearchManager.findPublicProfileById(orcid);
            return getOrcidMessageResponse(orcidMessage, orcid);
        } catch (OrcidSearchException e) {
            LOGGER.warn("Error searching, so falling back to DB", e);
            return findFullDetails(orcid);
        }
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the affiiation
     * details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @VisibilityControl
    @NonLocked
    public Response findAffiliationsDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedAffiliations(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    @Override
    @VisibilityControl()
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @NonLocked
    public Response findAffiliationsDetailsFromPublicCache(String orcid) {
        try {
            OrcidMessage orcidMessage = orcidSearchManager.findPublicProfileById(orcid);
            if (orcidMessage != null) {
                OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
                if (orcidProfile != null) {
                    orcidProfile.downgradeToAffiliationsOnly();
                }
            }
            return getOrcidMessageResponse(orcidMessage, orcid);
        } catch (OrcidSearchException e) {
            LOGGER.warn("Error searching, so falling back to DB", e);
            return findAffiliationsDetails(orcid);
        }
    }

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link javax.xml.ws.Response} with only the grants details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link javax.xml.ws.Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    @Override
    @VisibilityControl
    @NonLocked
    public Response findFundingDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedFundings(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    @Override
    @VisibilityControl()
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @NonLocked
    public Response findFundingDetailsFromPublicCache(String orcid) {
        try {
            OrcidMessage orcidMessage = orcidSearchManager.findPublicProfileById(orcid);
            if (orcidMessage != null) {
                OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
                if (orcidProfile != null) {
                    orcidProfile.downgradeToFundingsOnly();
                }
            }
            return getOrcidMessageResponse(orcidMessage, orcid);
        } catch (OrcidSearchException e) {
            LOGGER.warn("Error searching, so falling back to DB", e);
            return findAffiliationsDetails(orcid);
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
    @VisibilityControl
    @NonLocked
    public Response findWorksDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidWorks(orcid);
        return getOrcidMessageResponse(profile, orcid);
    }

    @Override
    @VisibilityControl()
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @NonLocked
    public Response findWorksDetailsFromPublicCache(String orcid) {
        try {
            OrcidMessage orcidMessage = orcidSearchManager.findPublicProfileById(orcid);
            if (orcidMessage != null) {
                OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
                if (orcidProfile != null) {
                    orcidProfile.downgradeToWorksOnly();
                }
            }
            return getOrcidMessageResponse(orcidMessage, orcid);
        } catch (OrcidSearchException e) {
            LOGGER.warn("Error searching, so falling back to DB", e);
            return findWorksDetails(orcid);
        }
    }

    @Override
    public Response redirectClientToGroup(String clientId) {
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        if (clientDetails == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        String groupOrcid = clientDetails.getGroupProfileId();
        URI groupUri;
        try {
            groupUri = new URI(jpa2JaxbAdapter.getOrcidIdBase(groupOrcid).getUri());
            return Response.seeOther(groupUri).build();
        } catch (URISyntaxException e) {
            LOGGER.error("Problem redirecting to group: {}", groupOrcid, e);
            return Response.serverError().build();
        }
    }

    /**
     * See {@link OrcidApiServiceDelegator}{@link #publicSearchByQuery(Map)}
     */
    @Override
    @VisibilityControl
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response publicSearchByQuery(Map<String, List<String>> queryMap) {
        return searchByQuery(queryMap);
    }

    /**
     * See {@link OrcidApiServiceDelegator}{@link #searchByQuery(Map)}
     */
    @Override
    @VisibilityControl
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        validateSearchParams(queryMap);
        OrcidMessage orcidMessage = orcidSearchManager.findOrcidsByQuery(queryMap);
        List<OrcidSearchResult> searchResults = orcidMessage.getOrcidSearchResults() != null ? orcidMessage.getOrcidSearchResults().getOrcidSearchResult() : null;
        List<OrcidSearchResult> filteredResults = new ArrayList<OrcidSearchResult>();
        OrcidSearchResults orcidSearchResults = new OrcidSearchResults();
        if (searchResults != null) {
            orcidSearchResults.setNumFound(orcidMessage.getOrcidSearchResults().getNumFound());
            if (searchResults.size() > 0) {
                for (OrcidSearchResult searchResult : searchResults) {
                    OrcidSearchResult filteredSearchResult = new OrcidSearchResult();
                    OrcidProfile filteredProfile = new OrcidProfile();
                    filteredSearchResult.setRelevancyScore(searchResult.getRelevancyScore());
                    filteredProfile.setOrcid(searchResult.getOrcidProfile().getOrcid());
                    filteredProfile.setOrcidId(searchResult.getOrcidProfile().getOrcidId());
                    filteredProfile.setOrcidIdentifier(searchResult.getOrcidProfile().getOrcidIdentifier());
                    filteredProfile.setOrcidBio(searchResult.getOrcidProfile().getOrcidBio());
                    filteredSearchResult.setOrcidProfile(filteredProfile);
                    filteredResults.add(filteredSearchResult);
                }
            }
        }
        orcidSearchResults.getOrcidSearchResult().addAll(filteredResults);
        return getOrcidSearchResultsResponse(orcidSearchResults, queryMap.toString());
    }

    private void validateSearchParams(Map<String, List<String>> queryMap) {
        List<String> rowsList = queryMap.get("rows");
        if (rowsList != null && !rowsList.isEmpty()) {
            try {
                String rowsString = rowsList.get(0);
                int rows = Integer.valueOf(rowsString);
                if (rows < 0 || rows > MAX_SEARCH_ROWS) {
                    throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_search_rows.exception"));
                }
            } catch (NumberFormatException e) {
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_search_rows.exception"));
            }
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
        if (profile == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", requestedOrcid);
            throw new OrcidNotFoundException(params);
        }

        profile.setOrcidInternal(null);
        OrcidMessage orcidMessage = new OrcidMessage(profile);

        return Response.ok(orcidMessage).build();
    }

    private Response getOrcidMessageResponse(OrcidMessage orcidMessage, String requestedOrcid) {
        boolean isProfileDeprecated = false;
        if (orcidMessage == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", requestedOrcid);
            throw new OrcidNotFoundException(params);
        }
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            orcidProfile.setOrcidInternal(null);
            // If profile is deprecated
            if (orcidMessage.getOrcidProfile().getOrcidDeprecated() != null) {
                isProfileDeprecated = true;
            }
        }

        Response response = null;

        if (isProfileDeprecated) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, orcidProfile.getOrcidDeprecated().getPrimaryRecord().getOrcidIdentifier().getUri());
            if (orcidProfile.getOrcidDeprecated().getDate() != null) {
                XMLGregorianCalendar deprecatedDate = orcidProfile.getOrcidDeprecated().getDate().getValue();
                params.put(OrcidDeprecatedException.DEPRECATED_DATE, deprecatedDate.toString());
            }
            throw new OrcidDeprecatedException(params);
        } else {
            response = Response.ok(orcidMessage).build();
        }
        return response;
    }

    private Response getOrcidSearchResultsResponse(OrcidSearchResults orcidSearchResults, String query) {

        if (orcidSearchResults != null) {
            OrcidMessage orcidMessage = new OrcidMessage();
            orcidMessage.setMessageVersion("1.2");
            orcidMessage.setOrcidSearchResults(orcidSearchResults);
            return Response.ok(orcidMessage).build();
        } else {
            Object params[] = { query };
            throw new NoResultException(localeManager.resolveMessage("apiError.no_search_result.exception", params));
        }
    }
}
