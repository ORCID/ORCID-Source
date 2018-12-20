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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class LoadRinggoldData {
    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRinggoldData.class);
    
    private static final List<String> ALLOWED_EXTERNAL_IDENTIFIERS = Arrays.asList("ISNI", "IPED", "NCES", "OFR");
    
    @Option(name = "-z", usage = "Path to zip file containing Ringgold data to process")
    private File zipFile;
    @Option(name = "-d", usage = "Path to directory containing Ringgold data to process")
    private File directory;
    
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

    private TransactionTemplate transactionTemplate;
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }

    public void setOrgDisambiguatedDao(OrgDisambiguatedDao orgDisambiguatedDao) {
        this.orgDisambiguatedDao = orgDisambiguatedDao;
    }

    public void setOrgDisambiguatedExternalIdentifierDao(OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao) {
        this.orgDisambiguatedExternalIdentifierDao = orgDisambiguatedExternalIdentifierDao;
    }
    
    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

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
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(zipFile, directory)) {
            throw new CmdLineException(parser, "One of -f | -d must be specificed");
        }
        
        if(zipFile != null && !zipFile.exists()) {
            throw new CmdLineException(parser, "Couldn't find the zip file at: " + zipFile.getPath());
        }
        
        if(directory != null && (!directory.exists() || !directory.isDirectory()) ) {
            throw new CmdLineException(parser, "Couldn't find the directory at: " + directory.getPath());
        }
    }

    public void execute() {
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
        } else {
            File altNamesFile = null;
            File deletedElementsFile = null;
            File identifiersFile = null;
            File institutesFile = null;
            for(File file : directory.listFiles()) {
                if(file.isFile()) {
                    if(file.getName().endsWith("_alt_names.json")) {
                        altNamesFile = file;
                    } else if(file.getName().endsWith("_deleted_ids.json")) {
                        deletedElementsFile = file;
                    } else if(file.getName().endsWith("_identifiers.json")) {
                        identifiersFile = file;
                    } else if(file.getName().endsWith("_institutions.json")) {
                        institutesFile = file;
                    }
                }
            }
            if(institutesFile == null || !institutesFile.exists() || !institutesFile.isFile()) {
                throw new RuntimeException("Couldent find _institutions.json file or it is invalid");
            }
            processAltNamesFile(altNamesFile);
            processIdentifiersFile(identifiersFile);
            processDeletedElementsFile(deletedElementsFile);
            processInstitutions(institutesFile);
            processDeletedElements();
        }
    }

    private JsonNode getJsonNode(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        LOGGER.info("Generating json node for: " + entry.getName());
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        return JsonUtils.read(reader);
    }
    
    private JsonNode getJsonNode(File file) {
        return JsonUtils.read(file);
    }

    private void processAltNamesFile(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        LOGGER.info("Processing alt names");
        JsonNode altNames = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_alt_names.json"));
        processAltNames(altNames);
    }
    
    private void processAltNamesFile(File file) {
        JsonNode altNames = getJsonNode(file);
        processAltNames(altNames);
    }
    
    private void processAltNames(JsonNode altNames) {
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
        JsonNode identifiers = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_identifiers.json"));
        processIdentifiers(identifiers);
    }
    
    private void processIdentifiersFile(File file) {
        JsonNode identifiers = getJsonNode(file);
        processIdentifiers(identifiers);
    }
    
    private void processIdentifiers(JsonNode identifiers) {
        LOGGER.info("Processing identifiers");
        identifiers.forEach(identifier -> {
            Integer ringgoldId = identifier.get("ringgold_id").asInt();
            String identifierType = identifier.get("identifier_type").asText();
            if(ALLOWED_EXTERNAL_IDENTIFIERS.contains(identifierType)) {
                identifiersMap.computeIfAbsent(ringgoldId, element -> new ArrayList<>()).add(identifier);
            } else {
                LOGGER.info("Ignoring identifier {} of type {}", ringgoldId, identifierType);
            }
        });
    }

    private void processDeletedElementsFile(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        JsonNode deletedIds = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_deleted_ids.json"));
        processDeletedElements(deletedIds);
    }
    
    private void processDeletedElementsFile(File file) {
        JsonNode deletedIds = getJsonNode(file);
        processDeletedElements(deletedIds);
    }
    
    private void processDeletedElements(JsonNode deletedIds) {
        LOGGER.info("Processing deleted elements");
        deletedIds.forEach(element -> {
            Integer oldId = element.has("old_ringgold_id") ? element.get("old_ringgold_id").asInt() : null;
            Integer newId = element.has("new_ringgold_id") ? element.get("new_ringgold_id").asInt() : null;
            deletedElementsMap.put(oldId, newId);
        });
    }

    private void processInstitutions(ZipFile mainFile) throws UnsupportedEncodingException, IOException {
        JsonNode institutions = getJsonNode(mainFile, mainFile.getEntry("Ringgold_Identify_json_institutions.json"));
        processInstitutions(institutions);
    }
    
    private void processInstitutions(File file) {
        JsonNode institutions = getJsonNode(file);
        processInstitutions(institutions);
    }
    
    private void processInstitutions(JsonNode institutions) {
        LOGGER.info("Processing institutions");
        institutions.forEach(institution -> {            
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    OrgDisambiguatedEntity entity = processInstitution(institution);
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

    private void processDeletedElements() {
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
            entity.setDateCreated(now);
            entity.setLastIndexedDate(now);
            entity.setCity(city);
            entity.setCountry(country.name());
            entity.setName(name);
            entity.setOrgType(type);
            entity.setRegion(state);
            entity.setSourceId(String.valueOf(ringgoldId));
            if(parentId != null && parentId > 0) {
                entity.setSourceParentId(String.valueOf(parentId));                
            }
            entity.setSourceType(OrgDisambiguatedSourceType.RINGGOLD.name());
            entity.setIndexingStatus(IndexingStatus.PENDING);
            orgDisambiguatedDao.persist(entity);
            numAdded++;
        } else {
            // If the element have changed
            if (changed(entity, parentId, name, country, city, state, type)) {
                entity.setCity(city);
                entity.setCountry(country.name());
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
        Set<OrgDisambiguatedExternalIdentifierEntity> existingExternalIdentifiers = disambiguatedEntity.getExternalIdentifiers();
        Map<Pair<String, String>, OrgDisambiguatedExternalIdentifierEntity> existingExternalIdentifiersMap = new HashMap<>();
        
        if(existingExternalIdentifiers != null) {
            for(OrgDisambiguatedExternalIdentifierEntity entity : existingExternalIdentifiers) {
                Pair<String, String> id = Pair.of(entity.getIdentifierType(), entity.getIdentifier());
                existingExternalIdentifiersMap.put(id, entity);
            }
        }
        
        if(identifiers != null && !identifiers.isEmpty()) {
            identifiers.forEach(identifierNode -> {
                String type = identifierNode.get("identifier_type").asText();
                LOGGER.info("Processing external identifier {} for {}", type, disambiguatedEntity.getId());
                String value = identifierNode.get("value").asText();
                Pair<String, String> id = Pair.of(type, value);
                OrgDisambiguatedExternalIdentifierEntity existingEntity = existingExternalIdentifiersMap.get(id);
                //If the external identifier doesn't exists or it doesn't belong to the disambiguatedEntity, lets create it 
                if (existingEntity == null || !existingEntity.getOrgDisambiguated().getId().equals(disambiguatedEntity.getId())) {
                    OrgDisambiguatedExternalIdentifierEntity newEntity = new OrgDisambiguatedExternalIdentifierEntity();
                    Date now = new Date();
                    newEntity.setDateCreated(now);
                    newEntity.setLastModified(now);
                    newEntity.setIdentifier(value);
                    newEntity.setIdentifierType(type);
                    newEntity.setOrgDisambiguated(disambiguatedEntity);
                    newEntity.setPreferred(false);
                    orgDisambiguatedExternalIdentifierDao.persist(newEntity);
                    numAddedExtIds++;
                } else {
                    existingExternalIdentifiersMap.remove(id);
                }
            });
        }        
        
        // Then, remove all existing external identifiers that are not present in the ringgold data anymore
        for(OrgDisambiguatedExternalIdentifierEntity extIdToBeRemoved : existingExternalIdentifiersMap.values()) {
            orgDisambiguatedExternalIdentifierDao.remove(extIdToBeRemoved.getId());
        }
    }

    private void generateOrganizations(OrgDisambiguatedEntity disambiguatedEntity, List<JsonNode> altNames) {
        Date now = new Date();
        altNames.forEach(altName -> {
            String name = altName.get("name").asText();
            LOGGER.info("Processing organization {} for {}", name, disambiguatedEntity.getId());
            String city = altName.get("city").asText();
            Iso3166Country country = Iso3166Country.fromValue(altName.get("country").asText());
            //Not happy with line below.  Can steal from other ORG ID types.
            OrgEntity existingOrg = orgDao.findByNameCityRegionAndCountry(name, city, null, country.name());
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
                newOrg.setCountry(country.name());
                newOrg.setName(name);
                newOrg.setOrgDisambiguated(disambiguatedEntity);
                orgDao.persist(newOrg);
                numAddedOrgs++;
            }
        });
    }

    private void generateOrganizationFromInstitutionNode(OrgDisambiguatedEntity disambiguatedEntity, String name, Iso3166Country country, String city, String region) {
        Date now = new Date();
        //Not happy with line below.  Can steal from other ORG ID types.
        OrgEntity existingOrg = orgDao.findByNameCityRegionAndCountry(name, city, region, country.name());
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
            newOrg.setCountry(country.name());
            newOrg.setName(name);
            newOrg.setOrgDisambiguated(disambiguatedEntity);
            orgDao.persist(newOrg);
            numAddedOrgs++;
        }
    }

    private boolean changed(OrgDisambiguatedEntity entity, Integer parentId, String name, Iso3166Country country, String city, String state, String type) {
        if (!name.equals(entity.getName()) || !country.name().equals(entity.getCountry())
                || !city.equals(entity.getCity()) || !type.equals(entity.getOrgType())) {
            return true;
        }
        if(parentId == null) {
            if(entity.getSourceParentId() != null) {
                return true;
            }
        } else if(entity.getSourceParentId() == null || !parentId.equals(Integer.valueOf(entity.getSourceParentId()))) {
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
