package org.orcid.core.orgs.load.source.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.orgs.load.io.FileRotator;
import org.orcid.core.orgs.load.io.FtpsFileDownloader;
import org.orcid.core.orgs.load.source.LoadSourceDisabledException;
import org.orcid.core.orgs.load.source.OrgLoadSource;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class RinggoldOrgLoadSource implements OrgLoadSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RinggoldOrgLoadSource.class);

    @Resource(name = "ringgoldFtpsFileDownloader")
    private FtpsFileDownloader ftpsFileDownloader;

    @Resource
    private OrgDao orgDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

    @Resource
    private FileRotator fileRotator;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Value("${org.orcid.core.orgs.ringgold.enabled:true}")
    private boolean enabled;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";

    private static final List<String> ALLOWED_EXTERNAL_IDENTIFIERS = Arrays.asList("ISNI", "IPED", "NCES", "OFR");

    @Override
    public String getSourceName() {
        return "RINGGOLD";
    }

    @Override
    public boolean loadLatestOrgs() {
        if (!enabled) {
            throw new LoadSourceDisabledException(getSourceName());
        }

        if (downloadData()) {
            importData();
            return true;
        } else {
            return false;
        }
    }

    private boolean downloadData() {
        fileRotator.removeFileIfExists(ftpsFileDownloader.getLocalFilePath());
        return ftpsFileDownloader.downloadFile();
    }

    private void importData() {
        Map<Integer, List<JsonNode>> altNamesMap = new HashMap<>();
        Map<Integer, List<JsonNode>> identifiersMap = new HashMap<>();
        Map<Integer, JsonNode> dnNameMap = new HashMap<>();
        Map<Integer, Integer> deletedElementsMap = new HashMap<>();

        try (ZipFile zip = new ZipFile(ftpsFileDownloader.getLocalFilePath())) {
            processAltNamesFile(zip, altNamesMap, dnNameMap);
            processIdentifiersFile(zip, identifiersMap);
            processDeletedElementsFile(zip, deletedElementsMap);
            processInstitutions(zip, altNamesMap, identifiersMap, dnNameMap);
            processDeletedElements(deletedElementsMap);
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip file", e);
        } finally {
            LOGGER.info("Ringgold import completed");
        }
    }

    private JsonNode getJsonNode(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        LOGGER.info("Generating json node for: " + entry.getName());
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        return JsonUtils.read(reader);
    }

    private void processAltNamesFile(ZipFile mainFile, Map<Integer, List<JsonNode>> altNamesMap, Map<Integer, JsonNode> dnNameMap)
            throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing alt names");
        JsonNode altNames = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_alt_names.json"));
        processAltNames(altNames, altNamesMap, dnNameMap);
    }

    private void processAltNames(JsonNode altNames, Map<Integer, List<JsonNode>> altNamesMap, Map<Integer, JsonNode> dnNameMap) {
        altNames.forEach(altName -> {
            Integer ringgoldId = altName.get("ringgold_id").asInt();
            if (altName.has("notes") && "DN".equals(altName.get("notes").asText())) {
                // If there is already a DN name for this org, lets keep just
                // the newest one
                if (dnNameMap.containsKey(ringgoldId)) {
                    JsonNode existing = dnNameMap.get(ringgoldId);
                    try {
                        Date existingDate = DATE_FORMAT.parse(existing.get("timestamp").asText());
                        Date date = DATE_FORMAT.parse(altName.get("timestamp").asText());
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

    private void processIdentifiersFile(ZipFile mainFile, Map<Integer, List<JsonNode>> identifiersMap) throws UnsupportedEncodingException, IOException {
        JsonNode identifiers = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_identifiers.json"));
        processIdentifiers(identifiers, identifiersMap);
    }

    private void processIdentifiers(JsonNode identifiers, Map<Integer, List<JsonNode>> identifiersMap) {
        LOGGER.info("Processing identifiers");
        identifiers.forEach(identifier -> {
            Integer ringgoldId = identifier.get("ringgold_id").asInt();
            String identifierType = identifier.get("identifier_type").asText();
            if (ALLOWED_EXTERNAL_IDENTIFIERS.contains(identifierType)) {
                identifiersMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(identifier);
            } else {
                LOGGER.info("Ignoring identifier {} of type {}", ringgoldId, identifierType);
            }
        });
    }

    private void processDeletedElementsFile(ZipFile mainFile, Map<Integer, Integer> deletedElementsMap) throws UnsupportedEncodingException, IOException {
        JsonNode deletedIds = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_deleted_ids.json"));
        processDeletedElements(deletedIds, deletedElementsMap);
    }

    private void processDeletedElements(JsonNode deletedIds, Map<Integer, Integer> deletedElementsMap) {
        LOGGER.info("Processing deleted elements");
        deletedIds.forEach(element -> {
            Integer oldId = element.has("old_ringgold_id") ? element.get("old_ringgold_id").asInt() : null;
            Integer newId = element.has("new_ringgold_id") ? element.get("new_ringgold_id").asInt() : null;
            deletedElementsMap.put(oldId, newId);
        });
    }

    private void processInstitutions(ZipFile mainFile, Map<Integer, List<JsonNode>> altNamesMap, Map<Integer, List<JsonNode>> identifiersMap,
            Map<Integer, JsonNode> dnNameMap) throws UnsupportedEncodingException, IOException {
        JsonNode institutions = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_institutions.json"));
        processInstitutions(institutions, altNamesMap, identifiersMap, dnNameMap);
    }

    private void processInstitutions(JsonNode institutions, Map<Integer, List<JsonNode>> altNamesMap, Map<Integer, List<JsonNode>> identifiersMap,
            Map<Integer, JsonNode> dnNameMap) {
        LOGGER.info("Processing institutions");
        institutions.forEach(institution -> {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    OrgDisambiguatedEntity entity = processInstitution(institution, dnNameMap);
                    Integer ringgoldId = institution.get("ringgold_id").asInt();
                    // Create external identifiers
                    generateExternalIdentifiers(entity, identifiersMap.get(ringgoldId));

                    // Create orgs based on the alt names information
                    if (altNamesMap.containsKey(ringgoldId)) {
                        generateOrganizations(entity, altNamesMap.get(ringgoldId));
                    }
                }
            });
        });
    }

    private void processDeletedElements(Map<Integer, Integer> deletedElementsMap) {
        LOGGER.info("Processing deleted elements");
        deletedElementsMap.forEach((oldId, newId) -> {
            OrganizationStatus status = OrganizationStatus.OBSOLETE;
            if (newId != null) {
                status = OrganizationStatus.DEPRECATED;
            }

            LOGGER.info("Deleting org {} with status {}", oldId, status);
            OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(oldId), OrgDisambiguatedSourceType.RINGGOLD.name());
            OrgDisambiguatedEntity replacementEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(newId), OrgDisambiguatedSourceType.RINGGOLD.name());
            if (existingEntity != null) {
                // Check if the status is up to date, if not, update it
                if (!status.name().equals(existingEntity.getStatus())) {
                    existingEntity.setStatus(status.name());
                    existingEntity.setIndexingStatus(IndexingStatus.REINDEX);
                    if (newId != null) {
                        existingEntity.setSourceParentId(String.valueOf(newId));
                    }
                    orgDisambiguatedDao.merge(existingEntity);

                    if (replacementEntity != null) {
                        orgManager.updateDisambiguatedOrgReferences(existingEntity.getId(), replacementEntity.getId());
                    } else {
                        LOGGER.warn("Couldn't find replacement entity ringgold_id:'{}' for ringgold_id:'{}'", newId, oldId);
                    }
                }
            } else {
                LOGGER.warn("Couldn't find entity to be deleted with ringgold_id: '{}'", oldId);
            }
        });
    }

    private OrgDisambiguatedEntity processInstitution(JsonNode institution, Map<Integer, JsonNode> dnNameMap) {
        Integer recId = institution.get("rec_id").asInt();
        Integer ringgoldId = institution.get("ringgold_id").asInt();
        LOGGER.info("Processing ringgold_id: {} rec_id: {}", ringgoldId, recId);
        Integer parentId = institution.get("parent_ringgold_id").asInt() == 0 ? null : institution.get("parent_ringgold_id").asInt();
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

        OrgDisambiguatedEntity entity = orgDisambiguatedDao.findBySourceIdAndSourceType(String.valueOf(ringgoldId), OrgDisambiguatedSourceType.RINGGOLD.name());
        Date now = new Date();
        if (entity == null) {
            entity = new OrgDisambiguatedEntity();
            entity.setLastIndexedDate(now);
            entity.setCity(city);
            entity.setCountry(country.name());
            entity.setName(name);
            entity.setOrgType(type);
            entity.setRegion(state);
            entity.setSourceId(String.valueOf(ringgoldId));
            if (parentId != null && parentId > 0) {
                entity.setSourceParentId(String.valueOf(parentId));
            }
            entity.setSourceType(OrgDisambiguatedSourceType.RINGGOLD.name());
            entity.setIndexingStatus(IndexingStatus.PENDING);
            orgDisambiguatedDao.persist(entity);
        } else {
            // If the element have changed
            if (changed(entity, parentId, name, country, city, state, type)) {
                entity.setCity(city);
                entity.setCountry(country.name());
                entity.setName(name);
                entity.setOrgType(type);
                entity.setRegion(state);
                entity.setIndexingStatus(IndexingStatus.REINDEX);
                orgDisambiguatedDao.merge(entity);
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
        Set<OrgDisambiguatedExternalIdentifierEntity> existingExternalIdentifiers = disambiguatedEntity.getExternalIdentifiers();
        Map<Pair<String, String>, OrgDisambiguatedExternalIdentifierEntity> existingExternalIdentifiersMap = new HashMap<>();

        if (existingExternalIdentifiers != null) {
            for (OrgDisambiguatedExternalIdentifierEntity entity : existingExternalIdentifiers) {
                Pair<String, String> id = Pair.of(entity.getIdentifierType(), entity.getIdentifier());
                existingExternalIdentifiersMap.put(id, entity);
            }
        }

        if (identifiers != null && !identifiers.isEmpty()) {
            identifiers.forEach(identifierNode -> {
                String type = identifierNode.get("identifier_type").asText();
                LOGGER.info("Processing external identifier {} for {}", type, disambiguatedEntity.getId());
                String value = identifierNode.get("value").asText();
                Pair<String, String> id = Pair.of(type, value);
                OrgDisambiguatedExternalIdentifierEntity existingEntity = existingExternalIdentifiersMap.get(id);
                // If the external identifier doesn't exists or it doesn't
                // belong to the disambiguatedEntity, lets create it
                if (existingEntity == null || !existingEntity.getOrgDisambiguated().getId().equals(disambiguatedEntity.getId())) {
                    OrgDisambiguatedExternalIdentifierEntity newEntity = new OrgDisambiguatedExternalIdentifierEntity();
                    newEntity.setIdentifier(value);
                    newEntity.setIdentifierType(type);
                    newEntity.setOrgDisambiguated(disambiguatedEntity);
                    newEntity.setPreferred(false);
                    orgDisambiguatedExternalIdentifierDao.persist(newEntity);
                } else {
                    existingExternalIdentifiersMap.remove(id);
                }
            });
        }

        // Then, remove all existing external identifiers that are not present
        // in the ringgold data anymore
        for (OrgDisambiguatedExternalIdentifierEntity extIdToBeRemoved : existingExternalIdentifiersMap.values()) {
            orgDisambiguatedExternalIdentifierDao.remove(extIdToBeRemoved.getId());
        }
    }

    private void generateOrganizations(OrgDisambiguatedEntity disambiguatedEntity, List<JsonNode> altNames) {
        altNames.forEach(altName -> {
            String name = altName.get("name").asText();
            LOGGER.info("Processing organization {} for {}", name, disambiguatedEntity.getId());
            String city = altName.get("city").asText();
            Iso3166Country country = Iso3166Country.fromValue(altName.get("country").asText());
            OrgEntity existingOrg = orgDao.findByNameCityRegionCountryAndType(name, city, "", country.name(), "RINGGOLD");
            if (existingOrg != null) {
                if (existingOrg.getOrgDisambiguated() == null) {
                    existingOrg.setOrgDisambiguated(disambiguatedEntity);
                    orgDao.merge(existingOrg);
                }
            } else {
                OrgEntity newOrg = new OrgEntity();
                newOrg.setCity(city);
                newOrg.setCountry(country.name());
                newOrg.setName(name);
                newOrg.setOrgDisambiguated(disambiguatedEntity);
                orgDao.persist(newOrg);
            }
        });
    }

    private void generateOrganizationFromInstitutionNode(OrgDisambiguatedEntity disambiguatedEntity, String name, Iso3166Country country, String city, String region) {
        OrgEntity existingOrg = orgDao.findByNameCityRegionCountryAndType(name, city, region, country.name(), "RINGGOLD");
        if (existingOrg != null) {
            if (existingOrg.getOrgDisambiguated() == null) {
                existingOrg.setOrgDisambiguated(disambiguatedEntity);
                orgDao.merge(existingOrg);
            }
        } else {
            OrgEntity newOrg = new OrgEntity();
            newOrg.setRegion(region);
            newOrg.setCity(city);
            newOrg.setCountry(country.name());
            newOrg.setName(name);
            newOrg.setOrgDisambiguated(disambiguatedEntity);
            orgDao.persist(newOrg);
        }
    }

    private boolean changed(OrgDisambiguatedEntity entity, Integer parentId, String name, Iso3166Country country, String city, String state, String type) {
        if (!name.equals(entity.getName()) || !country.name().equals(entity.getCountry()) || !city.equals(entity.getCity()) || !type.equals(entity.getOrgType())) {
            return true;
        }
        if (parentId == null) {
            if (entity.getSourceParentId() != null) {
                return true;
            }
        } else if (entity.getSourceParentId() == null || !parentId.equals(Integer.valueOf(entity.getSourceParentId()))) {
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

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
