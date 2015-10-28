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
package org.orcid.api.t1.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.util.ActivityUtils;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.api.common.writer.citeproc.WorkToCiteprocTranslator;
import org.orcid.api.t1.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Emails;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.ResearcherUrl;
import org.orcid.jaxb.model.record_rc1.ResearcherUrls;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.beans.factory.annotation.Value;

import de.undercouch.citeproc.csl.CSLItemData;

public class PublicV2ApiServiceDelegatorImpl implements PublicV2ApiServiceDelegator {

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
    
    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource(name = "visibilityFilterV2")
    private VisibilityFilterV2 visibilityFilter;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Resource
    private EmailManager emailManager;
    
    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewActivities(String orcid) {        
        if (profileDao.isProfileDeprecated(orcid)) {
            ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
            StringBuffer primary = new StringBuffer(baseUrl).append("/").append(entity.getPrimaryRecord().getId());
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", primary.toString());
            throw new OrcidDeprecatedException(params);
        }
        ActivitiesSummary as = profileEntityManager.getPublicActivitiesSummary(orcid);
        ActivityUtils.cleanEmptyFields(as);
        visibilityFilter.filter(as);
        ActivityUtils.setPathToActivity(as, orcid);
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
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
        if (!entity.getCreditNameVisibility().isMoreRestrictiveThan(Visibility.PUBLIC)){
            creditName = entity.getCreditName();
        }
        WorkToCiteprocTranslator tran = new  WorkToCiteprocTranslator();
        CSLItemData item = tran.toCiteproc(w, creditName ,true);
        return Response.ok(item).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewWorkSummary(String orcid, Long putCode) {
        WorkSummary ws = workManager.getWorkSummary(orcid, putCode);
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkVisibility(ws);
        ActivityUtils.setPathToActivity(ws, orcid);
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewFunding(String orcid, Long putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkVisibility(f);
        ActivityUtils.setPathToActivity(f, orcid);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewFundingSummary(String orcid, Long putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(fs);
        ActivityUtils.setPathToActivity(fs, orcid);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewEducation(String orcid, Long putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewEducationSummary(String orcid, Long putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewEmployment(String orcid, Long putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        ActivityUtils.setPathToActivity(e, orcid);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        ActivityUtils.setPathToActivity(es, orcid);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewPeerReview(String orcid, Long putCode) {
        PeerReview peerReview = peerReviewManager.getPeerReview(orcid, putCode);
        orcidSecurityManager.checkVisibility(peerReview);
        ActivityUtils.setPathToActivity(peerReview, orcid);
        return Response.ok(peerReview).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        PeerReviewSummary summary = peerReviewManager.getPeerReviewSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(summary);
        ActivityUtils.setPathToActivity(summary, orcid);
        return Response.ok(summary).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewResearcherUrls(String orcid) {
        ResearcherUrls researcherUrls = researcherUrlManager.getPublicResearcherUrlsV2(orcid);
        ElementUtils.setPathToResearcherUrls(researcherUrls, orcid);
        return Response.ok(researcherUrls).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response viewResearcherUrl(String orcid, String putCode) {
        ResearcherUrl researcherUrl = researcherUrlManager.getResearcherUrlV2(orcid, Long.valueOf(putCode));
        orcidSecurityManager.checkVisibility(researcherUrl);
        return Response.ok(researcherUrl).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.PERSON_READ_LIMITED, enableAnonymousAccess = true)
    public Response viewEmails(String orcid) {
        Emails emails = emailManager.getPublicEmails(orcid);        
        return Response.ok(emails).build();
    }
}
