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

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

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
import org.orcid.jaxb.model.common_rc2.Visibility;
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
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.Record;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWork(String orcid, Long putCode) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        Work w = workManager.getWork(orcid, putCode, lastModifiedTime);
        orcidSecurityManager.checkIsPublic(w);
        ActivityUtils.cleanEmptyFields(w);        
        ActivityUtils.setPathToActivity(w, orcid);
        return Response.ok(w).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewWorkCitation(String orcid, Long putCode) { 
        Work w = (Work) this.viewWork(orcid, putCode).getEntity();
        ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        String creditName = null;
        if(entity.getRecordNameEntity() != null) {
            if (!entity.getRecordNameEntity().getVisibility().isMoreRestrictiveThan(Visibility.PUBLIC)){
                creditName = entity.getRecordNameEntity().getCreditName();
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
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkIsPublic(f);
        ActivityUtils.setPathToActivity(f, orcid);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManager.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkIsPublic(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        return Response.ok(peerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkIsPublic(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
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
        return Response.ok(researcherUrls).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrl(orcid, putCode);
        orcidSecurityManager.checkIsPublic(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        return Response.ok(researcherUrl).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmails(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Emails emails = emailManager.getPublicEmails(orcid, lastModifiedTime);
        ElementUtils.setPathToEmail(emails, orcid);
        return Response.ok(emails).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManager.getPublicPersonalDetails(orcid);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        return Response.ok(personalDetails).build();
    }    

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewOtherNames(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid, lastModifiedTime);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        return Response.ok(otherNames).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManager.getOtherName(orcid, putCode);
        orcidSecurityManager.checkIsPublic(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        return Response.ok(otherName).build();
    }    
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewExternalIdentifiers(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        PersonExternalIdentifiers extIds = externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime);  
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        return Response.ok(extIds).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        PersonExternalIdentifier extId = externalIdentifierManager.getExternalIdentifier(orcid, putCode);
        orcidSecurityManager.checkIsPublic(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        return Response.ok(extId).build();
    }    
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewBiography(String orcid) {
        Biography bio = biographyManager.getPublicBiography(orcid);
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
        return Response.ok(keywords).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = keywordsManager.getKeyword(orcid, putCode);
        orcidSecurityManager.checkIsPublic(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        return Response.ok(keyword).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewAddresses(String orcid) {
        Addresses addresses = addressManager.getPublicAddresses(orcid, getLastModifiedTime(orcid));
        ElementUtils.setPathToAddresses(addresses, orcid);
        return Response.ok(addresses).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManager.getAddress(orcid, putCode);
        orcidSecurityManager.checkIsPublic(address);
        ElementUtils.setPathToAddress(address, orcid);
        return Response.ok(address).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPerson(String orcid) {
        Person person = profileEntityManager.getPublicPersonDetails(orcid);
        ElementUtils.setPathToPerson(person, orcid);
        return Response.ok(person).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewRecord(String orcid) {
        Record record = recordManager.getPublicRecord(orcid);
        if(record.getPerson() != null) {
            ElementUtils.setPathToPerson(record.getPerson(), orcid);
        }
        if(record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            ActivityUtils.setPathToActivity(record.getActivitiesSummary(), orcid);
        }         
        return Response.ok(record).build();
    }
}
