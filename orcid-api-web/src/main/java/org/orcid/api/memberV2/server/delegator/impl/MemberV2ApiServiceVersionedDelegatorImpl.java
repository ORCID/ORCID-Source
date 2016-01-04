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
package org.orcid.api.memberV2.server.delegator.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;

public class MemberV2ApiServiceVersionedDelegatorImpl implements MemberV2ApiServiceDelegator {

    @Resource
    private MemberV2ApiServiceDelegator memberV2ApiServiceDelegator;

    private String externalVersion;

    @Resource
    private OrcidMessageVersionConverterChain orcidMessageVersionConverterChain;

    @Override
    public Response viewStatusText() {
        Response response = memberV2ApiServiceDelegator.viewStatusText();
        return response;
    }

    @Override
    public Response viewActivities(String orcid) {
        Response response = memberV2ApiServiceDelegator.viewActivities(orcid);
        return response;
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewWork(orcid, putCode);
        return response;
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewWorkSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response createWork(String orcid, Work work) {
        Response response = memberV2ApiServiceDelegator.createWork(orcid, work);
        return response;
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Work work) {
        Response response = memberV2ApiServiceDelegator.updateWork(orcid, putCode, work);
        return response;
    }

    @Override
    public Response deleteWork(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteWork(orcid, putCode);
        return response;
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewFunding(orcid, putCode);
        return response;
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewFundingSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response createFunding(String orcid, Funding funding) {
        Response response = memberV2ApiServiceDelegator.createFunding(orcid, funding);
        return response;
    }

    @Override
    public Response updateFunding(String orcid, Long putCode, Funding funding) {
        Response response = memberV2ApiServiceDelegator.updateFunding(orcid, putCode, funding);
        return response;
    }

    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteFunding(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewEducation(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewEducationSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response createEducation(String orcid, Education education) {
        Response response = memberV2ApiServiceDelegator.createEducation(orcid, education);
        return response;
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Education education) {
        Response response = memberV2ApiServiceDelegator.updateEducation(orcid, putCode, education);
        return response;
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewEmployment(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response createEmployment(String orcid, Employment employment) {
        Response response = memberV2ApiServiceDelegator.createEmployment(orcid, employment);
        return response;
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Employment employment) {
        Response response = memberV2ApiServiceDelegator.updateEmployment(orcid, putCode, employment);
        return response;
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteAffiliation(orcid, putCode);
        return response;
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewPeerReview(orcid, putCode);
        return response;
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode);
        return response;
    }

    @Override
    public Response createPeerReview(String orcid, PeerReview peerReview) {
        Response response = memberV2ApiServiceDelegator.createPeerReview(orcid, peerReview);
        return response;
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview) {
        Response response = memberV2ApiServiceDelegator.updatePeerReview(orcid, putCode, peerReview);
        return response;
    }

    @Override
    public Response deletePeerReview(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deletePeerReview(orcid, putCode);
        return response;
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewGroupIdRecord(putCode);
        return response;
    }

    @Override
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        Response response = memberV2ApiServiceDelegator.createGroupIdRecord(groupIdRecord);
        return response;
    }

    @Override
    public Response updateGroupIdRecord(GroupIdRecord groupIdRecord, Long putCode) {
        Response response = memberV2ApiServiceDelegator.updateGroupIdRecord(groupIdRecord, putCode);
        return response;
    }

    @Override
    public Response deleteGroupIdRecord(Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteGroupIdRecord(putCode);
        return response;
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        Response response = memberV2ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum);
        return response;
    }

    private Response downgradeAndValidateResponse(Response response) {
        checkProfileStatus(response);
        Response downgradedResponse = downgradeResponse(response);
        return downgradedResponse;
    }

    private Response downgradeResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null) {
            orcidMessageVersionConverterChain.downgradeMessage(orcidMessage, externalVersion);
        }
        return Response.fromResponse(response).entity(orcidMessage).build();
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
        Response response = memberV2ApiServiceDelegator.viewResearcherUrls(orcid);
        return response;
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode);
        return response;
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, ResearcherUrl researcherUrl) {
        Response response = memberV2ApiServiceDelegator.updateResearcherUrl(orcid, putCode, researcherUrl);
        return response;
    }

    @Override
    public Response createResearcherUrl(String orcid, ResearcherUrl researcherUrl) {
        Response response = memberV2ApiServiceDelegator.createResearcherUrl(orcid, researcherUrl);
        return response;
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteResearcherUrl(orcid, putCode);
        return response;
    }

    @Override
    public Response viewEmails(String orcid) {
        Response response = memberV2ApiServiceDelegator.viewEmails(orcid);
        return response;
    }

    @Override
    public Response viewOtherNames(String orcid) {
        Response response = memberV2ApiServiceDelegator.viewOtherNames(orcid);
        return response;
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewOtherName(orcid, putCode);
        return response;
    }

    @Override
    public Response createOtherName(String orcid, OtherName otherName) {
        Response response = memberV2ApiServiceDelegator.createOtherName(orcid, otherName);
        return response;
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, OtherName otherName) {
        Response response = memberV2ApiServiceDelegator.updateOtherName(orcid, putCode, otherName);
        return response;
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteOtherName(orcid, putCode);
        return response;
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        Response response = memberV2ApiServiceDelegator.viewPersonalDetails(orcid);
        return response;
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        Response response = memberV2ApiServiceDelegator.viewExternalIdentifiers(orcid);
        return response;
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode);
        return response;
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, ExternalIdentifier externalIdentifier) {
        Response response = memberV2ApiServiceDelegator.updateExternalIdentifier(orcid, putCode, externalIdentifier);
        return response;
    }

    @Override
    public Response createExternalIdentifier(String orcid, ExternalIdentifier externalIdentifier) {
        Response response = memberV2ApiServiceDelegator.createExternalIdentifier(orcid, externalIdentifier);
        return response;
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        Response response = memberV2ApiServiceDelegator.deleteExternalIdentifier(orcid, putCode);
        return response;
    }

    public MemberV2ApiServiceDelegator getMemberV2ApiServiceDelegator() {
        return memberV2ApiServiceDelegator;
    }

    public void setMemberV2ApiServiceDelegator(MemberV2ApiServiceDelegator memberV2ApiServiceDelegator) {
        this.memberV2ApiServiceDelegator = memberV2ApiServiceDelegator;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }
}
