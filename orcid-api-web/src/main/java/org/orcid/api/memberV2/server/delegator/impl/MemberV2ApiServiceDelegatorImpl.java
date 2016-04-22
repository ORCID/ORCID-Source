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
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.common_rc2.Filterable;
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
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
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
        implements MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, Address, Keyword> {

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
        ActivitiesSummary as = null;
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ACTIVITIES_READ_LIMITED, orcid);
            as = visibilityFilter.filter(profileEntityManager.getActivitiesSummary(orcid), orcid);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public activities.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                as = profileEntityManager.getPublicActivitiesSummary(orcid);                
            } else {
                throw e;
            }
        }
        ActivityUtils.cleanEmptyFields(as);
        ActivityUtils.setPathToActivity(as, orcid);
        return Response.ok(as).build();
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        long lastModifiedTime = getLastModifiedTime(orcid);
        Work w = workManager.getWork(orcid, putCode, lastModifiedTime);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_WORKS_READ_LIMITED, w);
        ActivityUtils.cleanEmptyFields(w);        
        ActivityUtils.setPathToActivity(w, orcid);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {        
        long lastModifiedTime = getLastModifiedTime(orcid);
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode, lastModifiedTime);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_WORKS_READ_LIMITED, ws);
        ActivityUtils.cleanEmptyFields(ws);        
        ActivityUtils.setPathToActivity(ws, orcid);
        return Response.ok(ws).build();
    }

    @Override
    public Response createWork(String orcid, Work work) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_CREATE, orcid);
        Work w = workManager.createWork(orcid, work, true);
        try {
            return Response.created(new URI(String.valueOf(w.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creatework_response.exception"), e);
        }
    }

    @Override
    public Response updateWork(String orcid, Long putCode, Work work) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_UPDATE, orcid);
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
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_WORKS_UPDATE, orcid);
        workManager.checkSourceAndRemoveWork(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.FUNDING_READ_LIMITED, f);
        ActivityUtils.setPathToActivity(f, orcid);
        return Response.ok(f).build();
    }

    @Override    
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.FUNDING_READ_LIMITED, fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        return Response.ok(fs).build();
    }

    @Override
    public Response createFunding(String orcid, Funding funding) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_CREATE, orcid);
        Funding f = profileFundingManager.createFunding(orcid, funding, true);
        try {
            return Response.created(new URI(String.valueOf(f.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createfunding_response.exception"), e);
        }
    }

    @Override    
    public Response updateFunding(String orcid, Long putCode, Funding funding) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_UPDATE, orcid);
        if (!putCode.equals(funding.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(funding.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Funding f = profileFundingManager.updateFunding(orcid, funding, true);
        return Response.ok(f).build();
    }
    
    @Override
    public Response deleteFunding(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.FUNDING_UPDATE, orcid);
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);        
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    public Response createEducation(String orcid, Education education) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_CREATE, orcid);
        Education e = affiliationsManager.createEducationAffiliation(orcid, education, true);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createeducation_response.exception"), ex);
        }
    }

    @Override
    public Response updateEducation(String orcid, Long putCode, Education education) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_UPDATE, orcid);
        if (!putCode.equals(education.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(education.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Education e = affiliationsManager.updateEducationAffiliation(orcid, education, true);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.AFFILIATIONS_READ_LIMITED, es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    public Response createEmployment(String orcid, Employment employment) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_CREATE, orcid);
        Employment e = affiliationsManager.createEmploymentAffiliation(orcid, employment, true);
        try {
            return Response.created(new URI(String.valueOf(e.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createemployment_response.exception"), ex);
        }
    }

    @Override
    public Response updateEmployment(String orcid, Long putCode, Employment employment) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_UPDATE, orcid);
        if (!putCode.equals(employment.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(employment.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        Employment e = affiliationsManager.updateEmploymentAffiliation(orcid, employment, true);
        return Response.ok(e).build();
    }

    @Override
    public Response deleteAffiliation(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.AFFILIATIONS_UPDATE, orcid);
        affiliationsManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }    

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview p = peerReviewManager.getPeerReview(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.PEER_REVIEW_READ_LIMITED, p);
        ActivityUtils.setPathToActivity(p, orcid);
        return Response.ok(p).build();
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary ps = peerReviewManager.getPeerReviewSummary(orcid, putCode);        
        checkPermissionsOnElement(orcid, ScopePathType.PEER_REVIEW_READ_LIMITED, ps);
        ActivityUtils.setPathToActivity(ps, orcid);
        return Response.ok(ps).build();
    }

    @Override
    public Response createPeerReview(String orcid, PeerReview peerReview) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_CREATE, orcid);
        PeerReview newPeerReview = peerReviewManager.createPeerReview(orcid, peerReview, true);
        try {
            return Response.created(new URI(String.valueOf(newPeerReview.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createpeerreview_response.exception"), ex);
        }
    }

    @Override
    public Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview) {
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_UPDATE, orcid);
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
        orcidSecurityManager.checkPermissions(ScopePathType.PEER_REVIEW_UPDATE, orcid);
        peerReviewManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_READ, null);
        GroupIdRecord record = groupIdRecordManager.getGroupIdRecord(putCode);
        return Response.ok(record).build();
    }

    @Override
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_UPDATE, null);
        GroupIdRecord newRecord = groupIdRecordManager.createGroupIdRecord(groupIdRecord);
        try {
            return Response.created(new URI(String.valueOf(newRecord.getPutCode()))).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.creategroupidrecord_response.exception"), ex);
        }
    }

    @Override
    public Response updateGroupIdRecord(GroupIdRecord groupIdRecord, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_UPDATE, null);
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
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_UPDATE, null);
        groupIdRecordManager.deleteGroupIdRecord(putCode);
        return Response.noContent().build();
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        orcidSecurityManager.checkPermissions(ScopePathType.GROUP_ID_RECORD_READ, null);
        GroupIdRecords records = groupIdRecordManager.getGroupIdRecords(pageSize, pageNum);
        return Response.ok(records).build();
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
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);            
            researcherUrls = researcherUrlManager.getResearcherUrls(orcid, lastModifiedTime);
            researcherUrls.setResearcherUrls((List<ResearcherUrl>) visibilityFilter.filter(researcherUrls.getResearcherUrls(), orcid));
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                researcherUrls = researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime);            
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        return Response.ok(researcherUrls).build();
    }

    public Response viewResearcherUrl(String orcid, Long putCode) {
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrl(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, researcherUrl);
        ElementUtils.setPathToResearcherUrl(researcherUrl, orcid);
        return Response.ok(researcherUrl).build();
    }

    @Override
    public Response updateResearcherUrl(String orcid, Long putCode, ResearcherUrl researcherUrl) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        if (!putCode.equals(researcherUrl.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(researcherUrl.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }
        ResearcherUrl updatedResearcherUrl = researcherUrlManager.updateResearcherUrl(orcid, researcherUrl, true);
        ElementUtils.setPathToResearcherUrl(updatedResearcherUrl, orcid);
        return Response.ok(updatedResearcherUrl).build();
    }

    @Override
    public Response createResearcherUrl(String orcid, ResearcherUrl researcherUrl) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        researcherUrl = researcherUrlManager.createResearcherUrl(orcid, researcherUrl, true);
        try {
            return Response.created(new URI(String.valueOf(researcherUrl.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response deleteResearcherUrl(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        researcherUrlManager.deleteResearcherUrl(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewEmails(String orcid) {
        Emails emails = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);            
            emails = emailManager.getEmails(orcid, lastModified);
            emails.setEmails((List<Email>) visibilityFilter.filter(emails.getEmails(), orcid));            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                emails = emailManager.getPublicEmails(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToEmail(emails, orcid);
        return Response.ok(emails).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewOtherNames(String orcid) {
        OtherNames otherNames = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);        
            otherNames = otherNameManager.getOtherNames(orcid, lastModified);
            List<OtherName> allOtherNames = otherNames.getOtherNames();
            List<OtherName> filterdOtherNames = (List<OtherName>) visibilityFilter.filter(allOtherNames, orcid);
            otherNames.setOtherNames(filterdOtherNames);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                otherNames = otherNameManager.getPublicOtherNames(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToOtherNames(otherNames, orcid);
        return Response.ok(otherNames).build();
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        OtherName otherName = otherNameManager.getOtherName(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, otherName);
        ElementUtils.setPathToOtherName(otherName, orcid);
        return Response.ok(otherName).build();
    }

    @Override
    public Response createOtherName(String orcid, OtherName otherName) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        otherName = otherNameManager.createOtherName(orcid, otherName, true);
        try {
            return Response.created(new URI(String.valueOf(otherName.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateOtherName(String orcid, Long putCode, OtherName otherName) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        if (!putCode.equals(otherName.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(otherName.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        OtherName updatedOtherName = otherNameManager.updateOtherName(orcid, putCode, otherName, true);
        ElementUtils.setPathToOtherName(updatedOtherName, orcid);
        return Response.ok(updatedOtherName).build();
    }

    @Override
    public Response deleteOtherName(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        otherNameManager.deleteOtherName(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewExternalIdentifiers(String orcid) {
        PersonExternalIdentifiers extIds = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);        
            extIds = externalIdentifierManager.getExternalIdentifiers(orcid, lastModified);
            List<PersonExternalIdentifier> allExtIds = extIds.getExternalIdentifier();
            List<PersonExternalIdentifier> filteredExtIds = (List<PersonExternalIdentifier>) visibilityFilter.filter(allExtIds, orcid);
            extIds.setExternalIdentifiers(filteredExtIds);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                extIds = externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToExternalIdentifiers(extIds, orcid);
        return Response.ok(extIds).build();
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        PersonExternalIdentifier extId = externalIdentifierManager.getExternalIdentifier(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, extId);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        return Response.ok(extId).build();
    }

    @Override
    public Response updateExternalIdentifier(String orcid, Long putCode, PersonExternalIdentifier externalIdentifier) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE, orcid);
        if (!putCode.equals(externalIdentifier.getPutCode())) {            
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(externalIdentifier.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }        
        PersonExternalIdentifier extId = externalIdentifierManager.updateExternalIdentifier(orcid, externalIdentifier, true);
        ElementUtils.setPathToExternalIdentifier(extId, orcid);
        return Response.ok(extId).build();
    }

    @Override
    public Response createExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE, orcid);
        externalIdentifier = externalIdentifierManager.createExternalIdentifier(orcid, externalIdentifier, true);
        try {
            return Response.created(new URI(String.valueOf(externalIdentifier.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response deleteExternalIdentifier(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        externalIdentifierManager.deleteExternalIdentifier(orcid, putCode, true);
        return Response.noContent().build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response viewKeywords(String orcid) {
        Keywords keywords = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);        
            keywords = keywordsManager.getKeywords(orcid, lastModified);
            List<Keyword> allKeywords = keywords.getKeywords();
            List<Keyword> filterdKeywords = (List<Keyword>) visibilityFilter.filter(allKeywords, orcid);
            keywords.setKeywords(filterdKeywords);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                keywords = keywordsManager.getPublicKeywords(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToKeywords(keywords, orcid);
        return Response.ok(keywords).build();
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        Keyword keyword = keywordsManager.getKeyword(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, keyword);
        ElementUtils.setPathToKeyword(keyword, orcid);
        return Response.ok(keyword).build();
    }

    @Override    
    public Response createKeyword(String orcid, Keyword keyword) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        keyword = keywordsManager.createKeyword(orcid, keyword, true);
        try {
            return Response.created(new URI(String.valueOf(keyword.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateKeyword(String orcid, Long putCode, Keyword keyword) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        if (!putCode.equals(keyword.getPutCode())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("urlPutCode", String.valueOf(putCode));
            params.put("bodyPutCode", String.valueOf(keyword.getPutCode()));
            throw new MismatchedPutCodeException(params);
        }

        keyword = keywordsManager.updateKeyword(orcid, putCode, keyword, true);      
        ElementUtils.setPathToKeyword(keyword, orcid);
        return Response.ok(keyword).build();
    }

    @Override
    public Response deleteKeyword(String orcid, Long putCode) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        keywordsManager.deleteKeyword(orcid, putCode, true);
        return Response.noContent().build();
    }
                    
    @SuppressWarnings("unchecked")
    @Override
    public Response viewAddresses(String orcid) {
        Addresses addresses = null;
        long lastModified = getLastModifiedTime(orcid);
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);
            addresses = addressManager.getAddresses(orcid, lastModified);
            List<Address> allAddresses = addresses.getAddress();
            List<Address> filteredAddresses = (List<Address>) visibilityFilter.filter(allAddresses, orcid);
            addresses.setAddress(filteredAddresses);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                addresses = addressManager.getPublicAddresses(orcid, lastModified);
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToAddresses(addresses, orcid);
        return Response.ok(addresses).build();
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        Address address = addressManager.getAddress(orcid, putCode);
        checkPermissionsOnElement(orcid, ScopePathType.ORCID_BIO_READ_LIMITED, address);
        ElementUtils.setPathToAddress(address, orcid);
        return Response.ok(address).build();
    }

    @Override
    public Response createAddress(String orcid, Address address) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        address = addressManager.createAddress(orcid, address, true);
        try {
            return Response.created(new URI(String.valueOf(address.getPutCode()))).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.createelement_response.exception"), e);
        }
    }

    @Override
    public Response updateAddress(String orcid, Long putCode, Address address) {
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
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
        orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_UPDATE, orcid);
        addressManager.deleteAddress(orcid, putCode);
        return Response.noContent().build();
    }   
    
    @Override
    public Response viewBiography(String orcid) {
        Biography bio = null;
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);
            bio = biographyManager.getBiography(orcid);
            orcidSecurityManager.checkVisibility(bio, orcid);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the list of public elements.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                bio = biographyManager.getPublicBiography(orcid);
                if(bio == null) {
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
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);
            personalDetails = personalDetailsManager.getPersonalDetails(orcid);
            personalDetails = visibilityFilter.filter(personalDetails, orcid);            
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the public element.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                personalDetails = personalDetailsManager.getPublicPersonalDetails(orcid);                
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToPersonalDetails(personalDetails, orcid);    
        return Response.ok(personalDetails).build();
    }
    
    @Override
    public Response viewPerson(String orcid) {
        Person person = null;
        try {
            orcidSecurityManager.checkPermissions(ScopePathType.ORCID_BIO_READ_LIMITED, orcid);
            person = profileEntityManager.getPersonDetails(orcid);
            person = visibilityFilter.filter(person, orcid);
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, return him the public element.
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                person = profileEntityManager.getPublicPersonDetails(orcid);                
            } else {
                throw e;
            }
        }
        ElementUtils.setPathToPerson(person, orcid);
        return Response.ok(person).build();
    }
    
    private void checkPermissionsOnElement(String orcid, ScopePathType requiredScope, Filterable element) {
        try {
            orcidSecurityManager.checkPermissions(requiredScope, orcid);
            orcidSecurityManager.checkVisibility(element, orcid);
        } catch(AccessControlException | OrcidUnauthorizedException e) {
            //If the user have the READ_PUBLIC scope, check that the work is public
            if(orcidSecurityManager.hasScope(ScopePathType.READ_PUBLIC)) {
                orcidSecurityManager.checkIsPublic(element);
            } else {
                throw e;
            }
        }
    }
}
