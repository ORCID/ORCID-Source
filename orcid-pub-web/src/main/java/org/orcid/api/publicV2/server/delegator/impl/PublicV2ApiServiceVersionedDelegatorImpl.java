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
        Response response = publicV2ApiServiceDelegator.viewStatusText();
        return response;
    }

    @Override
    public Response viewActivities(String orcid) {
        Response response = publicV2ApiServiceDelegator.viewActivities(orcid);
        response = downgradeResponse(response);
        return response;
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewWork(orcid, putCode);
        return response;
    }

    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewWorkCitation(orcid, putCode);
        return response;
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewWorkSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewFunding(orcid, putCode);
        return response;
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewFundingSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewEducation(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewEducationSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewEmployment(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewPeerReview(orcid, putCode);
        return response;
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewGroupIdRecord(putCode);
        return response;
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        Response response = publicV2ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum);
        return response;
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

    @Override
    public Response viewResearcherUrls(String orcid) {
        Response response = publicV2ApiServiceDelegator.viewResearcherUrls(orcid);
        return response;
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEmails(String orcid) {
        Response response = publicV2ApiServiceDelegator.viewEmails(orcid);
        return response;
    }

    @Override
    public Response viewOtherNames(String orcid) {
        Response response = publicV2ApiServiceDelegator.viewOtherNames(orcid);
        return response;
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewOtherName(orcid, putCode);
        return response;
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        Response response = publicV2ApiServiceDelegator.viewPersonalDetails(orcid);
        return response;
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        Response response = publicV2ApiServiceDelegator.viewExternalIdentifiers(orcid);
        return response;
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        Response response = publicV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode);
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

    @Override
    public Response viewBiography(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response viewKeywords(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response viewAddresses(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response viewPerson(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

}
