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
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
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
    @Option(name = "-f", usage = "Path to json file containing Ringgold parents to load into DB")
    private File fileToLoad;
    @Option(name = "-d", usage = "Path to json file containing Ringgold deleted IDs to process")
    private File deletedIdsFile;
    @Option(name = "-z", usage = "Path to zip file containing Ringgold data to process")
    private File zipFile;

    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    private int numAdded;
    private int numUpdated;
    private int numUnchanged;
    private int numSkipped;
    private int numDeleted;
    private int numDeletionsSkipped;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private Map<Integer, List<JsonNode>> altNamesMap = new HashMap<>();
    private Map<Integer, List<JsonNode>> identifiersMap = new HashMap<>();
    private Map<Integer, JsonNode> dnNameMap = new HashMap<>();

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
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedSolrDao = (OrgDisambiguatedSolrDao) context.getBean("orgDisambiguatedSolrDao");
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, deletedIdsFile, zipFile)) {
            throw new CmdLineException(parser, "At least one of -f | -d | -z must be specificed");
        }
    }

    private void execute() {
        LOGGER.info("Execute");
        try (ZipFile zip = new ZipFile(zipFile)) {
            processAltNames(zip);
            processIdentifiers(zip);
            processInstitutions(zip);
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip file", e);
        } finally {
            LOGGER.info("Number added={}, number updated={}, number unchanged={}, num skipped={}", new Object[] { numAdded, numUpdated, numUnchanged, numSkipped });
        }
    }

    private JsonNode getJsonNode(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        LOGGER.info("Generating json node for: " + entry.getName());
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        return JsonUtils.read(reader);
    }

    private void processAltNames(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing alt names");
        JsonNode altNames = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_alt_names.json"));
        altNames.forEach(altName -> {
            Integer ringgoldId = altName.get("ringgold_id").asInt();
            if ("DN".equals(altName.get("notes").asText())) {
                // If there is already a DN name for this org, lets keep just
                // the newest one
                if (dnNameMap.containsKey(ringgoldId)) {
                    JsonNode existing = dnNameMap.get(ringgoldId);
                    try {
                        Date existingDate = dateFormat.parse(existing.get("timestamp").asText());
                        Date date = dateFormat.parse(altName.get("timestamp").asText());
                        if (date.after(existingDate)) {
                            dnNameMap.put(ringgoldId, altName);
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

    private void processIdentifiers(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing identifiers");
        JsonNode identifiers = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_identifiers.json"));

        identifiers.forEach(identifier -> {
            Integer ringgoldId = identifier.get("ringgold_id").asInt();
            identifiersMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(identifier);
        });
    }

    private void processInstitutions(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing institutions");
        JsonNode institutions = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_institutions.json"));

        institutions.forEach(institution -> {
            processInstitution(institution);
            Integer ringgoldId = institution.get("ringgold_id").asInt();
            if (altNamesMap.containsKey(ringgoldId)) {
                List<JsonNode> altNamesList = altNamesMap.get(ringgoldId);
            }
        });
    }

    private void processDeletedElemens(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing identifiers");
        JsonNode deletedIds = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_deleted_ids.json"));
        deletedIds.forEach(element -> {
            Integer oldId = element.get("old_ringgold_id").asInt();
            Integer newId = element.get("new_ringgold_id").asInt();
            OrganizationStatus status = OrganizationStatus.OBSOLETE;
            if (newId == null) {
                status = OrganizationStatus.DEPRECATED;
            }
            LOGGER.info("Deleting org {} with status {}", oldId, status);
        });
    }

    private void processInstitution(JsonNode institution) {
        Integer recId = institution.get("rec_id").asInt();
        Integer ringgoldId = institution.get("ringgold_id").asInt();
        LOGGER.info("Processing: {} rec_id: {}", ringgoldId, recId);
        Integer parentId = institution.get("parent_ringgold_id").asInt();
        String name = institution.get("name").asText();
        Iso3166Country country = Iso3166Country.fromValue(institution.get("country").asText());
        String state = institution.get("state").asText();
        String city = institution.get("city").asText();
        String type = institution.get("type").asText();

        // If we already have a DN name for it, lets keep just the newest
        // one
        if (dnNameMap.containsKey(ringgoldId)) {
            name = dnNameMap.get(ringgoldId).get("name").asText();
        }

        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(ringgoldId), RINGGOLD_SOURCE_TYPE);
        Date now = new Date();
        if (existingEntity == null) {
            // TODO: create org
            OrgDisambiguatedEntity newEntity = new OrgDisambiguatedEntity();
            newEntity.setDateCreated(now);
            newEntity.setLastIndexedDate(now);
            newEntity.setCity(city);
            newEntity.setCountry(country);
            newEntity.setIndexingStatus(IndexingStatus.PENDING);
            newEntity.setName(name);
            newEntity.setOrgType(type);
            newEntity.setRegion(state);
            newEntity.setSourceId(String.valueOf(ringgoldId));
            newEntity.setSourceParentId(String.valueOf(parentId));
            newEntity.setSourceType(RINGGOLD_SOURCE_TYPE);
            orgDisambiguatedDao.persist(newEntity);
            numAdded++;
        } else {
            // TODO: check if the org have changed
            if (changed(existingEntity, parentId, name, country, city, state, type)) {
                existingEntity.setCity(city);
                existingEntity.setCountry(country);
                existingEntity.setIndexingStatus(IndexingStatus.REINDEX);
                existingEntity.setLastModified(now);
                existingEntity.setName(name);
                existingEntity.setOrgType(type);
                existingEntity.setRegion(state);
                orgDisambiguatedDao.merge(existingEntity);
                numUpdated++;
            } else {
                numUnchanged++;
            }
        }
    }

    private void processNew() {

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
