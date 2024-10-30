package org.orcid.scheduler.loader.source.ror;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.orgs.grouping.OrgGrouping;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.scheduler.loader.io.FileRotator;
import org.orcid.scheduler.loader.io.OrgDataClient;
import org.orcid.scheduler.loader.source.LoadSourceDisabledException;
import org.orcid.scheduler.loader.source.OrgLoadSource;
import org.orcid.scheduler.loader.source.zenodo.api.ZenodoRecords;
import org.orcid.scheduler.loader.source.zenodo.api.ZenodoRecordsHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class RorOrgLoadSource implements OrgLoadSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RorOrgLoadSource.class);

    private static final String WIKIPEDIA_URL = "wikipedia_url";

    @Value("${org.orcid.core.orgs.ror.enabled:true}")
    private boolean enabled;

    @Value("${org.orcid.core.orgs.clients.userAgent}")
    private String userAgent;

    @Resource(name = "rorOrgDataClient")
    private OrgDataClient orgDataClient;

    @Value("${org.orcid.core.orgs.ror.localZipPath:/tmp/grid/ror.zip}")
    private String zipFilePath;

    @Value("${org.orcid.core.orgs.ror.localDataPath:/tmp/grid/ror.json}")
    private String localDataPath;

    @Value("${org.orcid.core.orgs.ror.indexAllEnabled:false}")
    private boolean indexAllEnabled;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;

    @Value("${org.orcid.core.orgs.ror.zenodoRecordsUrl:https://zenodo.org/api/records?communities=ror-data}")
    private String rorZenodoRecordsUrl;

    @Resource
    private FileRotator fileRotator;

    private Set<Long> UPDATED_RORS;

    @Override
    public String getSourceName() {
        return "ROR";
    }

    @Override
    public boolean loadOrgData() {
        if (!enabled) {
            throw new LoadSourceDisabledException(getSourceName());
        }

        return loadData();
    }

    @Override
    public boolean downloadOrgData() {
        try {
            fileRotator.removeFileIfExists(zipFilePath);
            fileRotator.removeFileIfExists(localDataPath);

            ZenodoRecords zenodoRecords = orgDataClient.get(rorZenodoRecordsUrl + "&sort=mostrecent&size=1", userAgent, ZenodoRecords.class);
            ZenodoRecordsHit zenodoHit = zenodoRecords.getHits().getHits().get(0);

            boolean success = false;

            // we are returning the collection ordered by mostrecent and size 1,
            // we need to
            // get the last element in the list that has the last version
            String zenodoUrl = zenodoHit.getFiles().get(zenodoHit.getFiles().size() > 0 ? zenodoHit.getFiles().size() - 1 : 0).getLinks().getSelf();
            LOGGER.info("Retrieving ROR data from: " + zenodoUrl);
            success = orgDataClient.downloadFile(zenodoUrl, userAgent, zipFilePath);

            try {
                LOGGER.info("Unzipping  ROR ....");
                unzipData();
            } catch (IOException e) {
                LOGGER.error("Error unzipping Zenodo ROR data", e);
                throw new RuntimeException(e);
            }
            return success;
        } catch (Exception e) {
            LOGGER.error("Error downloading Zenodo ROR data", e);
            return false;
        }
    }

    private void unzipData() throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            String zipEntryName = zipEntry.getName();
            if (zipEntryName.endsWith("v2.json")) {
                File jsonData = new File(localDataPath);
                FileOutputStream fos = new FileOutputStream(jsonData);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                break;
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private boolean loadData() {
        try {
            LOGGER.info("Loading ROR data...");
            Instant start = Instant.now();
            File fileToLoad = new File(localDataPath);
            if (!fileToLoad.exists()) {
                LOGGER.error("File {} doesn't exist", localDataPath);
                return false;
            }

            // ror returns the JSON as Array of institutes
            JsonNode rootNode = JsonUtils.read(fileToLoad);
            UPDATED_RORS = new HashSet<Long>();

            rootNode.forEach(institute -> {
                String sourceId = institute.get("id").isNull() ? null : institute.get("id").asText();
                String status = institute.get("status").isNull() ? null : institute.get("status").asText();
                if ("active".equalsIgnoreCase(status) || "inactive".equalsIgnoreCase(status)) {
                    ArrayNode namesNode = institute.get("names").isNull() ? null : (ArrayNode) institute.get("names");
                    String name = null;
                    String namesJson = null;

                    if (namesNode != null) {
                        for (JsonNode nameJson : namesNode) {
                            ArrayNode nameTypes = nameJson.get("types").isNull() ? null : (ArrayNode) nameJson.get("types");
                            for (JsonNode nameType : nameTypes) {
                                if (StringUtils.equalsIgnoreCase(nameType.asText(), "ror_display")) {
                                    name = nameJson.get("value").asText();
                                    break;
                                }
                            }
                        }
                        namesJson = namesNode.toString();
                    }

                    StringJoiner sj = new StringJoiner(",");
                    String orgType = null;
                    if (!institute.get("types").isNull()) {
                        ((ArrayNode) institute.get("types")).forEach(x -> sj.add(x.textValue()));
                        orgType = sj.toString();
                    }

                    // location node

                    ArrayNode locationsNode = institute.get("locations").isNull() ? null : (ArrayNode) institute.get("locations");
                    Iso3166Country country = null;
                    String region = null;
                    String city = null;

                    String locationsJson = null;
                    if (locationsNode != null) {
                        for (JsonNode locationJson : locationsNode) {
                            JsonNode geoDetailsNode = locationJson.get("geonames_details").isNull() ? null : (JsonNode) locationJson.get("geonames_details");

                            if (geoDetailsNode != null) {
                                String countryCode = geoDetailsNode.get("country_code").isNull() ? null : geoDetailsNode.get("country_code").asText();
                                country = StringUtils.isBlank(countryCode) ? null : Iso3166Country.fromValue(countryCode);
                                // for now storing just the first location
                                city = geoDetailsNode.get("name").isNull() ? null : geoDetailsNode.get("name").asText();
                                if (country != null) {
                                    break;
                                }
                            }

                        }
                        locationsJson = locationsNode.toString();
                    }

                    ArrayNode urls = institute.get("links").isNull() ? null : (ArrayNode) institute.get("links");
                    // Use the first URL
                    String url = (urls != null && urls.size() > 0) ? urls.get(0).asText() : null;

                    // Creates or updates an institute
                    OrgDisambiguatedEntity entity = processInstitute(sourceId, name, country, city, region, url, orgType, locationsJson, namesJson);

                    // Creates external identifiers
                    processExternalIdentifiers(entity, institute);
                } else if ("redirected".equals(status)) {
                    String primaryId = institute.get("redirect").isNull() ? null : institute.get("redirect").asText();
                    deprecateOrg(sourceId, primaryId);
                } else if ("withdrawn".equals(status) || "obsolete".equals(status)) {
                    obsoleteOrg(sourceId);
                } else {
                    LOGGER.error("Illegal status '" + status + "' for institute " + sourceId);
                }
            });

            // Check if any RORs with external identifiers updated and group
            // them
            groupRORsWithUpdatedExternalModifiers();

            LOGGER.info("Time taken to process the data: {}", Duration.between(start, Instant.now()).toString());
            return true;
        } catch (Exception e) {
            LOGGER.error("Error loading ROR data", e);
            return false;
        }
    }

    private OrgDisambiguatedEntity processInstitute(String sourceId, String name, Iso3166Country country, String city,

            String region, String url, String orgType, String locationsJson, String namesJson) {
        OrgDisambiguatedEntity existingBySourceId = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, OrgDisambiguatedSourceType.ROR.name());
        if (existingBySourceId != null) {
            if (entityChanged(existingBySourceId, name, country.value(), city, region, url, orgType) || indexAllEnabled) {
                existingBySourceId.setCity(city);
                existingBySourceId.setCountry(country.name());
                existingBySourceId.setName(name);
                existingBySourceId.setOrgType(orgType);
                existingBySourceId.setRegion(region);
                existingBySourceId.setUrl(url);
                existingBySourceId.setLocationsJson(locationsJson);
                existingBySourceId.setNamesJson(namesJson);

                existingBySourceId.setIndexingStatus(IndexingStatus.PENDING);
                try {
                    // mark group for indexing
                    new OrgGrouping(existingBySourceId, orgDisambiguatedManager).markGroupForIndexing(orgDisambiguatedDao);

                } catch (Exception ex) {
                    LOGGER.error("Error when grouping by ROR and marking group orgs for reindexing, eating the exception", ex);
                }
                orgDisambiguatedManager.updateOrgDisambiguated(existingBySourceId);
            }
            return existingBySourceId;
        }

        // Create a new disambiguated org
        OrgDisambiguatedEntity newOrg = createDisambiguatedOrg(sourceId, name, orgType, country, city, region, url, locationsJson, namesJson);
        try {
            // mark group for indexing
            new OrgGrouping(newOrg, orgDisambiguatedManager).markGroupForIndexing(orgDisambiguatedDao);
        } catch (Exception ex) {
            LOGGER.error("Error when grouping by ROR and removing related orgs solr index, eating the exception", ex);
        }
        return newOrg;
    }

    private void processExternalIdentifiers(OrgDisambiguatedEntity org, JsonNode institute) {
        ArrayNode nodes = institute.get("external_ids") == null ? null : (ArrayNode) institute.get("external_ids");
        if (nodes != null) {
            for (JsonNode entry : nodes) {
                String identifierTypeName = entry.get("type").asText().toUpperCase();
                String preferredId = entry.get("preferred").isNull() ? null : entry.get("preferred").asText();
                if (StringUtils.equalsIgnoreCase(OrgDisambiguatedSourceType.GRID.name(), identifierTypeName)) {
                    JsonNode extId = (JsonNode) entry.get("all");
                    setExternalId(org, identifierTypeName, preferredId, extId);
                    UPDATED_RORS.add(org.getId());
                } else {
                    ArrayNode elements = (ArrayNode) entry.get("all");
                    for (JsonNode extId : elements) {
                        setExternalId(org, identifierTypeName, preferredId, extId);
                        UPDATED_RORS.add(org.getId());
                    }
                }
            }
        }
    }

    private void setExternalId(OrgDisambiguatedEntity org, String identifierTypeName, String preferredId, JsonNode extId) {
        // If the external identifier doesn't exists yet
        OrgDisambiguatedExternalIdentifierEntity existingExternalId = orgDisambiguatedExternalIdentifierDao.findByDetails(org.getId(), extId.asText(),
                identifierTypeName);
        Boolean preferred = extId.asText().equals(preferredId);
        if (existingExternalId == null) {
            if (preferred) {
                createExternalIdentifier(org, extId.asText(), identifierTypeName, true);
            } else {
                createExternalIdentifier(org, extId.asText(), identifierTypeName, false);
            }
        } else {
            if (existingExternalId.getPreferred() != preferred) {
                existingExternalId.setPreferred(preferred);
                orgDisambiguatedManager.updateOrgDisambiguatedExternalIdentifier(existingExternalId);
                LOGGER.info("External identifier for {} with ext id {} and type {} was updated", new Object[] { org.getId(), extId.asText(), identifierTypeName });
            } else {
                LOGGER.info("External identifier for {} with ext id {} and type {} already exists", new Object[] { org.getId(), extId.asText(), identifierTypeName });
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
    private OrgDisambiguatedEntity createDisambiguatedOrg(String sourceId, String name, String orgType, Iso3166Country country, String city, String region, String url,String locationsJson, String namesJson) {
        LOGGER.info("Creating disambiguated org {}", name);
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setCountry(country != null ? country.name() : null);
        orgDisambiguatedEntity.setCity(city);
        orgDisambiguatedEntity.setRegion(region);
        orgDisambiguatedEntity.setUrl(url);
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(sourceId);
        orgDisambiguatedEntity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
        orgDisambiguatedEntity.setLocationsJson(locationsJson);
        orgDisambiguatedEntity.setNamesJson(namesJson);
        orgDisambiguatedManager.createOrgDisambiguated(orgDisambiguatedEntity);
        return orgDisambiguatedEntity;
    }

    /**
     * Creates an external identifier in the
     * org_disambiguated_external_identifier table
     */
    private boolean createExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String identifier, String externalIdType, Boolean preferred) {
        LOGGER.info("Creating external identifier for {}", disambiguatedOrg.getId());
        OrgDisambiguatedExternalIdentifierEntity externalIdentifier = new OrgDisambiguatedExternalIdentifierEntity();
        externalIdentifier.setIdentifier(identifier);
        externalIdentifier.setIdentifierType(externalIdType);
        externalIdentifier.setOrgDisambiguated(disambiguatedOrg);
        externalIdentifier.setPreferred(preferred);
        orgDisambiguatedManager.createOrgDisambiguatedExternalIdentifier(externalIdentifier);
        return true;
    }

    /**
     * Mark an existing org as DEPRECATED
     */
    private void deprecateOrg(String sourceId, String primarySourceId) {
        LOGGER.info("Deprecating org {} for {}", sourceId, primarySourceId);
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, OrgDisambiguatedSourceType.ROR.name());
        if (existingEntity != null) {
            if (existingEntity.getStatus() == null || !existingEntity.getStatus().equals(OrganizationStatus.DEPRECATED.name())
                    || !existingEntity.getSourceParentId().equals(primarySourceId)) {
                existingEntity.setStatus(OrganizationStatus.DEPRECATED.name());
                existingEntity.setSourceParentId(primarySourceId);
                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                orgDisambiguatedManager.updateOrgDisambiguated(existingEntity);
            }
        } else {
            OrgDisambiguatedEntity deprecatedEntity = new OrgDisambiguatedEntity();
            deprecatedEntity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
            deprecatedEntity.setStatus(OrganizationStatus.DEPRECATED.name());
            deprecatedEntity.setSourceId(sourceId);
            deprecatedEntity.setSourceParentId(primarySourceId);
            // We don't need to index it
            deprecatedEntity.setIndexingStatus(IndexingStatus.DONE);
            orgDisambiguatedManager.createOrgDisambiguated(deprecatedEntity);
        }
    }

    /**
     * Mark an existing org as OBSOLETE
     */
    private void obsoleteOrg(String sourceId) {
        LOGGER.info("Marking or as obsolete {}", sourceId);
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(sourceId, OrgDisambiguatedSourceType.ROR.name());
        if (existingEntity != null) {
            if (existingEntity.getStatus() == null || !existingEntity.getStatus().equals(OrganizationStatus.OBSOLETE.name())) {
                existingEntity.setStatus(OrganizationStatus.OBSOLETE.name());
                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                orgDisambiguatedManager.updateOrgDisambiguated(existingEntity);
                new OrgGrouping(existingEntity, orgDisambiguatedManager).ungroupObsoleteRorForIndexing(orgDisambiguatedDao);
            }
        } else {
            OrgDisambiguatedEntity obsoletedEntity = new OrgDisambiguatedEntity();
            obsoletedEntity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
            obsoletedEntity.setStatus(OrganizationStatus.OBSOLETE.name());
            obsoletedEntity.setSourceId(sourceId);
            // We don't need to index it
            obsoletedEntity.setIndexingStatus(IndexingStatus.DONE);
            orgDisambiguatedManager.createOrgDisambiguated(obsoletedEntity);
            new OrgGrouping(obsoletedEntity, orgDisambiguatedManager).ungroupObsoleteRorForIndexing(orgDisambiguatedDao);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private void groupRORsWithUpdatedExternalModifiers() {
        for (Long id : UPDATED_RORS) {
            OrgDisambiguatedEntity entity = orgDisambiguatedDao.find(id);
            if (entity != null) {
                entity.setIndexingStatus(IndexingStatus.PENDING);
                try {
                    // mark group for indexing
                    new OrgGrouping(entity, orgDisambiguatedManager).markGroupForIndexing(orgDisambiguatedDao);

                } catch (Exception ex) {
                    LOGGER.error("Error when grouping by ROR and marking group orgs for reindexing, eating the exception", ex);
                }
                entity = orgDisambiguatedManager.updateOrgDisambiguated(entity);

            }
        }
    }

}