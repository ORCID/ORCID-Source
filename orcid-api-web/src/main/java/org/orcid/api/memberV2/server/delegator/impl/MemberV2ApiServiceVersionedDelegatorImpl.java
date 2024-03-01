package org.orcid.api.memberV2.server.delegator.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.orcid.api.common.jaxb.OrcidValidationJaxbContextResolver;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record.bulk.BulkElementContainer;

public class MemberV2ApiServiceVersionedDelegatorImpl
        implements MemberV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> {

    @Resource
    private MemberV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> memberV2ApiServiceDelegator;

    private String externalVersion;

    @Resource
    private V2VersionConverterChain v2VersionConverterChain;

    @Resource
    private V2VersionConverterChain v2_1VersionConverterChain;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    private OrcidValidationJaxbContextResolver schemaValidator = new OrcidValidationJaxbContextResolver();

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Override
    public Response viewStatusText() {
        return memberV2ApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response viewRecord(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewRecord(orcid));
    }

    @Override
    public Response viewActivities(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewActivities(orcid));
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewWork(orcid, putCode));
    }

    @Override
    public Response viewWorks(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewWorks(orcid));
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewWorkSummary(orcid, putCode));
    }

    @Override
    public Response createWork(String orcid, Object work) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(work);
        work = processObject(work);
        return memberV2ApiServiceDelegator.createWork(orcid, work);
    }

    @Override
    public Response createWorks(String orcid, Object works) {
        checkProfileStatus(orcid, false);

        // validate works object before changing version
        Map<Integer, BulkElement> errors = new HashMap<>();

        if (works != null) {
            BulkElementContainer workBulk = (BulkElementContainer) works;
            for (int i = workBulk.getBulk().size() - 1; i >= 0; i--) {
                BulkElement bulkElement = workBulk.getBulk().get(i);
                try {
                    schemaValidator.validate(bulkElement);
                } catch (WebApplicationException e) {

                    org.orcid.jaxb.model.error_v2.OrcidError error = orcidCoreExceptionMapper.getOrcidErrorV2(9001, 400, e);
                    workBulk.getBulk().remove(i);
                    errors.put(i, error);
                    workBulk.getBulk().add(i, error);

                }
            }
        }

        works = processObject(works);
        return memberV2ApiServiceDelegator.createWorks(orcid, works);
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Object work) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(work);
        work = processObject(work);
        return processReponse(memberV2ApiServiceDelegator.updateWork(orcid, putCode, work));
    }

    @Override
    public Response deleteWork(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteWork(orcid, putCode);
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewFunding(orcid, putCode));
    }

    @Override
    public Response viewFundings(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewFundings(orcid));
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewFundingSummary(orcid, putCode));
    }

    @Override
    public Response createFunding(String orcid, Object funding) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(funding);
        funding = processObject(funding);
        return memberV2ApiServiceDelegator.createFunding(orcid, funding);
    }

    @Override
    public Response updateFunding(String orcid, Long putCode, Object funding) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(funding);
        funding = processObject(funding);
        return processReponse(memberV2ApiServiceDelegator.updateFunding(orcid, putCode, funding));
    }

    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteFunding(orcid, putCode);
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEducation(orcid, putCode));
    }

    @Override
    public Response viewEducations(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEducations(orcid));
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEducationSummary(orcid, putCode));
    }

    @Override
    public Response createEducation(String orcid, Object education) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(education);
        education = processObject(education);
        return memberV2ApiServiceDelegator.createEducation(orcid, education);
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Object education) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(education);
        education = processObject(education);
        return processReponse(memberV2ApiServiceDelegator.updateEducation(orcid, putCode, education));
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEmployment(orcid, putCode));
    }

    @Override
    public Response viewEmployments(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEmployments(orcid));
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode));
    }

    @Override
    public Response createEmployment(String orcid, Object employment) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(employment);
        employment = processObject(employment);
        return memberV2ApiServiceDelegator.createEmployment(orcid, employment);
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Object employment) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(employment);
        employment = processObject(employment);
        return processReponse(memberV2ApiServiceDelegator.updateEmployment(orcid, putCode, employment));
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteAffiliation(orcid, putCode);
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewPeerReview(orcid, putCode));
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewPeerReviews(orcid));
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode));
    }

    @Override
    public Response createPeerReview(String orcid, Object peerReview) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(peerReview);
        peerReview = processObject(peerReview);
        return memberV2ApiServiceDelegator.createPeerReview(orcid, peerReview);
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, Object peerReview) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(peerReview);
        peerReview = processObject(peerReview);
        return processReponse(memberV2ApiServiceDelegator.updatePeerReview(orcid, putCode, peerReview));
    }

    @Override
    public Response deletePeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deletePeerReview(orcid, putCode);
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        return processReponse(memberV2ApiServiceDelegator.viewGroupIdRecord(putCode));
    }

    @Override
    public Response createGroupIdRecord(Object groupIdRecord) {
        groupIdRecord = processObject(groupIdRecord);
        return memberV2ApiServiceDelegator.createGroupIdRecord(groupIdRecord);
    }

    @Override
    public Response updateGroupIdRecord(Object groupIdRecord, Long putCode) {
        groupIdRecord = processObject(groupIdRecord);
        return processReponse(memberV2ApiServiceDelegator.updateGroupIdRecord(groupIdRecord, putCode));
    }

    @Override
    public Response deleteGroupIdRecord(Long putCode) {
        return memberV2ApiServiceDelegator.deleteGroupIdRecord(putCode);
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        return processReponse(memberV2ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum));
    }

    @Override
    public Response findGroupIdRecordByName(String name) {
        return processReponse(memberV2ApiServiceDelegator.findGroupIdRecordByName(name));
    }

    @Override
    public Response findGroupIdRecordByGroupId(String groupId) {
        return processReponse(memberV2ApiServiceDelegator.findGroupIdRecordByGroupId(groupId));
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewResearcherUrls(orcid));
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode));
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, Object researcherUrl) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(researcherUrl);
        researcherUrl = processObject(researcherUrl);
        return processReponse(memberV2ApiServiceDelegator.updateResearcherUrl(orcid, putCode, researcherUrl));
    }

    @Override
    public Response createResearcherUrl(String orcid, Object researcherUrl) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(researcherUrl);
        researcherUrl = processObject(researcherUrl);
        return memberV2ApiServiceDelegator.createResearcherUrl(orcid, researcherUrl);
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteResearcherUrl(orcid, putCode);
    }

    @Override
    public Response viewEmails(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewEmails(orcid));
    }

    @Override
    public Response viewOtherNames(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewOtherNames(orcid));
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewOtherName(orcid, putCode));
    }

    @Override
    public Response createOtherName(String orcid, Object otherName) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(otherName);
        otherName = processObject(otherName);
        return memberV2ApiServiceDelegator.createOtherName(orcid, otherName);
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, Object otherName) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(otherName);
        otherName = processObject(otherName);
        return processReponse(memberV2ApiServiceDelegator.updateOtherName(orcid, putCode, otherName));
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteOtherName(orcid, putCode);
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewPersonalDetails(orcid));
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewExternalIdentifiers(orcid));
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode));
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, Object externalIdentifier) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(externalIdentifier);
        externalIdentifier = processObject(externalIdentifier);
        return processReponse(memberV2ApiServiceDelegator.updateExternalIdentifier(orcid, putCode, externalIdentifier));
    }

    @Override
    public Response createExternalIdentifier(String orcid, Object externalIdentifier) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(externalIdentifier);
        externalIdentifier = processObject(externalIdentifier);
        return memberV2ApiServiceDelegator.createExternalIdentifier(orcid, externalIdentifier);
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
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
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewBiography(orcid));
    }

    @Override
    public Response viewKeywords(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewKeywords(orcid));
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewKeyword(orcid, putCode));
    }

    @Override
    public Response createKeyword(String orcid, Object keyword) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(keyword);
        keyword = processObject(keyword);
        return memberV2ApiServiceDelegator.createKeyword(orcid, keyword);
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Object keyword) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(keyword);
        keyword = processObject(keyword);
        return processReponse(memberV2ApiServiceDelegator.updateKeyword(orcid, putCode, keyword));
    }

    @Override
    public Response deleteKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteKeyword(orcid, putCode);
    }

    @Override
    public Response viewAddresses(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewAddresses(orcid));
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewAddress(orcid, putCode));
    }

    @Override
    public Response createAddress(String orcid, Object address) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(address);
        address = processObject(address);
        return memberV2ApiServiceDelegator.createAddress(orcid, address);
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Object address) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(address);
        address = processObject(address);
        return processReponse(memberV2ApiServiceDelegator.updateAddress(orcid, putCode, address));
    }

    @Override
    public Response deleteAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV2ApiServiceDelegator.deleteAddress(orcid, putCode);
    }

    @Override
    public Response viewPerson(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV2ApiServiceDelegator.viewPerson(orcid));
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> solrParams) {
        return processReponse(memberV2ApiServiceDelegator.searchByQuery(solrParams));
    }

    @Override
    public Response viewBulkWorks(String orcid, String putCodes) {
        return processReponse(memberV2ApiServiceDelegator.viewBulkWorks(orcid, putCodes));
    }

    public Response viewClient(String clientId) {
        return memberV2ApiServiceDelegator.viewClient(clientId);
    }

    private Response processReponse(Response response) {
        if (externalVersion.equals("2.1")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V2Convertible(entity, MemberV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }

    private Response upgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2_1VersionConverterChain.upgrade(new V2Convertible(entity, MemberV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }

    private Object processObject(Object object) {
        if (externalVersion.equals("2.1")) {
            return downgradeObject(object);
        } else {
            return upgradeObject(object);
        }
    }

    private Object upgradeObject(Object entity) {
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.upgrade(new V2Convertible(entity, externalVersion), MemberV2ApiServiceDelegator.LATEST_V2_VERSION);
        }
        return result.getObjectToConvert();
    }

    private Object downgradeObject(Object entity) {
        V2Convertible result = null;
        if (entity != null) {
            if (externalVersion.equals("2.1")) {
                result = v2_1VersionConverterChain.downgrade(new V2Convertible(entity, externalVersion), MemberV2ApiServiceDelegator.LATEST_V2_VERSION);
            } else {
                result = v2VersionConverterChain.downgrade(new V2Convertible(entity, externalVersion), MemberV2ApiServiceDelegator.LATEST_V2_VERSION);
            }
        }
        return result.getObjectToConvert();
    }

    private void checkProfileStatus(String orcid, boolean readOperation) {
        orcidSecurityManager.checkProfile(orcid);        
    }

}