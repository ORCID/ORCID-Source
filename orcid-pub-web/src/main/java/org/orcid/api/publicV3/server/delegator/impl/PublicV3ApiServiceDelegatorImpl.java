package org.orcid.api.publicV3.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.orcid.api.common.util.v3.ActivityUtils;
import org.orcid.api.common.util.v3.ElementUtils;
import org.orcid.api.common.writer.citeproc.V3WorkToCiteprocTranslator;
import org.orcid.api.publicV3.server.delegator.PublicV3ApiServiceDelegator;
import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.SearchStartParameterLimitExceededException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.RecordManager;
import org.orcid.core.manager.v3.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.core.version.impl.Api3_0_RC1LastModifiedDatesHelper;
import org.orcid.jaxb.model.v3.rc1.client.Client;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecords;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Record;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Memberships;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Services;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.jaxb.model.v3.rc1.search.Search;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.undercouch.citeproc.csl.CSLItemData;

@Component
public class PublicV3ApiServiceDelegatorImpl
        implements PublicV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work> {

    // Activities managers
    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource(name = "profileFundingManagerReadOnlyV3")
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Resource(name = "peerReviewManagerReadOnlyV3")
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Resource(name = "researchResourceManagerReadOnlyV3")
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

    @Resource(name = "activitiesSummaryManagerReadOnlyV3")
    private ActivitiesSummaryManagerReadOnly activitiesSummaryManagerReadOnly;

    // Person managers
    @Resource(name = "researcherUrlManagerReadOnlyV3")
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Resource(name = "otherNameManagerReadOnlyV3")
    private OtherNameManagerReadOnly otherNameManagerReadOnly;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource(name = "externalIdentifierManagerReadOnlyV3")
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Resource(name = "personalDetailsManagerReadOnlyV3")
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Resource(name = "profileKeywordManagerReadOnlyV3")
    private ProfileKeywordManagerReadOnly profileKeywordManagerReadOnly;

    @Resource(name = "addressManagerReadOnlyV3")
    private AddressManagerReadOnly addressManagerReadOnly;

    @Resource(name = "biographyManagerReadOnlyV3")
    private BiographyManagerReadOnly biographyManagerReadOnly;

    @Resource(name = "personDetailsManagerReadOnlyV3")
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;

    // Record manager
    @Resource(name = "profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    // Other managers
    @Resource(name = "groupIdRecordManagerReadOnlyV3")
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Resource(name = "sourceUtilsReadOnlyV3")
    private SourceUtils sourceUtilsReadOnly;

    @Resource(name = "contributorUtilsV3")
    private ContributorUtils contributorUtilsReadOnly;

    @Resource(name = "recordManagerV3")
    private RecordManager recordManager;

    @Resource(name = "sourceUtilsV3")
    private SourceUtils sourceUtils;

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManager orcidSearchManager;
    
    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "publicAPISecurityManagerV3")
    private PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "clientDetailsManagerReadOnlyV3")
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Resource
    private OpenIDConnectKeyService openIDConnectKeyService;
    
    @Resource
    private StatusManager statusManager;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    public Response viewStatus() {
        return Response.ok(statusManager.createStatusMap()).build();
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
        checkProfileStatus(orcid);
        ActivitiesSummary as = activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(orcid);
        publicAPISecurityManagerV3.filter(as);
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(as);
        sourceUtilsReadOnly.setSourceName(as);
        return Response.ok(as).build();
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Work w = workManagerReadOnly.getWork(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(w);
        contributorUtilsReadOnly.filterContributorPrivateData(w);        
        ActivityUtils.cleanEmptyFields(w);
        ActivityUtils.setPathToActivity(w, orcid);
        sourceUtilsReadOnly.setSourceName(w);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorks(String orcid) {
        checkProfileStatus(orcid);
        List<WorkSummary> works = workManagerReadOnly.getWorksSummaryList(orcid);
        Works publicWorks = workManagerReadOnly.groupWorks(works, true);
        publicAPISecurityManagerV3.filter(publicWorks);
        ActivityUtils.cleanEmptyFields(publicWorks);
        ActivityUtils.setPathToWorks(publicWorks, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(publicWorks);
        sourceUtilsReadOnly.setSourceName(publicWorks);
        return Response.ok(publicWorks).build();
    }

    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Work w = (Work) this.viewWork(orcid, putCode).getEntity();
        ProfileEntity entity = profileEntityManagerReadOnly.findByOrcid(orcid);
        String creditName = null;
        RecordNameEntity recordNameEntity = entity.getRecordNameEntity();
        if (recordNameEntity != null) {
            if (!Visibility.valueOf(recordNameEntity.getVisibility()).isMoreRestrictiveThan(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC)) {
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

        V3WorkToCiteprocTranslator tran = new V3WorkToCiteprocTranslator();
        CSLItemData item = tran.toCiteproc(w, creditName, true);
        return Response.ok(item).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        WorkSummary ws = workManagerReadOnly.getWorkSummary(orcid, putCode);
        ActivityUtils.cleanEmptyFields(ws);
        publicAPISecurityManagerV3.checkIsPublic(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        sourceUtilsReadOnly.setSourceName(ws);
        return Response.ok(ws).build();
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Funding f = profileFundingManagerReadOnly.getFunding(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(f);
        ActivityUtils.setPathToActivity(f, orcid);
        ActivityUtils.cleanEmptyFields(f);
        sourceUtilsReadOnly.setSourceName(f);
        contributorUtilsReadOnly.filterContributorPrivateData(f);
        return Response.ok(f).build();
    }

    @Override
    public Response viewFundings(String orcid) {
        checkProfileStatus(orcid);
        List<FundingSummary> fundings = profileFundingManagerReadOnly.getFundingSummaryList(orcid);
        Fundings publicFundings = profileFundingManagerReadOnly.groupFundings(fundings, true);
        publicAPISecurityManagerV3.filter(publicFundings);
        ActivityUtils.setPathToFundings(publicFundings, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(publicFundings);
        sourceUtilsReadOnly.setSourceName(publicFundings);
        return Response.ok(publicFundings).build();
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        FundingSummary fs = profileFundingManagerReadOnly.getSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        sourceUtilsReadOnly.setSourceName(fs);
        return Response.ok(fs).build();
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Education e = affiliationsManagerReadOnly.getEducationAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducations(String orcid) {
        checkProfileStatus(orcid);
        List<EducationSummary> educations = affiliationsManagerReadOnly.getEducationSummaryList(orcid);
        List<EducationSummary> publicEducations = new ArrayList<>();
        for (EducationSummary summary : educations) {
            if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtilsReadOnly.setSourceName(summary);
                publicEducations.add(summary);
            }
        }
        
        Educations groupedEducations = new Educations(affiliationsManagerReadOnly.groupAffiliations(publicEducations, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedEducations);
        ActivityUtils.setPathToAffiliations(groupedEducations, orcid);
        return Response.ok(groupedEducations).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        EducationSummary es = affiliationsManagerReadOnly.getEducationSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtilsReadOnly.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Employment e = affiliationsManagerReadOnly.getEmploymentAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployments(String orcid) {
        checkProfileStatus(orcid);
        List<EmploymentSummary> employments = affiliationsManagerReadOnly.getEmploymentSummaryList(orcid);
        List<EmploymentSummary>  publicEmployments = new ArrayList<>();
        for (EmploymentSummary summary : employments) {
            if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtilsReadOnly.setSourceName(summary);
                publicEmployments.add(summary);
            }
        }
        Employments groupedEmployments = new Employments(affiliationsManagerReadOnly.groupAffiliations(publicEmployments, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedEmployments);
        ActivityUtils.setPathToAffiliations(groupedEmployments, orcid);
        return Response.ok(groupedEmployments).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        EmploymentSummary es = affiliationsManagerReadOnly.getEmploymentSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtilsReadOnly.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        PeerReview peerReview = peerReviewManagerReadOnly.getPeerReview(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        sourceUtilsReadOnly.setSourceName(peerReview);
        return Response.ok(peerReview).build();
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        checkProfileStatus(orcid);
        List<PeerReviewSummary> peerReviews = peerReviewManagerReadOnly.getPeerReviewSummaryList(orcid);
        PeerReviews publicPeerReviews = peerReviewManagerReadOnly.groupPeerReviews(peerReviews, true);
        publicAPISecurityManagerV3.filter(publicPeerReviews);
        ActivityUtils.setPathToPeerReviews(publicPeerReviews, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(publicPeerReviews);
        sourceUtilsReadOnly.setSourceName(publicPeerReviews);
        return Response.ok(publicPeerReviews).build();
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        PeerReviewSummary summary = peerReviewManagerReadOnly.getPeerReviewSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
        sourceUtilsReadOnly.setSourceName(summary);
        return Response.ok(summary).build();
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        GroupIdRecord record = groupIdRecordManagerReadOnly.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        GroupIdRecords records = groupIdRecordManagerReadOnly.getGroupIdRecords(pageSize, pageNum);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(records);
        return Response.ok(records).build();
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        checkProfileStatus(orcid);
        ResearcherUrls researcherUrls = researcherUrlManagerReadOnly.getPublicResearcherUrls(orcid);
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(researcherUrls);
        sourceUtilsReadOnly.setSourceName(researcherUrls);
        return Response.ok(researcherUrls).build();
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        ResearcherUrl researcherUrl = researcherUrlManagerReadOnly.getResearcherUrl(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        sourceUtilsReadOnly.setSourceName(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @Override
    public Response viewEmails(String orcid) {
        checkProfileStatus(orcid);
        Emails emails = emailManagerReadOnly.getPublicEmails(orcid);
        publicAPISecurityManagerV3.filter(emails);
        ElementUtils.setPathToEmail(emails, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(emails);
        sourceUtilsReadOnly.setSourceName(emails);
        return Response.ok(emails).build();
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        checkProfileStatus(orcid);
        PersonalDetails personalDetails = personalDetailsManagerReadOnly.getPublicPersonalDetails(orcid);
        publicAPISecurityManagerV3.filter(personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(personalDetails);
        sourceUtilsReadOnly.setSourceName(personalDetails);
        return Response.ok(personalDetails).build();
    }

    @Override
    public Response viewOtherNames(String orcid) {
        checkProfileStatus(orcid);
        OtherNames otherNames = otherNameManagerReadOnly.getPublicOtherNames(orcid);
        publicAPISecurityManagerV3.filter(otherNames);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(otherNames);
        sourceUtilsReadOnly.setSourceName(otherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        OtherName otherName = otherNameManagerReadOnly.getOtherName(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        sourceUtilsReadOnly.setSourceName(otherName);
        return Response.ok(otherName).build();
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        checkProfileStatus(orcid);
        PersonExternalIdentifiers extIds = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid);
        publicAPISecurityManagerV3.filter(extIds);
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(extIds);
        sourceUtilsReadOnly.setSourceName(extIds);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        PersonExternalIdentifier extId = externalIdentifierManagerReadOnly.getExternalIdentifier(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtilsReadOnly.setSourceName(extId);
        return Response.ok(extId).build();
    }

    @Override
    public Response viewBiography(String orcid) {
        checkProfileStatus(orcid);
        Biography bio = biographyManagerReadOnly.getBiography(orcid);
        publicAPISecurityManagerV3.checkIsPublic(bio);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }

    @Override
    public Response viewKeywords(String orcid) {
        checkProfileStatus(orcid);
        Keywords keywords = profileKeywordManagerReadOnly.getPublicKeywords(orcid);
        publicAPISecurityManagerV3.filter(keywords);
        ElementUtils.setPathToKeywords(keywords, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(keywords);
        sourceUtilsReadOnly.setSourceName(keywords);
        return Response.ok(keywords).build();
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Keyword keyword = profileKeywordManagerReadOnly.getKeyword(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtilsReadOnly.setSourceName(keyword);
        return Response.ok(keyword).build();
    }

    @Override
    public Response viewAddresses(String orcid) {
        checkProfileStatus(orcid);
        Addresses addresses = addressManagerReadOnly.getPublicAddresses(orcid);
        publicAPISecurityManagerV3.filter(addresses);
        ElementUtils.setPathToAddresses(addresses, orcid);
        // Set the latest last modified
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(addresses);
        sourceUtilsReadOnly.setSourceName(addresses);
        return Response.ok(addresses).build();
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        checkProfileStatus(orcid);
        Address address = addressManagerReadOnly.getAddress(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(address);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtilsReadOnly.setSourceName(address);
        return Response.ok(address).build();
    }

    @Override
    public Response viewPerson(String orcid) {
        checkProfileStatus(orcid);
        Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
        publicAPISecurityManagerV3.filter(person);
        ElementUtils.setPathToPerson(person, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(person);
        sourceUtilsReadOnly.setSourceName(person);
        return Response.ok(person).build();
    }

    @Override
    public Response viewRecord(String orcid) {
        checkProfileStatus(orcid);
        Record record = recordManagerReadOnly.getPublicRecord(orcid);
        publicAPISecurityManagerV3.filter(record);
        if (record.getPerson() != null) {
            sourceUtilsReadOnly.setSourceName(record.getPerson());
        }
        if (record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            sourceUtilsReadOnly.setSourceName(record.getActivitiesSummary());
        }
        ElementUtils.setPathToRecord(record, orcid);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(record);
        return Response.ok(record).build();
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> solrParams) {
        validateSearchParams(solrParams);
        Search search = orcidSearchManager.findOrcidIds(solrParams);
        return Response.ok(search).build();
    }

    @Override
    public Response viewBulkWorks(String orcid, String putCodes) {
        ProfileEntity profileEntity = profileEntityManagerReadOnly.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new OrcidNoResultException("No such profile: " + orcid);
        }
        WorkBulk workBulk = workManagerReadOnly.findWorkBulk(orcid, putCodes);
        publicAPISecurityManagerV3.filter(workBulk);
        contributorUtilsReadOnly.filterContributorPrivateData(workBulk);        
        ActivityUtils.cleanEmptyFields(workBulk);
        ActivityUtils.setPathToBulk(workBulk, orcid);
        sourceUtils.setSourceName(workBulk);
        return Response.ok(workBulk).build();
    }

    private void validateSearchParams(Map<String, List<String>> queryMap) {
        validateRows(queryMap);
        validateStart(queryMap);
    }

    private void validateStart(Map<String, List<String>> queryMap) {
        String clientId = orcidSecurityManager.getClientIdFromAPIRequest();
        if (clientId == null) { 
            // only validate start param where no client credentials
            List<String> startList = queryMap.get("start");
            if (startList != null && !startList.isEmpty()) {
                try {
                    String startString = startList.get(0);
                    int start = Integer.valueOf(startString);
                    if (start < 0 || start > OrcidSearchManager.MAX_SEARCH_START) {
                        throw new SearchStartParameterLimitExceededException(
                                localeManager.resolveMessage("apiError.badrequest_invalid_search_start.exception", OrcidSearchManager.MAX_SEARCH_START));
                    }
                } catch (NumberFormatException e) {
                    throw new OrcidBadRequestException(
                            localeManager.resolveMessage("apiError.badrequest_invalid_search_start.exception", OrcidSearchManager.MAX_SEARCH_START));
                }
            }
        }
    }

    private void validateRows(Map<String, List<String>> queryMap) {
        List<String> rowsList = queryMap.get("rows");
        if (rowsList != null && !rowsList.isEmpty()) {
            try {
                String rowsString = rowsList.get(0);
                int rows = Integer.valueOf(rowsString);
                if (rows < 0 || rows > OrcidSearchManager.MAX_SEARCH_ROWS) {
                    throw new OrcidBadRequestException(
                            localeManager.resolveMessage("apiError.badrequest_invalid_search_rows.exception", OrcidSearchManager.MAX_SEARCH_ROWS));
                }
            } catch (NumberFormatException e) {
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_search_rows.exception", OrcidSearchManager.MAX_SEARCH_ROWS));
            }
        } else {
            // Set the default number of results
            queryMap.put("rows", Arrays.asList(String.valueOf(OrcidSearchManager.DEFAULT_SEARCH_ROWS)));
        }
    }

    @Override
    public Response viewClient(String clientId) {
        Client client = clientDetailsManagerReadOnly.getClient(clientId);
        return Response.ok(client).build();
    }

    @Override
    public Response viewDistinction(String orcid, Long putCode) {
        Distinction e = affiliationsManagerReadOnly.getDistinctionAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewDistinctions(String orcid) {
        List<DistinctionSummary> distinctions = affiliationsManagerReadOnly.getDistinctionSummaryList(orcid);
        List<DistinctionSummary> publicDistinctions = new ArrayList<>();
        for (DistinctionSummary summary : distinctions) {
            if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtilsReadOnly.setSourceName(summary);
                publicDistinctions.add(summary);
            }
        }
        
        Distinctions groupedDistinctions = new Distinctions(affiliationsManagerReadOnly.groupAffiliations(publicDistinctions, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedDistinctions);
        ActivityUtils.setPathToAffiliations(groupedDistinctions, orcid);
        return Response.ok(groupedDistinctions).build();
    }

    @Override
    public Response viewDistinctionSummary(String orcid, Long putCode) {
        DistinctionSummary s = affiliationsManagerReadOnly.getDistinctionSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(s);
        ActivityUtils.setPathToActivity(s, orcid);
        sourceUtilsReadOnly.setSourceName(s);
        return Response.ok(s).build();
    }

    @Override
    public Response viewInvitedPosition(String orcid, Long putCode) {
        InvitedPosition e = affiliationsManagerReadOnly.getInvitedPositionAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewInvitedPositions(String orcid) {
        List<InvitedPositionSummary> invitedPositions = affiliationsManagerReadOnly.getInvitedPositionSummaryList(orcid);
        List<InvitedPositionSummary>  publicInvitedPositions = new ArrayList<>();
        for (InvitedPositionSummary summary : invitedPositions) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                        sourceUtilsReadOnly.setSourceName(summary);
                        publicInvitedPositions.add(summary);
                }
        }
        
        InvitedPositions groupedInvitedPositions = new InvitedPositions(affiliationsManagerReadOnly.groupAffiliations(publicInvitedPositions, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedInvitedPositions);
        ActivityUtils.setPathToAffiliations(groupedInvitedPositions, orcid);
        return Response.ok(groupedInvitedPositions).build();
    }

    @Override
    public Response viewInvitedPositionSummary(String orcid, Long putCode) {
        InvitedPositionSummary s = affiliationsManagerReadOnly.getInvitedPositionSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(s);
        ActivityUtils.setPathToActivity(s, orcid);
        sourceUtilsReadOnly.setSourceName(s);
        return Response.ok(s).build();
    }

    @Override
    public Response viewMembership(String orcid, Long putCode) {
        Membership e = affiliationsManagerReadOnly.getMembershipAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewMemberships(String orcid) {
        List<MembershipSummary> memberships = affiliationsManagerReadOnly.getMembershipSummaryList(orcid);
        List<MembershipSummary> publicMemberships = new ArrayList<>();
        for (MembershipSummary summary : memberships) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                        sourceUtilsReadOnly.setSourceName(summary);
                        publicMemberships.add(summary);
                }
        }
        
        Memberships groupedMemberships = new Memberships(affiliationsManagerReadOnly.groupAffiliations(publicMemberships, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedMemberships);
        ActivityUtils.setPathToAffiliations(groupedMemberships, orcid);
        return Response.ok(groupedMemberships).build();
    }

    @Override
    public Response viewMembershipSummary(String orcid, Long putCode) {
        MembershipSummary s = affiliationsManagerReadOnly.getMembershipSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(s);
        ActivityUtils.setPathToActivity(s, orcid);
        sourceUtilsReadOnly.setSourceName(s);
        return Response.ok(s).build();
    }

    @Override
    public Response viewQualification(String orcid, Long putCode) {
        Qualification e = affiliationsManagerReadOnly.getQualificationAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewQualifications(String orcid) {
        List<QualificationSummary> qualifications = affiliationsManagerReadOnly.getQualificationSummaryList(orcid);
        List<QualificationSummary>  publicQualifications = new ArrayList<>();
        for (QualificationSummary summary : qualifications) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                        sourceUtilsReadOnly.setSourceName(summary);
                        publicQualifications.add(summary);
                }
        }
        Qualifications groupedQualifications = new Qualifications(affiliationsManagerReadOnly.groupAffiliations(publicQualifications, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedQualifications);
        ActivityUtils.setPathToAffiliations(groupedQualifications, orcid);
        return Response.ok(groupedQualifications).build();
    }

    @Override
    public Response viewQualificationSummary(String orcid, Long putCode) {
        QualificationSummary s = affiliationsManagerReadOnly.getQualificationSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(s);
        ActivityUtils.setPathToActivity(s, orcid);
        sourceUtilsReadOnly.setSourceName(s);
        return Response.ok(s).build();
    }

    @Override
    public Response viewService(String orcid, Long putCode) {
        Service e = affiliationsManagerReadOnly.getServiceAffiliation(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewServices(String orcid) {
        List<ServiceSummary> services = affiliationsManagerReadOnly.getServiceSummaryList(orcid);
        List<ServiceSummary> publicServices = new ArrayList<>();
        for (ServiceSummary summary : services) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                        sourceUtilsReadOnly.setSourceName(summary);
                        publicServices.add(summary);
                }
        }
        Services groupedServices = new Services(affiliationsManagerReadOnly.groupAffiliations(publicServices, true));
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(groupedServices);
        ActivityUtils.setPathToAffiliations(groupedServices, orcid);
        return Response.ok(groupedServices).build();
    }

    @Override
    public Response viewServiceSummary(String orcid, Long putCode) {
        ServiceSummary s = affiliationsManagerReadOnly.getServiceSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(s);
        ActivityUtils.setPathToActivity(s, orcid);
        sourceUtilsReadOnly.setSourceName(s);
        return Response.ok(s).build();
    }
    
    private void checkProfileStatus(String orcid) {
        try {
            orcidSecurityManager.checkProfile(orcid);
        } catch(DeactivatedException e) {
            // Ignore the DeactivatedException since we should be able to return the empty element
        }
    }

    @Override
    public Response viewResearchResource(String orcid, Long putCode) {
        ResearchResource e = researchResourceManagerReadOnly.getResearchResource(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewResearchResources(String orcid) {
        List<ResearchResourceSummary> researchResources = researchResourceManagerReadOnly.getResearchResourceSummaryList(orcid);
        List<ResearchResourceSummary> publicResearchResources = new ArrayList<>();
        for (ResearchResourceSummary summary : researchResources) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        publicResearchResources.add(summary);
                }
        }
        ResearchResources rr = researchResourceManagerReadOnly.groupResearchResources(publicResearchResources, true);
        Api3_0_RC1LastModifiedDatesHelper.calculateLastModified(rr);
        ActivityUtils.setPathToResearchResources(rr, orcid);
        sourceUtilsReadOnly.setSourceName(rr);
        return Response.ok(rr).build();
    }

    @Override
    public Response viewResearchResourceSummary(String orcid, Long putCode) {
        ResearchResourceSummary e = researchResourceManagerReadOnly.getResearchResourceSummary(orcid, putCode);
        publicAPISecurityManagerV3.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

}
