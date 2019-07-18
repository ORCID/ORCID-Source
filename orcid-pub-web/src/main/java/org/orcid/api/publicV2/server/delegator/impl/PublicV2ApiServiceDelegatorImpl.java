package org.orcid.api.publicV2.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.common.writer.citeproc.WorkToCiteprocTranslator;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.api.publicV2.server.security.PublicAPISecurityManagerV2;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.SearchStartParameterLimitExceededException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.RecordManager;
import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
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
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.utils.ContributorUtils;
import org.orcid.core.utils.SourceUtils;
import org.orcid.core.version.impl.Api2_0_LastModifiedDatesHelper;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
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
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.search_v2.Search;
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
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Resource
    private RecordManagerReadOnly recordManagerReadOnly;

    // Other managers
    @Resource
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Resource
    private SourceUtils sourceUtilsReadOnly;

    @Resource
    private ContributorUtils contributorUtilsReadOnly;

    @Resource
    private RecordManager recordManager;

    @Resource
    private SourceUtils sourceUtils;

    @Resource
    private OrcidSearchManager orcidSearchManager;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private PublicAPISecurityManagerV2 publicAPISecurityManagerV2;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OpenIDConnectKeyService openIDConnectKeyService;
    
    @Resource
    private ClientManagerReadOnly clientManagerReadOnly;
    
    @Resource
    private RecordNameManagerReadOnly recordNameManagerReadOnly;
    
    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

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
        ActivitiesSummary as = activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(orcid);
        publicAPISecurityManagerV2.filter(as);
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(as);
        sourceUtilsReadOnly.setSourceName(as);
        return Response.ok(as).build();
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        Work w = workManagerReadOnly.getWork(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(w);
        contributorUtilsReadOnly.filterContributorPrivateData(w);        
        ActivityUtils.cleanEmptyFields(w);
        ActivityUtils.setPathToActivity(w, orcid);
        sourceUtilsReadOnly.setSourceName(w);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorks(String orcid) {
        List<WorkSummary> works = workManagerReadOnly.getWorksSummaryList(orcid);
        Works publicWorks = workManagerReadOnly.groupWorks(works, true);
        publicAPISecurityManagerV2.filter(publicWorks);
        ActivityUtils.cleanEmptyFields(publicWorks);
        ActivityUtils.setPathToWorks(publicWorks, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicWorks);
        sourceUtilsReadOnly.setSourceName(publicWorks);
        return Response.ok(publicWorks).build();
    }

    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        Work w = (Work) this.viewWork(orcid, putCode).getEntity();
        String creditName = recordNameManagerReadOnly.fetchDisplayablePublicName(orcid);
        WorkToCiteprocTranslator tran = new WorkToCiteprocTranslator();
        CSLItemData item = tran.toCiteproc(w, creditName, true);
        return Response.ok(item).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        WorkSummary ws = workManagerReadOnly.getWorkSummary(orcid, putCode);
        ActivityUtils.cleanEmptyFields(ws);
        publicAPISecurityManagerV2.checkIsPublic(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        sourceUtilsReadOnly.setSourceName(ws);
        return Response.ok(ws).build();
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManagerReadOnly.getFunding(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(f);
        ActivityUtils.setPathToActivity(f, orcid);
        ActivityUtils.cleanEmptyFields(f);
        sourceUtilsReadOnly.setSourceName(f);
        contributorUtilsReadOnly.filterContributorPrivateData(f);
        return Response.ok(f).build();
    }

    @Override
    public Response viewFundings(String orcid) {
        List<FundingSummary> fundings = profileFundingManagerReadOnly.getFundingSummaryList(orcid);
        Fundings publicFundings = profileFundingManagerReadOnly.groupFundings(fundings, true);
        publicAPISecurityManagerV2.filter(publicFundings);
        ActivityUtils.setPathToFundings(publicFundings, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicFundings);
        sourceUtilsReadOnly.setSourceName(publicFundings);
        return Response.ok(publicFundings).build();
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManagerReadOnly.getSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        sourceUtilsReadOnly.setSourceName(fs);
        return Response.ok(fs).build();
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManagerReadOnly.getEducationAffiliation(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducations(String orcid) {
        List<EducationSummary> educations = affiliationsManagerReadOnly.getEducationSummaryList(orcid);
        Educations publicEducations = new Educations();
        for (EducationSummary summary : educations) {
            if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtilsReadOnly.setSourceName(summary);
                publicEducations.getSummaries().add(summary);
            }
        }
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicEducations);
        ActivityUtils.setPathToEducations(publicEducations, orcid);
        return Response.ok(publicEducations).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManagerReadOnly.getEducationSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtilsReadOnly.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManagerReadOnly.getEmploymentAffiliation(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(e);
        ActivityUtils.setPathToActivity(e, orcid);
        sourceUtilsReadOnly.setSourceName(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployments(String orcid) {
        List<EmploymentSummary> employments = affiliationsManagerReadOnly.getEmploymentSummaryList(orcid);
        Employments publicEmployments = new Employments();
        for (EmploymentSummary summary : employments) {
            if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                ActivityUtils.setPathToActivity(summary, orcid);
                sourceUtilsReadOnly.setSourceName(summary);
                publicEmployments.getSummaries().add(summary);
            }
        }
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicEmployments);
        ActivityUtils.setPathToEmployments(publicEmployments, orcid);
        return Response.ok(publicEmployments).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManagerReadOnly.getEmploymentSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(es);
        ActivityUtils.setPathToActivity(es, orcid);
        sourceUtilsReadOnly.setSourceName(es);
        return Response.ok(es).build();
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManagerReadOnly.getPeerReview(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        sourceUtilsReadOnly.setSourceName(peerReview);
        return Response.ok(peerReview).build();
    }

    @Override
    public Response viewPeerReviews(String orcid) {
        List<PeerReviewSummary> peerReviews = peerReviewManagerReadOnly.getPeerReviewSummaryList(orcid);
        PeerReviews publicPeerReviews = peerReviewManagerReadOnly.groupPeerReviews(peerReviews, true);
        publicAPISecurityManagerV2.filter(publicPeerReviews);
        ActivityUtils.setPathToPeerReviews(publicPeerReviews, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(publicPeerReviews);
        sourceUtilsReadOnly.setSourceName(publicPeerReviews);
        return Response.ok(publicPeerReviews).build();
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManagerReadOnly.getPeerReviewSummary(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(summary);
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
        Api2_0_LastModifiedDatesHelper.calculateLastModified(records);
        return Response.ok(records).build();
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        ResearcherUrls researcherUrls = researcherUrlManagerReadOnly.getPublicResearcherUrls(orcid);
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(researcherUrls);
        sourceUtilsReadOnly.setSourceName(researcherUrls);
        return Response.ok(researcherUrls).build();
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManagerReadOnly.getResearcherUrl(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        sourceUtilsReadOnly.setSourceName(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @Override
    public Response viewEmails(String orcid) {
        Emails emails = emailManagerReadOnly.getPublicEmails(orcid);
        publicAPISecurityManagerV2.filter(emails);
        ElementUtils.setPathToEmail(emails, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(emails);
        sourceUtilsReadOnly.setSourceName(emails);
        return Response.ok(emails).build();
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManagerReadOnly.getPublicPersonalDetails(orcid);
        publicAPISecurityManagerV2.filter(personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(personalDetails);
        sourceUtilsReadOnly.setSourceName(personalDetails);
        return Response.ok(personalDetails).build();
    }

    @Override
    public Response viewOtherNames(String orcid) {
        OtherNames otherNames = otherNameManagerReadOnly.getPublicOtherNames(orcid);
        publicAPISecurityManagerV2.filter(otherNames);
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(otherNames);
        sourceUtilsReadOnly.setSourceName(otherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManagerReadOnly.getOtherName(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        sourceUtilsReadOnly.setSourceName(otherName);
        return Response.ok(otherName).build();
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        PersonExternalIdentifiers extIds = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid);
        publicAPISecurityManagerV2.filter(extIds);
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(extIds);
        sourceUtilsReadOnly.setSourceName(extIds);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        PersonExternalIdentifier extId = externalIdentifierManagerReadOnly.getExternalIdentifier(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        sourceUtilsReadOnly.setSourceName(extId);
        return Response.ok(extId).build();
    }

    @Override
    public Response viewBiography(String orcid) {
        Biography bio = biographyManagerReadOnly.getBiography(orcid);
        publicAPISecurityManagerV2.checkIsPublic(bio);
        ElementUtils.setPathToBiography(bio, orcid);
        return Response.ok(bio).build();
    }

    @Override
    public Response viewKeywords(String orcid) {
        Keywords keywords = profileKeywordManagerReadOnly.getPublicKeywords(orcid);
        publicAPISecurityManagerV2.filter(keywords);
        ElementUtils.setPathToKeywords(keywords, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(keywords);
        sourceUtilsReadOnly.setSourceName(keywords);
        return Response.ok(keywords).build();
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = profileKeywordManagerReadOnly.getKeyword(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        sourceUtilsReadOnly.setSourceName(keyword);
        return Response.ok(keyword).build();
    }

    @Override
    public Response viewAddresses(String orcid) {
        Addresses addresses = addressManagerReadOnly.getPublicAddresses(orcid);
        publicAPISecurityManagerV2.filter(addresses);
        ElementUtils.setPathToAddresses(addresses, orcid);
        // Set the latest last modified
        Api2_0_LastModifiedDatesHelper.calculateLastModified(addresses);
        sourceUtilsReadOnly.setSourceName(addresses);
        return Response.ok(addresses).build();
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManagerReadOnly.getAddress(orcid, putCode);
        publicAPISecurityManagerV2.checkIsPublic(address);
        ElementUtils.setPathToAddress(address, orcid);
        sourceUtilsReadOnly.setSourceName(address);
        return Response.ok(address).build();
    }

    @Override
    public Response viewPerson(String orcid) {
        Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
        publicAPISecurityManagerV2.filter(person);
        ElementUtils.setPathToPerson(person, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(person);
        sourceUtilsReadOnly.setSourceName(person);
        return Response.ok(person).build();
    }

    @Override
    public Response viewRecord(String orcid) {
        Record record = recordManagerReadOnly.getPublicRecord(orcid);
        publicAPISecurityManagerV2.filter(record);
        if (record.getPerson() != null) {
            sourceUtilsReadOnly.setSourceName(record.getPerson());
        }
        if (record.getActivitiesSummary() != null) {
            ActivityUtils.cleanEmptyFields(record.getActivitiesSummary());
            sourceUtilsReadOnly.setSourceName(record.getActivitiesSummary());
        }
        ElementUtils.setPathToRecord(record, orcid);
        Api2_0_LastModifiedDatesHelper.calculateLastModified(record);
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
        publicAPISecurityManagerV2.filter(workBulk);
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
        ClientSummary client = clientManagerReadOnly.getSummary(clientId);
        return Response.ok(client).build();
    }

}
