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

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.exception.MismatchedPutCodeException;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.message.ScopePathType;
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
public class MemberV2ApiServiceDelegatorImpl implements MemberV2ApiServiceDelegator {

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
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewActivities(String orcid) {
        ActivitiesSummary as = visibilityFilter.filter(profileEntityManager.getActivitiesSummary(orcid));
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewWork(String orcid, String putCode) {
        Work w = profileWorkManager.getWork(orcid, putCode);
        orcidSecurityManager.checkVisibility(w);
        return Response.ok(w).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewWorkSummary(String orcid, String putCode) {
        WorkSummary ws = profileWorkManager.getWorkSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(ws);
        return Response.ok(ws).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response createWork(String orcid, Work work) {
        Work w = profileWorkManager.createWork(orcid, work);
        try {
            return Response.created(new URI(w.getPutCode())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating URI for new work", e);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response updateWork(String orcid, String putCode, Work work) {
        if (!putCode.equals(work.getPutCode())) {
            throw new MismatchedPutCodeException("The put code in the URL was " + putCode + " whereas the one in the body was " + work.getPutCode());
        }
        Work w = profileWorkManager.updateWork(orcid, work);
        return Response.ok(w).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response deleteWork(String orcid, String putCode) {
        profileWorkManager.checkSourceAndRemoveWork(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewFunding(String orcid, String putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        orcidSecurityManager.checkVisibility(f);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewFundingSummary(String orcid, String putCode) {
        FundingSummary fs = profileFundingManager.getSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(fs);
        return Response.ok(fs).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response createFunding(String orcid, Funding funding) {
        Funding f = profileFundingManager.createFunding(orcid, funding);
        try {
            return Response.created(new URI(f.getPutCode())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating URI for new funding", e);
        }        
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response updateFunding(String orcid, String putCode, Funding funding) {
        if (!putCode.equals(funding.getPutCode())) {
            throw new MismatchedPutCodeException("The put code in the URL was " + putCode + " whereas the one in the body was " + funding.getPutCode());
        }
        Funding f = profileFundingManager.updateFunding(orcid, funding);
        return Response.ok(f).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewEducation(String orcid, String putCode) {
        Education e = affiliationsManager.getEducationAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewEducationSummary(String orcid, String putCode) {
        EducationSummary es = affiliationsManager.getEducationSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response createEducation(String orcid, Education education) {
        Education e = affiliationsManager.createEducationAffiliation(orcid, education);
        try {
            return Response.created(new URI(e.getPutCode())).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Error creating URI for new education", ex);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response updateEducation(String orcid, String putCode, Education education) {
        if (!putCode.equals(education.getPutCode())) {
            throw new MismatchedPutCodeException("The put code in the URL was " + putCode + " whereas the one in the body was " + education.getPutCode());
        }
        Education e = affiliationsManager.updateEducationAffiliation(orcid, education);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewEmployment(String orcid, String putCode) {
        Employment e = affiliationsManager.getEmploymentAffiliation(orcid, putCode);
        orcidSecurityManager.checkVisibility(e);
        return Response.ok(e).build();
    }

    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewEmploymentSummary(String orcid, String putCode) {
        EmploymentSummary es = affiliationsManager.getEmploymentSummary(orcid, putCode);
        orcidSecurityManager.checkVisibility(es);
        return Response.ok(es).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response createEmployment(String orcid, Employment employment) {
        Employment e = affiliationsManager.createEmploymentAffiliation(orcid, employment);
        try {
            return Response.created(new URI(e.getPutCode())).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Error creating URI for new work", ex);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response updateEmployment(String orcid, String putCode, Employment employment) {
        Employment e = affiliationsManager.updateEmploymentAffiliation(orcid, employment);
        return Response.ok(e).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response deleteAffiliation(String orcid, String putCode) {
        affiliationsManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response deleteFunding(String orcid, String putCode) {
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }
}
