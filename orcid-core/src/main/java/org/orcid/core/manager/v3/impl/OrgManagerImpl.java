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
package org.orcid.core.manager.v3.impl;

import java.io.Writer;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.v3.dev1.common.OrganizationHolder;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgManagerImpl implements OrgManager {

    private static final String[] AMBIGUOUS_ORGS_HEADER = new String[] { "id", "source_orcid", "name", "city", "region", "country", "used_count" };

    private static final String[] DISAMBIGUATED_ORGS_HEADER = new String[] { "id", "source_id", "source_type", "org_type", "name", "city", "region", "country",
            "used_count" };

    private static final int CHUNK_SIZE = 10000;

    @Resource
    private OrgDao orgDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource
    private SourceManager sourceManager;

    @Override
    public List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults) {
        return orgDao.getAmbiguousOrgs(firstResult, maxResults);
    }

    @Override
    public void writeAmbiguousOrgs(Writer writer) {
        @SuppressWarnings("resource")
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(AMBIGUOUS_ORGS_HEADER);
        int firstResult = 0;
        List<AmbiguousOrgEntity> chunk = null;
        do {
            chunk = getAmbiguousOrgs(firstResult, CHUNK_SIZE);
            for (AmbiguousOrgEntity orgEntity : chunk) {
                String[] line = new String[] { String.valueOf(orgEntity.getId()), orgEntity.getSourceOrcid(), orgEntity.getName(), orgEntity.getCity(),
                        orgEntity.getRegion(), orgEntity.getCountry().value(), String.valueOf(orgEntity.getUsedCount()) };
                csvWriter.writeNext(line);
            }
            firstResult += chunk.size();
        } while (!chunk.isEmpty());

    }

    @Override
    public void writeDisambiguatedOrgs(Writer writer) {
        @SuppressWarnings("resource")
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(DISAMBIGUATED_ORGS_HEADER);
        int firstResult = 0;
        List<OrgDisambiguatedEntity> chunk = null;
        do {
            chunk = orgDisambiguatedDao.getChunk(firstResult, CHUNK_SIZE);
            for (OrgDisambiguatedEntity orgEntity : chunk) {
                String[] line = new String[] { String.valueOf(orgEntity.getId()), orgEntity.getSourceId(), orgEntity.getSourceType(), orgEntity.getOrgType(),
                        orgEntity.getName(), orgEntity.getCity(), orgEntity.getRegion(), orgEntity.getCountry().value(), String.valueOf(orgEntity.getPopularity()) };
                csvWriter.writeNext(line);
            }
            firstResult += chunk.size();
        } while (!chunk.isEmpty());
    }

    @Override
    public List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults) {
        return orgDao.getOrgs(searchTerm, firstResult, maxResults);
    }

    @Override
    public List<OrgEntity> getOrgsByName(String searchTerm) {
    	return orgDao.getOrgsByName(searchTerm);
    }            
    
    @Override
    public OrgEntity createUpdate(OrgEntity org) {
        OrgEntity existingOrg = orgDao.findByNameCityRegionAndCountry(org.getName(), org.getCity(), org.getRegion(), org.getCountry());
        if (existingOrg != null) {
            return existingOrg;
        }
        String sourceId = sourceManager.retrieveSourceOrcid();
        org.setSource(new SourceEntity(sourceId));
        orgDao.persist(org);
        return org;
    }

    @Override
    public OrgEntity createUpdate(OrgEntity org, Long orgDisambiguatedId) {
        OrgEntity existingOrg = orgDao.findByNameCityRegionAndCountry(org.getName(), org.getCity(), org.getRegion(), org.getCountry());
        if (existingOrg != null) {
            org = existingOrg;
        }
        if (org.getOrgDisambiguated() == null) {
            OrgDisambiguatedEntity disambiguatedOrg = orgDisambiguatedDao.find(orgDisambiguatedId);
            if (disambiguatedOrg == null) {
                throw new IllegalArgumentException("No such disambiguated org with id=" + orgDisambiguatedId);
            }
            org.setOrgDisambiguated(disambiguatedOrg);
        }
        if (org.getSource() == null) {
            org.setSource(new SourceEntity(sourceManager.retrieveSourceOrcid()));
        }
        return orgDao.merge(org);
    }
    
    
    @Override
    public OrgEntity getOrgEntity(OrganizationHolder holder) {
        if(holder == null)
            return null;
        
        OrgEntity orgEntity = new OrgEntity();
        org.orcid.jaxb.model.v3.dev1.common.Organization organization = holder.getOrganization();
        orgEntity.setName(organization.getName());
        org.orcid.jaxb.model.v3.dev1.common.OrganizationAddress address = organization.getAddress();
        orgEntity.setCity(address.getCity());
        orgEntity.setRegion(address.getRegion());
        orgEntity.setCountry(Iso3166Country.fromValue(address.getCountry().value()));
        if (organization.getDisambiguatedOrganization() != null && organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() != null) {
            orgEntity.setOrgDisambiguated(orgDisambiguatedDao.findBySourceIdAndSourceType(organization.getDisambiguatedOrganization()
                    .getDisambiguatedOrganizationIdentifier(), organization.getDisambiguatedOrganization().getDisambiguationSource()));
        }
        return createUpdate(orgEntity);        
    }
    
    @Override
    public OrgEntity getOrgEntity(Organization org) {
        String name = org.getName();
        String city = "";
        String region = "";
        Iso3166Country country = null;
        if(org.getAddress() != null) {
            city = org.getAddress().getCity();
            region = org.getAddress().getRegion();
            country = org.getAddress().getCountry();
                    
        }
        return orgDao.findByNameCityRegionAndCountry(name, city, region, country);        
    }

}
