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

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.t1.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.WebhookDao;

public class PublicV2ApiServiceDelegatorImpl implements PublicV2ApiServiceDelegator {

    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    private ProfileFundingManager profileFundingManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private AffiliationsManager affiliationsManager;

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

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    public Response viewActivities(String orcid) {
        ActivitiesSummary as = profileEntityManager.getPublicActivitiesSummary(orcid);
        visibilityFilter.filter(as);
        return Response.ok(as).build();
    }

    @Override
    public Response viewWork(String orcid, String putCode) {        
        Work w = profileWorkManager.getWork(orcid, putCode);
        orcidSecurityManager.checkVisibility(w);
        return Response.ok(w).build();
    }

    @Override
    public Response viewWorkSummary(String orcid, String putCode) {
        WorkSummary ws = profileWorkManager.getWorkSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(ws);
        return Response.ok(ws).build();
    }

    @Override
    public Response viewFunding(String orcid, String putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkVisibility(f);
        return Response.ok(f).build();
    }

    @Override
    public Response viewFundingSummary(String orcid, String putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(fs);
        return Response.ok(fs).build();
    }

    @Override
    public Response viewEducation(String orcid, String putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEducationSummary(String orcid, String putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        return Response.ok(es).build();
    }

    @Override
    public Response viewEmployment(String orcid, String putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        return Response.ok(e).build();
    }

    @Override
    public Response viewEmploymentSummary(String orcid, String putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        return Response.ok(es).build();
    }

}
