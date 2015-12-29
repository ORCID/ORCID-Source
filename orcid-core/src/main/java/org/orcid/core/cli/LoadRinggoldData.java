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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author Will Simpson
 * 
 */
public class LoadRinggoldData {

    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";
    private static final String RINGGOLD_SOURCE_TYPE = "RINGGOLD";
    private static final String DN = "DN";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRinggoldData.class);
    @Option(name = "-f", usage = "Path to CSV file containing Ringgold parents to load into DB")
    private File fileToLoad;
    @Option(name = "-d", usage = "Path to CSV file containing Ringgold deleted IDs to process")
    private File deletedIdsFile;
    @Option(name = "-z", usage = "Path to zip file containing Ringgold data to process")
    private File zipFile;
    @Option(name = "-c", usage = "Check for duplicates only (no load)")
    private Boolean checkForDuplicates;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;
    private OrgManager orgManager;
    private int numAdded;
    private int numUpdated;
    private int numUnchanged;
    private int numSkipped;
    private int numDeleted;
    private int numDeletionsSkipped;

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

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, deletedIdsFile, zipFile, checkForDuplicates)) {
            throw new CmdLineException(parser, "At least one of -f | -d | -z | -c must be specificed");
        }
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedSolrDao = (OrgDisambiguatedSolrDao) context.getBean("orgDisambiguatedSolrDao");
        orgManager = (OrgManager) context.getBean("orgManager");
    }

    public void execute() {
        if (checkForDuplicates != null && checkForDuplicates) {
            checkForDuplicates();
            return;
        }
        dropUniqueConstraint();
        if (fileToLoad != null) {
            processParentsCsv();
        }
        if (deletedIdsFile != null) {
            processDeletedIds();
        }
        if (zipFile != null) {
            processZip();
        }
        createUniqueConstraint();
        LOGGER.info("Finished loading Ringgold data");
    }

    private void checkForDuplicates() {
        LOGGER.info("Checking for duplicates");
        List<OrgDisambiguatedEntity> duplicates = orgDisambiguatedDao.findDuplicates();
        for (OrgDisambiguatedEntity duplicate : duplicates) {
            LOGGER.info("Found duplicate: {}\t{}\t{}\t{}\t{}\t{}\t{}", new Object[] { duplicate.getSourceType(), duplicate.getSourceId(), duplicate.getName(),
                    duplicate.getCity(), duplicate.getRegion(), duplicate.getCountry(), duplicate.getOrgType() });
        }
        LOGGER.info("Finished checking for duplicates");
    }

    private void processDeletedIds() {
        try (Reader reader = openFile(deletedIdsFile)) {
            processDeletedIdsReader(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error reading csv file", e);
        }

    }

    private void processZip() {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry parentsEntry = null;
            ZipEntry deletedIdsEntry = null;
            ZipEntry altNamesEntry = null;
            for (ZipEntry entry : Collections.list(zip.entries())) {
                String entryName = entry.getName();
                if (entryName.endsWith("_parents.csv")) {
                    LOGGER.info("Found parents file: " + entryName);
                    parentsEntry = entry;
                }
                if (entryName.endsWith("deleted_ids.csv")) {
                    LOGGER.info("Found deleted ids file: " + entryName);
                    deletedIdsEntry = entry;
                }
                if (entryName.endsWith("alt_names.csv")) {
                    LOGGER.info("Found alt names file: " + entryName);
                    altNamesEntry = entry;
                }
            }
            if (parentsEntry != null) {
                Reader reader = getReader(zip, parentsEntry);
                if (altNamesEntry != null) {
                    Reader altNamesReader = getReader(zip, altNamesEntry);
                    Map<String, String> altNames = processAltNamesFile(altNamesReader);
                    processReader(reader, altNames);
                } else {
                    processReader(reader, null);
                }
            }
            if (deletedIdsEntry != null) {
                Reader reader = getReader(zip, deletedIdsEntry);
                processDeletedIdsReader(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip file", e);
        }
    }

    private Reader getReader(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        return reader;
    }

    private void processParentsCsv() {
        try (Reader reader = openFile(fileToLoad)) {
            processReader(reader, null);
        } catch (IOException e) {
            throw new RuntimeException("Error reading csv file", e);
        }
    }

    private Reader openFile(File fileToLoad) {
        Reader reader = null;
        try {
            FileInputStream fis = new FileInputStream(fileToLoad);
            reader = new InputStreamReader(fis, RINGGOLD_CHARACTER_ENCODING);
        } catch (FileNotFoundException e) {
            if (!fileToLoad.exists()) {
                throw new IllegalArgumentException("Input file does not exist: " + fileToLoad);
            }
            if (!fileToLoad.canRead()) {
                throw new IllegalArgumentException("Input exists, but can't read: " + fileToLoad);
            }
            throw new IllegalArgumentException("Unable to read input file: " + fileToLoad + "\n" + e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return reader;
    }

    private void processReader(Reader reader, Map<String, String> altNames) throws IOException {
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processLine(line, altNames);
            }
        } finally {
            LOGGER.info("Number added={}, number updated={}, number unchanged={}, num skipped={}, total={}",
                    new Object[] { numAdded, numUpdated, numUnchanged, numSkipped, getTotal() });
        }
    }

    private int getTotal() {
        return numAdded + numUpdated + numUnchanged + numSkipped;
    }

    private CSVReader createCSVReader(Reader reader) {
        return new CSVReader(reader, ',', '"', 1);
    }

    private void processLine(String[] line, Map<String, String> altNames) {
        String gpCode = line[0];
        String pCode = line[1];
        String name = line[2];
        String extName = line[3];
        if (StringUtils.isNotBlank(extName)) {
            name = extName;
        }
        String city = line[4];
        String extCity = line[5];
        if (StringUtils.isNotBlank(extCity)) {
            city = extCity;
        }
        Iso3166Country country = parseCountry(line[7]);
        String state = line[8];
        if (StringUtils.isBlank(state)) {
            state = null;
        }
        String type = line[9];

        /**
         * Look for the name in the alt names map, if there is one name, replace
         * the one found in the parents file
         */
        if (altNames != null && altNames.containsKey(pCode)) {
            if (!PojoUtil.isEmpty(altNames.get(pCode))) {
                name = altNames.get(pCode);
            }
        }

        processOrg(gpCode, pCode, name, city, state, country, type);
    }

    private Map<String, String> processAltNamesFile(Reader reader) throws IOException {
        Map<String, String> altNamesMap = new HashMap<String, String>();
        Map<String, Date> altNamesTimestamps = new HashMap<String, Date>();
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // If the DN indicator exists
                if (!PojoUtil.isEmpty(line[7]) && DN.equals(line[7])) {
                    String name = null;
                    // Get the name
                    // If the ext_name is not empty, use it
                    if (!PojoUtil.isEmpty(line[2])) {
                        LOGGER.info("Using ext_name {} for pCode {}", new Object[] { line[2], line[0] });
                        name = line[2];
                    } else {
                        LOGGER.info("Using name {} for pCode {}", new Object[] { line[2], line[0] });
                        name = line[1];
                    }

                    // get the timestamp
                    Date timestamp = null;
                    try {
                        timestamp = getDateFromTimestamp(line[8]);
                    } catch (ParseException p) {
                        LOGGER.warn("Unable to parse timestamp {} for p_code {}", new Object[] { line[8], line[0] });
                    }

                    // Check if there is already a name for that pCode
                    if (altNamesMap.containsKey(line[0])) {
                        // If the timestamp is not empty, check it against the
                        // new timestamp
                        if (altNamesTimestamps.containsKey(line[0]) && altNamesTimestamps.get(line[0]) != null) {
                            Date existing = altNamesTimestamps.get(line[0]);
                            if (existing.before(timestamp)) {
                                LOGGER.info("Replacing old name {}({}) with {}({})",
                                        new Object[] { altNamesMap.get(line[0]), altNamesTimestamps.get(line[0]), name, timestamp });
                                altNamesMap.put(line[0], name);
                                altNamesTimestamps.put(line[0], timestamp);
                            } else {
                                LOGGER.info("Leaving old name {}({}) instead of using this one {}({})",
                                        new Object[] { altNamesMap.get(line[0]), altNamesTimestamps.get(line[0]), name, timestamp });
                            }
                        } else {
                            // Else, just replace it with the new one
                            altNamesMap.put(line[0], name);
                            altNamesTimestamps.put(line[0], timestamp);
                        }
                    } else {
                        altNamesMap.put(line[0], name);
                        altNamesTimestamps.put(line[0], timestamp);
                    }
                }
            }
        } finally {
            LOGGER.info("Number added={}, number updated={}, number unchanged={}, num skipped={}, total={}",
                    new Object[] { numAdded, numUpdated, numUnchanged, numSkipped, getTotal() });
        }

        return altNamesMap;
    }

    private Date getDateFromTimestamp(String timestamp) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        try {
            return formatter.parse(timestamp);
        } catch (ParseException e) {
            throw e;
        }
    }

    private void processOrg(String gpCode, String pCode, String name, String city, String state, Iso3166Country country, String type) {
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(pCode, RINGGOLD_SOURCE_TYPE);
        if (existingEntity == null) {
            LOGGER.info("No existing disambiguated org with sourceId={} and sourceType={}", pCode, RINGGOLD_SOURCE_TYPE);
            processNew(gpCode, pCode, name, city, state, country, type);
        } else {
            LOGGER.info("Found existing disambiguated org with sourceId={} and sourceType={}", pCode, RINGGOLD_SOURCE_TYPE);
            processExisting(existingEntity, gpCode, pCode, name, city, country, state, type);
        }
    }

    private void processNew(String gpCode, String pCode, String name, String city, String state, Iso3166Country country, String type) {
        if (isDuplicate(pCode, name, city, state, country)) {
            return;
        }
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        setFields(orgDisambiguatedEntity, gpCode, pCode, name, city, country, state, type);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        createOrUpdateOrg(name, city, country, state, orgDisambiguatedEntity.getId());
        numAdded++;
    }

    private void processExisting(OrgDisambiguatedEntity existingEntity, String gpCode, String pCode, String name, String city, Iso3166Country country, String state,
            String type) {
        if (!hasChanged(existingEntity, gpCode, name, city, country, state, type)) {
            numUnchanged++;
            return;
        }
        existingEntity.setIndexingStatus(IndexingStatus.PENDING);
        setFields(existingEntity, gpCode, pCode, name, city, country, state, type);
        orgDisambiguatedDao.merge(existingEntity);
        createOrUpdateOrg(name, city, country, state, existingEntity.getId());
        numUpdated++;
    }

    private boolean isDuplicate(String pCode, String name, String city, String state, Iso3166Country country) {
        OrgDisambiguatedEntity duplicate = orgDisambiguatedDao.findByNameCityRegionCountryAndSourceType(name, city, state, country, RINGGOLD_SOURCE_TYPE);
        if (duplicate != null) {
            LOGGER.info("Skipping disambiguated org with sourceId={} because it appears to be a duplicate of sourceId={}, sourceType={}",
                    new Object[] { pCode, duplicate.getSourceId(), RINGGOLD_SOURCE_TYPE });
            numSkipped++;
            return true;
        }
        return false;
    }

    private void createOrUpdateOrg(String name, String city, Iso3166Country country, String state, Long orgDisambiguatedId) {
        // Ensure there is a corresponding org and that the org is linked to the
        // disambiguated org
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setName(name);
        orgEntity.setRegion(state);
        orgEntity.setCity(city);
        orgEntity.setCountry(country);
        orgManager.createUpdate(orgEntity, orgDisambiguatedId);
    }

    private boolean hasChanged(OrgDisambiguatedEntity existingEntity, String gpCode, String name, String city, Iso3166Country country, String state, String type) {
        if (!gpCode.equals(existingEntity.getSourceParentId())) {
            return true;
        }
        if (!name.equals(existingEntity.getName())) {
            return true;
        }
        if (!city.equals(existingEntity.getCity())) {
            return true;
        }
        if (!country.equals(existingEntity.getCountry())) {
            return true;
        }
        String existingRegion = existingEntity.getRegion();
        if (state == null) {
            if (existingRegion != null) {
                return true;
            }
        } else if (!state.equals(existingRegion)) {
            return true;
        }
        if (!type.equals(existingEntity.getOrgType())) {
            return true;
        }
        return false;
    }

    private void setFields(OrgDisambiguatedEntity orgDisambiguatedEntity, String gpCode, String pCode, String name, String city, Iso3166Country country, String state,
            String type) {
        orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setCity(city);
        orgDisambiguatedEntity.setRegion(state);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setOrgType(type);
        orgDisambiguatedEntity.setSourceId(pCode);
        orgDisambiguatedEntity.setSourceParentId(gpCode);
        orgDisambiguatedEntity.setSourceType(RINGGOLD_SOURCE_TYPE);
    }

    private Iso3166Country parseCountry(String countryString) {
        countryString = countryString.toUpperCase();
        if ("USA".equals(countryString)) {
            countryString = "US";
        } else if ("CAN".equals(countryString)) {
            countryString = "CA";
        }
        return Iso3166Country.valueOf(countryString);
    }

    private void processDeletedIdsReader(Reader reader) throws IOException {
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processDeletedIdsLine(line);
            }
        } finally {
            LOGGER.info("Number deleted={}, number deletions skipped={}", new Object[] { numDeleted, numDeletionsSkipped });
        }
    }

    private void processDeletedIdsLine(String[] line) {
        String deletedSourceId = line[0];
        String replacementSourceId = line[1];
        OrgDisambiguatedEntity deletedEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(deletedSourceId, RINGGOLD_SOURCE_TYPE);
        if (deletedEntity != null) {
            LOGGER.info("Deleted ID exists in DB, id={}", deletedSourceId);
            Long deletedEntityId = deletedEntity.getId();
            OrgDisambiguatedEntity replacementEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(replacementSourceId, RINGGOLD_SOURCE_TYPE);
            if (replacementEntity == null) {
                LOGGER.warn("Replacement does not exist, id={}", replacementEntity);
                numDeletionsSkipped++;
            } else {
                Long replacementEntityId = replacementEntity.getId();
                orgDisambiguatedSolrDao.remove(deletedEntityId);
                orgDisambiguatedDao.replace(deletedEntityId, replacementEntityId);
                orgDisambiguatedDao.remove(deletedEntityId);
                numDeleted++;
            }
        }
    }

    private void createUniqueConstraint() {
        LOGGER.info("About to create unique constraint");
        try {
            orgDisambiguatedDao.createUniqueConstraint();
            LOGGER.info("Finished creating unique constraint");
        } catch (RuntimeException e) {
            LOGGER.warn("Problem creating unique constraint");
            checkForDuplicates();
        }
    }

    private void dropUniqueConstraint() {
        LOGGER.info("About to drop unique constraint");
        orgDisambiguatedDao.dropUniqueConstraint();
    }

}