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

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.common.writer.citeproc.WorkToCiteprocTranslator;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.api.publicV2.server.security.PublicAPISecurityManagerV2;
import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.utils.SourceUtils;
import org.orcid.core.version.impl.Api2_0_LastModifiedDatesHelper;
import org.orcid.core.version.impl.Api2_0_v2_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Work;
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

    //Activities managers
    @Resource
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;       

    @Resource
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Resource
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Resource
    private ActivitiesSummaryManagerReadOnly activitiesSummaryManagerReadOnly;
    
    //Person managers
    @Resource
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Resource
    private OtherNameManagerReadOnly otherNameManagerReadOnly;

    @Resource
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;    

    @Resource
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Resource
    private ProfileKeywordManagerReadOnly profileKeywordManagerReadOnly;
    
    @Resource
    private AddressManagerReadOnly addressManagerReadOnly;
    
    @Resource
    private BiographyManagerReadOnly biographyManagerReadOnly;

    @Resource
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;
    
    //Record manager
    @Resource
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;
    
    @Resource
    private RecordManagerReadOnly recordManagerReadOnly;                
    
    //Other managers
    @Resource
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;
    
    @Resource
    private SourceUtils sourceUtilsReadOnly;
    
    @Resource
    private PublicAPISecurityManagerV2 publicAPISecurityManagerV2;
    
    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;
    
    private long getLastModifiedTime(String orcid) {        
        return profileEntityManagerReadOnly.getLastModified(orcid);        
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
        ActivitiesSummary as = activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(orcid);
        publicAPISecurityManagerV2.filter(as);
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(as);
        sourceUtilsReadOnly.setSourceName(as);
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWork(String orcid, Long putCode) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Work w = workManagerReadOnly.getWork(orcid, putCode, lastModifiedTime);
        publicAPISecurityManagerV2.checkIsPublic(w);
        ActivityUtils.cleanEmptyFields(w);
        ActivityUtils.setPathToActivity(w, orcid);
        sourceUtilsReadOnly.setSourceName(w);
        return Response.ok(w).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWorks(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        List<WorkSummary> works = workManagerReadOnly.getWorksSummaryList(orcid, lastModifiedTime);
        Works publicWorks = workManagerReadOnly.groupWorks(works, true);
        publicAPISecurityManagerV2.filter(publicWorks);
        ActivityUtils.cleanEmptyFields(publicWorks);
        ActivityUtils.setPathToWorks(publicWorks, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicWorks);
        sourceUtilsReadOnly.setSourceName(publicWorks);
        return Response.ok(publicWorks).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewWorkCitation(String orcid, Long putCode) { 
        Work w = (Work) this.viewWork(orcid, putCode).getEntity();
        ProfileEntity entity = profileEntityManagerReadOnly.findByOrcid(orcid);
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
        WorkSummary ws = workManagerReadOnly.getWorkSummary(orcid, putCode, lastModifiedTime);
        ActivityUtils.cleanEmptyFields(ws);
        publicAPISecurityManagerV2.checkIsPublic(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        sourceUtilsReadOnly.setSourceName(ws);
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManagerReadOnly.getFunding(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(f);
        ActivityUtils.setPathToActivity(f, orcid);
        sourceUtilsReadOnly.setSourceName(f);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFundings(String orcid) {        
        List<FundingSummary> fundings = profileFundingManagerReadOnly.getFundingSummaryList(orcid, getLastModifiedTime(orcid));
        Fundings publicFundings = profileFundingManagerReadOnly.groupFundings(fundings, true);
        publicAPISecurityManagerV2.filter(publicFundings);        
        ActivityUtils.setPathToFundings(publicFundings, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicFundings);
        sourceUtilsReadOnly.setSourceName(publicFundings);
        return Response.ok(publicFundings).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManagerReadOnly.getSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        sourceUtilsReadOnly.setSourceName(fs);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManagerReadOnly.getEducationAffiliation(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducations(String orcid) {        
        List<EducationSummary> educations = affiliationsManagerReadOnly.getEducationSummaryList(orcid, getLastModifiedTime(orcid));        
        Educations publicEducations = new Educations();
        for(EducationSummary summary : educations) {
        	if(Visibility.PUBLIC.equals(summary.getVisibility())) {
	        	ActivityUtils.setPathToActivity(summary, orcid);
	        	sourceUtilsReadOnly.setSourceName(summary);
	        	publicEducations.getSummaries().add(summary);          
        	}
        }
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicEducations);
        return Response.ok(publicEducations).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManagerReadOnly.getEducationSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtilsReadOnly.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManagerReadOnly.getEmploymentAffiliation(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmployments(String orcid) {        
        List<EmploymentSummary> employments = affiliationsManagerReadOnly.getEmploymentSummaryList(orcid, getLastModifiedTime(orcid));
        Employments publicEmployments = new Employments();
        for(EmploymentSummary summary : employments) {
        	if(Visibility.PUBLIC.equals(summary.getVisibility())) {
	        	ActivityUtils.setPathToActivity(summary, orcid);
	            sourceUtilsReadOnly.setSourceName(summary);
	            publicEmployments.getSummaries().add(summary);         
        	}
        }
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicEmployments);
        return Response.ok(publicEmployments).build();
    }
    
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManagerReadOnly.getEmploymentSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtilsReadOnly.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManagerReadOnly.getPeerReview(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        sourceUtilsReadOnly.setSourceName(peerReview);
        return Response.ok(peerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReviews(String orcid) {
        List<PeerReviewSummary> peerReviews = peerReviewManagerReadOnly.getPeerReviewSummaryList(orcid, getLastModifiedTime(orcid));
        PeerReviews publicPeerReviews = peerReviewManagerReadOnly.groupPeerReviews(peerReviews, true);
        publicAPISecurityManagerV2.filter(publicPeerReviews);
        ActivityUtils.setPathToPeerReviews(publicPeerReviews, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicPeerReviews);
        sourceUtilsReadOnly.setSourceName(publicPeerReviews);
        return Response.ok(publicPeerReviews).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManagerReadOnly.getPeerReviewSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
        sourceUtilsReadOnly.setSourceName(summary);
        return Response.ok(summary).build();
    }

    @Override 
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_READ, enableAnonymousAccess = true)
    public Response viewGroupIdRecord(Long putCode) {
        GroupIdRecord record = groupIdRecordManagerReadOnly.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.GROUP_ID_RECORD_READ, enableAnonymousAccess = true)
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        GroupIdRecords records = groupIdRecordManagerReadOnly.getGroupIdRecords(pageSize, pageNum);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(records);
        return Response.ok(records).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewResearcherUrls(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        ResearcherUrls researcherUrls = researcherUrlManagerReadOnly.getPublicResearcherUrls(orcid, lastModifiedTime);
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(researcherUrls);
        sourceUtilsReadOnly.setSourceName(researcherUrls);
        return Response.ok(researcherUrls).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManagerReadOnly.getResearcherUrl(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        sourceUtilsReadOnly.setSourceName(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmails(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Emails emails = emailManagerReadOnly.getPublicEmails(orcid, lastModifiedTime);
        publicAPISecurityManagerV2.filter(emails);
        ElementUtils.setPathToEmail(emails, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(emails);
        sourceUtilsReadOnly.setSourceName(emails);
        return Response.ok(emails).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManagerReadOnly.getPublicPersonalDetails(orcid);
        publicAPISecurityManagerV2.filter(personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(personalDetails);
        sourceUtilsReadOnly.setSourceName(personalDetails);
        return Response.ok(personalDetails).build();
    }    

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewOtherNames(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        OtherNames otherNames = otherNameManagerReadOnly.getPublicOtherNames(orcid, lastModifiedTime);
        publicAPISecurityManagerV2.filter(otherNames);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(otherNames);
        sourceUtilsReadOnly.setSourceName(otherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManagerReadOnly.getOtherName(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        sourceUtilsReadOnly.setSourceName(otherName);
        return Response.ok(otherName).build();
    }    
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewExternalIdentifiers(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        PersonExternalIdentifiers extIds = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid, lastModifiedTime);  
        publicAPISecurityManagerV2.filter(extIds);
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(extIds);
        sourceUtilsReadOnly.setSourceName(extIds);
        return Response.ok(extIds).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        PersonExternalIdentifier extId = externalIdentifierManagerReadOnly.getExternalIdentifier(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtilsReadOnly.setSourceName(extId);
        return Response.ok(extId).build();
    }    
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewBiography(String orcid) {
        Biography bio = biographyManagerReadOnly.getBiography(orcid, getLastModifiedTime(orcid));
        publicAPISecurityManagerV2.checkIsPublic(bio);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }
            
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewKeywords(String orcid) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Keywords keywords = profileKeywordManagerReadOnly.getPublicKeywords(orcid, lastModifiedTime);
        publicAPISecurityManagerV2.filter(keywords);
        ElementUtils.setPathToKeywords(keywords, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(keywords);
        sourceUtilsReadOnly.setSourceName(keywords);
        return Response.ok(keywords).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = profileKeywordManagerReadOnly.getKeyword(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtilsReadOnly.setSourceName(keyword);
        return Response.ok(keyword).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewAddresses(String orcid) {
        Addresses addresses = addressManagerReadOnly.getPublicAddresses(orcid, getLastModifiedTime(orcid));
        publicAPISecurityManagerV2.filter(addresses);
        ElementUtils.setPathToAddresses(addresses, orcid);
        //Set the latest last modified
        Api2_0_LastModifiedDatesHelper.calculateLastModified(addresses);
        sourceUtilsReadOnly.setSourceName(addresses);
        return Response.ok(addresses).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManagerReadOnly.getAddress(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(address);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtilsReadOnly.setSourceName(address);
        return Response.ok(address).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPerson(String orcid) {
        Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
        publicAPISecurityManagerV2.filter(person);
        ElementUtils.setPathToPerson(person, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(person);
        sourceUtilsReadOnly.setSourceName(person);
        return Response.ok(person).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewRecord(String orcid) {
        Record record = recordManagerReadOnly.getPublicRecord(orcid);
        publicAPISecurityManagerV2.filter(record);
        if(record.getPerson() != null) {
            ElementUtils.setPathToPerson(record.getPerson(), orcid);
            sourceUtilsReadOnly.setSourceName(record.getPerson());
        }
        if(record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            ActivityUtils.setPathToActivity(record.getActivitiesSummary(), orcid);
            sourceUtilsReadOnly.setSourceName(record.getActivitiesSummary());
        }
        Api2_0_LastModifiedDatesHelper.calculateLastModified(record);
        return Response.ok(record).build();
    }
}
