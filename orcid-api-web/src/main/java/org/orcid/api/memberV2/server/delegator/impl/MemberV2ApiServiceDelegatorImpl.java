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
import java.util.Date;
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

    private long getLastModifiedTime(String orcid) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();        
    }
    
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
    public Response viewActivities(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ACTIVITIES_READ_LIMITED);
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
    public Response viewWork(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_READ_LIMITED);
        long lastModifiedTime = getLastModifiedTime(orcid);
        Work w = workManager.getWork(orcid, putCode, lastModifiedTime);
        ActivityUtils.cleanEmptyFields(w);
        orcidSecurityManager.checkVisibility(w);
        ActivityUtils.setPathToActivity(w, orcid);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_READ_LIMITED);
        long lastModifiedTime = getLastModifiedTime(orcid);
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode, lastModifiedTime);
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkVisibility(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        return Response.ok(ws).build();
    }

    @Override
    public Response createWork(String orcid, Work work) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_CREATE);
        Work w = workManager.createWork(orcid, work, true);
        try {
            return Response.created(new URI(String.valueOf(w.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creatework_response.exception"), e);
        }
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Work work) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_UPDATE);
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
    public Response deleteWork(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_UPDATE);
        workManager.checkSourceAndRemoveWork(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_READ_LIMITED);
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkVisibility(f);
        ActivityUtils.setPathToActivity(f, orcid);
        return Response.ok(f).build();
    }

    @Override    
    public Response viewFundingSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_READ_LIMITED);
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        return Response.ok(fs).build();
    }

    @Override
    public Response createFunding(String orcid, Funding funding) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_CREATE);
        Funding f = profileFundingManager.createFunding(orcid, funding);
        try {
            return Response.created(new URI(String.valueOf(f.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createfunding_response.exception"), e);
        }
    }

    @Override    
    public Response updateFunding(String orcid, Long putCode, Funding funding) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_UPDATE);
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
    public Response deleteFunding(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_UPDATE);
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_READ_LIMITED);
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_READ_LIMITED);
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    public Response createEducation(String orcid, Education education) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_CREATE);
        Education e = affiliationsManager.createEducationAffiliation(orcid, education);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createeducation_response.exception"), ex);
        }
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Education education) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_UPDATE);
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
    public Response viewEmployment(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_READ_LIMITED);
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_READ_LIMITED);
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    public Response createEmployment(String orcid, Employment employment) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_CREATE);
        Employment e = affiliationsManager.createEmploymentAffiliation(orcid, employment);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createemployment_response.exception"), ex);
        }
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Employment employment) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_UPDATE);
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
    public Response deleteAffiliation(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_UPDATE);
        affiliationsManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }    

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_READ_LIMITED);
        PeerReview peerReview = peerReviewManager.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkVisibility(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        return Response.ok(peerReview).build();
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_READ_LIMITED);
        PeerReviewSummary summary = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
        return Response.ok(summary).build();
    }

    @Override
    public Response createPeerReview(String orcid, PeerReview peerReview) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_CREATE);
        PeerReview newPeerReview = peerReviewManager.createPeerReview(orcid, peerReview, true);
        try {
            return Response.created(new URI(String.valueOf(newPeerReview.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createpeerreview_response.exception"), ex);
        }
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_UPDATE);
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
    public Response deletePeerReview(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_UPDATE);
        peerReviewManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_READ);
        GroupIdRecord record = groupIdRecordManager.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_UPDATE);
        GroupIdRecord newRecord = groupIdRecordManager.createGroupIdRecord(groupIdRecord);
        try {
            return Response.created(new URI(String.valueOf(newRecord.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creategroupidrecord_response.exception"), ex);
        }
    }

    @Override
    public Response updateGroupIdRecord(GroupIdRecord groupIdRecord, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_UPDATE);
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
    public Response deleteGroupIdRecord(Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_UPDATE);
        groupIdRecordManager.deleteGroupIdRecord(putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_READ);
        GroupIdRecords records = groupIdRecordManager.getGroupIdRecords(pageSize, pageNum);
        return Response.ok(records).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewResearcherUrls(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        ResearcherUrls researcherUrls = researcherUrlManager.getResearcherUrls(orcid);
        researcherUrls.setResearcherUrls((List<ResearcherUrl>) visibilityFilter.filter(researcherUrls.getResearcherUrls()));
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        return Response.ok(researcherUrls).build();
    }

    public Response viewResearcherUrl(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrl(orcid, putCode);
        orcidSecurityManager.checkVisibility(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        return Response.ok(researcherUrl).build();
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, ResearcherUrl researcherUrl) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
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
    public Response createResearcherUrl(String orcid, ResearcherUrl researcherUrl) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        researcherUrl = researcherUrlManager.createResearcherUrl(orcid, researcherUrl);
        try {
            return Response.created(new URI(String.valueOf(researcherUrl.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        researcherUrlManager.deleteResearcherUrl(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewEmails(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        Emails emails = emailManager.getEmails(orcid);
        emails.setEmails((List<Email>) visibilityFilter.filter(emails.getEmails()));
        ElementUtils.setPathToEmail(emails, orcid);
        return Response.ok(emails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewOtherNames(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        long lastModifiedTime = getLastModifiedTime(orcid);
        OtherNames otherNames = otherNameManager.getOtherNames(orcid, lastModifiedTime);
        List<OtherName> allOtherNames = otherNames.getOtherNames();
        List<OtherName> filterdOtherNames = (List<OtherName>) visibilityFilter.filter(allOtherNames);
        otherNames.setOtherNames(filterdOtherNames);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        OtherName otherName = otherNameManager.getOtherName(orcid, putCode);
        orcidSecurityManager.checkVisibility(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        return Response.ok(otherName).build();
    }

    @Override
    public Response createOtherName(String orcid, OtherName otherName) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        otherName = otherNameManager.createOtherName(orcid, otherName);
        try {
            return Response.created(new URI(String.valueOf(otherName.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, OtherName otherName) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
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
    public Response deleteOtherName(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        otherNameManager.deleteOtherName(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override    
    public Response viewPersonalDetails(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(orcid);
        personalDetails = visibilityFilter.filter(personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);    
        return Response.ok(personalDetails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewExternalIdentifiers(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        ExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiersV2(orcid);
        List<ExternalIdentifier> allExtIds = extIds.getExternalIdentifier();
        List<ExternalIdentifier> filteredExtIds = (List<ExternalIdentifier>) visibilityFilter.filter(allExtIds);
        extIds.setExternalIdentifiers(filteredExtIds);
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        ExternalIdentifier extId = externalIdentifierManager.getExternalIdentifierV2(orcid, putCode);
        orcidSecurityManager.checkVisibility(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        return Response.ok(extId).build();
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, ExternalIdentifier externalIdentifier) {
        if (!putCode.equals(externalIdentifier.getPutCode())) {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
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
    public Response createExternalIdentifier(String orcid, ExternalIdentifier externalIdentifier) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        externalIdentifier = externalIdentifierManager.createExternalIdentifierV2(orcid, externalIdentifier);
        try {
            return Response.created(new URI(String.valueOf(externalIdentifier.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        externalIdentifierManager.deleteExternalIdentifier(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override
    public Response viewBiography(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        Biography bio = profileEntityManager.getBiography(orcid);
        orcidSecurityManager.checkVisibility(bio);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }
        
    @SuppressWarnings("unchecked")
    @Override
    public Response viewKeywords(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        long lastModifiedTime = getLastModifiedTime(orcid);
        Keywords keywords = keywordsManager.getKeywords(orcid, lastModifiedTime);
        List<Keyword> allKeywords = keywords.getKeywords();
        List<Keyword> filterdKeywords = (List<Keyword>) visibilityFilter.filter(allKeywords);
        keywords.setKeywords(filterdKeywords);
        ElementUtils.setPathToKeywords(keywords, orcid);
        return Response.ok(keywords).build();
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        Keyword keyword = keywordsManager.getKeyword(orcid, putCode);
        orcidSecurityManager.checkVisibility(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        return Response.ok(keyword).build();
    }

    @Override    
    public Response createKeyword(String orcid, Keyword keyword) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        keyword = keywordsManager.createKeyword(orcid, keyword);
        try {
            return Response.created(new URI(String.valueOf(keyword.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Keyword keyword) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
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
    public Response deleteKeyword(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        keywordsManager.deleteKeyword(orcid, putCode, true);
        return Response.noContent().build();
    }
                    
    @SuppressWarnings("unchecked")
    @Override
    public Response viewAddresses(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        Addresses addresses = addressManager.getAddresses(orcid, getLastModifiedTime(orcid));
        List<Address> allAddresses = addresses.getAddress();
        List<Address> filteredAddresses = (List<Address>) visibilityFilter.filter(allAddresses);
        addresses.setAddress(filteredAddresses);
        ElementUtils.setPathToAddresses(addresses, orcid);
        return Response.ok(addresses).build();
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        Address address = addressManager.getAddress(orcid, putCode);
        orcidSecurityManager.checkVisibility(address);
        ElementUtils.setPathToAddress(address, orcid);
        return Response.ok(address).build();
    }

    @Override
    public Response createAddress(String orcid, Address address) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        address = addressManager.createAddress(orcid, address);
        try {
            return Response.created(new URI(String.valueOf(address.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Address address) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
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
    public Response deleteAddress(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE);
        addressManager.deleteAddress(orcid, putCode);
        return Response.noContent().build();
    }   
    
    @Override
    public Response viewPerson(String orcid) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED);
        Person person = profileEntityManager.getPersonDetails(orcid);
        person = visibilityFilter.filter(person);
        ElementUtils.setPathToPerson(person, orcid);
        return Response.ok(person).build();
    }
}
