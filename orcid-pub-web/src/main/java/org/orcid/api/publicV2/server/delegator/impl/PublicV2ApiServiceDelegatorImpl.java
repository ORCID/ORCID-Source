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

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.common.writer.citeproc.WorkToCiteprocTranslator;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.BiographyManager;
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
import org.orcid.core.manager.RecordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.core.utils.SourceUtils;
import org.orcid.core.version.impl.Api2_0_rc3_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Addresses;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.Education;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.jaxb.model.record_rc3.Employment;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.Keywords;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.ResearcherUrls;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.undercouch.citeproc.csl.CSLItemData;

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
public class PublicV2ApiServiceDelegatorImpl
        implements PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> {

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
    
    @Resource
    private BiographyManager biographyManager;

    @Resource
    private RecordManager recordManager;
    
    @Resource
    private SourceUtils sourceUtils;
    
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
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewActivities(String orcid) {
        ActivitiesSummary as = visibilityFilter.filter(profileEntityManager.getPublicActivitiesSummary(orcid), orcid);
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        sourceUtils.setSourceName(as);
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWork(String orcid, Long putCode) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Work w = workManager.getWork(orcid, putCode, lastModifiedTime);
        orcidSecurityManager.checkIsPublic(w);
        ActivityUtils.cleanEmptyFields(w);
        ActivityUtils.setPathToActivity(w, orcid);
        sourceUtils.setSourceName(w);
        return Response.ok(w).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWorks(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        List<WorkSummary> works = workManager.getWorksSummaryList(orcid, lastModifiedTime);
        Works publicWorks = workManager.groupWorks(works, true);
        publicWorks = visibilityFilter.filter(publicWorks, orcid);
        ActivityUtils.cleanEmptyFields(publicWorks);
        ActivityUtils.setPathToWorks(publicWorks, orcid);
        sourceUtils.setSourceName(publicWorks);
        return Response.ok(publicWorks).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewWorkCitation(String orcid, Long putCode) { 
        Work w = (Work) this.viewWork(orcid, putCode).getEntity();
        ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        String creditName = null;
        RecordNameEntity recordNameEntity = entity.getRecordNameEntity();
        if(recordNameEntity != null) {
            if (!recordNameEntity.getVisibility().isMoreRestrictiveThan(Visibility.PUBLIC)) {
                creditName = recordNameEntity.getCreditName();
                if (StringUtils.isBlank(creditName)) {
                    creditName = recordNameEntity.getGivenNames();
                    String familyName = recordNameEntity.getFamilyName();
                    if (StringUtils.isNotBlank(familyName)) {
                        creditName += " " + familyName;
                    }
                }
            }
        }
        
        WorkToCiteprocTranslator tran = new  WorkToCiteprocTranslator();
        CSLItemData item = tran.toCiteproc(w, creditName ,true);
        return Response.ok(item).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWorkSummary(String orcid, Long putCode) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode, lastModifiedTime);
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkIsPublic(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        sourceUtils.setSourceName(ws);
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkIsPublic(f);
        ActivityUtils.setPathToActivity(f, orcid);
        sourceUtils.setSourceName(f);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFundings(String orcid) {        
        List<FundingSummary> fundings = profileFundingManager.getFundingSummaryList(orcid, getLastModifiedTime(orcid));
        Fundings publicFundings = profileFundingManager.groupFundings(fundings, true);
        publicFundings = visibilityFilter.filter(publicFundings, orcid);        
        ActivityUtils.setPathToFundings(publicFundings, orcid);
        sourceUtils.setSourceName(publicFundings);
        return Response.ok(publicFundings).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        sourceUtils.setSourceName(fs);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducations(String orcid) {        
        List<EducationSummary> educations = affiliationsManager.getEducationSummaryList(orcid, getLastModifiedTime(orcid));        
        Educations publicEducations = new Educations();
        for(EducationSummary summary : educations) {
            if(Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtils.setSourceName(summary);
                publicEducations.getSummaries().add(summary);
            }
        }
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(publicEducations);
        return Response.ok(publicEducations).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtils.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmployments(String orcid) {        
        List<EmploymentSummary> employments = affiliationsManager.getEmploymentSummaryList(orcid, getLastModifiedTime(orcid));
        Employments publicEmployments = new Employments();
        for(EmploymentSummary summary : employments) {
            if(Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtils.setSourceName(summary);
                publicEmployments.getSummaries().add(summary);
            }
        }
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(publicEmployments);
        return Response.ok(publicEmployments).build();
    }
    
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtils.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManager.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkIsPublic(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        sourceUtils.setSourceName(peerReview);
        return Response.ok(peerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReviews(String orcid) {
        List<PeerReviewSummary> peerReviews = peerReviewManager.getPeerReviewSummaryList(orcid, getLastModifiedTime(orcid));
        PeerReviews publicPeerReviews = peerReviewManager.groupPeerReviews(peerReviews, true);
        publicPeerReviews = visibilityFilter.filter(publicPeerReviews, orcid);
        ActivityUtils.setPathToPeerReviews(publicPeerReviews, orcid);
        sourceUtils.setSourceName(publicPeerReviews);
        return Response.ok(publicPeerReviews).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
        sourceUtils.setSourceName(summary);
        return Response.ok(summary).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_READ, enableAnonymousAccess = true)
    public Response viewGroupIdRecord(Long putCode) {
        GroupIdRecord record = groupIdRecordManager.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_READ, enableAnonymousAccess = true)
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        GroupIdRecords records = groupIdRecordManager.getGroupIdRecords(pageSize, pageNum);
        return Response.ok(records).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewResearcherUrls(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        ResearcherUrls researcherUrls = researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime);
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        sourceUtils.setSourceName(researcherUrls);
        return Response.ok(researcherUrls).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrl(orcid, putCode);
        orcidSecurityManager.checkIsPublic(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        sourceUtils.setSourceName(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmails(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Emails emails = emailManager.getPublicEmails(orcid, lastModifiedTime);
        ElementUtils.setPathToEmail(emails, orcid);
        sourceUtils.setSourceName(emails);
        return Response.ok(emails).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManager.getPublicPersonalDetails(orcid);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        sourceUtils.setSourceName(personalDetails);
        return Response.ok(personalDetails).build();
    }    

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewOtherNames(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid, lastModifiedTime);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        sourceUtils.setSourceName(otherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManager.getOtherName(orcid, putCode);
        orcidSecurityManager.checkIsPublic(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        sourceUtils.setSourceName(otherName);
        return Response.ok(otherName).build();
    }    
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewExternalIdentifiers(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        PersonExternalIdentifiers extIds = externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime);  
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        sourceUtils.setSourceName(extIds);
        return Response.ok(extIds).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        PersonExternalIdentifier extId = externalIdentifierManager.getExternalIdentifier(orcid, putCode);
        orcidSecurityManager.checkIsPublic(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtils.setSourceName(extId);
        return Response.ok(extId).build();
    }    
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewBiography(String orcid) {
        Biography bio = biographyManager.getBiography(orcid);
        orcidSecurityManager.checkIsPublic(bio);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }
            
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewKeywords(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Keywords keywords = keywordsManager.getPublicKeywords(orcid, lastModifiedTime);
        ElementUtils.setPathToKeywords(keywords, orcid);
        sourceUtils.setSourceName(keywords);
        return Response.ok(keywords).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = keywordsManager.getKeyword(orcid, putCode);
        orcidSecurityManager.checkIsPublic(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtils.setSourceName(keyword);
        return Response.ok(keyword).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewAddresses(String orcid) {
        Addresses addresses = addressManager.getPublicAddresses(orcid, getLastModifiedTime(orcid));
        ElementUtils.setPathToAddresses(addresses, orcid);
        sourceUtils.setSourceName(addresses);
        return Response.ok(addresses).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManager.getAddress(orcid, putCode);
        orcidSecurityManager.checkIsPublic(address);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtils.setSourceName(address);
        return Response.ok(address).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPerson(String orcid) {
        Person person = profileEntityManager.getPublicPersonDetails(orcid);
        ElementUtils.setPathToPerson(person, orcid);
        sourceUtils.setSourceName(person);
        return Response.ok(person).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewRecord(String orcid) {
        Record record = recordManager.getPublicRecord(orcid);
        if(record.getPerson() != null) {
            ElementUtils.setPathToPerson(record.getPerson(), orcid);
            sourceUtils.setSourceName(record.getPerson());
        }
        if(record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            ActivityUtils.setPathToActivity(record.getActivitiesSummary(), orcid);
            sourceUtils.setSourceName(record.getActivitiesSummary());
        }         
        return Response.ok(record).build();
    }
}
