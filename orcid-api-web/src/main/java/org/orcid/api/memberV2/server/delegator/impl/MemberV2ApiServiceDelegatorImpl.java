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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.exception.OrcidNotFoundException;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.core.exception.MismatchedPutCodeException;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.ActivitiesSummary;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Source;
import org.orcid.jaxb.model.record.Title;
import org.orcid.jaxb.model.record.Visibility;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkTitle;
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
        profileEntityManager.findByOrcid(orcid);

        // hard coding for now for testing
        ActivitiesSummary as = new ActivitiesSummary();
        Work w = new Work();
        WorkTitle wt = new WorkTitle();
        wt.setTitle(new Title("Test"));
        w.setWorkTitle(wt);
        List<Work> works = new ArrayList<Work>();
        works.add(w);
        as.setWorks(works);
        return Response.ok(as).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewWork(String orcid, String putCode) {
        Work w = profileWorkManager.getWork(orcid, putCode);
        checkVisbility(w);
        return Response.ok(w).build();
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

    private void checkVisbility(Work work) {
        Source existingSource = work.getSource();
        String sourceIdOfUpdater = sourceManager.retrieveSourceOrcid();
        if (sourceIdOfUpdater != null && (existingSource == null || !sourceIdOfUpdater.equals(existingSource.retrieveSourcePath()))
                && Visibility.PRIVATE.equals(work.getVisibility())) {
            throw new OrcidNotFoundException("The work does not exist, or it is private and you are not the source");
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewFunding(String orcid, String putCode) {
        Funding f = profileFundingManager.getFunding(orcid, putCode);
        return Response.ok(f).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response createFunding(String orcid, Funding funding) {
        Funding f = profileFundingManager.createFunding(orcid, funding);
        return Response.ok(f).build();
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
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_UPDATE)
    public Response deleteFunding(String orcid, String putCode) {
        profileFundingManager.checkSourceAndDelete(orcid, putCode);
        return Response.noContent().build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.ACTIVITIES_READ_LIMITED)
    public Response viewEducation(String orcid, String putCode) {
        Education f = affiliationsManager.getEducationAffiliation(orcid, putCode);
        return Response.ok(f).build();
    }    
}
