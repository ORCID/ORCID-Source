package org.orcid.api.publicV3.server.delegator.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.publicV3.server.delegator.PublicV3ApiServiceDelegator;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.version.V3Convertible;
import org.orcid.core.version.V3VersionConverterChain;

public class PublicV3ApiServiceVersionedDelegatorImpl implements PublicV3ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> {
    
    private PublicV3ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> publicV3ApiServiceDelegator;    
    
    private String externalVersion;

    @Resource
    private V3VersionConverterChain v3VersionConverterChain;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;    
    
    public void setPublicV3ApiServiceDelegator(
            PublicV3ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> publicV3ApiServiceDelegator) {
        this.publicV3ApiServiceDelegator = publicV3ApiServiceDelegator;
    }

    @Override
    public Response viewStatusText() {
        return publicV3ApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response viewActivities(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewActivities(orcid));
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewWork(orcid, putCode));
    }

    @Override
    public Response viewWorks(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewWorks(orcid));
    }
    
    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        // DO not downgrade non-orcid schema responses (this is citeproc);
        return publicV3ApiServiceDelegator.viewWorkCitation(orcid, putCode);
        // return
        // processReponse(publicV2ApiServiceDelegator.viewWorkCitation(orcid,
        // putCode), orcid);
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewWorkSummary(orcid, putCode));
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewFunding(orcid, putCode));
    }

    @Override
    public Response viewFundings(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewFundings(orcid));
    }
    
    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewFundingSummary(orcid, putCode));
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEducation(orcid, putCode));
    }

    @Override
    public Response viewEducations(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEducations(orcid));
    }
    
    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEducationSummary(orcid, putCode));
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEmployment(orcid, putCode));
    }

    @Override
    public Response viewEmployments(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEmployments(orcid));
    }
    
    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEmploymentSummary(orcid, putCode));
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewPeerReview(orcid, putCode));
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewPeerReviews(orcid));
    }
    
    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode));
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        return publicV3ApiServiceDelegator.viewGroupIdRecord(putCode);
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        return publicV3ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum);
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewResearcherUrls(orcid));
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewResearcherUrl(orcid, putCode));
    }

    @Override
    public Response viewEmails(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewEmails(orcid));
    }

    @Override
    public Response viewOtherNames(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewOtherNames(orcid));
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewOtherName(orcid, putCode));
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewPersonalDetails(orcid));
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewExternalIdentifiers(orcid));
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewExternalIdentifier(orcid, putCode));
    }

    @Override
    public Response viewBiography(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewBiography(orcid));
    }

    @Override
    public Response viewKeywords(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewKeywords(orcid));
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewKeyword(orcid, putCode));
    }

    @Override
    public Response viewAddresses(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewAddresses(orcid));
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewAddress(orcid, putCode));
    }

    @Override
    public Response viewPerson(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewPerson(orcid));
    }

    @Override
    public Response viewRecord(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewRecord(orcid));
    }
    
    @Override
    public Response searchByQuery(Map<String, List<String>> solrParams) {
        return processReponse(publicV3ApiServiceDelegator.searchByQuery(solrParams));
    }
    
    @Override
    public Response viewBulkWorks(String orcid, String putCodes) {
        return processReponse(publicV3ApiServiceDelegator.viewBulkWorks(orcid, putCodes));
    }
    
    @Override
    public Response viewStatus() {
        return publicV3ApiServiceDelegator.viewStatus();
    }

    @Override
    public Response viewDistinction(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewDistinction(orcid, putCode));
    }

    @Override
    public Response viewDistinctions(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewDistinctions(orcid));
    }

    @Override
    public Response viewDistinctionSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewDistinctionSummary(orcid, putCode));
    }

    @Override
    public Response viewInvitedPosition(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewInvitedPosition(orcid, putCode));
    }

    @Override
    public Response viewInvitedPositions(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewInvitedPositions(orcid));
    }

    @Override
    public Response viewInvitedPositionSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewInvitedPositionSummary(orcid, putCode));
    }

    @Override
    public Response viewMembership(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewMembership(orcid, putCode));
    }

    @Override
    public Response viewMemberships(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewMemberships(orcid));
    }

    @Override
    public Response viewMembershipSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewMembershipSummary(orcid, putCode));
    }

    @Override
    public Response viewQualification(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewQualification(orcid, putCode));
    }

    @Override
    public Response viewQualifications(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewQualifications(orcid));
    }

    @Override
    public Response viewQualificationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewQualificationSummary(orcid, putCode));
    }

    @Override
    public Response viewService(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewService(orcid, putCode));
    }

    @Override
    public Response viewServices(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewServices(orcid));
    }

    @Override
    public Response viewServiceSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewServiceSummary(orcid, putCode));
    }

    @Override
    public Response viewResearchResource(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewResearchResource(orcid, putCode));
    }

    @Override
    public Response viewResearchResources(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewResearchResources(orcid));
    }

    @Override
    public Response viewResearchResourceSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV3ApiServiceDelegator.viewResearchResourceSummary(orcid, putCode));
    }
    
    private Response processReponse(Response response) {
        if(externalVersion.equals("3.0_rc2")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }
    
    private Response upgradeResponse(Response response) {
        Object entity = response.getEntity();
        V3Convertible result = null;
        if (entity != null) {
            result = v3VersionConverterChain.upgrade(new V3Convertible(entity, PublicV3ApiServiceDelegator.LATEST_V3_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }
    
    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V3Convertible result = null;
        if (entity != null) {
            result = v3VersionConverterChain.downgrade(new V3Convertible(entity, PublicV3ApiServiceDelegator.LATEST_V3_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }

    private void checkProfileStatus(String orcid) {
        try {
            orcidSecurityManager.checkProfile(orcid);
        } catch(DeactivatedException e) {
            // Ignore the DeactivatedException since we should be able to return the empty element
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMemberV3ApiServiceDelegator(PublicV3ApiServiceDelegator publicV3ApiServiceDelegator) {
        this.publicV3ApiServiceDelegator = publicV3ApiServiceDelegator;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    @Override
    public Response viewClient(String clientId) {
        return publicV3ApiServiceDelegator.viewClient(clientId);
    }

}
