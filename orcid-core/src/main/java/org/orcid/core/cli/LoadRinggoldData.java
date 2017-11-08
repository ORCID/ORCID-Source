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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;

public class LoadRinggoldData {
    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";
    private static final String RINGGOLD_SOURCE_TYPE = "RINGGOLD";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRinggoldData.class);
    @Option(name = "-z", usage = "Path to zip file containing Ringgold data to process")
    private File zipFile;

    private OrgDao orgDao;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;

    private int numAdded;
    private int numUpdated;
    private int numUnchanged;
    private int numObsoleted;
    private int numDeprecated;
    private int numSkipDeletion;
    private int numAddedOrgs;
    private int numUpdatedOrgs;
    private int numAddedExtIds;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private Map<Integer, List<JsonNode>> altNamesMap = new HashMap<>();
    private Map<Integer, List<JsonNode>> identifiersMap = new HashMap<>();
    private Map<Integer, JsonNode> dnNameMap = new HashMap<>();
    private Map<Integer, Integer> deletedElementsMap = new HashMap<>();

    public static void main(String[] args) {
        LoadRinggoldData loadRinggoldData = new LoadRinggoldData();
        CmdLineParser parser = new CmdLineParser(loadRinggoldData);
        try {
            parser.parseArgument(args);
            loadRinggoldData.validateArgs(parser);
            loadRinggoldData.init();
            loadRinggoldData.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            System.err.println(t);
            t.printStackTrace();
            System.exit(2);
        }
        System.exit(0);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDao = (OrgDao) context.getBean("orgDao");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedExternalIdentifierDao = (OrgDisambiguatedExternalIdentifierDao) context.getBean("orgDisambiguatedExternalIdentifierDao");
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.anyNull(zipFile)) {
            throw new CmdLineException(parser, "-f must be specificed");
        }
    }

    private void execute() {
        LOGGER.info("Execute");
        if (zipFile != null) {
            try (ZipFile zip = new ZipFile(zipFile)) {
                processAltNamesFile(zip);
                processIdentifiersFile(zip);
                processDeletedElementsFile(zip);
                processInstitutions(zip);
                processDeletedElements();
            } catch (IOException e) {
                throw new RuntimeException("Error reading zip file", e);
            } finally {
                LOGGER.info(
                        "Number added={}, number updated={}, number unchanged={}, obsoleted={}, num deprecated={}, skip deletion={}, added orgs={}, updated orgs={}, added ext ids={}",
                        new Object[] { numAdded, numUpdated, numUnchanged, numObsoleted, numDeprecated, numSkipDeletion, numAddedOrgs, numUpdatedOrgs, numAddedExtIds });
            }
        }
    }

    private JsonNode getJsonNode(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        LOGGER.info("Generating json node for: " + entry.getName());
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        return JsonUtils.read(reader);
    }

    private void processAltNamesFile(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing alt names");
        JsonNode altNames = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_alt_names.json"));
        altNames.forEach(altName -> {
            Integer ringgoldId = altName.get("ringgold_id").asInt();
            if (altName.has("notes") && "DN".equals(altName.get("notes").asText())) {
                // If there is already a DN name for this org, lets keep just
                // the newest one
                if (dnNameMap.containsKey(ringgoldId)) {
                    JsonNode existing = dnNameMap.get(ringgoldId);
                    try {
                        Date existingDate = dateFormat.parse(existing.get("timestamp").asText());
                        Date date = dateFormat.parse(altName.get("timestamp").asText());
                        if (date.after(existingDate)) {
                            // If the DN name is newer than the existing one,
                            // put the existing one in the list of alt names and
                            // set this one as the DN name
                            altNamesMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(existing);
                            dnNameMap.put(ringgoldId, altName);
                        } else {
                            // If the DN name is older than the existing one,
                            // put it in the list of alt names
                            altNamesMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(altName);
                        }
                    } catch (ParseException e) {
                        LOGGER.error("Exception parsing date {}", e);
                    }
                } else {
                    dnNameMap.put(ringgoldId, altName);
                }
            } else {
                altNamesMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(altName);
            }
        });
    }

    private void processIdentifiersFile(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing identifiers");
        JsonNode identifiers = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_identifiers.json"));
        identifiers.forEach(identifier -> {
            Integer ringgoldId = identifier.get("ringgold_id").asInt();
            identifiersMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(identifier);
        });
    }

    private void processDeletedElementsFile(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing deleted elements");
        JsonNode deletedIds = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_deleted_ids.json"));
        deletedIds.forEach(element -> {
            Integer oldId = element.has("old_ringgold_id") ? element.get("old_ringgold_id").asInt() : null;
            Integer newId = element.has("new_ringgold_id") ? element.get("new_ringgold_id").asInt() : null;
            deletedElementsMap.put(oldId, newId);
        });
    }

    private void processInstitutions(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing institutions");
        JsonNode institutions = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_institutions.json"));
        institutions.forEach(institution -> {
            OrgDisambiguatedEntity entity = processInstitution(institution);
            Integer ringgoldId = institution.get("ringgold_id").asInt();
            // Create external identifiers
            if (identifiersMap.containsKey(ringgoldId)) {
                generateExternalIdentifiers(entity, identifiersMap.get(ringgoldId));
            }

            // Create orgs based on the alt names information
            if (altNamesMap.containsKey(ringgoldId)) {
                generateOrganizations(entity, altNamesMap.get(ringgoldId));
            }
        });
    }

    private void processDeletedElements() {
        LOGGER.info("Processing deleted elements");
        deletedElementsMap.forEach((oldId, newId) -> {
            OrganizationStatus status = OrganizationStatus.OBSOLETE;
            if (newId == null) {
                status = OrganizationStatus.DEPRECATED;
            }

            LOGGER.info("Deleting org {} with status {}", oldId, status);
            OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(oldId), RINGGOLD_SOURCE_TYPE);
            OrgDisambiguatedEntity replacementEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(newId), RINGGOLD_SOURCE_TYPE);
            if (existingEntity != null) {
                // Check if the status is up to date, if not, update it
                if (!status.name().equals(existingEntity.getStatus())) {
                    existingEntity.setStatus(status.name());
                    if (newId != null) {
                        existingEntity.setSourceParentId(String.valueOf(newId));
                    }
                    orgDisambiguatedDao.merge(existingEntity);
                    if (status.equals(OrganizationStatus.OBSOLETE)) {
                        numObsoleted++;
                    } else {
                        numDeprecated++;
                    }

                    if (replacementEntity != null) {
                        // Replace the org disambiguated id in all org that had
                        // the
                        // deprecated/obsoleted entity
                        orgDisambiguatedDao.replace(existingEntity.getId(), replacementEntity.getId());
                    } else {
                        LOGGER.warn("Couldn't find replacement entity ringgold_id:'{}' for ringgold_id:'{}'", newId, oldId);
                    }
                }
            } else {
                LOGGER.warn("Couldn't find entity to be deleted with ringgold_id: '{}'", oldId);
                numSkipDeletion++;
            }
        });
    }

    private OrgDisambiguatedEntity processInstitution(JsonNode institution) {
        Integer recId = institution.get("rec_id").asInt();
        Integer ringgoldId = institution.get("ringgold_id").asInt();
        LOGGER.info("Processing ringgold_id: {} rec_id: {}", ringgoldId, recId);
        Integer parentId = institution.get("parent_ringgold_id").asInt();
        String name = institution.get("name").asText();
        Iso3166Country country = Iso3166Country.fromValue(institution.get("country").asText());
        String state = institution.has("state") ? institution.get("state").asText() : null;
        String city = institution.get("city").asText();
        String type = institution.get("type").asText();

        String originalName = null;

        // Replace the name with the DN (Diacritic Name) name
        if (dnNameMap.containsKey(ringgoldId)) {
            // Save the original name
            originalName = name;
            name = dnNameMap.get(ringgoldId).get("name").asText();
        }

        OrgDisambiguatedEntity entity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(ringgoldId), RINGGOLD_SOURCE_TYPE);
        Date now = new Date();
        if (entity == null) {
            entity = new OrgDisambiguatedEntity();
            entity.setDateCreated(now);
            entity.setLastIndexedDate(now);
            entity.setCity(city);
            entity.setCountry(country);
            entity.setName(name);
            entity.setOrgType(type);
            entity.setRegion(state);
            entity.setSourceId(String.valueOf(ringgoldId));
            entity.setSourceParentId(String.valueOf(parentId));
            entity.setSourceType(RINGGOLD_SOURCE_TYPE);
            entity.setIndexingStatus(IndexingStatus.PENDING);
            orgDisambiguatedDao.persist(entity);
            numAdded++;
        } else {
            // If the element have changed
            if (changed(entity, parentId, name, country, city, state, type)) {
                entity.setCity(city);
                entity.setCountry(country);
                entity.setLastModified(now);
                entity.setName(name);
                entity.setOrgType(type);
                entity.setRegion(state);
                entity.setIndexingStatus(IndexingStatus.REINDEX);
                orgDisambiguatedDao.merge(entity);
                numUpdated++;
            } else {
                numUnchanged++;
            }
        }

        // If the original name was replaces by the DN name, lets create an org
        // with the original name
        if (originalName != null) {
            generateOrganizationFromInstitutionNode(entity, originalName, country, city, state);
        }

        return entity;
    }

    private void generateExternalIdentifiers(OrgDisambiguatedEntity disambiguatedEntity, List<JsonNode> identifiers) {
        identifiers.forEach(identifierNode -> {
            String type = identifierNode.get("identifier_type").asText();
            LOGGER.info("Processing external identifier {} for {}", type, disambiguatedEntity.getId());
            String identifier = identifierNode.get("value").asText();
            if (!orgDisambiguatedExternalIdentifierDao.exists(disambiguatedEntity.getId(), identifier, type)) {
                OrgDisambiguatedExternalIdentifierEntity newEntity = new OrgDisambiguatedExternalIdentifierEntity();
                Date now = new Date();
                newEntity.setDateCreated(now);
                newEntity.setLastModified(now);
                newEntity.setIdentifier(identifier);
                newEntity.setIdentifierType(type);
                newEntity.setOrgDisambiguated(disambiguatedEntity);
                newEntity.setPreferred(false);
                orgDisambiguatedExternalIdentifierDao.persist(newEntity);
                numAddedExtIds++;
            }
        });
    }

    private void generateOrganizations(OrgDisambiguatedEntity disambiguatedEntity, List<JsonNode> altNames) {
        Date now = new Date();
        altNames.forEach(altName -> {
            String name = altName.get("name").asText();
            LOGGER.info("Processing organization {} for {}", name, disambiguatedEntity.getId());
            String city = altName.get("city").asText();
            Iso3166Country country = Iso3166Country.fromValue(altName.get("country").asText());
            OrgEntity existingOrg = orgDao.findByNameCityRegionAndCountry(name, city, null, country);
            if (existingOrg != null) {
                if (existingOrg.getOrgDisambiguated() == null) {
                    existingOrg.setOrgDisambiguated(disambiguatedEntity);
                    existingOrg.setLastModified(now);
                    orgDao.merge(existingOrg);
                    numUpdatedOrgs++;
                }
            } else {
                OrgEntity newOrg = new OrgEntity();
                newOrg.setDateCreated(now);
                newOrg.setLastModified(now);
                newOrg.setCity(city);
                newOrg.setCountry(country);
                newOrg.setName(name);
                newOrg.setOrgDisambiguated(disambiguatedEntity);
                orgDao.persist(newOrg);
                numAddedOrgs++;
            }
        });
    }

    private void generateOrganizationFromInstitutionNode(OrgDisambiguatedEntity disambiguatedEntity, String name, Iso3166Country country, String city, String region) {
        Date now = new Date();
        OrgEntity existingOrg = orgDao.findByNameCityRegionAndCountry(name, city, region, country);
        if (existingOrg != null) {
            if (existingOrg.getOrgDisambiguated() == null) {
                existingOrg.setOrgDisambiguated(disambiguatedEntity);
                existingOrg.setLastModified(now);
                orgDao.merge(existingOrg);
                numUpdatedOrgs++;
            }
        } else {
            OrgEntity newOrg = new OrgEntity();
            newOrg.setDateCreated(now);
            newOrg.setLastModified(now);
            newOrg.setRegion(region);
            newOrg.setCity(city);
            newOrg.setCountry(country);
            newOrg.setName(name);
            newOrg.setOrgDisambiguated(disambiguatedEntity);
            orgDao.persist(newOrg);
            numAddedOrgs++;
        }
    }

    private boolean changed(OrgDisambiguatedEntity entity, Integer parentId, String name, Iso3166Country country, String city, String state, String type) {
        if (!parentId.equals(Integer.valueOf(entity.getSourceParentId())) || !name.equals(entity.getName()) || !country.equals(entity.getCountry())
                || !city.equals(entity.getCity()) || !type.equals(entity.getOrgType())) {
            return true;
        }

        String existingRegion = entity.getRegion();
        if (state == null) {
            if (existingRegion != null) {
                return true;
            }
        } else if (!state.equals(existingRegion)) {
            return true;
        }

        return false;
    }

}
