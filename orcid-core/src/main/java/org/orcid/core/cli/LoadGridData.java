package org.orcid.core.cli;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class LoadGridData {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGridData.class);
    private static final String WIKIPEDIA_URL = "wikipedia_url";

    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;
    private OrgDisambiguatedDao orgDisambiguatedDao;

    // Statistics
    private long updatedOrgs = 0;
    private long addedDisambiguatedOrgs = 0;
    private long addedExternalIdentifiers = 0;
    private long deprecatedOrgs = 0;
    private long obsoletedOrgs = 0;
    private long updatedExternalIdentifiers = 0;

    // Params
    @Option(name = "-f", usage = "Path to JSON file containing GRID info to load into DB")
    private File fileToLoad;

    public void setFileToLoad(File fileToLoad) {
        this.fileToLoad = fileToLoad;
    }

    public void setOrgDisambiguatedExternalIdentifierDao(OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao) {
        this.orgDisambiguatedExternalIdentifierDao = orgDisambiguatedExternalIdentifierDao;
    }

    public void setOrgDisambiguatedDao(OrgDisambiguatedDao orgDisambiguatedDao) {
        this.orgDisambiguatedDao = orgDisambiguatedDao;
    }

    public long getUpdatedOrgs() {
        return updatedOrgs;
    }

    public long getAddedDisambiguatedOrgs() {
        return addedDisambiguatedOrgs;
    }

    public long getAddedExternalIdentifiers() {
        return addedExternalIdentifiers;
    }

    public long getDeprecatedOrgs() {
        return deprecatedOrgs;
    }

    public long getObsoletedOrgs() {
        return obsoletedOrgs;
    }        

    public long getUpdatedExternalIdentifiers() {
        return updatedExternalIdentifiers;
    }

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

    @SuppressWarnings({ "resource" })
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedExternalIdentifierDao = (OrgDisambiguatedExternalIdentifierDao) context.getBean("orgDisambiguatedExternalIdentifierDao");

    }

    public void execute() {
        Instant start = Instant.now();
        JsonNode rootNode = JsonUtils.read(fileToLoad);
        ArrayNode institutes = (ArrayNode) rootNode.get("institutes");
        institutes.forEach(institute -> {
            String sourceId = institute.get("id").isNull() ? null : institute.get("id").asText();

            // Case that should never happen
            if (PojoUtil.isEmpty(sourceId)) {
                LOGGER.error("Invalid institute with null id found {}", institute.toString());
            }

            String status = institute.get("status").isNull() ? null : institute.get("status").asText();
            if ("active".equals(status)) {
                String name = institute.get("name").isNull() ? null : institute.get("name").asText();
                StringJoiner sj = new StringJoiner(",");
                String orgType = null;
                if (!institute.get("types").isNull()) {
                    ((ArrayNode) institute.get("types")).forEach(x -> sj.add(x.textValue()));
                    orgType = sj.toString();
                }

                ArrayNode addresses = institute.get("addresses").isNull() ? null : (ArrayNode) institute.get("addresses");
                String city = null;
                String region = null;
                Iso3166Country country = null;
                if (addresses != null) {
                    for (JsonNode address : addresses) {
                        if (addresses.size() == 1 || (address.get("primary") != null && address.get("primary").asBoolean())) {
                            city = address.get("city").isNull() ? null : address.get("city").asText();
                            region = address.get("state").isNull() ? null : address.get("state").asText();
                            String countryCode = address.get("country_code").isNull() ? null : address.get("country_code").asText();
                            country = StringUtils.isBlank(countryCode) ? null : Iso3166Country.fromValue(countryCode);
                        }
                    }
                }

                ArrayNode urls = institute.get("links").isNull() ? null : (ArrayNode) institute.get("links");
                // Use the first URL
                String url = (urls != null && urls.size() > 0) ? urls.get(0).asText() : null;

                // Creates or updates an institute
                OrgDisambiguatedEntity entity = processInstitute(sourceId, name, country, city, region, url, orgType);

                // Creates external identifiers
                processExternalIdentifiers(entity, institute);               
            } else if ("redirected".equals(status)) {
                String primaryId = institute.get("redirect").isNull() ? null : institute.get("redirect").asText();
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
        LOGGER.info("Updated external identifiers: {}", updatedExternalIdentifiers);
        LOGGER.info("Deprecated orgs: {}", deprecatedOrgs);
        LOGGER.info("Obsoleted orgs: {}", obsoletedOrgs);        
        LOGGER.info("Time taken to process the data: {}", Duration.between(start, Instant.now()).toString());
    }

    private OrgDisambiguatedEntity processInstitute(String sourceId, String name, Iso3166Country country, String city, String region, String url, String orgType) {
        OrgDisambiguatedEntity existingBySourceId = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, OrgDisambiguatedSourceType.GRID.name());
        if (existingBySourceId != null) {
            if (entityChanged(existingBySourceId, name, country.value(), city, region, url, orgType)) {
                existingBySourceId.setCity(city);
                existingBySourceId.setCountry(country.name());
                existingBySourceId.setName(name);
                existingBySourceId.setOrgType(orgType);
                existingBySourceId.setRegion(region);
                existingBySourceId.setUrl(url);
                existingBySourceId.setLastModified(new Date());
                existingBySourceId.setIndexingStatus(IndexingStatus.PENDING);
                orgDisambiguatedDao.merge(existingBySourceId);
                updatedOrgs++;
            }
            return existingBySourceId;
        }

        // Create a new disambiguated org
        return createDisambiguatedOrg(sourceId, name, orgType, country, city, region, url);
    }

    private void processExternalIdentifiers(OrgDisambiguatedEntity org, JsonNode institute) {
        JsonNode externalIdsContainer = institute.get("external_ids") == null ? null : institute.get("external_ids");
        if (externalIdsContainer != null) {

            Iterator<Entry<String, JsonNode>> nodes = externalIdsContainer.fields();

            while (nodes.hasNext()) {
                Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();
                String identifierTypeName = entry.getKey().toUpperCase();
                String preferredId = entry.getValue().get("preferred").isNull() ? null : entry.getValue().get("preferred").asText();
                ArrayNode elements = (ArrayNode) entry.getValue().get("all");
                for (JsonNode extId : elements) {
                    // If the external identifier doesn't exists yet
                    OrgDisambiguatedExternalIdentifierEntity existingExternalId = orgDisambiguatedExternalIdentifierDao.findByDetails(org.getId(), extId.asText(), identifierTypeName);
                    Boolean preferred = extId.asText().equals(preferredId);
                    if (existingExternalId == null) {
                        if(preferred) {
                            createExternalIdentifier(org, extId.asText(), identifierTypeName, true);
                        } else {
                            createExternalIdentifier(org, extId.asText(), identifierTypeName, false);
                        }                        
                    } else {
                        if(existingExternalId.getPreferred() != preferred) {
                            existingExternalId.setPreferred(preferred);
                            orgDisambiguatedExternalIdentifierDao.merge(existingExternalId);
                            LOGGER.info("External identifier for {} with ext id {} and type {} was updated",
                                    new Object[] { org.getId(), extId.asText(), identifierTypeName });
                            updatedExternalIdentifiers++;
                        } else {
                            LOGGER.info("External identifier for {} with ext id {} and type {} already exists",
                                    new Object[] { org.getId(), extId.asText(), identifierTypeName });
                        }                        
                    }
                }
            }
        }
        
        if(!institute.get(WIKIPEDIA_URL).isNull()) {
            String url = institute.get(WIKIPEDIA_URL).asText();
            // If the external identifier doesn't exists yet
            if (orgDisambiguatedExternalIdentifierDao.findByDetails(org.getId(), url, WIKIPEDIA_URL.toUpperCase()) == null) {
                createExternalIdentifier(org, url, WIKIPEDIA_URL.toUpperCase(), true);
            } else {
                LOGGER.info("Wikipedia URL for {} already exists", org.getId());
            }
        }
    }

    /**
     * Indicates if an entity changed his address, url or org type
     * 
     * @return true if the entity has changed.
     */
    private boolean entityChanged(OrgDisambiguatedEntity entity, String name, String countryCode, String city, String region, String url, String orgType) {
        // Check name
        if (StringUtils.isNotBlank(name)) {
            if (!name.equalsIgnoreCase(entity.getName()))
                return true;
        } else if (StringUtils.isNotBlank(entity.getName())) {
            return true;
        }
        // Check country
        if (StringUtils.isNotBlank(countryCode)) {
            if (entity.getCountry() == null || !countryCode.equals(entity.getCountry())) {
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
     * Creates a disambiguated ORG in the org_disambiguated table
     */
    private OrgDisambiguatedEntity createDisambiguatedOrg(String sourceId, String name, String orgType, Iso3166Country country, String city, String region, String url) {
        LOGGER.info("Creating disambiguated org {}", name);
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setCountry(country != null ? country.name() : null);
        orgDisambiguatedEntity.setCity(city);
        orgDisambiguatedEntity.setRegion(region);
        orgDisambiguatedEntity.setUrl(url);
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(sourceId);
        orgDisambiguatedEntity.setSourceType(OrgDisambiguatedSourceType.GRID.name());
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        addedDisambiguatedOrgs++;
        return orgDisambiguatedEntity;
    }

    /**
     * Creates an external identifier in the
     * org_disambiguated_external_identifier table
     */
    private boolean createExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String identifier, String externalIdType, Boolean preferred) {
        LOGGER.info("Creating external identifier for {}", disambiguatedOrg.getId());
        Date creationDate = new Date();
        OrgDisambiguatedExternalIdentifierEntity externalIdentifier = new OrgDisambiguatedExternalIdentifierEntity();
        externalIdentifier.setIdentifier(identifier);
        externalIdentifier.setIdentifierType(externalIdType);
        externalIdentifier.setOrgDisambiguated(disambiguatedOrg);
        externalIdentifier.setDateCreated(creationDate);
        externalIdentifier.setLastModified(creationDate);
        externalIdentifier.setPreferred(preferred);
        orgDisambiguatedExternalIdentifierDao.persist(externalIdentifier);
        addedExternalIdentifiers++;
        return true;
    }

    /**
     * Mark an existing org as DEPRECATED
     */
    private void deprecateOrg(String sourceId, String primarySourceId) {
        LOGGER.info("Deprecating org {} for {}", sourceId, primarySourceId);
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, OrgDisambiguatedSourceType.GRID.name());
        Date now = new Date();
        if (existingEntity != null) {
            if (existingEntity.getStatus() == null || !existingEntity.getStatus().equals(OrganizationStatus.DEPRECATED.name())
                    || !existingEntity.getSourceParentId().equals(primarySourceId)) {
                existingEntity.setStatus(OrganizationStatus.DEPRECATED.name());
                existingEntity.setSourceParentId(primarySourceId);
                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                existingEntity.setLastModified(now);
                orgDisambiguatedDao.merge(existingEntity);
                deprecatedOrgs++;
            }
        } else {
            OrgDisambiguatedEntity deprecatedEntity = new OrgDisambiguatedEntity();
            deprecatedEntity.setSourceType(OrgDisambiguatedSourceType.GRID.name());
            deprecatedEntity.setStatus(OrganizationStatus.DEPRECATED.name());
            deprecatedEntity.setSourceId(sourceId);
            deprecatedEntity.setSourceParentId(primarySourceId);
            deprecatedEntity.setDateCreated(now);
            deprecatedEntity.setLastModified(now);
            //We don't need to index it
            deprecatedEntity.setIndexingStatus(IndexingStatus.DONE);
            orgDisambiguatedDao.persist(deprecatedEntity);
            deprecatedOrgs++;
        }
    }

    /**
     * Mark an existing org as OBSOLETE
     */
    private void obsoleteOrg(String sourceId) {
        LOGGER.info("Marking or as obsolete {}", sourceId);
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, OrgDisambiguatedSourceType.GRID.name());
        Date now = new Date();
        if (existingEntity != null) {
            if (existingEntity.getStatus() == null || !existingEntity.getStatus().equals(OrganizationStatus.OBSOLETE.name())) {
                existingEntity.setStatus(OrganizationStatus.OBSOLETE.name());
                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                existingEntity.setLastModified(now);
                orgDisambiguatedDao.merge(existingEntity);
                obsoletedOrgs++;
            }
        } else {
            OrgDisambiguatedEntity obsoletedEntity = new OrgDisambiguatedEntity();
            obsoletedEntity.setSourceType(OrgDisambiguatedSourceType.GRID.name());
            obsoletedEntity.setStatus(OrganizationStatus.OBSOLETE.name());
            obsoletedEntity.setSourceId(sourceId);            
            obsoletedEntity.setDateCreated(now);
            obsoletedEntity.setLastModified(now);            
            //We don't need to index it
            obsoletedEntity.setIndexingStatus(IndexingStatus.DONE);
            orgDisambiguatedDao.persist(obsoletedEntity);
            obsoletedOrgs++;
        }
    }
}
