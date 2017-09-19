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
package org.orcid.core.cli;

import java.io.File;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class LoadGridData {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGridData.class);
    private static final String GRID_SOURCE_TYPE = "GRID";

    private GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> orgDisambiguatedExternalIdentifierDao;
    private OrgDisambiguatedDao orgDisambiguatedDao;

    // Statistics
    private long updatedOrgs = 0;
    private long addedDisambiguatedOrgs = 0;
    private long addedExternalIdentifiers = 0;
    private long deprecatedOrgs = 0;
    private long obsoletedOrgs = 0;

    // Params
    @Option(name = "-f", usage = "Path to JSON file containing GRID info to load into DB")
    private File fileToLoad;

    public static void main(String[] args) {
        LoadGridData element = new LoadGridData();
        CmdLineParser parser = new CmdLineParser(element);
        try {
            parser.parseArgument(args);
            element.validateArgs(parser);
            element.init();
            element.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (fileToLoad == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "resource" })
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedExternalIdentifierDao = (GenericDao) context.getBean("orgDisambiguatedExternalIdentifierEntityDao");

    }

    private void execute() {
        JsonNode rootNode = JsonUtils.read(fileToLoad);
        ArrayNode institutes = (ArrayNode) rootNode.get("institutes");
        institutes.forEach(institute -> {
            String sourceId = institute.get("id") == null ? null : institute.get("id").asText();
            String status = institute.get("status") == null ? null : institute.get("status").asText();
            if ("active".equals(status)) {
                String name = institute.get("name") == null ? null : institute.get("name").asText();
                StringJoiner sj = new StringJoiner(",");
                String orgType = null;
                if (institute.get("types") != null) {
                    ((ArrayNode) institute.get("types")).forEach(x -> sj.add(x.textValue()));
                    orgType = sj.toString();
                }

                ArrayNode addresses = institute.get("addresses") == null ? null : (ArrayNode) institute.get("addresses");
                String city = null;
                String region = null;
                Iso3166Country country = null;
                if (addresses != null) {
                    for (JsonNode address : addresses) {
                        if (addresses.size() == 1 || (address.get("primary") != null && address.get("primary").asBoolean())) {
                            city = address.get("city").asText();
                            region = address.get("state").asText();
                            String countryCode = address.get("country_code").asText();
                            country = StringUtils.isBlank(countryCode) ? null : Iso3166Country.fromValue(countryCode);
                        }
                    }
                }

                ArrayNode urls = institute.get("links") == null ? null : (ArrayNode) institute.get("links");
                // TODO: Am assuming we are going to use the first URL
                String url = (urls != null && urls.size() > 0) ? urls.get(0).asText() : null;
                // TODO: Am ignoring all external identifiers for now

                // Creates or updates an institute
                processInstitute(sourceId, name, country, city, region, url, orgType);
            } else if ("redirected".equals(status)) {
                String primaryId = institute.get("redirect") == null ? null : institute.get("redirect").asText();
                deprecateOrg(sourceId, primaryId);
            } else if ("obsolete".equals(status)) {
                obsoleteOrg(sourceId);
            } else {
                LOGGER.error("Illegal status '" + status + "' for institute " + sourceId);
            }
        });

        LOGGER.info("Updated orgs: {}", updatedOrgs);
        LOGGER.info("New orgs: {}", addedDisambiguatedOrgs);
        LOGGER.info("New external identifiers: {}", addedExternalIdentifiers);
        LOGGER.info("Deprecated orgs: {}", deprecatedOrgs);
        LOGGER.info("Obsoleted orgs: {}", obsoletedOrgs);
    }

    public void processInstitute(String sourceId, String name, Iso3166Country country, String city, String region, String url, String orgType) {
        // TODO: Add transaction here
        OrgDisambiguatedEntity existingBySourceId = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, GRID_SOURCE_TYPE);
        if (existingBySourceId != null) {
            if (entityChanged(existingBySourceId, name, country.value(), city, region, url, orgType)) {
                existingBySourceId.setCity(city);
                existingBySourceId.setCountry(country);
                existingBySourceId.setName(name);
                existingBySourceId.setOrgType(orgType);
                existingBySourceId.setRegion(region);
                existingBySourceId.setUrl(url);
                existingBySourceId.setLastModified(new Date());
                existingBySourceId.setIndexingStatus(IndexingStatus.PENDING);
                orgDisambiguatedDao.merge(existingBySourceId);
                updatedOrgs++;
            }
        } else {
            OrgDisambiguatedEntity existingByNameAndAddress = orgDisambiguatedDao.findByNameCityRegionCountryAndSourceType(name, city, region, country, GRID_SOURCE_TYPE);
            if (existingByNameAndAddress != null) {
                if (sourceIdChanged(existingByNameAndAddress, sourceId)) {
                    createExternalIdentifier(existingByNameAndAddress, sourceId);
                }
            } else {
                // Create a new disambiguated org
                createDisambiguatedOrg(sourceId, name, orgType, country, city, region, url);
            }
        }
    }

    /**
     * Indicates if an entity changed his address, url or org type
     * 
     * @return true if the entity has changed.
     */
    public boolean entityChanged(OrgDisambiguatedEntity entity, String name, String countryCode, String city, String region, String url, String orgType) {
        // Check name
        if (StringUtils.isNotBlank(name)) {
            if (!name.equalsIgnoreCase(entity.getName()))
                return true;
        } else if (StringUtils.isNotBlank(entity.getName())) {
            return true;
        }
        // Check country
        if (StringUtils.isNotBlank(countryCode)) {
            if (entity.getCountry() == null || !countryCode.equals(entity.getCountry().value())) {
                return true;
            }
        } else if (entity.getCountry() != null) {
            return true;
        }
        // Check city
        if (StringUtils.isNotBlank(city)) {
            if (entity.getCity() == null || !city.equals(entity.getCity())) {
                return true;
            }
        } else if (StringUtils.isNotBlank(entity.getCity())) {
            return true;
        }
        // Check region
        if (StringUtils.isNotBlank(region)) {
            if (entity.getRegion() == null || !region.equals(entity.getRegion())) {
                return true;
            }
        } else if (StringUtils.isNotBlank(entity.getRegion())) {
            return true;
        }
        // Check url
        if (StringUtils.isNotBlank(url)) {
            if (entity.getUrl() == null || !url.equals(entity.getUrl())) {
                return true;
            }
        } else if (StringUtils.isNotBlank(entity.getUrl())) {
            return true;
        }
        // Check org_type
        if (StringUtils.isNotBlank(orgType)) {
            if (entity.getOrgType() == null || !orgType.equals(entity.getOrgType())) {
                return true;
            }
        } else if (StringUtils.isNotBlank(entity.getOrgType())) {
            return true;
        }

        return false;
    }

    /**
     * Indicates if an exiting entity changed its source id
     * 
     * @return true if the source id changed for the given entity
     */
    public boolean sourceIdChanged(OrgDisambiguatedEntity entity, String sourceId) {
        if (entity.getSourceId().equals(sourceId)) {
            return false;
        }
        return true;
    }

    /**
     * Creates a disambiguated ORG in the org_disambiguated table
     */
    private void createDisambiguatedOrg(String sourceId, String name, String orgType, Iso3166Country country, String city, String region, String url) {
        // TODO: Transaction here
        LOGGER.info("Creating disambiguated org {}", name);
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setCity(city);
        orgDisambiguatedEntity.setRegion(region);
        orgDisambiguatedEntity.setUrl(url);
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(sourceId);
        orgDisambiguatedEntity.setSourceType(GRID_SOURCE_TYPE);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        addedDisambiguatedOrgs++;
    }

    /**
     * Creates an external identifier in the
     * org_disambiguated_external_identifier table
     */
    private boolean createExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String identifier) {
        // TODO: Transaction here
        LOGGER.info("Creating external identifier for {}", disambiguatedOrg.getId());
        Date creationDate = new Date();
        OrgDisambiguatedExternalIdentifierEntity externalIdentifier = new OrgDisambiguatedExternalIdentifierEntity();
        externalIdentifier.setIdentifier(identifier);
        externalIdentifier.setIdentifierType(GRID_SOURCE_TYPE);
        externalIdentifier.setOrgDisambiguated(disambiguatedOrg);
        externalIdentifier.setDateCreated(creationDate);
        externalIdentifier.setLastModified(creationDate);
        orgDisambiguatedExternalIdentifierDao.persist(externalIdentifier);
        addedExternalIdentifiers++;
        return true;
    }

    /**
     * Mark an existing org as DEPRECATED
     */
    public void deprecateOrg(String sourceId, String primarySourceId) {
        // TODO: Transaction here
        LOGGER.info("Deprecating org {} for {}", sourceId, primarySourceId);
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, GRID_SOURCE_TYPE);
        if (existingEntity != null) {
            if (existingEntity.getStatus() == null || !existingEntity.getStatus().equals(OrganizationStatus.DEPRECATED.name())
                    || !existingEntity.getSourceParentId().equals(primarySourceId)) {
                existingEntity.setStatus(OrganizationStatus.DEPRECATED.name());
                existingEntity.setSourceParentId(primarySourceId);
                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                existingEntity.setLastModified(new Date());
                orgDisambiguatedDao.merge(existingEntity);
                deprecatedOrgs++;
            }
        }
    }

    /**
     * Mark an existing org as OBSOLETE
     */
    public void obsoleteOrg(String sourceId) {
        // TODO: Transaction here
        LOGGER.info("Marking or as obsolete {}", sourceId);
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, GRID_SOURCE_TYPE);
        if (existingEntity != null) {
            if (existingEntity.getStatus() == null || !existingEntity.getStatus().equals(OrganizationStatus.OBSOLETE.name())) {
                existingEntity.setStatus(OrganizationStatus.OBSOLETE.name());
                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                existingEntity.setLastModified(new Date());
                orgDisambiguatedDao.merge(existingEntity);
                obsoletedOrgs++;
            }
        }
    }
}
