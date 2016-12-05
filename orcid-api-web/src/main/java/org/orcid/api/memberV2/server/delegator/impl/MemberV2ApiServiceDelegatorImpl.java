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
import java.security.AccessControlException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.exception.MismatchedPutCodeException;
import org.orcid.core.exception.OrcidUnauthorizedException;
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
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.core.utils.SourceUtils;
import org.orcid.core.version.impl.Api2_0_rc3_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc3.Filterable;
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
import org.orcid.jaxb.model.record_rc3.Email;
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
import org.orcid.jaxb.model.record_rc3.WorkBulk;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;

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
        implements
        MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> {

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

    @Override
    public Response viewRecord(String orcid) {
        Record record = null;
        try {
            checkClientAccessAndScope(orcid, ScopePathType.READ_LIMITED);
            record = visibilityFilter.filter(recordManager.getRecord(orcid), orcid);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public activities.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                record = recordManager.getPublicRecord(orcid);
            } else {
                throw e;
            }
        }

        if (record.getPerson() != null) {
            ElementUtils.setPathToPerson(record.getPerson(), orcid);
            sourceUtils.setSourceName(record.getPerson());
        }
        if (record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            ActivityUtils.setPathToActivity(record.getActivitiesSummary(), orcid);
            sourceUtils.setSourceName(record.getActivitiesSummary());
        }

        return Response.ok(record).build();
    }

    @Override
    public Response viewActivities(String orcid) {
        ActivitiesSummary as = null;
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ACTIVITIES_READ_LIMITED);
            as = visibilityFilter.filter(profileEntityManager.getActivitiesSummary(orcid), orcid);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public activities.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                as = profileEntityManager.getPublicActivitiesSummary(orcid);
            } else {
                throw e;
            }
        }
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        sourceUtils.setSourceName(as);
        return Response.ok(as).build();
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        long lastModifiedTime = getLastModifiedTime(orcid);
        Work w = workManager.getWork(orcid, putCode, lastModifiedTime);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_WORKS_READ_LIMITED, w);
        ActivityUtils.cleanEmptyFields(w);
        ActivityUtils.setPathToActivity(w, orcid);
        sourceUtils.setSourceName(w);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorks(String orcid) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_WORKS_READ_LIMITED);
        List<WorkSummary> worksList = workManager.getWorksSummaryList(orcid, getLastModifiedTime(orcid));
        Works works = workManager.groupWorks(worksList, false);
        works = visibilityFilter.filter(works, orcid);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(works);
        ActivityUtils.cleanEmptyFields(works);
        ActivityUtils.setPathToWorks(works, orcid);
        sourceUtils.setSourceName(works);
        return Response.ok(works).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        long lastModifiedTime = getLastModifiedTime(orcid);
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode, lastModifiedTime);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_WORKS_READ_LIMITED, ws);
        ActivityUtils.cleanEmptyFields(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        sourceUtils.setSourceName(ws);
        return Response.ok(ws).build();
    }

    @Override
    public Response createWork(String orcid, Work work) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_WORKS_CREATE);
        Work w = workManager.createWork(orcid, work, true);
        sourceUtils.setSourceName(w);
        try {
            return Response.created(new URI(String.valueOf(w.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creatework_response.exception"), e);
        }
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Work work) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_WORKS_UPDATE);
        if (!putCode.equals(work.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(work.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Work w = workManager.updateWork(orcid, work, true);
        sourceUtils.setSourceName(w);
        return Response.ok(w).build();
    }

    @Override
    public Response createWorks(String orcid, WorkBulk works) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_WORKS_CREATE);
        works = workManager.createWorks(orcid, works);
        sourceUtils.setSourceName(works);
        return Response.ok(works).build();
    }

    @Override
    public Response deleteWork(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_WORKS_UPDATE);
        workManager.checkSourceAndRemoveWork(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.FUNDING_READ_LIMITED, f);
        ActivityUtils.setPathToActivity(f, orcid);
        sourceUtils.setSourceName(f);
        return Response.ok(f).build();
    }

    @Override
    public Response viewFundings(String orcid) {
        checkClientAccessAndScope(orcid, ScopePathType.FUNDING_READ_LIMITED);
        List<FundingSummary> fundingSummaries = profileFundingManager.getFundingSummaryList(orcid, getLastModifiedTime(orcid));
        Fundings fundings = profileFundingManager.groupFundings(fundingSummaries, false);
        fundings = visibilityFilter.filter(fundings, orcid);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(fundings);
        ActivityUtils.setPathToFundings(fundings, orcid);
        sourceUtils.setSourceName(fundings);
        return Response.ok(fundings).build();
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.FUNDING_READ_LIMITED, fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        sourceUtils.setSourceName(fs);
        return Response.ok(fs).build();
    }

    @Override
    public Response createFunding(String orcid, Funding funding) {
        checkClientAccessAndScope(orcid, ScopePathType.FUNDING_CREATE);
        Funding f = profileFundingManager.createFunding(orcid, funding, true);
        sourceUtils.setSourceName(f);
        try {
            return Response.created(new URI(String.valueOf(f.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createfunding_response.exception"), e);
        }
    }

    @Override
    public Response updateFunding(String orcid, Long putCode, Funding funding) {
        checkClientAccessAndScope(orcid, ScopePathType.FUNDING_UPDATE);
        if (!putCode.equals(funding.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(funding.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Funding f = profileFundingManager.updateFunding(orcid, funding, true);
        sourceUtils.setSourceName(f);
        return Response.ok(f).build();
    }

    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.FUNDING_UPDATE);
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducations(String orcid) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        List<EducationSummary> educationsList = affiliationsManager.getEducationSummaryList(orcid, getLastModifiedTime(orcid));

        Educations educations = new Educations();
        for (EducationSummary summary : educationsList) {
            try {
                checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, summary);
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtils.setSourceName(summary);
                educations.getSummaries().add(summary);
            } catch (Exception e) {
                // Just ignore this element
            }
        }

        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(educations);
        return Response.ok(educations).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtils.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response createEducation(String orcid, Education education) {
        checkClientAccessAndScope(orcid, ScopePathType.AFFILIATIONS_CREATE);
        Education e = affiliationsManager.createEducationAffiliation(orcid, education, true);
        sourceUtils.setSourceName(e);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createeducation_response.exception"), ex);
        }
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Education education) {
        checkClientAccessAndScope(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        if (!putCode.equals(education.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(education.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Education e = affiliationsManager.updateEducationAffiliation(orcid, education, true);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployments(String orcid) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        List<EmploymentSummary> employmentsList = affiliationsManager.getEmploymentSummaryList(orcid, getLastModifiedTime(orcid));
        Employments employments = new Employments();
        for (EmploymentSummary summary : employmentsList) {
            try {
                checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, summary);
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtils.setSourceName(summary);
                employments.getSummaries().add(summary);
            } catch (Exception e) {
                // Just ignore this element
            }
        }

        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(employments);
        return Response.ok(employments).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtils.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response createEmployment(String orcid, Employment employment) {
        checkClientAccessAndScope(orcid, ScopePathType.AFFILIATIONS_CREATE);
        Employment e = affiliationsManager.createEmploymentAffiliation(orcid, employment, true);
        sourceUtils.setSourceName(e);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createemployment_response.exception"), ex);
        }
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Employment employment) {
        checkClientAccessAndScope(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        if (!putCode.equals(employment.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(employment.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Employment e = affiliationsManager.updateEmploymentAffiliation(orcid, employment, true);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        affiliationsManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        PeerReview p = peerReviewManager.getPeerReview(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.PEER_REVIEW_READ_LIMITED, p);
        ActivityUtils.setPathToActivity(p, orcid);
        sourceUtils.setSourceName(p);
        return Response.ok(p).build();
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        checkClientAccessAndScope(orcid, ScopePathType.PEER_REVIEW_READ_LIMITED);
        List<PeerReviewSummary> peerReviewList = peerReviewManager.getPeerReviewSummaryList(orcid, getLastModifiedTime(orcid));
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewList, false);
        peerReviews = visibilityFilter.filter(peerReviews, orcid);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(peerReviews);
        ActivityUtils.setPathToPeerReviews(peerReviews, orcid);
        sourceUtils.setSourceName(peerReviews);
        return Response.ok(peerReviews).build();
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        PeerReviewSummary ps = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.PEER_REVIEW_READ_LIMITED, ps);
        ActivityUtils.setPathToActivity(ps, orcid);
        sourceUtils.setSourceName(ps);
        return Response.ok(ps).build();
    }

    @Override
    public Response createPeerReview(String orcid, PeerReview peerReview) {
        checkClientAccessAndScope(orcid, ScopePathType.PEER_REVIEW_CREATE);
        PeerReview newPeerReview = peerReviewManager.createPeerReview(orcid, peerReview, true);
        sourceUtils.setSourceName(newPeerReview);
        try {
            return Response.created(new URI(String.valueOf(newPeerReview.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createpeerreview_response.exception"), ex);
        }
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview) {
        checkClientAccessAndScope(orcid, ScopePathType.PEER_REVIEW_UPDATE);
        if (!putCode.equals(peerReview.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(peerReview.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        PeerReview updatedPeerReview = peerReviewManager.updatePeerReview(orcid, peerReview, true);
        sourceUtils.setSourceName(updatedPeerReview);
        return Response.ok(updatedPeerReview).build();
    }

    @Override
    public Response deletePeerReview(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.PEER_REVIEW_UPDATE);
        peerReviewManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        GroupIdRecord record = groupIdRecordManager.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
        GroupIdRecord newRecord = groupIdRecordManager.createGroupIdRecord(groupIdRecord);
        try {
            return Response.created(new URI(String.valueOf(newRecord.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creategroupidrecord_response.exception"), ex);
        }
    }

    @Override
    public Response updateGroupIdRecord(GroupIdRecord groupIdRecord, Long putCode) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
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
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
        groupIdRecordManager.deleteGroupIdRecord(putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        GroupIdRecords records = groupIdRecordManager.getGroupIdRecords(pageSize, pageNum);
        return Response.ok(records).build();
    }
    
    @Override
    public Response findGroupIdRecordByName(String name) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        Optional<GroupIdRecord> record = groupIdRecordManager.findGroupIdRecordByName(name);
        if (record.isPresent())
            return Response.ok(record.get()).build();
        throw new NotFoundException();
        //return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * BIOGRAPHY ELEMENTS
     * */
    @SuppressWarnings("unchecked")
    @Override
    public Response viewResearcherUrls(String orcid) {
        ResearcherUrls researcherUrls = null;
        long lastModifiedTime = getLastModifiedTime(orcid);
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            researcherUrls = researcherUrlManager.getResearcherUrls(orcid, lastModifiedTime);
            researcherUrls.setResearcherUrls((List<ResearcherUrl>) visibilityFilter.filter(researcherUrls.getResearcherUrls(), orcid));
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                researcherUrls = researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        sourceUtils.setSourceName(researcherUrls);
        return Response.ok(researcherUrls).build();
    }

    public Response viewResearcherUrl(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrl(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        sourceUtils.setSourceName(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, ResearcherUrl researcherUrl) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(researcherUrl.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(researcherUrl.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        ResearcherUrl updatedResearcherUrl = researcherUrlManager.updateResearcherUrl(orcid, researcherUrl, true);
        ElementUtils.setPathToResearcherUrl(updatedResearcherUrl, orcid);
        sourceUtils.setSourceName(updatedResearcherUrl);
        return Response.ok(updatedResearcherUrl).build();
    }

    @Override
    public Response createResearcherUrl(String orcid, ResearcherUrl researcherUrl) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        researcherUrl = researcherUrlManager.createResearcherUrl(orcid, researcherUrl, true);
        sourceUtils.setSourceName(researcherUrl);
        try {
            return Response.created(new URI(String.valueOf(researcherUrl.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        researcherUrlManager.deleteResearcherUrl(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewEmails(String orcid) {
        Emails emails = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            emails = emailManager.getEmails(orcid, lastModified);
            emails.setEmails((List<Email>) visibilityFilter.filter(emails.getEmails(), orcid));
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                emails = emailManager.getPublicEmails(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToEmail(emails, orcid);
        sourceUtils.setSourceName(emails);
        return Response.ok(emails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewOtherNames(String orcid) {
        OtherNames otherNames = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            otherNames = otherNameManager.getOtherNames(orcid, lastModified);
            List<OtherName> allOtherNames = otherNames.getOtherNames();
            List<OtherName> filterdOtherNames = (List<OtherName>) visibilityFilter.filter(allOtherNames, orcid);
            otherNames.setOtherNames(filterdOtherNames);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                otherNames = otherNameManager.getPublicOtherNames(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        sourceUtils.setSourceName(otherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManager.getOtherName(orcid, putCode);
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        sourceUtils.setSourceName(otherName);
        return Response.ok(otherName).build();
    }

    @Override
    public Response createOtherName(String orcid, OtherName otherName) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        otherName = otherNameManager.createOtherName(orcid, otherName, true);
        sourceUtils.setSourceName(otherName);
        try {
            return Response.created(new URI(String.valueOf(otherName.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, OtherName otherName) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(otherName.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(otherName.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        OtherName updatedOtherName = otherNameManager.updateOtherName(orcid, putCode, otherName, true);
        ElementUtils.setPathToOtherName(updatedOtherName, orcid);
        sourceUtils.setSourceName(updatedOtherName);
        return Response.ok(updatedOtherName).build();
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        otherNameManager.deleteOtherName(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewExternalIdentifiers(String orcid) {
        PersonExternalIdentifiers extIds = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            extIds = externalIdentifierManager.getExternalIdentifiers(orcid, lastModified);
            List<PersonExternalIdentifier> allExtIds = extIds.getExternalIdentifiers();
            List<PersonExternalIdentifier> filteredExtIds = (List<PersonExternalIdentifier>) visibilityFilter.filter(allExtIds, orcid);
            extIds.setExternalIdentifiers(filteredExtIds);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                extIds = externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        sourceUtils.setSourceName(extIds);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        PersonExternalIdentifier extId = externalIdentifierManager.getExternalIdentifier(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtils.setSourceName(extId);
        return Response.ok(extId).build();
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, PersonExternalIdentifier externalIdentifier) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        if (!putCode.equals(externalIdentifier.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(externalIdentifier.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        PersonExternalIdentifier extId = externalIdentifierManager.updateExternalIdentifier(orcid, externalIdentifier, true);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtils.setSourceName(extId);
        return Response.ok(extId).build();
    }

    @Override
    public Response createExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        externalIdentifier = externalIdentifierManager.createExternalIdentifier(orcid, externalIdentifier, true);
        try {
            return Response.created(new URI(String.valueOf(externalIdentifier.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        externalIdentifierManager.deleteExternalIdentifier(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewKeywords(String orcid) {
        Keywords keywords = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            keywords = keywordsManager.getKeywords(orcid, lastModified);
            List<Keyword> allKeywords = keywords.getKeywords();
            List<Keyword> filterdKeywords = (List<Keyword>) visibilityFilter.filter(allKeywords, orcid);
            keywords.setKeywords(filterdKeywords);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                keywords = keywordsManager.getPublicKeywords(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToKeywords(keywords, orcid);
        sourceUtils.setSourceName(keywords);
        return Response.ok(keywords).build();
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        Keyword keyword = keywordsManager.getKeyword(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtils.setSourceName(keyword);
        return Response.ok(keyword).build();
    }

    @Override
    public Response createKeyword(String orcid, Keyword keyword) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        keyword = keywordsManager.createKeyword(orcid, keyword, true);
        sourceUtils.setSourceName(keyword);
        try {
            return Response.created(new URI(String.valueOf(keyword.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Keyword keyword) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(keyword.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(keyword.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        keyword = keywordsManager.updateKeyword(orcid, putCode, keyword, true);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtils.setSourceName(keyword);
        return Response.ok(keyword).build();
    }

    @Override
    public Response deleteKeyword(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        keywordsManager.deleteKeyword(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewAddresses(String orcid) {
        Addresses addresses = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            addresses = addressManager.getAddresses(orcid, lastModified);
            List<Address> allAddresses = addresses.getAddress();
            List<Address> filteredAddresses = (List<Address>) visibilityFilter.filter(allAddresses, orcid);
            addresses.setAddress(filteredAddresses);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                addresses = addressManager.getPublicAddresses(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToAddresses(addresses, orcid);
        sourceUtils.setSourceName(addresses);
        return Response.ok(addresses).build();
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        orcidSecurityManager.checkClientCanAccessRecord(orcid);
        Address address = addressManager.getAddress(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, address);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtils.setSourceName(address);
        return Response.ok(address).build();
    }

    @Override
    public Response createAddress(String orcid, Address address) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        address = addressManager.createAddress(orcid, address, true);
        sourceUtils.setSourceName(address);
        try {
            return Response.created(new URI(String.valueOf(address.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Address address) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(address.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(address.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        address = addressManager.updateAddress(orcid, putCode, address, true);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtils.setSourceName(address);
        return Response.ok(address).build();
    }

    @Override
    public Response deleteAddress(String orcid, Long putCode) {
        checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_UPDATE);
        addressManager.deleteAddress(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewBiography(String orcid) {
        Biography bio = null;
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            bio = biographyManager.getBiography(orcid);
            orcidSecurityManager.checkBiographicalVisibility(bio, orcid);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the list of
            // public elements.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                bio = biographyManager.getPublicBiography(orcid);
                if (bio == null) {
                    throw new OrcidUnauthorizedException("The biography is not public");
                }
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = null;
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            personalDetails = personalDetailsManager.getPersonalDetails(orcid);
            personalDetails = visibilityFilter.filter(personalDetails, orcid);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the public
            // element.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                personalDetails = personalDetailsManager.getPublicPersonalDetails(orcid);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        sourceUtils.setSourceName(personalDetails);
        return Response.ok(personalDetails).build();
    }

    @Override
    public Response viewPerson(String orcid) {
        Person person = null;
        try {
            checkClientAccessAndScope(orcid, ScopePathType.ORCID_BIO_READ_LIMITED);
            person = profileEntityManager.getPersonDetails(orcid);
            person = visibilityFilter.filter(person, orcid);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, return him the public
            // element.
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                person = profileEntityManager.getPublicPersonDetails(orcid);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToPerson(person, orcid);
        sourceUtils.setSourceName(person);
        return Response.ok(person).build();
    }

    private void checkPermissionsOnElement(String orcid, ScopePathType requiredScope, Filterable element) {
        try {
            orcidSecurityManager.checkScopes(requiredScope);
            orcidSecurityManager.checkVisibility(element, orcid);
        } catch (AccessControlException e) {
            // If the user have the READ_PUBLIC scope, check that the element is
            // public
            if (orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                orcidSecurityManager.checkIsPublic(element);
            } else {
                throw e;
            }
        }
    }

    private void checkClientAccessAndScope(String orcid, ScopePathType requiredScope) {
        if (orcid != null) {
            orcidSecurityManager.checkClientCanAccessRecord(orcid);
        }
        orcidSecurityManager.checkScopes(requiredScope);
    }
}
