package org.orcid.api.publicV2.server.delegator.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;

public class PublicV2ApiServiceVersionedDelegatorImpl implements PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> {

    @Resource
    private PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> publicV2ApiServiceDelegator;    
    
    private String externalVersion;

    @Resource
    private V2VersionConverterChain v2VersionConverterChain;

    @Resource
    private V2VersionConverterChain v2_1VersionConverterChain;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;    
    
    @Override
    public Response viewStatusText() {
        return publicV2ApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response viewActivities(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewActivities(orcid));
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewWork(orcid, putCode));
    }

    @Override
    public Response viewWorks(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewWorks(orcid));
    }
    
    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        // DO not downgrade non-orcid schema responses (this is citeproc);
        return publicV2ApiServiceDelegator.viewWorkCitation(orcid, putCode);
        // return
        // processReponse(publicV2ApiServiceDelegator.viewWorkCitation(orcid,
        // putCode), orcid);
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewWorkSummary(orcid, putCode));
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewFunding(orcid, putCode));
    }

    @Override
    public Response viewFundings(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewFundings(orcid));
    }
    
    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewFundingSummary(orcid, putCode));
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEducation(orcid, putCode));
    }

    @Override
    public Response viewEducations(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEducations(orcid));
    }
    
    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEducationSummary(orcid, putCode));
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEmployment(orcid, putCode));
    }

    @Override
    public Response viewEmployments(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEmployments(orcid));
    }
    
    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode));
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewPeerReview(orcid, putCode));
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewPeerReviews(orcid));
    }
    
    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode));
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
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewResearcherUrls(orcid));
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode));
    }

    @Override
    public Response viewEmails(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewEmails(orcid));
    }

    @Override
    public Response viewOtherNames(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewOtherNames(orcid));
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewOtherName(orcid, putCode));
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewPersonalDetails(orcid));
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewExternalIdentifiers(orcid));
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode));
    }

    @Override
    public Response viewBiography(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewBiography(orcid));
    }

    @Override
    public Response viewKeywords(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewKeywords(orcid));
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewKeyword(orcid, putCode));
    }

    @Override
    public Response viewAddresses(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewAddresses(orcid));
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewAddress(orcid, putCode));
    }

    @Override
    public Response viewPerson(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewPerson(orcid));
    }

    @Override
    public Response viewRecord(String orcid) {
        checkProfileStatus(orcid);
        return processReponse(publicV2ApiServiceDelegator.viewRecord(orcid));
    }
    
    @Override
    public Response searchByQuery(Map<String, List<String>> solrParams) {
        return processReponse(publicV2ApiServiceDelegator.searchByQuery(solrParams));
    }
    
    @Override
    public Response viewBulkWorks(String orcid, String putCodes) {
        return processReponse(publicV2ApiServiceDelegator.viewBulkWorks(orcid, putCodes));
    }
    
    private Response processReponse(Response response) {
        if(externalVersion.equals("2.1")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }
    
    private Response upgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2_1VersionConverterChain.upgrade(new V2Convertible(entity, PublicV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }
    
    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V2Convertible(entity, PublicV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
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
    public Response viewClient(String clientId) {
        return publicV2ApiServiceDelegator.viewClient(clientId);
    }

}
