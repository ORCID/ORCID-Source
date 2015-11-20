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
package org.orcid.api.common.delegator.impl;

import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.api.common.cerif.Cerif10APIFactory;
import org.orcid.api.common.cerif.Cerif16Builder;
import org.orcid.api.common.cerif.CerifTypeTranslator;
import org.orcid.api.common.delegator.CerifApiServiceDelgator;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Produces CERIF formatted representations of ORCID reseources cerif openAIRE
 * docs here: https://github.com/openaire/guidelines/blob/master/docs/cris/
 * cerif_xml_publication_entity.rst
 * 
 * This looks hideous as code because the XLS generates messy JAXB classes.
 * 
 * @author tom
 *
 */
public class CerifApiServiceDelegatorImpl implements CerifApiServiceDelgator {

    @Resource
    private ProfileEntityManager profileEntityManager;
    @Resource(name = "visibilityFilterV2")
    private VisibilityFilterV2 visibilityFilter;
    @Resource
    private WorkManager workManager;
    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    private CerifTypeTranslator translator = new CerifTypeTranslator();

    @Transactional
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getPerson(String orcid) {
        return this.getPerson(orcid, Visibility.PUBLIC);
    }
    
    /** Selective visibility profile
     * 
     * TODO: re-factor to remove and use new V2 visibility for names when available.
     * 
     * @param orcid
     * @param vis
     * @return
     */
     protected Response getPerson(String orcid, Visibility vis) {
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        if (profile == null)
            return Response.status(404).build();
       
        Optional<String> creditname = Optional.absent();
        if (!profile.getCreditNameVisibility().isMoreRestrictiveThan(vis)) {
            creditname = Optional.of(profile.getCreditName());
        }
        
        Set<ExternalIdentifierEntity> externalIDs = Sets.newHashSet();
        if (!profile.getExternalIdentifiersVisibility().isMoreRestrictiveThan(vis)) {
            externalIDs = profile.getExternalIdentifiers();
        }
        
        // TODO: we also need to check name visibility properly here, in line with V2 changes
        Optional<String> given = Optional.fromNullable(profile.getGivenNames());
        Optional<String> family = Optional.fromNullable(profile.getFamilyName());

        ActivitiesSummary as = profileEntityManager.getActivitiesSummary(orcid);
        ActivityUtils.cleanEmptyFields(as);
        visibilityFilter.filter(as);

        return Response.ok(
                new Cerif16Builder()
                .addPerson(profile.getId(), given, family, creditname, externalIDs)
                .concatPublications(as, orcid, false)
                .concatProducts(as, orcid, false)
                .build()
        ).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getPublication(String id) {
        Pair<String, Long> ids;
        try {
            ids = parseActivityID(id);
        } catch (Exception e) {
            return Response.status(400).build();
        }

        WorkSummary ws = workManager.getWorkSummary(ids.getLeft(), ids.getRight());
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkVisibility(ws);
        if (ws == null || !translator.isPublication(ws.getType()))
            return Response.status(404).build();

        return Response.ok(new Cerif16Builder().addPublication(ids.getLeft(), ws).build()).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getProduct(String id) {
        Pair<String, Long> ids;
        try {
            ids = parseActivityID(id);
        } catch (Exception e) {
            return Response.status(400).build();
        }

        WorkSummary ws = workManager.getWorkSummary(ids.getLeft(), ids.getRight());
        ActivityUtils.cleanEmptyFields(ws);
        orcidSecurityManager.checkVisibility(ws);
        if (ws == null || !translator.isProduct(ws.getType()))
            return Response.status(404).build();

        return Response.ok(new Cerif16Builder().addProduct(ids.getLeft(), ws).build()).build();
    }

    @Override
    public Response getEntities() {
        return Response.ok(new Cerif10APIFactory().getEntities()).build();
    }

    @Override
    public Response getSemantics() {
        return Response.ok(new Cerif10APIFactory().getSemantics()).type(MediaType.APPLICATION_XML).build();
    }

    /**
     * extract orcid and put code from id = 0000-0000-0000-0000:123456
     * 
     * @param id
     * @return Pair of ids, left = ORCID, right = PutCode
     * @throws Exception
     *             if can't parse
     */
    private Pair<String, Long> parseActivityID(String id) throws Exception {
        String[] ids = id.split(":");
        if (ids.length != 2)
            throw new Exception("Bad activity ID");
        try {
            Long.parseLong(ids[1]);
        } catch (NumberFormatException e) {
            throw new Exception("Bad put code ");
        }
        return Pair.of(ids[0], Long.parseLong(ids[1]));
    }

}
