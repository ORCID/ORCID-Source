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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.common.writer.citeproc.WorkToCiteprocTranslator;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.locale.LocaleManager;
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
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.groupid.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
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
        implements PublicV2ApiServiceDelegator<Education, Employment, ExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> {

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
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWork(String orcid, Long putCode) {
        Work w = workManager.getWork(orcid, putCode);
        ActivityUtils.cleanEmptyFields(w);
        orcidSecurityManager.checkVisibility(w);
        ActivityUtils.setPathToActivity(w, orcid);
        return Response.ok(w).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewWorkCitation(String orcid, Long putCode) { 
        Work w = (Work) this.viewWork(orcid, putCode).getEntity();
        ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        String creditName = null;
        if (!entity.getNamesVisibility().isMoreRestrictiveThan(Visibility.PUBLIC)){
            creditName = entity.getCreditName();
        }
        WorkToCiteprocTranslator tran = new  WorkToCiteprocTranslator();
        CSLItemData item = tran.toCiteproc(w, creditName ,true);
        return Response.ok(item).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ORCID_WORKS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewWorkSummary(String orcid, Long putCode) {
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode);
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkVisibility(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkVisibility(f);
        ActivityUtils.setPathToActivity(f, orcid);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.FUNDING_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @AccessControl(requiredScope = ScopePathType.AFFILIATIONS_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManager.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkVisibility(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        return Response.ok(peerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PEER_REVIEW_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(summary);
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

    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewResearcherUrls(String orcid) {
        ResearcherUrls researcherUrls = researcherUrlManager.getResearcherUrlsV2(orcid);
        researcherUrls.setResearcherUrls((List<ResearcherUrl>) visibilityFilter.filter(researcherUrls.getResearcherUrls()));
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        return Response.ok(researcherUrls).build();
    }

    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrlV2(orcid, putCode);
        orcidSecurityManager.checkVisibility(researcherUrl);
        return Response.ok(researcherUrl).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmails(String orcid) {
        Emails emails = emailManager.getEmails(orcid);
        emails.setEmails((List<Email>) visibilityFilter.filter(emails.getEmails()));
        return Response.ok(emails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewOtherNames(String orcid) {
        OtherNames otherNames = otherNameManager.getOtherNamesV2(orcid);
        List<OtherName> allOtherNames = otherNames.getOtherNames();
        List<OtherName> filterdOtherNames = (List<OtherName>) visibilityFilter.filter(allOtherNames);
        otherNames.setOtherNames(filterdOtherNames);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManager.getOtherNameV2(orcid, putCode);
        orcidSecurityManager.checkVisibility(otherName);
        return Response.ok(otherName).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_LIMITED, enableAnonymousAccess = true)
    public Response viewPersonalDetails(String orcid) {
        PersonalDetails personalDetails = personalDetailsManager.getPersonalDetails(orcid);
        personalDetails = visibilityFilter.filter(personalDetails);
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);
        return Response.ok(personalDetails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewExternalIdentifiers(String orcid) {
        ExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiersV2(orcid);
        List<ExternalIdentifier> allExtIds = extIds.getExternalIdentifier();
        List<ExternalIdentifier> filteredExtIds = (List<ExternalIdentifier>) visibilityFilter.filter(allExtIds);
        extIds.setExternalIdentifiers(filteredExtIds);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        ExternalIdentifier extId = externalIdentifierManager.getExternalIdentifierV2(orcid, putCode);
        orcidSecurityManager.checkVisibility(extId);
        return Response.ok(extId).build();
    }

}
