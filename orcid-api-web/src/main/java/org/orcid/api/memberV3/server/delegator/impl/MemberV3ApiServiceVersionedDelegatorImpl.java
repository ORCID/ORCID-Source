package org.orcid.api.memberV3.server.delegator.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.orcid.api.common.jaxb.OrcidValidationJaxbContextResolver;
import org.orcid.api.memberV3.server.delegator.MemberV3ApiServiceDelegator;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.version.V3Convertible;
import org.orcid.core.version.V3VersionConverterChain;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record.bulk.BulkElementContainer;

public class MemberV3ApiServiceVersionedDelegatorImpl implements
        MemberV3ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> {

    @Resource
    private MemberV3ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> memberV3ApiServiceDelegator;

    private String externalVersion;

    @Resource
    private V3VersionConverterChain v3VersionConverterChain;

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
        return memberV3ApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response viewRecord(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewRecord(orcid));
    }

    @Override
    public Response viewActivities(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewActivities(orcid));
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewWork(orcid, putCode));
    }

    @Override
    public Response viewWorks(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewWorks(orcid));
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewWorkSummary(orcid, putCode));
    }

    @Override
    public Response createWork(String orcid, Object work) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(work);
        work = processObject(work);
        return memberV3ApiServiceDelegator.createWork(orcid, work);
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
                    if (org.orcid.jaxb.model.v3.rc1.record.Work.class.isAssignableFrom(bulkElement.getClass())) {
                        org.orcid.jaxb.model.v3.rc1.error.OrcidError error = orcidCoreExceptionMapper.getOrcidErrorV3Rc1(9001, 400, e);
                        workBulk.getBulk().remove(i);
                        errors.put(i, error);
                        workBulk.getBulk().add(i, error);
                    } else {
                        org.orcid.jaxb.model.v3.rc2.error.OrcidError error = orcidCoreExceptionMapper.getOrcidErrorV3Rc2(9001, 400, e);
                        workBulk.getBulk().remove(i);
                        errors.put(i, error);
                        workBulk.getBulk().add(i, error);
                    }
                }
            }
        }

        works = processObject(works);
        return memberV3ApiServiceDelegator.createWorks(orcid, works);
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Object work) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(work);
        work = processObject(work);
        return processReponse(memberV3ApiServiceDelegator.updateWork(orcid, putCode, work));
    }

    @Override
    public Response deleteWork(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteWork(orcid, putCode);
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewFunding(orcid, putCode));
    }

    @Override
    public Response viewFundings(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewFundings(orcid));
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewFundingSummary(orcid, putCode));
    }

    @Override
    public Response createFunding(String orcid, Object funding) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(funding);
        funding = processObject(funding);
        return memberV3ApiServiceDelegator.createFunding(orcid, funding);
    }

    @Override
    public Response updateFunding(String orcid, Long putCode, Object funding) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(funding);
        funding = processObject(funding);
        return processReponse(memberV3ApiServiceDelegator.updateFunding(orcid, putCode, funding));
    }

    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteFunding(orcid, putCode);
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEducation(orcid, putCode));
    }

    @Override
    public Response viewEducations(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEducations(orcid));
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEducationSummary(orcid, putCode));
    }

    @Override
    public Response createEducation(String orcid, Object education) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(education);
        education = processObject(education);
        return memberV3ApiServiceDelegator.createEducation(orcid, education);
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Object education) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(education);
        education = processObject(education);
        return processReponse(memberV3ApiServiceDelegator.updateEducation(orcid, putCode, education));
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEmployment(orcid, putCode));
    }

    @Override
    public Response viewEmployments(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEmployments(orcid));
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEmploymentSummary(orcid, putCode));
    }

    @Override
    public Response createEmployment(String orcid, Object employment) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(employment);
        employment = processObject(employment);
        return memberV3ApiServiceDelegator.createEmployment(orcid, employment);
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Object employment) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(employment);
        employment = processObject(employment);
        return processReponse(memberV3ApiServiceDelegator.updateEmployment(orcid, putCode, employment));
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteAffiliation(orcid, putCode);
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewPeerReview(orcid, putCode));
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewPeerReviews(orcid));
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode));
    }

    @Override
    public Response createPeerReview(String orcid, Object peerReview) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(peerReview);
        peerReview = processObject(peerReview);
        return memberV3ApiServiceDelegator.createPeerReview(orcid, peerReview);
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, Object peerReview) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(peerReview);
        peerReview = processObject(peerReview);
        return processReponse(memberV3ApiServiceDelegator.updatePeerReview(orcid, putCode, peerReview));
    }

    @Override
    public Response deletePeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deletePeerReview(orcid, putCode);
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        return processReponse(memberV3ApiServiceDelegator.viewGroupIdRecord(putCode));
    }

    @Override
    public Response createGroupIdRecord(Object groupIdRecord) {
        groupIdRecord = processObject(groupIdRecord);
        return memberV3ApiServiceDelegator.createGroupIdRecord(groupIdRecord);
    }

    @Override
    public Response updateGroupIdRecord(Object groupIdRecord, Long putCode) {
        groupIdRecord = processObject(groupIdRecord);
        return processReponse(memberV3ApiServiceDelegator.updateGroupIdRecord(groupIdRecord, putCode));
    }

    @Override
    public Response deleteGroupIdRecord(Long putCode) {
        return memberV3ApiServiceDelegator.deleteGroupIdRecord(putCode);
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        return processReponse(memberV3ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum));
    }

    @Override
    public Response findGroupIdRecordByName(String name) {
        return processReponse(memberV3ApiServiceDelegator.findGroupIdRecordByName(name));
    }

    @Override
    public Response findGroupIdRecordByGroupId(String groupId) {
        return processReponse(memberV3ApiServiceDelegator.findGroupIdRecordByGroupId(groupId));
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewResearcherUrls(orcid));
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewResearcherUrl(orcid, putCode));
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, Object researcherUrl) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(researcherUrl);
        researcherUrl = processObject(researcherUrl);
        return processReponse(memberV3ApiServiceDelegator.updateResearcherUrl(orcid, putCode, researcherUrl));
    }

    @Override
    public Response createResearcherUrl(String orcid, Object researcherUrl) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(researcherUrl);
        researcherUrl = processObject(researcherUrl);
        return memberV3ApiServiceDelegator.createResearcherUrl(orcid, researcherUrl);
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteResearcherUrl(orcid, putCode);
    }

    @Override
    public Response viewEmails(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewEmails(orcid));
    }

    @Override
    public Response viewOtherNames(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewOtherNames(orcid));
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewOtherName(orcid, putCode));
    }

    @Override
    public Response createOtherName(String orcid, Object otherName) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(otherName);
        otherName = processObject(otherName);
        return memberV3ApiServiceDelegator.createOtherName(orcid, otherName);
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, Object otherName) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(otherName);
        otherName = processObject(otherName);
        return processReponse(memberV3ApiServiceDelegator.updateOtherName(orcid, putCode, otherName));
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteOtherName(orcid, putCode);
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewPersonalDetails(orcid));
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewExternalIdentifiers(orcid));
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewExternalIdentifier(orcid, putCode));
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, Object externalIdentifier) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(externalIdentifier);
        externalIdentifier = processObject(externalIdentifier);
        return processReponse(memberV3ApiServiceDelegator.updateExternalIdentifier(orcid, putCode, externalIdentifier));
    }

    @Override
    public Response createExternalIdentifier(String orcid, Object externalIdentifier) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(externalIdentifier);
        externalIdentifier = processObject(externalIdentifier);
        return memberV3ApiServiceDelegator.createExternalIdentifier(orcid, externalIdentifier);
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteExternalIdentifier(orcid, putCode);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMemberV3ApiServiceDelegator(MemberV3ApiServiceDelegator memberV3ApiServiceDelegator) {
        this.memberV3ApiServiceDelegator = memberV3ApiServiceDelegator;
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
        return processReponse(memberV3ApiServiceDelegator.viewBiography(orcid));
    }

    @Override
    public Response viewKeywords(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewKeywords(orcid));
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewKeyword(orcid, putCode));
    }

    @Override
    public Response createKeyword(String orcid, Object keyword) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(keyword);
        keyword = processObject(keyword);
        return memberV3ApiServiceDelegator.createKeyword(orcid, keyword);
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Object keyword) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(keyword);
        keyword = processObject(keyword);
        return processReponse(memberV3ApiServiceDelegator.updateKeyword(orcid, putCode, keyword));
    }

    @Override
    public Response deleteKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteKeyword(orcid, putCode);
    }

    @Override
    public Response viewAddresses(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewAddresses(orcid));
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewAddress(orcid, putCode));
    }

    @Override
    public Response createAddress(String orcid, Object address) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(address);
        address = processObject(address);
        return memberV3ApiServiceDelegator.createAddress(orcid, address);
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Object address) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(address);
        address = processObject(address);
        return processReponse(memberV3ApiServiceDelegator.updateAddress(orcid, putCode, address));
    }

    @Override
    public Response deleteAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteAddress(orcid, putCode);
    }

    @Override
    public Response viewPerson(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewPerson(orcid));
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> solrParams) {
        return processReponse(memberV3ApiServiceDelegator.searchByQuery(solrParams));
    }

    @Override
    public Response viewBulkWorks(String orcid, String putCodes) {
        return processReponse(memberV3ApiServiceDelegator.viewBulkWorks(orcid, putCodes));
    }

    public Response viewClient(String clientId) {
        return memberV3ApiServiceDelegator.viewClient(clientId);
    }

    @Override
    public Response viewStatus() {
        return memberV3ApiServiceDelegator.viewStatus();
    }

    @Override
    public Response viewDistinction(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewDistinction(orcid, putCode));
    }

    @Override
    public Response viewDistinctions(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewDistinctions(orcid));
    }

    @Override
    public Response viewDistinctionSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewDistinctionSummary(orcid, putCode));
    }

    @Override
    public Response createDistinction(String orcid, Object distinction) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(distinction);
        distinction = processObject(distinction);
        return processReponse(memberV3ApiServiceDelegator.createDistinction(orcid, distinction));
    }

    @Override
    public Response updateDistinction(String orcid, Long putCode, Object distinction) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(distinction);
        distinction = processObject(distinction);
        return processReponse(memberV3ApiServiceDelegator.updateDistinction(orcid, putCode, distinction));
    }

    @Override
    public Response viewInvitedPosition(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewInvitedPosition(orcid, putCode));
    }

    @Override
    public Response viewInvitedPositions(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewInvitedPositions(orcid));
    }

    @Override
    public Response viewInvitedPositionSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewInvitedPositionSummary(orcid, putCode));
    }

    @Override
    public Response createInvitedPosition(String orcid, Object invitedPosition) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(invitedPosition);
        invitedPosition = processObject(invitedPosition);
        return processReponse(memberV3ApiServiceDelegator.createInvitedPosition(orcid, invitedPosition));
    }

    @Override
    public Response updateInvitedPosition(String orcid, Long putCode, Object invitedPosition) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(invitedPosition);
        invitedPosition = processObject(invitedPosition);
        return processReponse(memberV3ApiServiceDelegator.updateInvitedPosition(orcid, putCode, invitedPosition));
    }

    @Override
    public Response viewMembership(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewMembership(orcid, putCode));
    }

    @Override
    public Response viewMemberships(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewMemberships(orcid));
    }

    @Override
    public Response viewMembershipSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewMembershipSummary(orcid, putCode));
    }

    @Override
    public Response createMembership(String orcid, Object membership) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(membership);
        membership = processObject(membership);
        return processReponse(memberV3ApiServiceDelegator.createMembership(orcid, membership));
    }

    @Override
    public Response updateMembership(String orcid, Long putCode, Object membership) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(membership);
        membership = processObject(membership);
        return processReponse(memberV3ApiServiceDelegator.updateMembership(orcid, putCode, membership));
    }

    @Override
    public Response viewQualification(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewQualification(orcid, putCode));
    }

    @Override
    public Response viewQualifications(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewQualifications(orcid));
    }

    @Override
    public Response viewQualificationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewQualificationSummary(orcid, putCode));
    }

    @Override
    public Response createQualification(String orcid, Object qualification) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(qualification);
        qualification = processObject(qualification);
        return processReponse(memberV3ApiServiceDelegator.createQualification(orcid, qualification));
    }

    @Override
    public Response updateQualification(String orcid, Long putCode, Object qualification) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(qualification);
        qualification = processObject(qualification);
        return processReponse(memberV3ApiServiceDelegator.updateQualification(orcid, putCode, qualification));
    }

    @Override
    public Response viewService(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewService(orcid, putCode));
    }

    @Override
    public Response viewServices(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewServices(orcid));
    }

    @Override
    public Response viewServiceSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewServiceSummary(orcid, putCode));
    }

    @Override
    public Response createService(String orcid, Object service) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(service);
        service = processObject(service);
        return processReponse(memberV3ApiServiceDelegator.createService(orcid, service));
    }

    @Override
    public Response updateService(String orcid, Long putCode, Object service) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(service);
        service = processObject(service);
        return processReponse(memberV3ApiServiceDelegator.updateService(orcid, putCode, service));
    }

    @Override
    public Response viewResearchResource(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewResearchResource(orcid, putCode));
    }

    @Override
    public Response viewResearchResources(String orcid) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewResearchResources(orcid));
    }

    @Override
    public Response viewResearchResourceSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid, true);
        return processReponse(memberV3ApiServiceDelegator.viewResearchResourceSummary(orcid, putCode));
    }

    @Override
    public Response createResearchResource(String orcid, Object researchResource) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(researchResource);
        researchResource = processObject(researchResource);
        return processReponse(memberV3ApiServiceDelegator.createResearchResource(orcid, researchResource));
    }

    @Override
    public Response updateResearchResource(String orcid, Long putCode, Object researchResource) {
        checkProfileStatus(orcid, false);
        schemaValidator.validate(researchResource);
        researchResource = processObject(researchResource);
        return processReponse(memberV3ApiServiceDelegator.updateResearchResource(orcid, putCode, researchResource));
    }

    @Override
    public Response deleteResearchResource(String orcid, Long putCode) {
        checkProfileStatus(orcid, false);
        return memberV3ApiServiceDelegator.deleteResearchResource(orcid, putCode);
    }

    private Response processReponse(Response response) {
        return downgradeResponse(response);
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V3Convertible result = null;
        if (entity != null) {
            result = v3VersionConverterChain.downgrade(new V3Convertible(entity, MemberV3ApiServiceDelegator.LATEST_V3_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }

    private Object processObject(Object object) {
        return upgradeObject(object);
    }

    private Object upgradeObject(Object entity) {
        V3Convertible result = null;
        if (entity != null) {
            result = v3VersionConverterChain.upgrade(new V3Convertible(entity, externalVersion), MemberV3ApiServiceDelegator.LATEST_V3_VERSION);
        }
        return result.getObjectToConvert();
    }

    private void checkProfileStatus(String orcid, boolean readOperation) {
        try {
            orcidSecurityManager.checkProfile(orcid);
        } catch (DeactivatedException e) {
            // If it is a read operation, ignore the deactivated status since we
            // are going to return the empty element with the deactivation date
            if (!readOperation) {
                throw e;
            }
        }
    }

}
