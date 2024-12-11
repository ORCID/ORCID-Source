package org.orcid.api.memberV2.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.MismatchedPutCodeException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNoBioException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.ContributorUtils;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.SourceUtils;
import org.orcid.core.version.impl.Api2_0_LastModifiedDatesHelper;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.Source;
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
import org.orcid.jaxb.model.record_v2.Email;
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
import org.orcid.jaxb.model.record_v2.SourceAware;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.search_v2.Search;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.springframework.context.MessageSource;
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
public class MemberV2ApiServiceDelegatorImpl implements
        MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> {
    
    // deliberately loose so we can recognise anything that claims to be an issn record
    private static Pattern issnGroupTypePattern = Pattern.compile("^issn:(.*)$");
    
    // Managers that goes to the primary database
    @Resource
    private WorkManager workManager;

    @Resource
    private ProfileFundingManager profileFundingManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private AffiliationsManager affiliationsManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private GroupIdRecordManager groupIdRecordManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileKeywordManager profileKeywordManager;

    @Resource
    private AddressManager addressManager;

    @Resource
    private SourceUtils sourceUtils;

    @Resource
    private ContributorUtils contributorUtils;

    @Resource
    private OrcidSearchManager orcidSearchManager;

    // Managers that goes to the replication database
    // Activities managers
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

    // Person managers
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

    // Record manager
    @Resource
    private RecordManagerReadOnly recordManagerReadOnly;

    // Other managers
    @Resource
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Resource
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Resource
    private ClientManagerReadOnly clientManagerReadOnly;

    @Resource
    private MessageSource messageSource;
    
    @Resource
    private ApiUtils apiUtils;

    @Resource
    private EmailDomainManager emailDomainManager;

    @Resource
    private SourceEntityUtils sourceEntityUtils;

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    public Response viewRecord(String orcid) {
        Record record = recordManagerReadOnly.getRecord(orcid);
        orcidSecurityManager.checkAndFilter(orcid, record);
        if (record.getPerson() != null) {
            emailDomainManager.processProfessionalEmailsForV2API(record.getPerson().getEmails());
            sourceUtils.setSourceName(record.getPerson());
        }
        if (record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            sourceUtils.setSourceName(record.getActivitiesSummary());
        }
        ElementUtils.setPathToRecord(record, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(record);
        return Response.ok(record).build();
    }

    @Override
    public Response viewActivities(String orcid) {
        ActivitiesSummary as = activitiesSummaryManagerReadOnly.getActivitiesSummary(orcid);
        orcidSecurityManager.checkAndFilter(orcid, as);
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(as);
        sourceUtils.setSourceName(as);
        return Response.ok(as).build();
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        Work w = workManagerReadOnly.getWork(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, w, ScopePathType.ORCID_WORKS_READ_LIMITED);
        contributorUtils.filterContributorPrivateData(w);
        ActivityUtils.cleanEmptyFields(w);
        ActivityUtils.setPathToActivity(w, orcid);
        sourceUtils.setSourceName(w);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorks(String orcid) {
        List<WorkSummary> worksList = workManagerReadOnly.getWorksSummaryList(orcid);
        // Lets copy the list so we don't modify the cached collection
        List<WorkSummary> filteredList = null;
        if (worksList != null) {
            filteredList = new ArrayList<WorkSummary>(worksList);
        }
        worksList = filteredList;

        orcidSecurityManager.checkAndFilter(orcid, worksList, ScopePathType.ORCID_WORKS_READ_LIMITED);
        Works works = workManager.groupWorks(worksList, false);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(works);
        ActivityUtils.cleanEmptyFields(works);
        ActivityUtils.setPathToWorks(works, orcid);
        sourceUtils.setSourceName(works);
        return Response.ok(works).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        WorkSummary ws = workManagerReadOnly.getWorkSummary(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, ws, ScopePathType.ORCID_WORKS_READ_LIMITED);
        ActivityUtils.cleanEmptyFields(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        sourceUtils.setSourceName(ws);
        return Response.ok(ws).build();
    }

    @Override
    public Response createWork(String orcid, Work work) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_UPDATE);
        clearSource(work);
        Work w = workManager.createWork(orcid, work, true);
        sourceUtils.setSourceName(w);
        return apiUtils.buildApiResponse(orcid, "work", String.valueOf(w.getPutCode()), "apiError.creatework_response.exception");
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Work work) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_WORKS_UPDATE);
        if (!putCode.equals(work.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(work.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(work);
        Work w = workManager.updateWork(orcid, work, true);
        sourceUtils.setSourceName(w);
        return Response.ok(w).build();
    }

    @Override
    public Response createWorks(String orcid, WorkBulk works) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_UPDATE);
        if (works != null) {
            for (int i = 0; i < works.getBulk().size(); i++) {
                if (Work.class.isAssignableFrom(works.getBulk().get(i).getClass())) {
                    Work work = (Work) works.getBulk().get(i);
                    clearSource(work);
                }
            }
        }
        works = workManager.createWorks(orcid, works);
        sourceUtils.setSourceName(works);
        return Response.ok(works).build();
    }

    @Override
    public Response deleteWork(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_WORKS_UPDATE);
        workManager.checkSourceAndRemoveWork(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManagerReadOnly.getFunding(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, f, ScopePathType.FUNDING_READ_LIMITED);
        ActivityUtils.setPathToActivity(f, orcid);
        ActivityUtils.cleanEmptyFields(f);
        sourceUtils.setSourceName(f);
        contributorUtils.filterContributorPrivateData(f);
        return Response.ok(f).build();
    }

    @Override
    public Response viewFundings(String orcid) {
        List<FundingSummary> fundingSummaries = profileFundingManagerReadOnly.getFundingSummaryList(orcid);

        // Lets copy the list so we don't modify the cached collection
        List<FundingSummary> filteredList = null;
        if (fundingSummaries != null) {
            filteredList = new ArrayList<FundingSummary>(fundingSummaries);
        }
        fundingSummaries = filteredList;

        orcidSecurityManager.checkAndFilter(orcid, fundingSummaries, ScopePathType.FUNDING_READ_LIMITED);
        Fundings fundings = profileFundingManager.groupFundings(fundingSummaries, false);
        ActivityUtils.setPathToFundings(fundings, orcid);
        ActivityUtils.cleanOrganizationEmptyFields(fundingSummaries);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(fundings);
        sourceUtils.setSourceName(fundings);
        return Response.ok(fundings).build();
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManagerReadOnly.getSummary(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, fs, ScopePathType.FUNDING_READ_LIMITED);
        ActivityUtils.setPathToActivity(fs, orcid);
        ActivityUtils.cleanOrganizationEmptyFields(fs);
        sourceUtils.setSourceName(fs);
        return Response.ok(fs).build();
    }

    @Override
    public Response createFunding(String orcid, Funding funding) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_UPDATE);
        clearSource(funding);
        Funding f = profileFundingManager.createFunding(orcid, funding, true);
        sourceUtils.setSourceName(f);
        return apiUtils.buildApiResponse(orcid, "funding", String.valueOf(f.getPutCode()), "apiError.createfunding_response.exception");
    }

    @Override
    public Response updateFunding(String orcid, Long putCode, Funding funding) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.FUNDING_UPDATE);
        if (!putCode.equals(funding.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(funding.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(funding);
        Funding f = profileFundingManager.updateFunding(orcid, funding, true);
        sourceUtils.setSourceName(f);
        return Response.ok(f).build();
    }

    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.FUNDING_UPDATE);
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManagerReadOnly.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, e, ScopePathType.AFFILIATIONS_READ_LIMITED);
        ActivityUtils.setPathToActivity(e, orcid);
        ActivityUtils.cleanOrganizationEmptyFields(e);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducations(String orcid) {
        List<EducationSummary> educationsList = affiliationsManagerReadOnly.getEducationSummaryList(orcid);

        // Lets copy the list so we don't modify the cached collection
        List<EducationSummary> filteredList = null;
        if (educationsList != null) {
            filteredList = new ArrayList<EducationSummary>(educationsList);
        }
        educationsList = filteredList;

        orcidSecurityManager.checkAndFilter(orcid, educationsList, ScopePathType.AFFILIATIONS_READ_LIMITED);
        Educations educations = new Educations(educationsList);
        ActivityUtils.setPathToEducations(educations, orcid);
        sourceUtils.setSourceName(educations);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(educations);
        return Response.ok(educations).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManagerReadOnly.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, es, ScopePathType.AFFILIATIONS_READ_LIMITED);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtils.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response createEducation(String orcid, Education education) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);
        clearSource(education);
        Education e = affiliationsManager.createEducationAffiliation(orcid, education, true);
        sourceUtils.setSourceName(e);
        return apiUtils.buildApiResponse(orcid, "education", String.valueOf(e.getPutCode()), "apiError.createeducation_response.exception");
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Education education) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        if (!putCode.equals(education.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(education.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(education);
        Education e = affiliationsManager.updateEducationAffiliation(orcid, education, true);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManagerReadOnly.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, e, ScopePathType.AFFILIATIONS_READ_LIMITED);
        ActivityUtils.setPathToActivity(e, orcid);
        ActivityUtils.cleanOrganizationEmptyFields(e);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployments(String orcid) {
        List<EmploymentSummary> employmentsList = affiliationsManagerReadOnly.getEmploymentSummaryList(orcid);

        // Lets copy the list so we don't modify the cached collection
        List<EmploymentSummary> filteredList = null;
        if (employmentsList != null) {
            filteredList = new ArrayList<EmploymentSummary>(employmentsList);
        }
        employmentsList = filteredList;

        orcidSecurityManager.checkAndFilter(orcid, employmentsList, ScopePathType.AFFILIATIONS_READ_LIMITED);
        Employments employments = new Employments(employmentsList);
        ActivityUtils.setPathToEmployments(employments, orcid);
        sourceUtils.setSourceName(employments);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(employments);
        return Response.ok(employments).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManagerReadOnly.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, es, ScopePathType.AFFILIATIONS_READ_LIMITED);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtils.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response createEmployment(String orcid, Employment employment) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);
        clearSource(employment);
        Employment e = affiliationsManager.createEmploymentAffiliation(orcid, employment, true);
        sourceUtils.setSourceName(e);
        return apiUtils.buildApiResponse(orcid, "employment", String.valueOf(e.getPutCode()), "apiError.createemployment_response.exception");
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Employment employment) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        if (!putCode.equals(employment.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(employment.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(employment);
        Employment e = affiliationsManager.updateEmploymentAffiliation(orcid, employment, true);
        sourceUtils.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        affiliationsManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview p = peerReviewManagerReadOnly.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, p, ScopePathType.PEER_REVIEW_READ_LIMITED);
        ActivityUtils.setPathToActivity(p, orcid);
        sourceUtils.setSourceName(p);
        return Response.ok(p).build();
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        List<PeerReviewSummary> peerReviewList = peerReviewManagerReadOnly.getPeerReviewSummaryList(orcid);

        // Lets copy the list so we don't modify the cached collection
        List<PeerReviewSummary> filteredList = null;
        if (peerReviewList != null) {
            filteredList = new ArrayList<PeerReviewSummary>(peerReviewList);
        }
        peerReviewList = filteredList;

        orcidSecurityManager.checkAndFilter(orcid, peerReviewList, ScopePathType.PEER_REVIEW_READ_LIMITED);
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewList, false);
        ActivityUtils.setPathToPeerReviews(peerReviews, orcid);
        ActivityUtils.cleanOrganizationEmptyFields(peerReviewList);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(peerReviews);
        sourceUtils.setSourceName(peerReviews);
        return Response.ok(peerReviews).build();
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary ps = peerReviewManagerReadOnly.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, ps, ScopePathType.PEER_REVIEW_READ_LIMITED);
        ActivityUtils.setPathToActivity(ps, orcid);
        ActivityUtils.cleanOrganizationEmptyFields(ps);
        sourceUtils.setSourceName(ps);
        return Response.ok(ps).build();
    }

    @Override
    public Response createPeerReview(String orcid, PeerReview peerReview) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_UPDATE);
        clearSource(peerReview);
        PeerReview newPeerReview = peerReviewManager.createPeerReview(orcid, peerReview, true);
        sourceUtils.setSourceName(newPeerReview);
        return apiUtils.buildApiResponse(orcid, "peer-review", String.valueOf(newPeerReview.getPutCode()), "apiError.createpeerreview_response.exception");
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.PEER_REVIEW_UPDATE);
        if (!putCode.equals(peerReview.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(peerReview.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(peerReview);
        PeerReview updatedPeerReview = peerReviewManager.updatePeerReview(orcid, peerReview, true);
        sourceUtils.setSourceName(updatedPeerReview);
        return Response.ok(updatedPeerReview).build();
    }

    @Override
    public Response deletePeerReview(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.PEER_REVIEW_UPDATE);
        peerReviewManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        GroupIdRecord record = groupIdRecordManagerReadOnly.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
        Matcher matcher = issnGroupTypePattern.matcher(groupIdRecord.getGroupId());
        
        if (!groupIdRecordManager.exists(groupIdRecord.getGroupId()) && matcher.find()) {
            // issn group type
            groupIdRecordManager.createOrcidSourceIssnGroupIdRecord(groupIdRecord.getGroupId(), matcher.group(1));
            throw new DuplicatedGroupIdRecordException();
        }
        // filter out invisible control characters such as \u0098
        groupIdRecord.setName(groupIdRecord.getName().replaceAll("\\p{C}", ""));
        GroupIdRecord newRecord = groupIdRecordManager.createGroupIdRecord(groupIdRecord);
        return apiUtils.buildApiResponse(null, "group-id-record", String.valueOf(newRecord.getPutCode()), "apiError.creategroupidrecord_response.exception");
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
        // filter out invisible control characters such as \u0098
        groupIdRecord.setName(groupIdRecord.getName().replaceAll("\\p{C}", ""));
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
        GroupIdRecords records = groupIdRecordManagerReadOnly.getGroupIdRecords(pageSize, pageNum);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(records);
        return Response.ok(records).build();
    }

    @Override
    public Response findGroupIdRecordByName(String name) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        Optional<GroupIdRecord> record = groupIdRecordManager.findGroupIdRecordByName(name);
        if (record.isPresent())
            return Response.ok(record.get()).build();
        return Response.ok(new GroupIdRecord()).build();
    }
    
    @Override
    public Response findGroupIdRecordByGroupId(String groupId) {
        orcidSecurityManager.checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        Optional<GroupIdRecord> record = groupIdRecordManager.findByGroupId(groupId);
        Matcher matcher = issnGroupTypePattern.matcher(groupId);
        if (record.isPresent()) {
            return Response.ok(record.get()).build();
        } else if (matcher.find()) {
            // issn group type
            return Response.ok(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord(groupId, matcher.group(1))).build();
        }
        return Response.ok(new GroupIdRecord()).build();
    }

    /**
     * BIOGRAPHY ELEMENTS
     */
    @Override
    public Response viewResearcherUrls(String orcid) {
        ResearcherUrls researcherUrls = researcherUrlManagerReadOnly.getResearcherUrls(orcid);

        // Lets copy the list so we don't modify the cached collection
        if (researcherUrls.getResearcherUrls() != null) {
            List<ResearcherUrl> filteredList = new ArrayList<ResearcherUrl>(researcherUrls.getResearcherUrls());
            researcherUrls = new ResearcherUrls();
            researcherUrls.setResearcherUrls(filteredList);
        }

        orcidSecurityManager.checkAndFilter(orcid, researcherUrls.getResearcherUrls(), ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(researcherUrls);
        sourceUtils.setSourceName(researcherUrls);
        return Response.ok(researcherUrls).build();
    }

    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManagerReadOnly.getResearcherUrl(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, researcherUrl, ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        sourceUtils.setSourceName(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, ResearcherUrl researcherUrl) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(researcherUrl.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(researcherUrl.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(researcherUrl);
        ResearcherUrl updatedResearcherUrl = researcherUrlManager.updateResearcherUrl(orcid, researcherUrl, true);
        ElementUtils.setPathToResearcherUrl(updatedResearcherUrl, orcid);
        sourceUtils.setSourceName(updatedResearcherUrl);
        return Response.ok(updatedResearcherUrl).build();
    }

    @Override
    public Response createResearcherUrl(String orcid, ResearcherUrl researcherUrl) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        clearSource(researcherUrl);
        researcherUrl = researcherUrlManager.createResearcherUrl(orcid, researcherUrl, true);
        sourceUtils.setSourceName(researcherUrl);
        return apiUtils.buildApiResponse(orcid, "researcher-urls", String.valueOf(researcherUrl.getPutCode()), "apiError.createelement_response.exception");
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        researcherUrlManager.deleteResearcherUrl(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override
    public Response viewEmails(String orcid) {
        Emails emails = null;

        try {
            // return all emails if client has /email/read-private scope
            orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.EMAIL_READ_PRIVATE);
            
            emails = emailManagerReadOnly.getVerifiedEmails(orcid);
            
            // Lets copy the list so we don't modify the cached collection
            List<Email> filteredList = new ArrayList<Email>(emails.getEmails());
            emails = new Emails();
            emails.setEmails(filteredList);
        } catch (OrcidAccessControlException e) {
            emails = emailManagerReadOnly.getVerifiedEmails(orcid);
            
            // Lets copy the list so we don't modify the cached collection
            List<Email> filteredList = new ArrayList<Email>(emails.getEmails());
            emails = new Emails();
            emails.setEmails(filteredList);

            // Filter just in case client doesn't have the /email/read-private
            // scope
            orcidSecurityManager.checkAndFilter(orcid, emails.getEmails(), ScopePathType.ORCID_BIO_READ_LIMITED);
        }

        emailDomainManager.processProfessionalEmailsForV2API(emails);
        ElementUtils.setPathToEmail(emails, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(emails);
        sourceUtils.setSourceName(emails);
        return Response.ok(emails).build();
    }

    @Override
    public Response viewOtherNames(String orcid) {
        OtherNames otherNames = otherNameManagerReadOnly.getOtherNames(orcid);

        // Lets copy the list so we don't modify the cached collection
        if (otherNames.getOtherNames() != null) {
            List<OtherName> filteredList = new ArrayList<OtherName>(otherNames.getOtherNames());
            otherNames = new OtherNames();
            otherNames.setOtherNames(filteredList);
        }

        orcidSecurityManager.checkAndFilter(orcid, otherNames.getOtherNames(), ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(otherNames);
        sourceUtils.setSourceName(otherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManagerReadOnly.getOtherName(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, otherName, ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToOtherName(otherName, orcid);
        sourceUtils.setSourceName(otherName);
        return Response.ok(otherName).build();
    }

    @Override
    public Response createOtherName(String orcid, OtherName otherName) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        clearSource(otherName);
        otherName = otherNameManager.createOtherName(orcid, otherName, true);
        sourceUtils.setSourceName(otherName);
        return apiUtils.buildApiResponse(orcid, "other-names", String.valueOf(otherName.getPutCode()), "apiError.createelement_response.exception");
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, OtherName otherName) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(otherName.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(otherName.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(otherName);
        OtherName updatedOtherName = otherNameManager.updateOtherName(orcid, putCode, otherName, true);
        ElementUtils.setPathToOtherName(updatedOtherName, orcid);
        sourceUtils.setSourceName(updatedOtherName);
        return Response.ok(updatedOtherName).build();
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        otherNameManager.deleteOtherName(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        PersonExternalIdentifiers extIds = externalIdentifierManagerReadOnly.getExternalIdentifiers(orcid);

        // Lets copy the list so we don't modify the cached collection
        if (extIds.getExternalIdentifiers() != null) {
            List<PersonExternalIdentifier> filteredList = new ArrayList<PersonExternalIdentifier>(extIds.getExternalIdentifiers());
            extIds = new PersonExternalIdentifiers();
            extIds.setExternalIdentifiers(filteredList);
        }

        orcidSecurityManager.checkAndFilter(orcid, extIds.getExternalIdentifiers(), ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(extIds);
        sourceUtils.setSourceName(extIds);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        PersonExternalIdentifier extId = externalIdentifierManagerReadOnly.getExternalIdentifier(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, extId, ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtils.setSourceName(extId);
        return Response.ok(extId).build();
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, PersonExternalIdentifier externalIdentifier) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        if (!putCode.equals(externalIdentifier.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(externalIdentifier.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(externalIdentifier);
        PersonExternalIdentifier extId = externalIdentifierManager.updateExternalIdentifier(orcid, externalIdentifier, true);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtils.setSourceName(extId);
        return Response.ok(extId).build();
    }

    @Override
    public Response createExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        clearSource(externalIdentifier);
        externalIdentifier = externalIdentifierManager.createExternalIdentifier(orcid, externalIdentifier, true);
        return apiUtils.buildApiResponse(orcid, "external-identifiers", String.valueOf(externalIdentifier.getPutCode()), "apiError.createelement_response.exception");
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        externalIdentifierManager.deleteExternalIdentifier(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override
    public Response viewKeywords(String orcid) {
        Keywords keywords = profileKeywordManagerReadOnly.getKeywords(orcid);

        // Lets copy the list so we don't modify the cached collection
        if (keywords.getKeywords() != null) {
            List<Keyword> filteredList = new ArrayList<Keyword>(keywords.getKeywords());
            keywords = new Keywords();
            keywords.setKeywords(filteredList);
        }

        orcidSecurityManager.checkAndFilter(orcid, keywords.getKeywords(), ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToKeywords(keywords, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(keywords);
        sourceUtils.setSourceName(keywords);
        return Response.ok(keywords).build();
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = profileKeywordManagerReadOnly.getKeyword(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, keyword, ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtils.setSourceName(keyword);
        return Response.ok(keyword).build();
    }

    @Override
    public Response createKeyword(String orcid, Keyword keyword) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        clearSource(keyword);
        keyword = profileKeywordManager.createKeyword(orcid, keyword, true);
        sourceUtils.setSourceName(keyword);
        return apiUtils.buildApiResponse(orcid, "keywords", String.valueOf(keyword.getPutCode()), "apiError.createelement_response.exception");
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Keyword keyword) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(keyword.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(keyword.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(keyword);
        keyword = profileKeywordManager.updateKeyword(orcid, putCode, keyword, true);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtils.setSourceName(keyword);
        return Response.ok(keyword).build();
    }

    @Override
    public Response deleteKeyword(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        profileKeywordManager.deleteKeyword(orcid, putCode, true);
        return Response.noContent().build();
    }

    @Override
    public Response viewAddresses(String orcid) {
        Addresses addresses = addressManagerReadOnly.getAddresses(orcid);

        // Lets copy the list so we don't modify the cached collection
        if (addresses.getAddress() != null) {
            List<Address> filteredAddresses = new ArrayList<Address>(addresses.getAddress());
            addresses = new Addresses();
            addresses.setAddress(filteredAddresses);
        }

        orcidSecurityManager.checkAndFilter(orcid, addresses.getAddress(), ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToAddresses(addresses, orcid);
        // Set the latest last modified
        Api2_0_LastModifiedDatesHelper.calculateLastModified(addresses);
        sourceUtils.setSourceName(addresses);
        return Response.ok(addresses).build();
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManagerReadOnly.getAddress(orcid, putCode);
        orcidSecurityManager.checkAndFilter(orcid, address, ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtils.setSourceName(address);
        return Response.ok(address).build();
    }

    @Override
    public Response createAddress(String orcid, Address address) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        clearSource(address);
        address = addressManager.createAddress(orcid, address, true);
        sourceUtils.setSourceName(address);
        return apiUtils.buildApiResponse(orcid, "address", String.valueOf(address.getPutCode()), "apiError.createelement_response.exception");
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Address address) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        if (!putCode.equals(address.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(address.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        clearSource(address);
        address = addressManager.updateAddress(orcid, putCode, address, true);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtils.setSourceName(address);
        return Response.ok(address).build();
    }

    @Override
    public Response deleteAddress(String orcid, Long putCode) {
        orcidSecurityManager.checkClientAccessAndScopes(orcid, ScopePathType.ORCID_BIO_UPDATE);
        addressManager.deleteAddress(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewBiography(String orcid) {
        Biography bio = biographyManagerReadOnly.getBiography(orcid);
        if (bio == null) {
            throw new OrcidNoBioException();
        }
        orcidSecurityManager.checkAndFilter(orcid, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManagerReadOnly.getPersonalDetails(orcid);
        orcidSecurityManager.checkAndFilter(orcid, personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(personalDetails);
        sourceUtils.setSourceName(personalDetails);
        return Response.ok(personalDetails).build();
    }

    @Override
    public Response viewPerson(String orcid) {
        Person person = personDetailsManagerReadOnly.getPersonDetails(orcid);
        orcidSecurityManager.checkAndFilter(orcid, person);
        emailDomainManager.processProfessionalEmailsForV2API(person.getEmails());
        ElementUtils.setPathToPerson(person, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(person);
        sourceUtils.setSourceName(person);
        return Response.ok(person).build();
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> solrParams) {
        orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
        validateSearchParams(solrParams);
        Search search = orcidSearchManager.findOrcidIds(solrParams);
        return Response.ok(search).build();
    }

    @Override
    public Response viewBulkWorks(String orcid, String putCodes) {
        if (!profileEntityManager.orcidExists(orcid)) {
            throw new OrcidNoResultException("No such record: " + orcid);
        }
        
        WorkBulk workBulk = workManagerReadOnly.findWorkBulk(orcid, putCodes);
        orcidSecurityManager.checkAndFilter(orcid, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        contributorUtils.filterContributorPrivateData(workBulk);
        ActivityUtils.cleanEmptyFields(workBulk);
        sourceUtils.setSourceName(workBulk);
        return Response.ok(workBulk).build();
    }

    private void validateSearchParams(Map<String, List<String>> queryMap) {
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
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_invalid_search_rows.exception"));
            }
        } else {
            // Set the default number of results
            queryMap.put("rows", Arrays.asList(String.valueOf(OrcidSearchManager.DEFAULT_SEARCH_ROWS)));
        }
    }

    @Override
    public Response viewClient(String clientId) {
        orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
        ClientSummary client = clientManagerReadOnly.getSummary(clientId);
        return Response.ok(client).build();
    }

    private void clearSource(SourceAware element) {
        element.setSource(null);
    }

}
