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
package org.orcid.api.publicV2.server.delegator.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;
import org.orcid.jaxb.model.message.OrcidMessage;

public class PublicV2ApiServiceVersionedDelegatorImpl implements PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> {

    @Resource
    private PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> publicV2ApiServiceDelegator;

    private String externalVersion;

    @Resource
    private V2VersionConverterChain v2VersionConverterChain;

    @Override
    public Response viewStatusText() {
        return publicV2ApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response viewActivities(String orcid) {        
        return downgradeResponse(publicV2ApiServiceDelegator.viewActivities(orcid));
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewWork(orcid, putCode));
    }

    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewWorkCitation(orcid, putCode));
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewWorkSummary(orcid, putCode));
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewFunding(orcid, putCode));
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewFundingSummary(orcid, putCode));
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewEducation(orcid, putCode));
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewEducationSummary(orcid, putCode));
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewEmployment(orcid, putCode));
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode));
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewPeerReview(orcid, putCode));
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode));
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        return publicV2ApiServiceDelegator.viewGroupIdRecord(putCode);
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        return publicV2ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum);
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewResearcherUrls(orcid));
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode));
    }

    @Override
    public Response viewEmails(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewEmails(orcid));
    }

    @Override
    public Response viewOtherNames(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewOtherNames(orcid));
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewOtherName(orcid, putCode));
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewPersonalDetails(orcid));
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewExternalIdentifiers(orcid));
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode));
    }

    @Override
    public Response viewBiography(String orcid) {       
        return downgradeResponse(publicV2ApiServiceDelegator.viewBiography(orcid));
    }

    @Override
    public Response viewKeywords(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewKeywords(orcid));
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewKeyword(orcid, putCode));
    }

    @Override
    public Response viewAddresses(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewAddresses(orcid));
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewAddress(orcid, putCode));
    }

    @Override
    public Response viewPerson(String orcid) {
        return downgradeResponse(publicV2ApiServiceDelegator.viewPerson(orcid));
    }

    private Response downgradeAndValidateResponse(Response response) {
        checkProfileStatus(response);
        Response downgradedResponse = downgradeResponse(response);
        return downgradedResponse;
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V2Convertible(entity, PublicV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
        }
        return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
    }

    protected Response checkProfileStatus(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidDeprecated() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcidMessage.getOrcidProfile().getOrcidDeprecated().getPrimaryRecord().getOrcidIdentifier().getPath());
            throw new OrcidDeprecatedException(params);
        }
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMemberV2ApiServiceDelegator(PublicV2ApiServiceDelegator memberV2ApiServiceDelegator) {
        this.publicV2ApiServiceDelegator = memberV2ApiServiceDelegator;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }
}
