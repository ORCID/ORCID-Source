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

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.exception.MismatchedPutCodeException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p/>
 * The delegator for the tier 2 API.
 * <p/>
 * The T2 delegator is responsible for the validation, retrieving results and
 * passing of objects to be from the core
 * 
 * @author Declan Newman (declan) Date: 07/03/2012
 */
@Component("orcidT2ServiceDelegator")
public class MemberV2ApiServiceDelegatorImpl
        implements MemberV2ApiServiceDelegator<Education, Employment, ExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, Address, Keyword> {

    @Resource
    private WorkManager workManager;

    @Resource
    private ProfileFundingManager profileFundingManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private AffiliationsManager affiliationsManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private WebhookDao webhookDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "visibilityFilterV2")
    private VisibilityFilterV2 visibilityFilter;

    @Resource
    private GroupIdRecordManager groupIdRecordManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private EmailManager emailManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Resource
    private PersonalDetailsManager personalDetailsManager;
    
    @Resource
    private ProfileKeywordManager keywordsManager;
    
    @Resource
    private AddressManager addressManager;

    @Override
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
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewActivities(String orcid) {
        ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        if (profileDao.isProfileDeprecated(orcid)) {
            StringBuffer primary = new StringBuffer(baseUrl).append("/").append(entity.getPrimaryRecord().getId());
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, primary.toString());
            if (entity.getDeprecatedDate() != null) {
                XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(entity.getDeprecatedDate());
                params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
            }
            throw new OrcidDeprecatedException(params);
        }
        ActivitiesSummary as = visibilityFilter.filter(profileEntityManager.getActivitiesSummary(orcid));
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED)
    public Response viewWork(String orcid, Long putCode) {
        Work w = workManager.getWork(orcid, putCode);
        ActivityUtils.cleanEmptyFields(w);
        orcidSecurityManager.checkVisibility(w);
        ActivityUtils.setPathToActivity(w, orcid);
        return Response.ok(w).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED)
    public Response viewWorkSummary(String orcid, Long putCode) {
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode);
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkVisibility(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_CREATE)
    public Response createWork(String orcid, Work work) {
        Work w = workManager.createWork(orcid, work, true);
        try {
            return Response.created(new URI(String.valueOf(w.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creatework_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_UPDATE)
    public Response updateWork(String orcid, Long putCode, Work work) {
        if (!putCode.equals(work.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(work.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Work w = workManager.updateWork(orcid, work, true);
        return Response.ok(w).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_UPDATE)
    public Response deleteWork(String orcid, Long putCode) {
        workManager.checkSourceAndRemoveWork(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED)
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkVisibility(f);
        ActivityUtils.setPathToActivity(f, orcid);
        return Response.ok(f).build();
    }

    @Override    
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED)
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_CREATE)
    public Response createFunding(String orcid, Funding funding) {
        Funding f = profileFundingManager.createFunding(orcid, funding);
        try {
            return Response.created(new URI(String.valueOf(f.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createfunding_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_UPDATE)
    public Response updateFunding(String orcid, Long putCode, Funding funding) {
        if (!putCode.equals(funding.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(funding.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Funding f = profileFundingManager.updateFunding(orcid, funding);
        return Response.ok(f).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_UPDATE)
    public Response deleteFunding(String orcid, Long putCode) {
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED)
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED)
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_CREATE)
    public Response createEducation(String orcid, Education education) {
        Education e = affiliationsManager.createEducationAffiliation(orcid, education);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createeducation_response.exception"), ex);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_UPDATE)
    public Response updateEducation(String orcid, Long putCode, Education education) {
        if (!putCode.equals(education.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(education.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Education e = affiliationsManager.updateEducationAffiliation(orcid, education);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED)
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED)
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_CREATE)
    public Response createEmployment(String orcid, Employment employment) {
        Employment e = affiliationsManager.createEmploymentAffiliation(orcid, employment);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createemployment_response.exception"), ex);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_UPDATE)
    public Response updateEmployment(String orcid, Long putCode, Employment employment) {
        if (!putCode.equals(employment.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(employment.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Employment e = affiliationsManager.updateEmploymentAffiliation(orcid, employment);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_UPDATE)
    public Response deleteAffiliation(String orcid, Long putCode) {
        affiliationsManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }    

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED)
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManager.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkVisibility(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        return Response.ok(peerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED)
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
        return Response.ok(summary).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_CREATE)
    public Response createPeerReview(String orcid, PeerReview peerReview) {
        PeerReview newPeerReview = peerReviewManager.createPeerReview(orcid, peerReview, true);
        try {
            return Response.created(new URI(String.valueOf(newPeerReview.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createpeerreview_response.exception"), ex);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_UPDATE)
    public Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview) {
        if (!putCode.equals(peerReview.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(peerReview.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        PeerReview updatedPeerReview = peerReviewManager.updatePeerReview(orcid, peerReview, true);
        return Response.ok(updatedPeerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_UPDATE)
    public Response deletePeerReview(String orcid, Long putCode) {
        peerReviewManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_READ)
    public Response viewGroupIdRecord(Long putCode) {
        GroupIdRecord record = groupIdRecordManager.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_UPDATE)
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        GroupIdRecord newRecord = groupIdRecordManager.createGroupIdRecord(groupIdRecord);
        try {
            return Response.created(new URI(String.valueOf(newRecord.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creategroupidrecord_response.exception"), ex);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_UPDATE)
    public Response updateGroupIdRecord(GroupIdRecord groupIdRecord, Long putCode) {
        if (!putCode.equals(groupIdRecord.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(groupIdRecord.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        GroupIdRecord updatedRecord = groupIdRecordManager.updateGroupIdRecord(putCode, groupIdRecord);
        return Response.ok(updatedRecord).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_UPDATE)
    public Response deleteGroupIdRecord(Long putCode) {
        groupIdRecordManager.deleteGroupIdRecord(putCode);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_READ)
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        GroupIdRecords records = groupIdRecordManager.getGroupIdRecords(pageSize, pageNum);
        return Response.ok(records).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewResearcherUrls(String orcid) {
        ResearcherUrls researcherUrls = researcherUrlManager.getResearcherUrls(orcid);
        researcherUrls.setResearcherUrls((List<ResearcherUrl>) visibilityFilter.filter(researcherUrls.getResearcherUrls()));
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        return Response.ok(researcherUrls).build();
    }

    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrl(orcid, putCode);
        orcidSecurityManager.checkVisibility(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        return Response.ok(researcherUrl).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response updateResearcherUrl(String orcid, Long putCode, ResearcherUrl researcherUrl) {
        if (!putCode.equals(researcherUrl.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(researcherUrl.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        ResearcherUrl updatedResearcherUrl = researcherUrlManager.updateResearcherUrl(orcid, researcherUrl);
        ElementUtils.setPathToResearcherUrl(updatedResearcherUrl, orcid);
        return Response.ok(updatedResearcherUrl).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response createResearcherUrl(String orcid, ResearcherUrl researcherUrl) {
        researcherUrl = researcherUrlManager.createResearcherUrl(orcid, researcherUrl);
        try {
            return Response.created(new URI(String.valueOf(researcherUrl.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        researcherUrlManager.deleteResearcherUrl(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewEmails(String orcid) {
        Emails emails = emailManager.getEmails(orcid);
        emails.setEmails((List<Email>) visibilityFilter.filter(emails.getEmails()));
        ElementUtils.setPathToEmail(emails, orcid);
        return Response.ok(emails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewOtherNames(String orcid) {
        OtherNames otherNames = otherNameManager.getOtherNames(orcid);
        List<OtherName> allOtherNames = otherNames.getOtherNames();
        List<OtherName> filterdOtherNames = (List<OtherName>) visibilityFilter.filter(allOtherNames);
        otherNames.setOtherNames(filterdOtherNames);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        return Response.ok(otherNames).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManager.getOtherName(orcid, putCode);
        orcidSecurityManager.checkVisibility(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        return Response.ok(otherName).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response createOtherName(String orcid, OtherName otherName) {
        otherName = otherNameManager.createOtherName(orcid, otherName);
        try {
            return Response.created(new URI(String.valueOf(otherName.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response updateOtherName(String orcid, Long putCode, OtherName otherName) {
        if (!putCode.equals(otherName.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(otherName.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        OtherName updatedOtherName = otherNameManager.updateOtherName(orcid, putCode, otherName);
        ElementUtils.setPathToOtherName(updatedOtherName, orcid);
        return Response.ok(updatedOtherName).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response deleteOtherName(String orcid, Long putCode) {
        otherNameManager.deleteOtherName(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override    
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(orcid);
        personalDetails = visibilityFilter.filter(personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);    
        return Response.ok(personalDetails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewExternalIdentifiers(String orcid) {
        ExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiersV2(orcid);
        List<ExternalIdentifier> allExtIds = extIds.getExternalIdentifier();
        List<ExternalIdentifier> filteredExtIds = (List<ExternalIdentifier>) visibilityFilter.filter(allExtIds);
        extIds.setExternalIdentifiers(filteredExtIds);
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        return Response.ok(extIds).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        ExternalIdentifier extId = externalIdentifierManager.getExternalIdentifierV2(orcid, putCode);
        orcidSecurityManager.checkVisibility(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        return Response.ok(extId).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response updateExternalIdentifier(String orcid, Long putCode, ExternalIdentifier externalIdentifier) {
        if (!putCode.equals(externalIdentifier.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(externalIdentifier.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        ExternalIdentifier extId = externalIdentifierManager.updateExternalIdentifierV2(orcid, externalIdentifier);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        return Response.ok(extId).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response createExternalIdentifier(String orcid, ExternalIdentifier externalIdentifier) {
        externalIdentifier = externalIdentifierManager.createExternalIdentifierV2(orcid, externalIdentifier);
        try {
            return Response.created(new URI(String.valueOf(externalIdentifier.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        externalIdentifierManager.deleteExternalIdentifier(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewBiography(String orcid) {
        Biography bio = profileEntityManager.getBiography(orcid);
        orcidSecurityManager.checkVisibility(bio);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }
        
    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewKeywords(String orcid) {
        Keywords keywords = keywordsManager.getKeywords(orcid);
        List<Keyword> allKeywords = keywords.getKeywords();
        List<Keyword> filterdKeywords = (List<Keyword>) visibilityFilter.filter(allKeywords);
        keywords.setKeywords(filterdKeywords);
        ElementUtils.setPathToKeywords(keywords, orcid);
        return Response.ok(keywords).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = keywordsManager.getKeyword(orcid, putCode);
        orcidSecurityManager.checkVisibility(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        return Response.ok(keyword).build();
    }

    @Override    
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response createKeyword(String orcid, Keyword keyword) {
        keyword = keywordsManager.createKeyword(orcid, keyword);
        try {
            return Response.created(new URI(String.valueOf(keyword.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response updateKeyword(String orcid, Long putCode, Keyword keyword) {
        if (!putCode.equals(keyword.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(keyword.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        keyword = keywordsManager.updateKeyword(orcid, putCode, keyword);      
        ElementUtils.setPathToKeyword(keyword, orcid);
        return Response.ok(keyword).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response deleteKeyword(String orcid, Long putCode) {
        keywordsManager.deleteKeyword(orcid, putCode, true);
        return Response.noContent().build();
    }
                    
    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewAddresses(String orcid) {
        Addresses addresses = addressManager.getAddresses(orcid);
        List<Address> allAddresses = addresses.getAddress();
        List<Address> filteredAddresses = (List<Address>) visibilityFilter.filter(allAddresses);
        addresses.setAddress(filteredAddresses);
        ElementUtils.setPathToAddresses(addresses, orcid);
        return Response.ok(addresses).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManager.getAddress(orcid, putCode);
        orcidSecurityManager.checkVisibility(address);
        ElementUtils.setPathToAddress(address, orcid);
        return Response.ok(address).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response createAddress(String orcid, Address address) {
        address = addressManager.createAddress(orcid, address);
        try {
            return Response.created(new URI(String.valueOf(address.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response updateAddress(String orcid, Long putCode, Address address) {
        if (!putCode.equals(address.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(address.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        
        address = addressManager.updateAddress(orcid, putCode, address, true);
        ElementUtils.setPathToAddress(address, orcid);
        return Response.ok(address).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_UPDATE)
    public Response deleteAddress(String orcid, Long putCode) {
        addressManager.deleteAddress(orcid, putCode);
        return Response.noContent().build();
    }   
    
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED)
    public Response viewPerson(String orcid) {
        Person person = profileEntityManager.getPersonDetails(orcid);
        person = visibilityFilter.filter(person);
        ElementUtils.setPathToPerson(person, orcid);
        return Response.ok(person).build();
    }
}
