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

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;

public class MemberV2ApiServiceVersionedDelegatorImpl implements
        MemberV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> {

    @Resource
    private MemberV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> memberV2ApiServiceDelegator;

    private String externalVersion;

    @Resource
    private V2VersionConverterChain v2VersionConverterChain;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Override
    public Response viewStatusText() {
        return memberV2ApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response viewRecord(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewRecord(orcid));
    }
    
    @Override
    public Response viewActivities(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewActivities(orcid));
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewWork(orcid, putCode));
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewWorkSummary(orcid, putCode));
    }

    @Override
    public Response createWork(String orcid, Object work) {
        checkProfileStatus(orcid);
        work = upgradeObject(work);
        return memberV2ApiServiceDelegator.createWork(orcid, work);
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Object work) {
        checkProfileStatus(orcid);
        work = upgradeObject(work);
        return downgradeResponse(memberV2ApiServiceDelegator.updateWork(orcid, putCode, work));
    }

    @Override
    public Response deleteWork(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteWork(orcid, putCode);
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewFunding(orcid, putCode));
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewFundingSummary(orcid, putCode));
    }

    @Override
    public Response createFunding(String orcid, Object funding) {
        checkProfileStatus(orcid);
        funding = upgradeObject(funding);
        return memberV2ApiServiceDelegator.createFunding(orcid, funding);
    }

    @Override
    public Response updateFunding(String orcid, Long putCode, Object funding) {
        checkProfileStatus(orcid);
        funding = upgradeObject(funding);
        return downgradeResponse(memberV2ApiServiceDelegator.updateFunding(orcid, putCode, funding));
    }

    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteFunding(orcid, putCode);
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewEducation(orcid, putCode));
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewEducationSummary(orcid, putCode));
    }

    @Override
    public Response createEducation(String orcid, Object education) {
        checkProfileStatus(orcid);
        education = upgradeObject(education);
        return memberV2ApiServiceDelegator.createEducation(orcid, education);
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Object education) {
        checkProfileStatus(orcid);
        education = upgradeObject(education);
        return downgradeResponse(memberV2ApiServiceDelegator.updateEducation(orcid, putCode, education));
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewEmployment(orcid, putCode));
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode));
    }

    @Override
    public Response createEmployment(String orcid, Object employment) {
        checkProfileStatus(orcid);
        employment = upgradeObject(employment);
        return memberV2ApiServiceDelegator.createEmployment(orcid, employment);
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Object employment) {
        checkProfileStatus(orcid);
        employment = upgradeObject(employment);
        return downgradeResponse(memberV2ApiServiceDelegator.updateEmployment(orcid, putCode, employment));
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteAffiliation(orcid, putCode);
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewPeerReview(orcid, putCode));
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode));
    }

    @Override
    public Response createPeerReview(String orcid, Object peerReview) {
        checkProfileStatus(orcid);
        peerReview = upgradeObject(peerReview);
        return memberV2ApiServiceDelegator.createPeerReview(orcid, peerReview);
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, Object peerReview) {
        checkProfileStatus(orcid);
        peerReview = upgradeObject(peerReview);
        return downgradeResponse(memberV2ApiServiceDelegator.updatePeerReview(orcid, putCode, peerReview));
    }

    @Override
    public Response deletePeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deletePeerReview(orcid, putCode);
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        return downgradeResponse(memberV2ApiServiceDelegator.viewGroupIdRecord(putCode));
    }

    @Override
    public Response createGroupIdRecord(Object groupIdRecord) {
        groupIdRecord = upgradeObject(groupIdRecord);
        return memberV2ApiServiceDelegator.createGroupIdRecord(groupIdRecord);
    }

    @Override
    public Response updateGroupIdRecord(Object groupIdRecord, Long putCode) {
        groupIdRecord = upgradeObject(groupIdRecord);
        return downgradeResponse(memberV2ApiServiceDelegator.updateGroupIdRecord(groupIdRecord, putCode));
    }

    @Override
    public Response deleteGroupIdRecord(Long putCode) {
        return memberV2ApiServiceDelegator.deleteGroupIdRecord(putCode);
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        return downgradeResponse(memberV2ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum));
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewResearcherUrls(orcid));
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode));
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, Object researcherUrl) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.updateResearcherUrl(orcid, putCode, researcherUrl));
    }

    @Override
    public Response createResearcherUrl(String orcid, Object researcherUrl) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.createResearcherUrl(orcid, researcherUrl);
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteResearcherUrl(orcid, putCode);
    }

    @Override
    public Response viewEmails(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewEmails(orcid));
    }

    @Override
    public Response viewOtherNames(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewOtherNames(orcid));
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewOtherName(orcid, putCode));
    }

    @Override
    public Response createOtherName(String orcid, Object otherName) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.createOtherName(orcid, otherName);
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, Object otherName) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.updateOtherName(orcid, putCode, otherName));
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteOtherName(orcid, putCode);
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewPersonalDetails(orcid));
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewExternalIdentifiers(orcid));
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode));
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, Object externalIdentifier) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.updateExternalIdentifier(orcid, putCode, externalIdentifier));
    }

    @Override
    public Response createExternalIdentifier(String orcid, Object externalIdentifier) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.createExternalIdentifier(orcid, externalIdentifier);
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteExternalIdentifier(orcid, putCode);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMemberV2ApiServiceDelegator(MemberV2ApiServiceDelegator memberV2ApiServiceDelegator) {
        this.memberV2ApiServiceDelegator = memberV2ApiServiceDelegator;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    @Override
    public Response viewBiography(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewBiography(orcid));
    }

    @Override
    public Response viewKeywords(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewKeywords(orcid));
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewKeyword(orcid, putCode));
    }

    @Override
    public Response createKeyword(String orcid, Object keyword) {
        checkProfileStatus(orcid);
        keyword = upgradeObject(keyword);
        return memberV2ApiServiceDelegator.createKeyword(orcid, keyword);
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Object keyword) {
        checkProfileStatus(orcid);
        keyword = upgradeObject(keyword);
        return downgradeResponse(memberV2ApiServiceDelegator.updateKeyword(orcid, putCode, keyword));
    }

    @Override
    public Response deleteKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteKeyword(orcid, putCode);
    }

    @Override
    public Response viewAddresses(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewAddresses(orcid));
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewAddress(orcid, putCode));
    }

    @Override
    public Response createAddress(String orcid, Object address) {
        checkProfileStatus(orcid);
        address = upgradeObject(address);
        return memberV2ApiServiceDelegator.createAddress(orcid, address);
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Object address) {
        checkProfileStatus(orcid);
        address = upgradeObject(address);
        return downgradeResponse(memberV2ApiServiceDelegator.updateAddress(orcid, putCode, address));
    }

    @Override
    public Response deleteAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return memberV2ApiServiceDelegator.deleteAddress(orcid, putCode);
    }

    @Override
    public Response viewPerson(String orcid) {
        checkProfileStatus(orcid);
        return downgradeResponse(memberV2ApiServiceDelegator.viewPerson(orcid));
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V2Convertible(entity, MemberV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
        }
        return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
    }

    private Object upgradeObject(Object entity) {
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.upgrade(new V2Convertible(entity, externalVersion), MemberV2ApiServiceDelegator.LATEST_V2_VERSION);
        }
        return result.getObjectToConvert();
    }

    private void checkProfileStatus(String orcid) {        
        orcidSecurityManager.checkProfile(orcid);
    }
}
