package org.orcid.api.memberV3.server.delegator;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

public interface MemberV3ApiServiceDelegator<DISTINCTION, EDUCATION, EMPLOYMENT, EXTERNAL_IDENTIFIER, INVITED_POSITION, FUNDING, GROUP_ID_RECORD, MEMBERSHIP, OTHER_NAME, PEER_REVIEW, QUALIFICATION, RESEARCHER_URL, SERVICE, WORK, WORK_BULK, ADDRESS, KEYWORD> {

    static final String LATEST_V3_VERSION = "3.0_dev1";

    Response viewStatusText();

    Response viewRecord(String orcid);

    Response viewActivities(String orcid);

    Response viewWork(String orcid, Long putCode);

    Response viewWorks(String orcid);

    Response viewWorkSummary(String orcid, Long putCode);

    Response createWorks(String orcid, WORK_BULK bulk);

    Response createWork(String orcid, WORK work);

    Response updateWork(String orcid, Long putCode, WORK work);

    Response deleteWork(String orcid, Long putCode);

    Response viewFunding(String orcid, Long putCode);

    Response viewFundings(String orcid);

    Response viewFundingSummary(String orcid, Long putCode);

    Response createFunding(String orcid, FUNDING funding);

    Response updateFunding(String orcid, Long putCode, FUNDING funding);

    Response deleteFunding(String orcid, Long putCode);

    Response viewEducation(String orcid, Long putCode);

    Response viewEducations(String orcid);

    Response viewEducationSummary(String orcid, Long putCode);

    Response createEducation(String orcid, EDUCATION education);

    Response updateEducation(String orcid, Long putCode, EDUCATION education);

    Response viewEmployment(String orcid, Long putCode);

    Response viewEmployments(String orcid);

    Response viewEmploymentSummary(String orcid, Long putCode);

    Response createEmployment(String orcid, EMPLOYMENT employment);

    Response updateEmployment(String orcid, Long putCode, EMPLOYMENT employment);

    Response deleteAffiliation(String orcid, Long putCode);

    Response viewPeerReview(String orcid, Long putCode);

    Response viewPeerReviews(String orcid);

    Response viewPeerReviewSummary(String orcid, Long putCode);

    Response createPeerReview(String orcid, PEER_REVIEW peerReview);

    Response updatePeerReview(String orcid, Long putCode, PEER_REVIEW peerReview);

    Response deletePeerReview(String orcid, Long putCode);

    Response viewGroupIdRecord(Long putCode);

    Response createGroupIdRecord(GROUP_ID_RECORD groupIdRecord);

    Response updateGroupIdRecord(GROUP_ID_RECORD groupIdRecord, Long putCode);

    Response deleteGroupIdRecord(Long putCode);

    Response viewGroupIdRecords(String pageSize, String pageNum);

    Response viewResearcherUrls(String orcid);

    Response viewResearcherUrl(String orcid, Long putCode);

    Response updateResearcherUrl(String orcid, Long putCode, RESEARCHER_URL researcherUrl);

    Response createResearcherUrl(String orcid, RESEARCHER_URL researcherUrl);

    Response deleteResearcherUrl(String orcid, Long putCode);

    Response viewEmails(String orcid);

    Response viewOtherNames(String orcid);

    Response viewOtherName(String orcid, Long putCode);

    Response createOtherName(String orcid, OTHER_NAME otherName);

    Response updateOtherName(String orcid, Long putCode, OTHER_NAME otherName);

    Response deleteOtherName(String orcid, Long putCode);

    Response viewPersonalDetails(String orcid);

    Response viewExternalIdentifiers(String orcid);

    Response viewExternalIdentifier(String orcid, Long putCode);

    Response updateExternalIdentifier(String orcid, Long putCode, EXTERNAL_IDENTIFIER externalIdentifier);

    Response createExternalIdentifier(String orcid, EXTERNAL_IDENTIFIER externalIdentifier);

    Response deleteExternalIdentifier(String orcid, Long putCode);

    Response viewBiography(String orcid);

    Response viewKeywords(String orcid);

    Response viewKeyword(String orcid, Long putCode);

    Response createKeyword(String orcid, KEYWORD keyword);

    Response updateKeyword(String orcid, Long putCode, KEYWORD keyword);

    Response deleteKeyword(String orcid, Long putCode);

    Response viewAddresses(String orcid);

    Response viewAddress(String orcid, Long putCode);

    Response createAddress(String orcid, ADDRESS address);

    Response updateAddress(String orcid, Long putCode, ADDRESS address);

    Response deleteAddress(String orcid, Long putCode);

    Response viewPerson(String orcid);

    Response searchByQuery(Map<String, List<String>> solrParams);

    Response viewClient(String clientId);

    Response findGroupIdRecordByName(String name);

    Response viewBulkWorks(String orcid, String putCodes);

    Response viewDistinction(String orcid, Long putCode);

    Response viewDistinctions(String orcid);

    Response viewDistinctionSummary(String orcid, Long putCode);

    Response createDistinction(String orcid, DISTINCTION distinction);

    Response updateDistinction(String orcid, Long putCode, DISTINCTION distinction);

    Response viewInvitedPosition(String orcid, Long putCode);

    Response viewInvitedPositions(String orcid);

    Response viewInvitedPositionSummary(String orcid, Long putCode);

    Response createInvitedPosition(String orcid, INVITED_POSITION invitedPosition);

    Response updateInvitedPosition(String orcid, Long putCode, INVITED_POSITION invitedPosition);

    Response viewMembership(String orcid, Long putCode);

    Response viewMemberships(String orcid);

    Response viewMembershipSummary(String orcid, Long putCode);

    Response createMembership(String orcid, MEMBERSHIP membership);

    Response updateMembership(String orcid, Long putCode, MEMBERSHIP membership);

    Response viewQualification(String orcid, Long putCode);

    Response viewQualifications(String orcid);

    Response viewQualificationSummary(String orcid, Long putCode);

    Response createQualification(String orcid, QUALIFICATION qualification);

    Response updateQualification(String orcid, Long putCode, QUALIFICATION qualification);

    Response viewService(String orcid, Long putCode);

    Response viewServices(String orcid);

    Response viewServiceSummary(String orcid, Long putCode);

    Response createService(String orcid, SERVICE service);

    Response updateService(String orcid, Long putCode, SERVICE service);
}
