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
package org.orcid.api.common.delegator.impl;

import static org.orcid.api.common.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.api.common.exception.OrcidNotFoundException;
import org.orcid.api.common.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.ValidationManager;
import org.orcid.core.security.visibility.aop.VisibilityControl;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.springframework.stereotype.Component;

/**
 * This class will retrieve {@link OrcidProfile}s and return them for use in the
 * Tier 1 API. Its is worth noting that this will not return
 * {@link OrcidProfile}s that have not been confirmed, but it does this by
 * checking the status on the object rather than at database level.
 * <p/>
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 02/03/2012
 */
@Component("orcidServiceDelegator")
public class OrcidApiServiceDelegatorImpl implements OrcidApiServiceDelegator {

    @Resource(name = "orcidProfileManager")
    private OrcidProfileManager orcidProfileManager;

    @Resource(name = "orcidSearchManager")
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private ValidationManager validationManager;

    /**
     * @return Plain text message indicating health of service
     */
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
    @VisibilityControl
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
    @VisibilityControl
    public Response findFullDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidProfile(orcid);
        return getOrcidMessageResponse(profile, orcid);
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
    public Response findWorksDetails(String orcid) {
        OrcidProfile profile = orcidProfileManager.retrieveClaimedOrcidWorks(orcid);
        return getOrcidMessageResponse(profile, orcid);
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
                OrcidProfile filteredProfile = new OrcidProfile();
                String retrievedOrcid = searchResult.getOrcidProfile().getOrcid().getValue();
                filteredSearchResult.setRelevancyScore(searchResult.getRelevancyScore());
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
            throw new OrcidNotFoundException("ORCID " + requestedOrcid + " not found");
        }

        profile.setOrcidInternal(null);
        OrcidMessage orcidMessage = new OrcidMessage(profile);
        validationManager.validateMessage(orcidMessage);

        return Response.ok(orcidMessage).build();
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
}
